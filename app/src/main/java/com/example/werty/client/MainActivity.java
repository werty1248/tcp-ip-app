package com.example.werty.client;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

interface Listener
{
    void Listener(String str);
}

public class MainActivity extends Activity implements Listener{

    EditText IPEdit,IDEdit,PWEdit,MSGEdit;
    Button Connect,Disconnect,Logout,NewAcc,Login,Send,Ready;
    //임시 게임 강제시작 버튼
    Button sudoReady;
    //임시 게임화면
    LinearLayout ConRoom;
    RelativeLayout InGame;
    TextView InfoText,MSGText;
    String chatText;
    String address;
    SocketManager SM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        IPEdit = (EditText)findViewById(R.id.IPEdit);
        IDEdit = (EditText)findViewById(R.id.IDEdit);
        PWEdit = (EditText)findViewById(R.id.PWEdit);
        MSGEdit = (EditText)findViewById(R.id.MSGEdit);

        Connect = (Button)findViewById(R.id.Connect);
        Disconnect = (Button)findViewById(R.id.Disconnect);
        Logout = (Button)findViewById(R.id.Logout);
        Login = (Button)findViewById(R.id.Login);
        NewAcc = (Button)findViewById(R.id.NewAcc);
        Send = (Button)findViewById(R.id.Send);
        Ready = (Button)findViewById(R.id.Ready);

        //임시 게임 강제시작 버튼
        sudoReady = (Button)findViewById(R.id.sudoStart);
        //임시 게임화면
        InGame = (RelativeLayout)findViewById(R.id.InGame);
        ConRoom = (LinearLayout)findViewById(R.id.ConnectRoom);

        InfoText = (TextView)findViewById(R.id.InfoText);
        MSGText = (TextView)findViewById(R.id.MSGText);


        SM = new SocketManager(this);

        sudoReady.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                ConRoom.setVisibility(View.GONE);

                InGame.setVisibility(View.VISIBLE);
            }
        });
        Connect.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                if(IPEdit.getText().length()!=0)
                {
                    SM.close();
                    SM.setSocket(IPEdit.getText().toString(),4000);
                    if(SM.Alive()) {
                        address = IPEdit.getText().toString();

                        IPEdit.setVisibility(View.GONE);
                        Connect.setVisibility(View.GONE);
                        Send.setVisibility(View.GONE);

                        InfoText.setText("Server : " + address);
                        InfoText.setVisibility(View.VISIBLE);
                        Disconnect.setVisibility(View.VISIBLE);
                        IDEdit.setVisibility(View.VISIBLE);
                        PWEdit.setVisibility(View.VISIBLE);
                        NewAcc.setVisibility(View.VISIBLE);
                        Login.setVisibility(View.VISIBLE);
                        MSGEdit.setVisibility(View.VISIBLE);
                        Send.setVisibility(View.VISIBLE);
                        IDEdit.requestFocus();
                    }
                }
            }
        });
        Disconnect.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                SM.close();
                IPEdit.setVisibility(View.VISIBLE);
                Connect.setVisibility(View.VISIBLE);
                Send.setVisibility(View.VISIBLE);

                InfoText.setText("");
                InfoText.setVisibility(View.GONE);
                Disconnect.setVisibility(View.GONE);
                IDEdit.setVisibility(View.GONE);
                PWEdit.setVisibility(View.GONE);
                NewAcc.setVisibility(View.GONE);
                Login.setVisibility(View.GONE);
                Logout.setVisibility(View.GONE);
                MSGText.setVisibility(View.GONE);
                MSGEdit.setVisibility(View.GONE);
                Send.setVisibility(View.GONE);
                Ready.setVisibility(View.GONE);
            }
        });
        Logout.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view)
            {
            }
        });
        Login.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                IDEdit.setVisibility(View.GONE);
                PWEdit.setVisibility(View.GONE);
                NewAcc.setVisibility(View.GONE);
                Login.setVisibility(View.GONE);

                InfoText.setText("Server : " + address + "\n" + "werty1248");
                Logout.setVisibility(View.VISIBLE);
                Ready.setVisibility(View.VISIBLE);
            }
        });
        NewAcc.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view)
            {
            }
        });
        Send.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                String[] str;
                if(MSGEdit.getText().length()!=0) {
                    str = MSGEdit.getText().toString().split(" ");
                    if(str[0].equals("!connect") && str.length==2)
                    {
                        SM.close();
                        SM.setSocket(str[1], 4000);
                        if(SM.Alive())
                            address = str[1];
                    }
                    else if(str[0].equals("!ready") && str.length==1)
                    {
                    }
                    else if(str[0].equals("!set") && str.length==3)
                    {
                        if(str[1].equals("name"))
                        {
                            if(str[2].length()>16)
                                Toast.makeText(getApplicationContext(), "ID must be shorter than 16byte!", Toast.LENGTH_LONG);
                            else
                                SM.setData("!name" + str[2]);
                        }
                    }
                    else {
                        if(SM.Alive())
                            SM.setData(MSGEdit.getText().toString());
                        else
                            Toast.makeText(getApplicationContext(), "Can't find server.\nUse \"!connect xxx.xxx.xxx.xxx\"", Toast.LENGTH_LONG);
                    }
                }
                MSGEdit.setText("");
                try {
                    Thread.sleep(200);
                }catch(Exception e){}
            }
        });
        Ready.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                InGame.setVisibility(View.VISIBLE);
            }
        });

    }

    public synchronized void Listener(String str)
    {
        switch(str) {
            case "Network Message":
                if (SM.getData() != null) {
                    if(chatText != null)
                        chatText = chatText + SM.getData() + "\n";
                    else
                        chatText = SM.getData() + "\n";
                    String[] a = chatText.split("\n");
                    if(a.length>10)
                    {
                        chatText = a[a.length-10] + "\n";
                        for(int i=a.length-9;i<a.length;i++)
                            chatText = chatText + a[i] + "\n";
                    }
                    MSGText.post(new Runnable() {
                        public void run() {
                            MSGText.setText(chatText);
                        }
                    });
                }
                break;
            case "Server Not Found" :
                runOnUiThread(new Runnable() {
                    public void run() {
                        final Toast toast = Toast.makeText(getApplicationContext(), "Server not founded!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                break;
            case "Connect" :
                runOnUiThread(new Runnable() {
                    public void run() {
                        final Toast toast = Toast.makeText(getApplicationContext(), "Server connected!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                break;
            case "Wrong Address" :
                runOnUiThread(new Runnable() {
                    public void run() {
                        final Toast toast = Toast.makeText(getApplicationContext(), "Wrong address!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                break;
            default :
                break;
        }
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        if(!SM.Alive() && address != null)
            SM.setSocket(address, 4000);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SM.close();
    }
}