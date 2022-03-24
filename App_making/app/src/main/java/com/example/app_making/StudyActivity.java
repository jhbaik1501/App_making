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

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StudyActivity extends Activity {

    private BroadcastReceiver mReceiver; // 브로드캐스트리시버 선언
    private Button mStartBtn, mStopBtn, mRecordBtn, mPauseBtn, btnWrite;
    private TextView mTimeTextView, mRecordTextView, edtDiary;
    private static Thread timeThread = null;
    private Boolean isRunning = true;
    private DatePicker dp;
    String filename;

    Long id;

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
        String filename = "id.txt";
        Long temp = (long) 0;
//        final Long id;
        FileInputStream infs;
        try {
            infs= openFileInput(filename);
            byte txt[]=new byte[500];
            infs.read(txt);
            infs.close();
            String rep = (new String(txt)).trim() ;
            temp = Long.valueOf(Integer.parseInt( rep ));
            Log.d("* : ", rep);
            GET("http://10.0.2.2:8080/api/UserCheck", Integer.parseInt( rep )  );
        } catch (FileNotFoundException e) {
            Log.d("에러 -> ", "id.txt가 없음");
        } catch (IOException e) {
            Log.d("에러 -> ", "id.txt에 내용이 없음");
            e.printStackTrace();
        }
        id = temp;

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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                mRecordBtn.setVisibility(View.GONE);
                mStartBtn.setVisibility(View.VISIBLE);
                mPauseBtn.setVisibility(View.GONE);
                mRecordTextView.setText("");
                String str = mRecordTextView.getText() + mTimeTextView.getText().toString() + "\n"; //시간량 저장


                String[] HMS = str.split(":");

                int a = Integer.valueOf(HMS[0].replaceAll("[^0-9]", ""));
                int b = Integer.valueOf(HMS[1].replaceAll("[^0-9]", ""));
                int c = Integer.valueOf(HMS[2].replaceAll("[^0-9]", ""));

                Log.d(" 저장 시간 -> ", String.valueOf(a + b + c));
                LocalTime parse = LocalTime.of(a,b,c);
                POST_BODY_JSON("http://10.0.2.2:8080/api/PostTime",getApplicationContext(), id, parse);


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
            String gettime = mTimeTextView.getText().toString();
//            String gettime = readTime(filename);

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
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                if(INDEX == 0) {
                                    String str = mRecordTextView.getText() + mTimeTextView.getText().toString() + "\n"; //시간량 저장
                                    saveTime_yesterday(str);
                                    String K = readTime_yesterday();
                                    int K1= K.charAt(0) - '0';
                                    int K2 = K.charAt(1) - '0';
//                                    int coin = K1*10 + K2;
//                                    int get_coin = Integer.parseInt(readCoin()) + coin;
//                                    saveCoin(String.valueOf(get_coin));
//                                    Log.d("INDEX = 0", "INDEX ^\n^\n\n" + "coin의 값은 바로바로 " + String.valueOf(get_coin) + "\n\n");


//                                    LocalTime parse = LocalTime.parse(str);
//                                    Log.d(" 저장 시간 -> ", str);
//                                    POST_BODY_JSON("http://10.0.2.2:8080/api/PostTime",getApplicationContext(), id, parse);


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


    public void GET(final String url, int id){
        // 만약 id가 존재한다면 그냥 있고 , 그렇지 않다면 id 값을 만들어준다.
        //TODO 데이터 Request 객체 생성
        RequestQueue queue = Volley.newRequestQueue(this);

        //TODO 파라미터값 선언 실시
        Map<String, String> params = new HashMap<>(); //TODO {userId=1} 형태

//        params.put("userId", "1");
        params.put("userId", String.valueOf(id));

        //TODO 전송 Url 및 Data 파싱 실시
        String dataParse = "";
        String getUrl = "";
        dataParse = String.valueOf(params.toString());
        dataParse = dataParse.replaceAll("[{]","");
        dataParse = dataParse.replaceAll("[}]","");
        dataParse = dataParse.replaceAll("[,]","&");
        getUrl = url + "?" + dataParse;
        getUrl = getUrl.replaceAll(" ","");
        Log.d("---","---");
        Log.w("//===========//","================================================");
        Log.d("","\n"+"[A_Main > getRequestVolleyGET() 메소드 : Volley GET 요청 실시]");
        Log.d("","\n"+"["+"요청 주소 - "+String.valueOf(url)+"]");
        Log.d("","\n"+"["+"전송 값 - "+String.valueOf(params)+"]");
        Log.d("","\n"+"["+"전송 형태 - "+String.valueOf(getUrl)+"]");
        Log.w("//===========//","================================================");
        Log.d("---","---");

        //TODO 데이터 Response 객체 생성


        final String requestBody = "";

        StringRequest request = new StringRequest(Request.Method.GET, getUrl,
                //TODO 데이터 전송 요청 성공
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() 메소드 : Volley GET 요청 응답]");
                        Log.d("","\n"+"["+"응답 전체 - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");

                        String str = "";

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.d(" json pro ->", jsonObject.get("time").toString() );
                            if ( !jsonObject.get("time").toString().equals("null") ) {
                                str = (String) jsonObject.get("time");
                            } else{
                                str = "00:00:00";
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mTimeTextView.setText(str);

                    }
                },
                //TODO 데이터 전송 요청 에러 발생
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("---","---");
                        Log.e("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() 메소드 : Volley GET 요청 실패]");
                        Log.d("","\n"+"["+"에러 코드 - "+String.valueOf(error.toString())+"]");
                        Log.e("//===========//","================================================");
                        Log.d("---","---");



                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    if (requestBody != null && requestBody.length() > 0 && !requestBody.equals("")) {
                        return requestBody.getBytes("utf-8");
                    } else {
                        return null;
                    }
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };

        Log.d(" RequestBody ", requestBody);
        request.setShouldCache(false);
        queue.add(request);



    }

    public void POST_BODY_JSON(final String url, Context context, Long id, LocalTime localTime){
        //TODO 데이터 Request 객체 생성
        RequestQueue queue = Volley.newRequestQueue(context);

        //TODO 파라미터값 선언 실시
        JSONObject jsonBodyObj = new JSONObject();
        try{
            //" { \"age\" : 24, \"coin\" : 3, \"name\" : \"jisoo\"} ");
            jsonBodyObj.put("id", id);
            jsonBodyObj.put("localTime", localTime);
        }catch (JSONException e){
            e.printStackTrace();
        }
        final String requestBody = String.valueOf(jsonBodyObj.toString());

        Log.d("---","---");
        Log.w("//===========//","================================================");
        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() 메소드 : Volley POST_BODY_JSON 요청 실시]");
        Log.d("","\n"+"["+"요청 주소 - "+String.valueOf(url)+"]");
        Log.d("","\n"+"["+"전송 값 - "+String.valueOf(jsonBodyObj.toString())+"]");
        Log.d("","\n"+"["+"전송 타입 - "+String.valueOf("application/json")+"]");
        Log.w("//===========//","================================================");
        Log.d("---","---");

        //TODO 데이터 Response 객체 생성
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                //TODO 데이터 전송 요청 성공
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {

                        LocalTime str = null;
                        int possible = 1;
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() 메소드 : Volley POST_BODY_JSON 요청 응답]");
                        Log.d("","\n"+"["+"응답 전체 - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");


                        try {
                            str = LocalTime.parse((CharSequence) response.get("time"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d(" time : ",str + "---" );
                    }
                },
                //TODO 데이터 전송 요청 에러 발생
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("---","---");
                        Log.e("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() 메소드 : Volley POST_BODY_JSON 요청 실패]");
                        Log.d("","\n"+"["+"에러 코드 - "+String.valueOf(error.toString())+"]");
                        Log.e("//===========//","================================================");
                        Log.d("---","---");
                    }
                })
                //TODO 헤더값 선언 실시 및 Body 데이터 바이트 변환 실시
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            public byte[] getBody() {
                try {
                    if (requestBody != null && requestBody.length()>0 && !requestBody.equals("")){
                        return requestBody.getBytes("utf-8");
                    }
                    else {
                        return null;
                    }
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };

        request.setShouldCache(false);
        queue.add(request);
    }
}

