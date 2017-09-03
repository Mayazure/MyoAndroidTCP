package com.example.mayazure.myodrivingtool;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.mayazure.myodrivingtool.tcptool.TCPClient;
import com.example.mayazure.myodrivingtool.util.Const;

public class SocketService extends Service {

    private TCPClient client = new TCPClient(this);
    private final IBinder binder = new ServiceBinder();
    private boolean clientIsOn = false;

    private ConnectReceiver connectReceiver;
    private SendReceiver sendReceiver;

    @Override
    public void onCreate(){
        super.onCreate();
//        System.out.println("----------------------- onCreate -----------------------");
        register();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregister();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //
        return super.onStartCommand(intent, flags, startId);
//        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class ServiceBinder extends Binder {

        SocketService getService(){
            return SocketService.this;
        }
    }

    private class ConnectReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String IP = intent.getStringExtra("IP");
            String Port = intent.getStringExtra("Port");
            client.connect(IP, Port);
            new Thread(client,"TCPClient").start();
            clientIsOn = true;

        }
    }

    private class SendReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String content=intent.getStringExtra("data");
            client.send(content);
        }
    }

    private void register(){
        IntentFilter filter = new IntentFilter(Const.BROADCAST_CONNECT);
        connectReceiver = new ConnectReceiver();
        registerReceiver(connectReceiver, filter);

        IntentFilter filter2 = new IntentFilter(Const.BROADCAST_SEND);
        sendReceiver = new SendReceiver();
        registerReceiver(sendReceiver, filter2);
    }

    private void unregister(){
        unregisterReceiver(connectReceiver);
        unregisterReceiver(sendReceiver);
    }
}
