package com.example.werty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import android.util.Log;

public class SocketManager{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String data,send;

    private String HostIp;
    private int Port;

    boolean Alive() {
        return (socket!=null && !socket.isClosed());
    }
    class InputHandler implements Runnable
    {
        public void run()
        {
            try {
                while (!socket.isClosed()) {
                    data = in.readLine();
                    while(in.ready())
                        data = data + "\n" + in.readLine();
                    Log.w("NETWORK","receive : " + data);
                }
            } catch (Exception e) {
            }
        }
    }
    public int setSocket(String host, int port) {

        HostIp = host;
        Port = port;

        Thread t = new Thread(new Runnable(){
            public void run() {
                try {
                    if (socket == null || socket.isClosed())
                    {
                        socket = new Socket(HostIp, Port);
                        out = new PrintWriter(socket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        new Thread(new InputHandler()).start();
                    }
                } catch (IOException e) {
                }

            }
        });
        try {
            t.start();
            t.join(); //socket이 thread 내에서 생성될때까지 기다렸다가 리턴
        }catch(Exception e){
        }
        Log.w("TAG","setSocket end");
        return 0;
    }
    public String getData()
    {
        return data;
    }
    public void setData(String str)
    {
        if(str.isEmpty())
            return;
        send = str;
        Log.w("NETWORK","send : " + send);
        new Thread(new Runnable() {
            @Override
            public void run() {
                out.printf(send + "\n");
            }
        }).start();
    }

    public void close()
    {
        try {
            if(socket!=null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
        }
    }
}
