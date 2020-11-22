package com.example.wgtest.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {

    static String TAG = "Server Socket Thread";


    private int serverPort;
    Thread thread;
    private Context context;
    MyLocationListener myLocationListener;
    public ServerThread(Context context, int ServerPort, MyLocationListener locationListener){
        this.serverPort = ServerPort;
        this.context = context;
        this.myLocationListener = locationListener;
        thread = new Thread(this, "ServerSocket thread");
        thread.start();
    }

    @Override
    public void run() {
        Log.e(TAG, "server started");

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {

            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                String line;



                line = in.readUTF();

                Log.e(TAG, line);

                switch (line){
                    case Requests.isOnline:
                        line = "Ok";
                        break;
                    case Requests.getLocation:

                        if(myLocationListener.getLongitude() != null && myLocationListener.getLatitude() !=null)
                            line = myLocationListener.getLatitude() + " " + myLocationListener.getLongitude();

                        else
                            line="error";



                        break;
                    default:
                        break;
                }

                out.writeUTF(line);
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
