package com.example.app_making;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudyActivity extends Activity {

    private BroadcastReceiver mReceiver; // 브로드캐스트리시버 선언
    private Button mStartBtn, mStopBtn, mRecordBtn, mPauseBtn, btnWrite;
    private TextView mTimeTextView, mRecordTextView, edtDiary;
    private static Thread timeThread = null;
    private Boolean isRunning = true;
    private DatePicker dp;
    String filename;
    private int INDEX = 0; //날짜가 바뀌어서 시계가 멈춘건지, 아니면 인위적으로 멈춘건지 구분하기 위한 INDEX
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        mReceiver = new TimeReset();

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#4ea1d3"));
        }

        mStartBtn = (Button) findViewById(R.id.btn_start);
        mStopBtn = (Button) findViewById(R.id.btn_stop);
        mRecordBtn = (Button) findViewById(R.id.btn_record);
        mPauseBtn = (Button) findViewById(R.id.btn_pause);
        mTimeTextView = (TextView) findViewById(R.id.timeView);

        //Toast.makeText(getApplicationContext(), "HI" + readTimeINIT(today()), Toast.LENGTH_LONG).show();
        mTimeTextView.setText(readTimeINIT(today()));


        mRecordTextView = (TextView) findViewById(R.id.recordView);
        btnWrite = findViewById(R.id.btnRead);
        edtDiary = findViewById(R.id.edtDiary);
        dp = findViewById(R.id.datePicker);

        initTime();

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                mStopBtn.setVisibility(View.VISIBLE);
                mRecordBtn.setVisibility(View.VISIBLE);
                mPauseBtn.setVisibility(View.VISIBLE);

                timeThread = new Thread(new timeThread());
                timeThread.start();
            }
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                mRecordBtn.setVisibility(View.GONE);
                mStartBtn.setVisibility(View.VISIBLE);
                mPauseBtn.setVisibility(View.GONE);
                mRecordTextView.setText("");
                String str = mRecordTextView.getText() + mTimeTextView.getText().toString() + "\n"; //시간량 저장
                saveTime(str);
                INDEX = 1;
                timeThread.interrupt();




            }
        });

        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordTextView.setText(mRecordTextView.getText() + mTimeTextView.getText().toString() + "\n");
            }
        });

        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = !isRunning;
                if (isRunning) {
                    mPauseBtn.setText("일시정지");
                } else {
                    mPauseBtn.setText("시작");
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int hour = (msg.arg1 ) / 3600;
            int sec = (msg.arg1 ) % 60;
            int min = (msg.arg1 ) / 60 - hour * 60;

            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d", hour, min, sec);
            if (result.equals("01:00:00")) {
                Toast.makeText(getApplicationContext(), "1시간이 지났습니다.", Toast.LENGTH_SHORT).show();
            }
            mTimeTextView.setText(result);
        }
    };

    public static void Time_interrupt(){
        timeThread.interrupt();
    }

    public class timeThread implements Runnable {
        @Override
        public void run() {
            filename = today();
            String gettime =readTime(filename);
            int i =0;
            if (gettime != null) {
                String[] HMS = gettime.split(":");
                int time = (Integer.parseInt(HMS[0]))* 3600  + Integer.parseInt(HMS[1]) * 60  + Integer.parseInt(HMS[2]);
                i += time;
            }



            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                if(INDEX == 0) {
                                    String str = mRecordTextView.getText() + mTimeTextView.getText().toString() + "\n"; //시간량 저장
                                    saveTime_yesterday(str);
                                    String K = readTime_yesterday();
                                    int K1= K.charAt(0) - '0';
                                    int K2 = K.charAt(1) - '0';
                                    int coin = K1*10 + K2;
                                    int get_coin = Integer.parseInt(readCoin()) + coin;
                                    saveCoin(String.valueOf(get_coin));
                                    Log.d("INDEX = 0", "INDEX ^\n^\n\n" + "coin의 값은 바로바로 " + String.valueOf(get_coin) + "\n\n");

                                    mStopBtn.setVisibility(View.GONE);
                                    mRecordBtn.setVisibility(View.GONE);
                                    mStartBtn.setVisibility(View.VISIBLE);
                                    mPauseBtn.setVisibility(View.GONE);
                                    mRecordTextView.setText("");
                                    mTimeTextView.setText("");
                                    mTimeTextView.setText("00:00:00");
                                } //코인량도 증가시켜야 한다!
                                else {
                                    mTimeTextView.setText(readTime(today()));
                                }




                                INDEX = 0;
                            }
                        });
                        return; // 인터럽트 받을 경우 return
                    }
                }
            }
        }
    }


    public void saveTime(String str){ // str은 날짜와 시간 정보.
        try {

            filename = today();
            Log.d("=====", "\n\n\n========="+filename);
            FileOutputStream outfs = openFileOutput(filename, Context.MODE_PRIVATE);
            outfs.write(str.getBytes());
            outfs.close();
            Toast.makeText(getApplicationContext(),filename+"이 저장됨",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCoin(String str){ // str은 코인정보.
        try {

            filename = "coin.txt";
            Log.d("=====", "\n\n\n========="+filename);
            FileOutputStream outfs = openFileOutput(filename, Context.MODE_PRIVATE);
            outfs.write(str.getBytes());
            outfs.close();
            Toast.makeText(getApplicationContext(),filename+"이 저장됨",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String readCoin() {
        String filename = "coin.txt";
        String coin=null;
        FileInputStream infs;
        try {
            infs= openFileInput(filename);
            byte txt[]=new byte[500];
            infs.read(txt);
            infs.close();
            coin=(new String(txt)).trim();

        } catch (FileNotFoundException e) {
            Log.d("코인", "코인 없음!");
            saveCoin("0");
            return "0";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coin;
    }

    public void saveTime_yesterday(String str){ // str은 날짜와 시간 정보.
        try {

            filename = yesterday();
            Log.d("=====", "\n\n\n========="+filename);
            FileOutputStream outfs = openFileOutput(filename, Context.MODE_PRIVATE);
            outfs.write(str.getBytes());
            outfs.close();
            Toast.makeText(getApplicationContext(),filename+"이 저장됨",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    String readTime_yesterday() {
        String filename = yesterday();
        String diaryStr=null;
        FileInputStream infs;
        try {
            infs= openFileInput(filename);
            byte txt[]=new byte[500];
            infs.read(txt);
            infs.close();
            diaryStr=(new String(txt)).trim();

        } catch (FileNotFoundException e) {
            return "00:00:00";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return diaryStr;
    }

    public String today(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.KOREA);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);

        String year = yearFormat.format(currentTime);
        String month = monthFormat.format(currentTime);
        String day_ = dateFormat.format(currentTime);
        String day = "";
        if(Integer.parseInt(day_) < 10){
            day = String.valueOf(day_.charAt(1));
        }
        else {
            day = day_;
        }
        Log.d("TAG", "today: " +  year + "_" + month + "_" + day + ".txt" + "***********");
        return year + "_" + month + "_" + day + ".txt";
    }

    public String yesterday(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.KOREA);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);

        String year = yearFormat.format(currentTime);
        String month = monthFormat.format(currentTime);
        String day_ = dateFormat.format(currentTime);
        String day = "";
        if(Integer.parseInt(day_) < 10){
            day = String.valueOf(day_.charAt(1));
        }
        int k = Integer.parseInt(day);
        k--;
        Log.d("TAG", "Yesterday: " +  year + "_" + month + "_" + k + ".txt" + "***********");
        return year + "_" + month + "_" + k + ".txt";
    }

    public void initTime(){
        Calendar cal=Calendar.getInstance();
        int cYear=cal.get(Calendar.YEAR);
        int cMonth=cal.get(Calendar.MONTH);
        int cday=cal.get(Calendar.DAY_OF_MONTH);

        String file = Integer.toString(cYear)+"_"+Integer.toString(cMonth+1)+"_"+Integer.toString(cday)+".txt";
        String read_str = readTime(file);
        edtDiary.setText(read_str);
        btnWrite.setEnabled(true);


        dp.init(cYear,cMonth,cday, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                filename=Integer.toString(i)+"_"+ Integer.toString(i1+1)+"_"+Integer.toString(i2)+".txt";
                String str=readTime(filename);
                edtDiary.setText(str);
                btnWrite.setEnabled(true);
            }
        });
    }



    String readTime(String filename) {
        String diaryStr=null;
        FileInputStream infs;
        try {
            infs= openFileInput(filename);
            byte txt[]=new byte[500];
            infs.read(txt);
            infs.close();
            diaryStr=(new String(txt)).trim();
            btnWrite.setText("시간 확인");
        } catch (FileNotFoundException e) {
            edtDiary.setHint("공부량 없음");
            btnWrite.setText("시간 확인");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return diaryStr;
    }

    String readTimeINIT(String filename) {
        String diaryStr=null;
        FileInputStream infs;
        try {
            infs= openFileInput(filename);
            byte txt[]=new byte[500];
            infs.read(txt);
            infs.close();
            diaryStr=(new String(txt)).trim();
            return diaryStr;
        } catch (FileNotFoundException e) {
            return "00:00:00";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return diaryStr;
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    @Override
    protected void onUserLeaveHint() { //홈 키 혹은 작업탭 키
        super.onUserLeaveHint();
        String str = mTimeTextView.getText().toString() + "\n"; //시간량 저장
        saveTime(str);

        restart(this);
    }

    private void restart(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
        Log.d("RSTART", "============\n\nonKeyDown: HOME \n\n=================");
    }
}

