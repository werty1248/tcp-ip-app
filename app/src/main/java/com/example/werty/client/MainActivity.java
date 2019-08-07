package com.example.werty.client;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    EditText input;         //화면구성
    Button button;          //화면구성
    TextView output;        //화면구성
    SocketManager SM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText) findViewById(R.id.input); // 글자입력칸을 찾는다.
        button = (Button) findViewById(R.id.button); // 버튼을 찾는다.
        output = (TextView) findViewById(R.id.output); // 글자출력칸을 찾는다.
        SM = new SocketManager();


        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(input.getText().length()!=0)
                    SM.setData(input.getText().toString());
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
//소켓에서 데이터를 읽어서 화면에 표시한다.
        try {
            new Thread(new Runnable() {
                public void run() {
                    SM.setSocket("61.78.190.44", 4000);

                    while (SM.Alive()) {
                        if(SM.getData()!=null) {
                            output.post(new Runnable() {
                                public void run() {
                                    output.setText(SM.getData()); //글자출력칸에 서버가 보낸 메시지를 받는다.
                                }
                            });
                        }
                        try {
                            Thread.sleep(100);
                        }catch(Exception e){
                        }
                    }
                }
            }).start();
        }catch (Exception e) {
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        SM.close();
    }

    @Override
    protected void onStop() {  //앱 종료시
        super.onStop();
        SM.close();
    }
}