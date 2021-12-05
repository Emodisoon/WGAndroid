# WGAndroid
Андроид приложение для подключения к VPN серверву Wireguard и обработке команд от backend сервера.

* Java
* Wireguard 

## Описание

Клиентское устройство и компьютер на котором запущен сервер должны быть в одной 
локальной сети VPN WireGuard.

VPN необходим для упрощения передачи сообщений от сервера к клиентскому устройству с испльзованием сокетов. 

На данный момент реализованы команды проверки на онлайн и получение геолокации устройства.

## Структура проекта

* Папка Acitivities

Здесь расположены классы для работы с GUI.

* Папка Acitivities

Здесь расположены вспомогательные классы приложния.

* Папка VpnTools

Здесь расположены классы для работы с VPN.

[Backend Сервер](https://github.com/Emodisoon/WGServer)
