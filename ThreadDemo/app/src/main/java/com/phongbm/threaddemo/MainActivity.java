package com.phongbm.threaddemo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int MESSAGE_COUNT_DOWN_TIME = 111;
    private static final int MESSAGE_COUNT_DOWN_TIME_DONE = 222;

    private ProgressBar pgbLoading;
    private TextView tvNumber;
    private Button btnStart;
    private ImageView imgPhoto;

    private boolean isThreadRunning;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponents();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_COUNT_DOWN_TIME:
                        tvNumber.setText(String.valueOf(msg.arg1));
                        break;

                    case MESSAGE_COUNT_DOWN_TIME_DONE:
                        Toast.makeText(getBaseContext(),
                                "Done!", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }
        };
    }

    private void initializeComponents() {
        pgbLoading = (ProgressBar) findViewById(R.id.pgb_loading);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        btnStart = (Button) findViewById(R.id.btn_start);
        imgPhoto = (ImageView) findViewById(R.id.img_photo);

        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                Downloader downloader = new Downloader();
                downloader.execute("https://androidkennel.org/wp-content/uploads/2015/11/AsyncTask-%E2%80%93-1.png");

                // loadData();

                /*if (isThreadRunning) {
                    break;
                }
                countDownTime();*/
                break;

            default:
                break;
        }
    }

    public class Downloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getBaseContext(),
                    "Waiting...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                String link = params[0];
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                connection.disconnect();
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imgPhoto.setImageBitmap(bitmap);
        }
    }

    private void loadData() {
        LoadingAsync async = new LoadingAsync();
        async.execute();
    }

    private class LoadingAsync extends AsyncTask<Void, Integer, Void> {
        private int progress;

        @Override
        protected void onPreExecute() {
            progress = 0;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (progress < 100) {
                progress++;
                publishProgress(progress);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pgbLoading.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getBaseContext(),
                    "Done!", Toast.LENGTH_SHORT).show();
        }
    }

    private void countDownTime() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isThreadRunning = true;
                int time = 10;
                while (time > 0) {
                    time--;

                    // Tạo ra tin nhắn
                    Message msg = new Message();
                    msg.what = MESSAGE_COUNT_DOWN_TIME;
                    msg.arg1 = time;

                    // / Gửi tin nhắn đến hanlder - Cách 1
                    handler.sendMessage(msg);

                    // Gửi tin nhắn đến hanlder - Cách 2
                    // msg.setTarget(handler);
                    // msg.sendToTarget();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Gửi tin nhắn không có dữ liệu - Cách 1
                // Message msg = new Message();
                // msg.what = MESSAGE_COUNT_DOWN_TIME_DONE;
                // handler.sendMessage(msg);

                // Gửi tin nhắn không có dữ liệu - Cách 2
                handler.sendEmptyMessage(MESSAGE_COUNT_DOWN_TIME_DONE);

                isThreadRunning = false;
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

}