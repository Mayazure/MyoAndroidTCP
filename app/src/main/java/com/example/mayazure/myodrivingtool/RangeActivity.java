package com.example.mayazure.myodrivingtool;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.example.mayazure.myodrivingtool.util.Const;

public class RangeActivity extends Activity {

    private String test = "test";

    private SeekBar range1;
    private SeekBar range2;
//    private Button submit;

    private Boolean flag1 = false;
    private Boolean flag2 = false;
//    private String recvTimestamp;

    private SubReceiver subReceiver;
    private MySeekbarChangeListener seekBarChangeListener = new MySeekbarChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_range);
        initView();

        Bundle bundle = this.getIntent().getExtras();
//        recvTimestamp = this.getIntent().getStringExtra("recvTimestamp");
//        recvTimestamp = bundle.getString("recvTimestamp");
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

    private void initView(){
        range1 = (SeekBar)findViewById(R.id.range1);
        range2 = (SeekBar)findViewById(R.id.range2);
//        submit = (Button)findViewById(R.id.submit);

        range1.setOnSeekBarChangeListener(seekBarChangeListener);
        range2.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    public void submit(View view){
        Intent intent = new Intent(Const.BROADCAST_SEND);
        intent.putExtra("data",generateData());
        sendBroadcast(intent);
        Intent intent2 = new Intent();
        intent2.setClass(RangeActivity.this, DrivingActivity.class);
        RangeActivity.this.startActivity(intent2);
        RangeActivity.this.finish();
    }

    private String generateData(){
        StringBuilder sb = new StringBuilder();
//        sb.append("$1=").append(recvTimestamp);
//        sb.append("$2=").append(System.currentTimeMillis());
        sb.append("$R1=").append(range1.getProgress());
        sb.append("$R2=").append(range2.getProgress());
        return sb.toString();
    }

    private class MySeekbarChangeListener implements SeekBar.OnSeekBarChangeListener{
        private MyTimer myTimer;
        private boolean timerIsOn = false;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(seekBar == range1){
                flag1 = true;
//                System.out.println("flag1 true");
            }
            else if(seekBar == range2){
                flag2 = true;
//                System.out.println("flag2 true");
            }
            if(flag1&&flag2){
//                System.out.println("true true");
//                submit.setBackgroundColor(getResources().getColor(R.color.highlightbtn));
//                submit.setTextColor(Color.WHITE);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if(flag1&&flag2){
                myTimer.resetTime();
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if(!timerIsOn){
                myTimer = new MyTimer(this);

                if(flag1&&flag2){
                    myTimer.start();
                    timerIsOn = true;
                }
            }
        }

        public void timeUp(){
            timerIsOn = false;
            submit(null);
        }
    }

//    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
//
//        private MyTimer myTimer;
//        private boolean timerIsOn = false;
//
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if(seekBar == range1){
//                flag1 = true;
//                System.out.println("flag1 true");
//            }
//            else if(seekBar == range2){
//                flag2 = true;
//                System.out.println("flag2 true");
//            }
//            if(flag1&&flag2){
//                System.out.println("true true");
//                submit.setBackgroundColor(getResources().getColor(R.color.highlightbtn));
//                submit.setTextColor(Color.WHITE);
//            }
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//            if(flag1&&flag2){
//                myTimer.resetTime();
//            }
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            if(!timerIsOn){
//                myTimer = new MyTimer(this);
//                timerIsOn = true;
//            }
//
//            if(flag1&&flag2){
//                myTimer.start();
//            }
//        }
//
//        public void setTimerState(boolean state){
//            timerIsOn = state;
//        }
//    };

    private class SubReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.BROADCAST_SUBMIT)) {
                submit(null);
            }
            else if(action.equals(Const.BROADCAST_NOTI)){
                ring();
            }
        }
    }

    private void register(){
        IntentFilter filter = new IntentFilter(Const.BROADCAST_SUBMIT);
        IntentFilter filter1 = new IntentFilter(Const.BROADCAST_NOTI);
        subReceiver = new SubReceiver();
        registerReceiver(subReceiver, filter);
        registerReceiver(subReceiver, filter1);
    }

    private void unregister(){
        unregisterReceiver(subReceiver);
    }

    private class MyTimer extends Thread{

        private volatile int time = 3;
        private MySeekbarChangeListener listener;

        public MyTimer(MySeekbarChangeListener listener){
            this.listener = listener;
        }

        public void resetTime(){
            time = 2;
//            System.out.println("------------------------reset time------------------------");
        }

        @Override
        public void run(){
            while(time>0){
                time--;
//                System.out.println("------------------------count down------------------------");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            listener.timeUp();
//            System.out.println("------------------------timer stop------------------------");
        }
    }

    private void ring(){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
        rt.play();
    }
}
