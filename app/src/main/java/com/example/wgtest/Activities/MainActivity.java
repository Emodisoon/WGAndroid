package com.example.wgtest.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.wgtest.R;
import com.example.wgtest.Utils.ServerThread;
import com.example.wgtest.VpnTools.VpnWorker;
import com.example.wgtest.VpnTools.WgConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.wireguard.android.backend.GoBackend;
import com.wireguard.android.backend.Statistics;


public class MainActivity extends AppCompatActivity {
    static String TAG = "MyVpn";

    ServerThread serverThread;

    FusedLocationProviderClient fusedLocationProviderClient;

    Button statsBtn, vpnConfActivityBtn;
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

        //Права на координаты
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);


        // показываем Activity для запроса прав ВПН у пользователя
        Intent intent = GoBackend.VpnService.prepare(this);
        if (intent != null)
            startActivityForResult(intent, 1); // запрос прав


        statsBtn.setOnClickListener(v -> GetStats());
        vpnConfActivityBtn.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), VpnConfigActivity.class);
            startActivity(intent1);
        });


        boolean isGps = true;

        //Location work:
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("На вашем устройстве выключен GPS. Вы хотитет его включить? Это необходимо для корректной работы приложения.")
                    .setCancelable(false)
                    .setPositiveButton("Да", (dialog, id) -> {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        serverThread = new ServerThread(getApplicationContext(), true, 9999);
                    })
                    .setNegativeButton("Нет", (dialog, id) -> {
                        dialog.cancel();
                        serverThread = new ServerThread(getApplicationContext(), false, 9999);
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            serverThread = new ServerThread(getApplicationContext(), true, 9999);
        }


    }

    @SuppressLint("SetTextI18n")
    void GetStats() {
        if (vpnWorker.isVpnConnected()) {
            stats = vpnWorker.getStatistics();
            if (stats.totalRx() > 1000000 && stats.totalTx() > 1000000) {
                RxTv.setText("Получено: " + stats.totalRx() / 1000000 + " мБ");
                TxTv.setText("Отправлено: " + stats.totalTx() / 1000000 + " мБ");
            } else {
                RxTv.setText("Получено: " + stats.totalRx() / 1000 + " кБ");
                TxTv.setText("Отправлено: " + stats.totalTx() / 1000 + " кБ");
            }
            IpTv.setText("IP: " + WgConfig.InterfaceAddress);
        } else
            Toast.makeText(getApplicationContext(), "Vpn is not running", Toast.LENGTH_SHORT).show();
    }

    void FindViews() {
        statsBtn = findViewById(R.id.Vpn_stat_btn);
        RxTv = findViewById(R.id.RxTv);
        TxTv = findViewById(R.id.TxTv);
        IpTv = findViewById(R.id.ipTv);
        vpnConfActivityBtn = findViewById(R.id.VpnConfActivityBtn);
    }

    void LoadCfgFromSave() {
        SharedPreferences prefs = getSharedPreferences("CFG", MODE_PRIVATE);
        if (prefs.getString("HaveSettings", "").equals("true")) {
            WgConfig.AllowedIp = prefs.getString("AllowedIp", "");
            WgConfig.Endpoint = prefs.getString("Endpoint", "");
            WgConfig.peerPublicKey = prefs.getString("peerPublicKey", "");
            WgConfig.PersistentKeepAlive = Integer.parseInt(prefs.getString("PersistentKeepAlive", "15"));
            WgConfig.InterfacePrivateKey = prefs.getString("InterfacePrivateKey", "");
            WgConfig.InterfaceAddress = prefs.getString("InterfaceAddress", "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vpnWorker.isVpnConnected())
            vpnWorker.DisconnetVPN();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadCfgFromSave();
    }
}
