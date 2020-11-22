/*

 *
 */

package com.example.wgtest.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.wgtest.R;
import com.example.wgtest.Utils.MyLocationListener;
import com.example.wgtest.Utils.ServerThread;
import com.example.wgtest.VpnTools.VpnWorker;
import com.example.wgtest.VpnTools.WgConfig;
import com.wireguard.android.backend.GoBackend;
import com.wireguard.android.backend.Statistics;


public class MainActivity extends AppCompatActivity {
    static String TAG = "MyVpn";

    ServerThread serverThread;

    Button statsBtn, vpnConfActivityBtn, testBtn;
    TextView RxTv, TxTv, IpTv;

    VpnWorker vpnWorker;
    Statistics stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FindViews();

        //Запуск впн
        vpnWorker = new VpnWorker(getApplicationContext());
        vpnWorker.ConnectVpn();



        //Права на координаты todo Сделать проверку на gps
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"location error", Toast.LENGTH_SHORT).show();
        }else
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.e(TAG, "enabled GPS provider");
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListener,null);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }
            else {
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListener,null);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        //Запуск сервер сокета
        serverThread = new ServerThread(getApplicationContext(),9999, locationListener);

        // показываем Activity для запроса прав ВПН у пользователя
        Intent intent = GoBackend.VpnService.prepare(this);
        if(intent !=null)
        startActivityForResult(intent, 1); // запрос прав

        testBtn = findViewById(R.id.TestBtn);
        testBtn.setOnClickListener(v -> {

        });

        statsBtn.setOnClickListener(v -> GetStats());
        vpnConfActivityBtn.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), VpnConfigActivity.class);
            startActivity(intent1);
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
        statsBtn = findViewById(R.id.Vpn_stat_btn);
        RxTv = findViewById(R.id.RxTv);
        TxTv = findViewById(R.id.TxTv);
        IpTv = findViewById(R.id.ipTv);
        vpnConfActivityBtn = findViewById(R.id.VpnConfActivityBtn);
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
