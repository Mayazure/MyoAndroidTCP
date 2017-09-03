package com.example.mayazure.myodrivingtool;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.mayazure.myodrivingtool.util.Const;

public class DrivingActivity extends Activity {

    private ReceiveReceiver receiveReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_driving);
    }

    @Override
    public void onStart(){
        super.onStart();
        register();
    }

    @Override
    public void onStop(){
        super.onStop();
        unregister();
    }

//    public void debug(View view){
//        Intent intent = new Intent();
////        intent.putExtra("name","LeiPei");
//        intent.setClass(DrivingActivity.this, RangeActivity.class);
//        DrivingActivity.this.startActivity(intent);
//        DrivingActivity.this.finish();
//    }

    private class ReceiveReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(action.equals(Const.BROADCAST_RECEIVE)){
                ring();
                intent.setClass(DrivingActivity.this, RangeActivity.class);
                DrivingActivity.this.startActivity(intent);
                DrivingActivity.this.finish();
            }
            else if(action.equals(Const.BROADCAST_END)){
                intent.setClass(DrivingActivity.this, EndActivity.class);
                DrivingActivity.this.startActivity(intent);
                DrivingActivity.this.finish();
            }

        }
    }

    private void register(){
        IntentFilter filter = new IntentFilter(Const.BROADCAST_RECEIVE);
        IntentFilter filter2 = new IntentFilter(Const.BROADCAST_END);
        receiveReceiver = new ReceiveReceiver();
        registerReceiver(receiveReceiver, filter);
        registerReceiver(receiveReceiver, filter2);
    }

    private void unregister(){
        unregisterReceiver(receiveReceiver);
    }

    private void ring(){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
        rt.play();
    }
}
