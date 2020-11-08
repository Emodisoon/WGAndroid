package com.example.wgtest.VpnTools;

//Класс конфигурации VPN

import android.content.SharedPreferences;

import java.util.prefs.Preferences;

public class WgConfig {
    //Конфигурация пира
    public static String AllowedIp           = "0.0.0.0/0";
    public static String Endpoint            = "82.148.16.221:51194";
    public static String peerPublicKey       = "EWY68J/fQ/kAGjiD3etT5X6CCI645PjWmY1C7XtVOS4=";
    public static int PersistentKeepAlive    = 15;
    //Конфигурация Интерфейса
    public static String InterfacePrivateKey = "QBFQ85WXg8uXgyKRsHKiewu4N7EnXoq5i1io8yl5L2I=";
    public static String InterfaceAddress    = "10.0.0.2/24";



}
