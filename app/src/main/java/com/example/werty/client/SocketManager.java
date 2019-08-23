package com.example.werty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;


public class SocketManager{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String data,send;
    private Listener L;
    private String code;

    private SocketAddress SocketAdr;
    private String HostIp;
    private int Port;

    SocketManager(Listener listen)
    {
        L = listen;
    }
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
                    L.Listener("Network Message");
                }
            } catch (Exception e) {
            }
        }
    }
    public int setSocket(String host, int port) {

        try {
        SocketAdr = new InetSocketAddress(host,port);

        Thread t = new Thread(new Runnable(){
            public void run() {
                try {
                    if (!Alive())
                    {
                        socket = new Socket();
                        socket.connect(SocketAdr,3000);
                        out = new PrintWriter(socket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        if(code != null)
                            setData("!remember" + code);
                        new Thread(new InputHandler()).start();
                    }
                } catch(SocketTimeoutException e){
                    close();
                    L.Listener("Server Not Found");
                }
                catch (IOException e) {
                }
            }
        });
            t.start();
            t.join(); //socket이 thread 내에서 생성될때까지 기다렸다가 리턴
        }catch(Exception e){
            L.Listener("Wrong Address");
        }
        if(Alive())
            L.Listener("Connect");
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
            if(Alive())
                socket.close();
        } catch (IOException e) {
        }
    }
}
