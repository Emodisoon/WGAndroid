package com.example.wgtest.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wgtest.R;
import com.example.wgtest.VpnTools.VpnWorker;
import com.example.wgtest.VpnTools.WgConfig;
import com.wireguard.android.backend.GoBackend;
import com.wireguard.android.backend.Statistics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    static String TAG = "MyVpn";

    Button vpnConnectBtn;
    Button vpnDisconnectBtn;
    Button statsBtn, SocketBtn, vpnConfActivityBtn;
    TextView RxTv, TxTv, IpTv;

    VpnWorker vpnWorker;
    Statistics stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FindViews();

        vpnWorker = new VpnWorker(getApplicationContext());

        // показываем Activity для запроса прав у пользователя
        Intent intent = GoBackend.VpnService.prepare(this);
        if(intent !=null)
        startActivityForResult(intent, 1); // запрос прав

        vpnConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpnWorker.ConnectVpn();
            }

        });
        vpnDisconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpnWorker.DisconnetVPN();
            }
        });
        statsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetStats();
            }
        });
        SocketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendSocket ss = new SendSocket();
                ss.execute();
            }
        });
        vpnConfActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VpnConfigActivity.class);
                startActivity(intent);
            }
        });


    }



    void GetStats(){
        if(vpnWorker.isVpnConnected()) {
            stats = vpnWorker.getStatistics();
            Log.d("transmitted traffic", Long.toString(stats.totalTx()));

            Log.d("recieved traffic", Long.toString(stats.totalRx()));
            if (stats.totalRx() > 1000000 && stats.totalTx() > 1000000) {
                RxTv.setText("Получено: " + stats.totalRx() / 1000000 + " мБ");
                TxTv.setText("Отправлено: " + stats.totalTx() / 1000000 + " мБ");
            } else {
                RxTv.setText("Получено: " + stats.totalRx() / 1000 + " кБ");
                TxTv.setText("Отправлено: " + stats.totalTx() / 1000 + " кБ");
            }
            IpTv.setText("IP: " + WgConfig.InterfaceAddress);
        }
        else
            Toast.makeText(getApplicationContext(), "Vpn is not running",Toast.LENGTH_SHORT).show();
    }

    void FindViews(){
        vpnConnectBtn = findViewById(R.id.Connect_Vpn_btn);
        vpnDisconnectBtn = findViewById(R.id.Disconnect_btn);
        statsBtn = findViewById(R.id.Vpn_stat_btn);
        RxTv = findViewById(R.id.RxTv);
        TxTv = findViewById(R.id.TxTv);
        IpTv = findViewById(R.id.ipTv);
        SocketBtn = findViewById(R.id.SocketBtn);
        vpnConfActivityBtn = findViewById(R.id.VpnConfActivityBtn);
    }

    private class SendSocket extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.e("socket", "sending");
                InetAddress ipAddress = InetAddress.getByName("10.0.0.3");
                Socket socket = new Socket(ipAddress, 8080);

                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                String line = "poshel nahooy";

                out.writeUTF(line);
                out.flush();
                line = in.readUTF();
                Log.e("line", line);

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    void LoadCfgFromSave() {
        SharedPreferences prefs =  getSharedPreferences("CFG",MODE_PRIVATE);
        if (prefs.getString("HaveSettings", "").equals("true")) {
           WgConfig.AllowedIp = prefs.getString("AllowedIp","");
           WgConfig.Endpoint =  prefs.getString("Endpoint","");
           WgConfig.peerPublicKey = prefs.getString("peerPublicKey","");
           WgConfig.PersistentKeepAlive = Integer.parseInt(prefs.getString("PersistentKeepAlive","15"));
           WgConfig.InterfacePrivateKey =  prefs.getString("InterfacePrivateKey","");
           WgConfig.InterfaceAddress = prefs.getString("InterfaceAddress","");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(vpnWorker.isVpnConnected())
            vpnWorker.DisconnetVPN();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadCfgFromSave();
    }
}
