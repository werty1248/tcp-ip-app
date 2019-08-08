package com.example.werty.client;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

interface Listener
{
    void Listener(String str);
}

public class MainActivity extends Activity implements Listener{

    EditText input;
    Button button;
    TextView output;
    String chatText;
    String address;
    SocketManager SM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText) findViewById(R.id.input);
        button = (Button) findViewById(R.id.button);
        output = (TextView) findViewById(R.id.output);
        SM = new SocketManager(this);


        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String[] str;
                if(input.getText().length()!=0) {
                    str = input.getText().toString().split(" ");
                    if(str[0].equals("!connect") && str.length==2)
                    {
                        SM.close();
                        SM.setSocket(str[1], 4000);
                        if(SM.Alive())
                            address = str[1];
                    }
                    else
                        SM.setData(input.getText().toString());
                }
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
                    output.post(new Runnable() {
                        public void run() {
                            output.setText(chatText);
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
    protected void onResume()
    {
        super.onResume();
        if(!SM.Alive() && address != null)
            SM.setSocket(address, 4000);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        SM.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SM.close();
    }
}