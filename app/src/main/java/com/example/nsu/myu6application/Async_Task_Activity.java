package com.example.nsu.myu6application;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Async_Task_Activity extends AppCompatActivity {

    private Button download, cancel,pause;
    private ProgressBar pb;
    private TextView tv;
    private DownLoadTask dTask;
    private static final String TAG = "Async_Task_Activity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.async_task_layout);

        pb = (ProgressBar) findViewById(R.id.pb);
        tv = (TextView) findViewById(R.id.tv);


        download = (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dTask = new DownLoadTask();

                    dTask.execute(1000,5);


            }
        });
        cancel = (Button)findViewById(R.id.cancle);
        pause = (Button)findViewById(R.id.pause);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dTask.cancel(true);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dTask.isPause){
                        dTask.isPause = false;
                        pause.setText("暂停");
                }else {
                    dTask.isPause = true;
                    pause.setText("继续");
                }

            }
        });


    }
    /**
     * 尖括号内分别是接收参数（例子里是线程休息时间，给doInBackground用）
     * 进度(通过publishProgress发送到onProgressUpdate中使用)
     * 返回值（由doInBackground返回给onPostExecute使用）
     */
class DownLoadTask extends AsyncTask<Integer,Integer,String>{

    boolean isPause = false;
    int currentProgress = 0;
    /**
     * 第一个执行方法
     * UI的准备工作,改变Button的可用性
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pause.setEnabled(true);
        download.setEnabled(false);
        cancel.setEnabled(true);
        //隐藏ProgressBar
        pb.setVisibility(View.VISIBLE);
        Log.d(TAG, "onPreExecute: ");
    }
    @Override
    protected String doInBackground(Integer... params) {
        Log.d(TAG, "doInBackground: ");

        while (currentProgress<100){
            Log.d(TAG, "暂停中");
            if(isCancelled()){
                break;
            }

            try {
                if(!isPause){
                    Thread.sleep(params[0]);
                    currentProgress = currentProgress+params[1];
                    publishProgress(currentProgress);
                    //currentProgress++;
                }else {
                    isPause = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                //中断则将进度置0 下载按钮打开 暂停按钮关闭
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText("当前进度显示");
                        pause.setText("暂停");
                        download.setEnabled(true);
                        pause.setEnabled(false);
                        currentProgress = 0;
                        pb.setProgress(currentProgress);
                    }
                });
            }
        }
            return "更新完成";

    }
        /**
         *  这个函数在doInBackground调用publishProgress时触发，虽然调用时只有一个参数
         *  但是这里取到的是一个数组,所以要用progesss[0]来取值
         *  第n个参数就用progress[n]来取值
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(isCancelled()){
                return;
            }
            Log.d(TAG, "onProgressUpdate: ");
            //更新TextView
            tv.setText("当前进度是"+values[0]);
            //更新ProgressBar
            pb.setProgress(values[0]);
        }

        /**
         * doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
         * 这里的result就是上面doInBackground执行后的返回值，所以这里是"更新完成"
         */
        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "onPostExecute: ");
            //更新按钮的可用性
            download.setEnabled(true);
            cancel.setEnabled(false);
            //更新Textview
            tv.setText(s);
            //隐藏ProgressBar
            pb.setVisibility(View.GONE);
        }
        @Override
        protected void  onCancelled (String result) {
            Log.e(TAG, "取消了, i：" + result);
            tv.setText("当前进度显示");
            pause.setText("暂停");
            download.setEnabled(true);
            pause.setEnabled(false);
            currentProgress = 0;
            pb.setProgress(currentProgress);
        }

    }





        /*
         * 在后台线程里执行任务，通过publishProgress发布更新到UI线程
         * @see android.os.AsyncTask#doInBackground(Params[])
         */





        /**
         * AsyncTask.cancel()并不意味着AsyncTask对象立即停止
         * 会在doInBackground()返回后触发onCanceled，而不是onPostExecute
         * 可以在doInBackground中定期调用AsyncTask.isCancelled()来检查，以便及早返回
         */

}
