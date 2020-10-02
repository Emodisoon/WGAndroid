package com.example.wgtest.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wgtest.R;

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

            }
        });
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