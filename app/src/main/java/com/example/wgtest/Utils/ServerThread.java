package com.example.wgtest.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class ServerThread implements Runnable {

    static String TAG = "Server Socket Thread";

    private LocationCallback locationCallback;

    private int serverPort;
    private Thread thread;

    private boolean isGPSEnabled;

    private Location myLocation;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            myLocation = location;
        }
        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    public ServerThread(Context context, boolean isGPSEnabled, int ServerPort){
        this.serverPort = ServerPort;
        this.isGPSEnabled = isGPSEnabled;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.e(TAG, "location error");
        }

        if(isGPSEnabled) {

            LocationRequest locationRequest = new LocationRequest();

            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(500);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null)
                        Log.e(TAG, "location result error");
                    for (Location location : locationResult.getLocations()) {
                        myLocation = location;
                    }
                }
            };

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        else{
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100*10, 1, locationListener);
        }
        thread = new Thread(this, "ServerSocket thread");
        thread.start();
        Log.e(TAG, "ServerSocket started succesfully");
    }



    private String getMyLocation(){
        if(myLocation!=null)
            return myLocation.getLatitude() + " " + myLocation.getLongitude();
        return "0 0";
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {

            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String line;
                line = in.readUTF();
                Log.e(TAG, "got request: " + line);
                switch (line){
                    case Requests.isOnline:
                        line = "Ok";
                        break;
                    case Requests.getLocation:
                        line = getMyLocation();
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
