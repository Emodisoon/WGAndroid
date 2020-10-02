package com.example.wgtest.VpnTools;

import androidx.annotation.Nullable;

import com.wireguard.android.backend.Tunnel;
import com.wireguard.config.Config;

public class MyTunnel implements Tunnel {
    String name;
    Config config;
    Tunnel.State state;

    public MyTunnel(String name, Config config, @Nullable Tunnel.State state){
        this.config = config;
        this.name = name;
        this.state = state;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onStateChange(State newState) {
        this.state = newState;
    }
}
