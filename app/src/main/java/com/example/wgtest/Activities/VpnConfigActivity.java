package com.example.wgtest.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wgtest.R;
import com.example.wgtest.VpnTools.WgConfig;

public class VpnConfigActivity extends AppCompatActivity {

    Button saveSettingsBtn;

    EditText allowedIpEt, endpointEt, peerPublicKeyEt, persistentKeepAliveEt,
            interfacePrivateKeyEt, interfaceAddressEt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpn_config);

        findViews();

        saveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        SharedPreferences prefs;
        prefs = getSharedPreferences("CFG",MODE_PRIVATE);
        if(prefs.getString("HaveSettings","").equals("true")){
            Log.e("vpnset", "loaded settings");
            allowedIpEt.setText(prefs.getString("AllowedIp",""));
            endpointEt.setText(prefs.getString("Endpoint",""));
            peerPublicKeyEt.setText(prefs.getString("peerPublicKey",""));
            persistentKeepAliveEt.setText(prefs.getString("PersistentKeepAlive",""));
            interfacePrivateKeyEt.setText(prefs.getString("InterfacePrivateKey",""));
            interfaceAddressEt.setText(prefs.getString("InterfaceAddress",""));
        }
        else{
            int i = WgConfig.PersistentKeepAlive;

            allowedIpEt.setText(WgConfig.AllowedIp);
            endpointEt.setText(WgConfig.Endpoint);
            peerPublicKeyEt.setText(WgConfig.peerPublicKey);
            persistentKeepAliveEt.setText(String.valueOf(WgConfig.PersistentKeepAlive));
            interfacePrivateKeyEt.setText(WgConfig.InterfacePrivateKey);
            interfaceAddressEt.setText(WgConfig.InterfaceAddress);
        }
    }

    private void saveData(){
        SharedPreferences prefs;
        prefs = getSharedPreferences("CFG",MODE_PRIVATE);
        Editor ed = prefs.edit();
        ed.putString("HaveSettings", "true");
        ed.putString("AllowedIp", allowedIpEt.getText().toString());
        ed.putString("Endpoint", endpointEt.getText().toString());
        ed.putString("peerPublicKey",peerPublicKeyEt.getText().toString());
        ed.putString("PersistentKeepAlive",persistentKeepAliveEt.getText().toString());
        ed.putString("InterfacePrivateKey",interfacePrivateKeyEt.getText().toString());
        ed.putString("InterfaceAddress",interfaceAddressEt.getText().toString());
        ed.apply();
    }

    private void findViews(){
        allowedIpEt = findViewById(R.id.AllowedIp_et);
        endpointEt= findViewById(R.id.EndPoint_et);
        peerPublicKeyEt= findViewById(R.id.PeerPublicKey_et);
        persistentKeepAliveEt = findViewById(R.id.PersistenrKeepAlive_et);
        interfacePrivateKeyEt = findViewById(R.id.InterfacePrivateKey_et);
        interfaceAddressEt = findViewById(R.id.InterfaceAddress_et);
        saveSettingsBtn = findViewById(R.id.SaveSetting_btn);
    }
}