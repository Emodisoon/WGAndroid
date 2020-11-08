package com.example.wgtest.VpnTools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.wireguard.android.backend.BackendException;
import com.wireguard.android.backend.GoBackend;
import com.wireguard.android.backend.Statistics;
import com.wireguard.android.backend.Tunnel;
import com.wireguard.config.Config;
import com.wireguard.config.InetNetwork;
import com.wireguard.config.Interface;
import com.wireguard.config.Peer;
import com.wireguard.crypto.Key;

import java.net.InetAddress;
import java.util.Objects;

//Класс для работы с VPN

public class VpnWorker {
    MyTunnel tunnel;
    GoBackend backend;
    Config.Builder config;
    Context context;
    Boolean isVpnRunning = false;
    public VpnWorker(Context context){
        this.context = context;
        config = new Config.Builder();

        backend = new GoBackend(context);

        //Создание конфигурации WireGuard

    }

    //Подключение к VPN
    public void ConnectVpn(){
        if(isVpnRunning)
            Toast.makeText(context,"VPN already running",Toast.LENGTH_SHORT).show();
        else {
            StartConnection start = new StartConnection();
            start.execute();
        }
    }

    //Отключение от VPN
    public void  DisconnetVPN(){
        try{
            backend.setState(tunnel, Tunnel.State.DOWN, null);
        }catch (Exception e ){
            e.printStackTrace();
        }
        isVpnRunning = false;
        Toast.makeText(context, "VPN disconnected", Toast.LENGTH_SHORT).show();
    }

    //Получение статистики
    public Statistics getStatistics(){
        return backend.getStatistics(tunnel);
    }

    public boolean isVpnConnected(){
        return isVpnRunning;
    }

    @SuppressLint("StaticFieldLeak")
    private class StartConnection extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Peer.Builder peer = new Peer.Builder();


            Interface.Builder inter = new Interface.Builder();
            try {
                Log.e("Endpoint:",WgConfig.Endpoint);
                peer.addAllowedIp(InetNetwork.parse(WgConfig.AllowedIp));
                peer.parseEndpoint(WgConfig.Endpoint);
                peer.setPublicKey(Key.fromBase64(WgConfig.peerPublicKey));
                peer.setPersistentKeepalive(WgConfig.PersistentKeepAlive);

                inter.parsePrivateKey(WgConfig.InterfacePrivateKey);
                inter.addDnsServer(InetAddress.getByName("8.8.8.8"));
                inter.addAddress(InetNetwork.parse(WgConfig.InterfaceAddress));

                config.addPeer(peer.build());
                config.setInterface(inter.build());
            }catch (Exception e){
                Log.e("VpnWorker", Objects.requireNonNull(e.getMessage()));
            }
            tunnel = new MyTunnel("MyTunnel", config.build(), Tunnel.State.DOWN);

            try {
                backend.setState(tunnel, Tunnel.State.UP, config.build());
            }catch (Exception e ){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isVpnRunning = true;
            Toast.makeText(context, "VPN connected", Toast.LENGTH_SHORT).show();
        }
    }
}
