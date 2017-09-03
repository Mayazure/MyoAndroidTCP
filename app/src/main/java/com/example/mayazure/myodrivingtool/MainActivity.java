package com.example.mayazure.myodrivingtool;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mayazure.myodrivingtool.util.Const;

public class MainActivity extends Activity {

    private EditText InputIP;
    private EditText InputPort;
    private ConnectSuccessReceiver connectSuccessReceiver;
    private ConnectFailReceiver connectFailReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        register();
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregister();
    }

    private void initView() {
        InputIP = (EditText) findViewById(R.id.InputIP);
        InputPort = (EditText) findViewById(R.id.InputPort);
    }

    public void connect(View view) {
        Intent intent = new Intent(Const.BROADCAST_CONNECT);
        intent.putExtra("IP",InputIP.getText().toString());
        intent.putExtra("Port", InputPort.getText().toString());
        sendBroadcast(intent);

//        Intent intent2 = new Intent();
//        intent2.setClass(MainActivity.this, DrivingActivity.class);
//        MainActivity.this.startActivity(intent2);
//        MainActivity.this.finish();
    }

    private class ConnectSuccessReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent2 = new Intent();
            intent2.setClass(MainActivity.this, DrivingActivity.class);
            MainActivity.this.startActivity(intent2);
            MainActivity.this.finish();
        }
    }

    private class ConnectFailReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this,"fail",Toast.LENGTH_LONG).show();
        }
    }

    private void register(){
        IntentFilter filter = new IntentFilter(Const.BROADCAST_CONNECT_SUCCESS);
        connectSuccessReceiver = new ConnectSuccessReceiver();
        registerReceiver(connectSuccessReceiver, filter);

        IntentFilter filter2 = new IntentFilter(Const.BROADCAST_CONNECT_FAIL);
        connectFailReceiver = new ConnectFailReceiver();
        registerReceiver(connectFailReceiver, filter2);
    }

    private void unregister(){
        unregisterReceiver(connectSuccessReceiver);
        unregisterReceiver(connectFailReceiver);
    }

}