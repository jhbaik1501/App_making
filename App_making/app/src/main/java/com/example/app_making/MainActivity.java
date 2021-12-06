package com.example.app_making;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {


    //CallCheckReceiver callCheckReceiver = new CallCheckReceiver();

    private BroadcastReceiver mReceiver; // 브로드캐스트리시버 선언

    TextView coin_textView;
    TextClock textClock;
    TextView TimetextView;
    Button call_button;
    Button SMS_button;
    Button Game_button;
    Button Study_button;
    String contents = ""; // CallCheck를 위한 contents 변수




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mReceiver = new TimeReset();


        SetVariance();

        requirePerms();

        textClock.setTextSize(30);

    }

    private void processIntent(Intent intent) {
        if(intent != null){
            contents = intent.getStringExtra("contents");

            print("받은 컨텐트의 값은 바로바로 contents = "+ contents);
        }
    }


    public void print(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void SetVariance() {
        coin_textView = findViewById(R.id.coin_textView);
        textClock = findViewById(R.id.textClock);
        call_button = findViewById(R.id.call_button);
        SMS_button = findViewById(R.id.SMS_button);
        Game_button = findViewById(R.id.Game_button);
        Study_button = findViewById(R.id.Study_button);

        coin_textView.setText("코인 : " + readCoin() + "\n" + Make_wise());
        call_button.setOnClickListener(this::OnClick);
        SMS_button.setOnClickListener(this::OnClick);
        Study_button.setOnClickListener(this::OnClick);
    }

    public void OnClick(View v){
        switch (v.getId()){



            case R.id.call_button:
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(), "Pause_INDEX", "Act_CHANGE");
                Intent intent_Call = new Intent(getApplicationContext(), CallActivity.class);
                startActivity(intent_Call);

                //CallSender callSender = new CallSender();
                //recvCall();
                break;
            case R.id.Game_button:
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(), "Pause_INDEX", "Act_CHANGE");
                Log.d("test", "OnClick: tset");
                Intent passedIntent1 = getIntent();
                processIntent(passedIntent1);

                break;
            case R.id.SMS_button:
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(), "Pause_INDEX", "Act_CHANGE");
                Intent intent_SMS = new Intent(getApplicationContext(), SMSActivity.class);
                print("SMS엑티비티");
                startActivity(intent_SMS);
                break;
            case R.id.Study_button:
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(), "Pause_INDEX", "Act_CHANGE");
                Intent intent_Study = new Intent(getApplicationContext(), StudyActivity.class);
                print("Study엑티비티");
                startActivity(intent_Study);
                break;
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                return false;
            case KeyEvent.KEYCODE_HOME:
                print("H");
                return false;
            case KeyEvent.KEYCODE_MENU:
                print("M");
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //print("HI");
    }



    @Override
    protected void onResume() {
        super.onResume();
        coin_textView.setText("코인 : " + readCoin() + "\n" + Make_wise());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);

        String Pause_INDEX = "";
//        Intent i = getIntent();
//        String title = i.getStringExtra("call");
//        print("Title " + title);
        try {
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
            print("오류");
        }

        Pause_INDEX = SharedPreferenceUtil.getSharedPreference(this, "Pause_INDEX");
        print("String A = " + Pause_INDEX);
        Log.d("A============", Pause_INDEX + "\n\n\n\n");

        if (Pause_INDEX != null) {
            if (Pause_INDEX.equals("OFFHOOK")) {
                print("통화중");
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(), "Pause_INDEX", "");
            }
            else if(Pause_INDEX.equals("Act_CHANGE")){
                print("엑티비티 변화");
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(), "Pause_INDEX", "");
            }
            else {
                print("pause");
            }
        }
    }



    private void requirePerms(){ // 메시지 수신 권한
        String[] permissions = {Manifest.permission.RECEIVE_SMS};
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            print("권한 2");
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    public void saveCoin(String str){ // str은 코인정보.
        try {
            String filename;
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

    public String Make_wise(){
        String [] wise_sentance = {"눈이 감기는가? 그럼 미래를 향한 눈도 감긴다.",
                "니가 자는 순간 니 앞등수의 책장이 넘어간다.",
                "지금 흘린 침은 내일 흘릴 눈물이 된다.",
                "늦게 시작하는 것을 두려워 말고, 하다 중단하는 것을 두려워해라.",
                "고통을 주지 않는 것은 쾌락도 주지 않는다",
                "가장 유능한 사람은 가장 배움에 힘쓰는 사람이다.",
                "실패란 넘어지는 것이 아니라 넘어진 자리에 머무는 것이다.",
                "성공의 비결은 좌절하지 않고 극복하는데 있다.",
                "고난이란 최선을 다 할 기회다.",
                "기초 없이 이룬 성취는 단계를 오르는 게 아니라 성취 후 다시 바닥으로 오게 된다.",
                "나만이 내 인생을 바꿀 수 있다. 아무도 날 대신해 줄 수 없다."};
        Random rand = new Random();
        int k = rand.nextInt(wise_sentance.length);

        return wise_sentance[k];
    }

}

