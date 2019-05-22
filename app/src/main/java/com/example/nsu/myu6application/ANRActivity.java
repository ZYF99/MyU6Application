package com.example.nsu.myu6application;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ANRActivity extends AppCompatActivity {

    int count = 0;
    TextView textView;
    private Handler handler ;
    //代表更新数据
    private static final int MESSAGE_UPDATE = 1001;
    //代表数据更新完成
    private static final int MESSAGE_COMPLETED = 1002;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr);

        textView = (TextView)findViewById(R.id.txt1);
        // TODO 实例化handler，并处理消息
        handler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
               switch (msg.what){
                   case MESSAGE_UPDATE:
                       textView.setText("当前值："+count);
                       break;
                   case MESSAGE_COMPLETED:
                       textView.setText("计时结束");
                       break;
               }
            }
        };
    }
    /**
     * 为Btn添加事件监听
     * @param v
     */
    public void startANR(View v){
        //1、主线程耗时太久
//		while (count <1000) {
//			count++;
//			try {
//				Thread.sleep(1000);
//                textView.setText(count);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
        //2、非UI线程中更新UI
        new Thread(){
            @Override
            public void run() {
                while(count<100){
                    count++;
                    try {
                        Thread.sleep(1000);
//                        textView.setText("当前值："+count);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = Message.obtain();
                    message.arg1 = count;
                    message.what = MESSAGE_UPDATE;
                    handler.sendMessage(message);
                }
                handler.sendEmptyMessage(MESSAGE_COMPLETED);
            }
        }.start();
    }
}
