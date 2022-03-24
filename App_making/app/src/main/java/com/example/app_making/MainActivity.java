package com.example.app_making;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_making.Time.TimeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    //CallCheckReceiver callCheckReceiver = new CallCheckReceiver();

    private BroadcastReceiver mReceiver; // 브로드캐스트리시버 선언


    TextView coin_textView;
    TextClock textClock;
    TextView TimetextView;
    Button call_button, BackgroundButton;
    Button SMS_button;
    Button Game_button;
    Button Study_button;
    Button Exit_button;
    String contents = ""; // CallCheck를 위한 contents 변수
    int BACKPRESS = 0; // 이거는 홈버튼 ONOFF
    int ButtonOnClick = 0;

    LinearLayout main_linearLayout;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mReceiver = new TimeReset();


        /*Intent intent = new Intent(this, MainActivity.class);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startService(intent);
*/
        SetVariance();

        requirePerms();
        requirePerms2();
        requirePerms3();
        requirePerms4();

        String filename = "id.txt";
        FileInputStream infs;
        try {
            infs= openFileInput(filename);
            byte txt[]=new byte[500];
            infs.read(txt);
            infs.close();
            String rep = (new String(txt)).trim() ;
            Log.d("* : ", rep);
            GET("http://10.0.2.2:8080/api/UserCheck", Integer.parseInt( rep )  );
        } catch (FileNotFoundException e) {
            Log.d("","\n"+"[처음 들어왔으므로 ,id 처음으로 생성.]");
            EditText et1 = new EditText(getApplicationContext());
            et1.setHint("이름");

            AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(MainActivity.this);
            myAlertBuilder.setTitle("본인의 이름을 적어주세요!")
                    .setView(et1)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            POST_BODY_JSON("http://10.0.2.2:8080/api/createAccount", getApplicationContext(), et1.getText().toString() );
                        }
                    });
            AlertDialog alert = myAlertBuilder.create();
            alert.show();


        } catch (IOException e) {
            e.printStackTrace();
        }

        textClock.setTextSize(30);

    }

    private void processIntent(Intent intent) {
        if(intent != null){
            contents = intent.getStringExtra("contents");

            //print("받은 컨텐트의 값은 바로바로 contents = "+ contents);
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
        Exit_button = findViewById(R.id.ExitButton);
        BackgroundButton = findViewById(R.id.Background_button);
        main_linearLayout = findViewById(R.id.Main_LinearLayout);

        BackgroundSet();







        coin_textView.setText(Make_wise());
        Game_button.setOnClickListener(this::OnClick);
        BackgroundButton.setOnClickListener(this::OnClick);
        call_button.setOnClickListener(this::OnClick);
        SMS_button.setOnClickListener(this::OnClick);
        Study_button.setOnClickListener(this::OnClick);
        Exit_button.setOnClickListener(this::OnClick);
    }

    public void OnClick(View v){
        ButtonOnClick = 1;
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
                Intent passedIntent1 = new Intent(getApplicationContext(), TimeActivity.class);
                startActivity(passedIntent1);

                break;
            case R.id.SMS_button:
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(), "Pause_INDEX", "Act_CHANGE");
                Intent intent_SMS = new Intent(getApplicationContext(), SMSActivity.class);
                //print("SMS엑티비티");
                startActivity(intent_SMS);
                break;
            case R.id.Study_button:
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(), "Pause_INDEX", "Act_CHANGE");
                Intent intent_Study = new Intent(getApplicationContext(), StudyActivity.class);
                //print("Study엑티비티");
                startActivity(intent_Study);
                break;
            case R.id.Background_button:
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(), "Pause_INDEX", "Act_CHANGE");
                Intent intent_Background = new Intent(getApplicationContext(), BackgroundActivity.class);
                //print("Background 선택");
                startActivity(intent_Background);
                break;
            case R.id.ExitButton:
                BACKPRESS = 1;
                AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(MainActivity.this);
                myAlertBuilder.setTitle("나가기");
                myAlertBuilder.setMessage("2G폰 사용을 그만하시겠습니까?");
                // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
                myAlertBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                        // OK 버튼을 눌렸을 경우
                        exitProgram();
                    }
                });
                myAlertBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancle 버튼을 눌렸을 경우
                        Toast.makeText(getApplicationContext(),"Pressed Cancle",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                myAlertBuilder.show();



                break;
        }

    }

    private void exitProgram() { //시스템 종료하는 코드
        // 종료
        // 태스크를 백그라운드로 이동
        // moveTaskToBack(true);
        if (Build.VERSION.SDK_INT >= 21) {
            // 액티비티 종료 + 태스크 리스트에서 지우기
            finishAndRemoveTask();
        } else {
            // 액티비티 종료
            finish();
        }
        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                return false;
            case KeyEvent.KEYCODE_HOME:
                //Log.d("HOME", "============\n\nonKeyDown: HOME \n\n=================");
                //restart(this);
                return false;
            case KeyEvent.KEYCODE_MENU:
                //print("M");
                log("MENU");
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void log(String msg){
        Log.d("TAG", "============\n\n" + msg + "\n\n=================");
    }

    @Override
    protected void onUserLeaveHint() { //홈 키 혹은 작업탭 키
        super.onUserLeaveHint();
        log("HOME");
        if(ButtonOnClick == 0) {
            log("restart");
            restart(this);
        }
        else{
            ButtonOnClick = 0;
            log("Activity");
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        coin_textView.setText(Make_wise());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        BackgroundSet();

        registerReceiver(mReceiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();

        //unregisterReceiver(mReceiver);

        String Pause_INDEX = "";
        //print("pause");
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
        //print("String A = " + Pause_INDEX);
        Log.d("A============", Pause_INDEX + "\n\n\n\n");

        if (Pause_INDEX != null) {
            if (Pause_INDEX.equals("OFFHOOK")) {
                //print("통화중");
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(), "Pause_INDEX", "");
            }
            else if(Pause_INDEX.equals("Act_CHANGE")){
                // print("엑티비티 변화");
                SharedPreferenceUtil.putSharedPreference(getApplicationContext(), "Pause_INDEX", "");
            }
            else {
                //print("pause");
                /*try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    print("오류");
                }
                restart(this);*/
            }
        }
    }

    @Override
    protected void onDestroy() {
        log("DESTROY");
        //restart(this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        //print("Stop");
        super.onStop();
        //restart(this);
    }

    private void requirePerms(){ // 메시지 수신 권한
        String[] permissions = {Manifest.permission.RECEIVE_SMS};
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            print("권한 2");
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private void requirePerms2(){ // 프로세서 죽이는 권한
        String[] permissions = {Manifest.permission.SEND_SMS};
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            print("권한 3");
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private void requirePerms3(){ // 메시지 수신 권한
        String[] permissions = {Manifest.permission.READ_PHONE_STATE};
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            print("권한 4");
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private void requirePerms4(){ // 메시지 수신 권한
        String[] permissions = {Manifest.permission.CALL_PHONE};
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            print("권한 5");
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
                "나만이 내 인생을 바꿀 수 있다. 아무도 날 대신해 줄 수 없다.",
                "반복은 천재를 낳고, 믿음은 기적을 낳는다",
                "당신은 지체할 수 있지만, 시간은 지체하지 않는다",
                "네가 지금 편한이유는 내리막길을 걷고 있기 때문이다."};
        Random rand = new Random();
        int k = rand.nextInt(wise_sentance.length);

        return wise_sentance[k];
    }

    private void restart(Context context) {
//        PackageManager packageManager = context.getPackageManager();
//        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
//        ComponentName componentName = intent.getComponent();
//        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
//        context.startActivity(mainIntent);
//        Runtime.getRuntime().exit(0);
//        Log.d("RSTART", "============\n\nonKeyDown: HOME \n\n=================");
    }



    String readPicture() {  //설정된 배경화면 읽어오기
        String filename = "picture.txt";
        String coin=null;
        FileInputStream infs;
        try {
            infs= getApplicationContext().openFileInput(filename);
            byte txt[]=new byte[500];
            infs.read(txt);
            infs.close();
            coin=(new String(txt)).trim();

        } catch (FileNotFoundException e) {
            Log.d("그림", "그림 없음!");
            return "picture 0";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coin;
    }


    public void BackgroundSet(){
        //배경화면 설정
        String return_picture = readPicture();
        if (return_picture.equals("picture 0")){
            main_linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.picture0));
        }
        if (return_picture.equals("picture 1")){
            main_linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.picture1));
        }
        if (return_picture.equals("picture 2")){
            main_linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.picture2));
        }
        if (return_picture.equals("picture 3")){
            main_linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.picture3));
        }
        if (return_picture.equals("picture 4")){
            main_linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.picture4));
        }
        if (return_picture.equals("picture 5")){
            main_linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.picture5));
        }
        if (return_picture.equals("picture 6")){
            main_linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.picture6));
        }
        if (return_picture.equals("picture 7")){
            main_linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.picture7));
        }
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



    public void POST_BODY_JSON(final String url, Context context, String name){
        //TODO 데이터 Request 객체 생성
        RequestQueue queue = Volley.newRequestQueue(context);

        //TODO 파라미터값 선언 실시
        JSONObject jsonBodyObj = new JSONObject();
        try{
            //" { \"age\" : 24, \"coin\" : 3, \"name\" : \"jisoo\"} ");
            jsonBodyObj.put("age", 20);
            jsonBodyObj.put("coin",30);
            jsonBodyObj.put("name",name);

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
                    @Override
                    public void onResponse(JSONObject response) {

                        String str = "";

                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() 메소드 : Volley POST_BODY_JSON 요청 응답]");
                        Log.d("","\n"+"["+"응답 전체 - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");

                        try {
                            str = String.valueOf(response.get("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String filename = "id.txt";

                        Log.d("=====", "\n\n\n========="+filename);
                        FileOutputStream outfs = null;
                        try {
                            outfs = openFileOutput(filename, Context.MODE_PRIVATE);
                            outfs.write(str.getBytes());
                            outfs.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


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

