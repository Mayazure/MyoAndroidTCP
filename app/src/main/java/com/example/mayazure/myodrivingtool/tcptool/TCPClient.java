package com.example.mayazure.myodrivingtool.tcptool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.example.mayazure.myodrivingtool.util.Const;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Mayazure on 2017/5/16.
 */

public class TCPClient implements Runnable{

    private Socket client;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    private boolean socketIsOn = false;
    private Context context;

    public TCPClient(Context context){
        this.context = context;
        socketIsOn = true;
    }

    public void connect(final String IP, final String PORT){
        final int port = Integer.parseInt(PORT);
        new Thread("connect"){
            @Override
            public void run(){
                try{
                    client = new Socket(IP, port);
                    inputStream = new DataInputStream(client.getInputStream());
                    outputStream = new DataOutputStream(client.getOutputStream());
                    System.out.println("-------------------------------------Connected-------------------------------------");

                    Intent intent = new Intent(Const.BROADCAST_CONNECT_SUCCESS);
                    context.sendBroadcast(intent);

                }catch(IOException e){
                    e.printStackTrace();
                    System.out.println("Fail to connect to server.");
                    socketIsOn = false;

                    Intent intent = new Intent(Const.BROADCAST_CONNECT_FAIL);
                    context.sendBroadcast(intent);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void send(String data){
        try{
            outputStream.writeUTF(data);
        }catch(IOException e){
            System.out.println("Failed to write.");
        }
    }

    @Override
    public void run(){
        while(socketIsOn){
            String data = null;
            if(inputStream == null){
                continue;
            }
            try {
                data = inputStream.readUTF();
                onRead(data);
            } catch (IOException e) {
                // TODO Auto-generated catch block
//				e.printStackTrace();
//                System.out.println("Read timeout.");
//				close();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void onRead(String data){
        Intent intent = new Intent();
        long recvtimestamp = System.currentTimeMillis();
        if(data.equals("SIG")){
            intent.setAction(Const.BROADCAST_RECEIVE);
        }
        else if(data.equals("END")){
            intent.setAction(Const.BROADCAST_END);
            socketIsOn = false;
        }
        else if(data.equals("SUB")){
            intent.setAction(Const.BROADCAST_SUBMIT);
//            System.out.println("---------------------SUB-------------------------");
        }
        else if(data.equals("NOTI")){
            intent.setAction(Const.BROADCAST_NOTI);
        }else if(data.equals("PING")){
            responsePing();
        }

        intent.putExtra("cmd",data);
        intent.putExtra("recvTimestamp",recvtimestamp+"");
        context.sendBroadcast(intent);
    }

    private void responsePing(){
        send("PingRes");
    }

}
