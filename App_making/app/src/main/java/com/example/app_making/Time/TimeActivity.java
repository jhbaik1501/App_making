package com.example.app_making.Time;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_making.MainActivity;
import com.example.app_making.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TimeActivity extends FragmentActivity {

    Button button;
//    TextView editText;
    RecyclerView recyclerView;
    TimeAdapter adapter;
    Button group1;
    Button group_rank_btn;





    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        button = findViewById(R.id.Save_button);
        recyclerView = findViewById(R.id.RecyclerView);

        group_rank_btn = findViewById(R.id.group_rank);
        group1 = findViewById(R.id.group1);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        adapter = new TimeAdapter();
        recyclerView.setAdapter(adapter);

        String filename = "id.txt";
        Long temp = (long) 0;
        final Long id;
        FileInputStream infs;
        try {
            infs= openFileInput(filename);
            byte txt[]=new byte[500];
            infs.read(txt);
            infs.close();
            String rep = (new String(txt)).trim() ;
            temp = Long.valueOf(Integer.parseInt( rep ));
            Log.d("* : ", rep);
            GET("http://10.0.2.2:8080/api/TimeList");
        } catch (FileNotFoundException e) {
            Log.d("에러 -> ", "id.txt가 없음");
        } catch (IOException e) {
            Log.d("에러 -> ", "id.txt에 내용이 없음");
            e.printStackTrace();
        }
        id = temp;
        Log.d("ID -> ", id + ", "  + temp);


        GET_GROUP("http://10.0.2.2:8080/api/UserGroup", id );

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });


        group1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int groupid = Integer.parseInt(group1.getText().toString().replaceAll("[^0-9]", ""));

                AlertDialog.Builder builder = new AlertDialog.Builder(TimeActivity.this);
                builder.setTitle("그룹에 참여 혹은 만들기");
                builder.setPositiveButton("만들기", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout dialogView;
                        dialogView = (LinearLayout) View.inflate(TimeActivity.this, R.layout.dialog, null);
                        AlertDialog.Builder dlg = new AlertDialog.Builder(TimeActivity.this);
                        dlg.setView(dialogView);
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText1 = dialogView.findViewById(R.id.editText1);
                                EditText editText2 = dialogView.findViewById(R.id.editText2);

                                String name = editText1.getText().toString();
                                String password = editText2.getText().toString();

                                if(name.isEmpty() || password.isEmpty() ) Toast.makeText(getApplicationContext(),"잘못 입력하셨습니다.", Toast.LENGTH_LONG).show();
                                else {
                                    POST_BODY_JSON_CREATE("http://10.0.2.2:8080/api/createGroup", getApplicationContext(), name, password, Integer.valueOf(id.toString()) );
                                }
                            }
                        });
                        dlg.setNegativeButton("취소", null);
                        dlg.show();

                    }
                });
                builder.setNegativeButton("참여", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout dialogView;
                        dialogView = (LinearLayout) View.inflate(TimeActivity.this, R.layout.dialog, null);
                        AlertDialog.Builder dlg = new AlertDialog.Builder(TimeActivity.this);
                        dlg.setView(dialogView);
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText1 = dialogView.findViewById(R.id.editText1);
                                EditText editText2 = dialogView.findViewById(R.id.editText2);

                                String group_id = editText1.getText().toString();
                                String password = editText2.getText().toString();

                                group_id = group_id.replaceAll("[^0-9]", "");
                                if( group_id.isEmpty() || password.isEmpty() ) Toast.makeText(getApplicationContext(),"잘못 입력하셨습니다.", Toast.LENGTH_LONG).show();

                                else {
                                    POST_BODY_JSON_JOIN("http://10.0.2.2:8080/api/joinGroup", getApplicationContext(), Integer.parseInt(group_id), password, Integer.valueOf(id.toString()) );
                                }

                            }
                        });
                        dlg.setNegativeButton("취소", null);
                        dlg.show();

                    }

                });

//                AlertDialog alertDialog = builder.create();
                builder.show();

//                if(groupid != 0 ) {
//                    GET_GROUP_TIME("http://10.0.2.2:8080/api/UserGroupTime", groupid);
//                }
//                else{
//
//                }

            }
        });





        group_rank_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupId=group1.getText().toString().replaceAll("[^0-9]", "");
                if(groupId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "그룹 선택부터 해주세요 !!!", Toast.LENGTH_SHORT).show();
                }
                else{

                    adapter.notifyAll();


                }
            }
        });

    }

    public void AdapterAdd(String name, String time, int id){
        Log.d(" adapter add -> ", name + " " + time + " " + id );
        adapter.addItem(new Time(name, time, id));
    }



    @Override
    protected void onUserLeaveHint() { //홈 키 혹은 작업탭 키
        super.onUserLeaveHint();
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

    public void GET(final String url){
        // 만약 id가 존재한다면 그냥 있고 , 그렇지 않다면 id 값을 만들어준다.
        //TODO 데이터 Request 객체 생성
        RequestQueue queue = Volley.newRequestQueue(this);

        //TODO 파라미터값 선언 실시
        Map<String, String> params = new HashMap<>(); //TODO {userId=1} 형태

//        params.put("userId", "1");
//        params.put("userId", String.valueOf(id));

        //TODO 전송 Url 및 Data 파싱 실시
        String dataParse = "";
        String getUrl = "";
        dataParse = String.valueOf(params.toString());
        dataParse = dataParse.replaceAll("[{]","");
        dataParse = dataParse.replaceAll("[}]","");
        dataParse = dataParse.replaceAll("[,]","&");
        getUrl = url ; // + "?" + dataParse;
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


                        recyclerView.setAdapter(adapter);
//                        editText.setText(response);

                        String[] split = response.split(",");

                        for(int i=0; i<split.length; i+= 2){
                            String s1 = split[i];
                            String s2 = split[i + 1];

                            s1= s1.replace("[", "");
                            s1= s1.replace("\"", "");
                            s1= s1.replace("]", "");

                            s2= s2.replace("[", "");
                            s2= s2.replace("\"", "");
                            s2= s2.replace("]", "");

                            AdapterAdd(s1, s2, i/2 + 1);
                        }



                        recyclerView.setAdapter(adapter);
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





    public void GET_GROUP(final String url, Long id){
        // 만약 id가 존재한다면 그냥 있고 , 그렇지 않다면 id 값을 만들어준다.
        //TODO 데이터 Request 객체 생성
        RequestQueue queue = Volley.newRequestQueue(this);

        //TODO 파라미터값 선언 실시
        Map<String, String> params = new HashMap<>(); //TODO {userId=1} 형태

        params.put("id", String.valueOf(id));
//        params.put("userId", String.valueOf(id));

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



                        String[] split = response.split(",");

                        for(int i=0; i<split.length; i+= 3){
                            String s1 = split[i];
                            String s2 = split[i + 1];
                            String s3 = split[i + 2];

                            s1 = s1.replaceAll("[^0-9]", "");
                            if(i == 0)
                                group1.setText("그룹 : " + s1);
                        }

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


    public void GET_GROUP_TIME(final String url, int id){
        // 만약 id가 존재한다면 그냥 있고 , 그렇지 않다면 id 값을 만들어준다.
        //TODO 데이터 Request 객체 생성
        RequestQueue queue = Volley.newRequestQueue(this);

        //TODO 파라미터값 선언 실시
        Map<String, String> params = new HashMap<>(); //TODO {userId=1} 형태

        params.put("id", String.valueOf(id));
//        params.put("userId", String.valueOf(id));

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



                        String[] split = response.split(",");

                        for(int i=0; i<split.length; i+= 3){
                            String s1 = split[i];
                            String s2 = split[i + 1];
                            String s3 = split[i + 2];

                            s1 = s1.replaceAll("[^0-9]", "");
                            if(i == 0)
                                group1.setText("그룹 : " + s1);

                        }

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

    public void POST_BODY_JSON_JOIN(final String url, Context context, int groupId, String password, int user_id){

        Log.d(" groupId, pw, userid ->" , groupId + ", " + password + ", " + user_id);

        //TODO 데이터 Request 객체 생성
        RequestQueue queue = Volley.newRequestQueue(context);

        //TODO 파라미터값 선언 실시
        JSONObject jsonBodyObj = new JSONObject();
        try{
            //" { \"age\" : 24, \"coin\" : 3, \"name\" : \"jisoo\"} ");
            jsonBodyObj.put("group_id", groupId);
            jsonBodyObj.put("password", password);
            jsonBodyObj.put("user_id", user_id);
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
                        int possible = 1;
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() 메소드 : Volley POST_BODY_JSON 요청 응답]");
                        Log.d("","\n"+"["+"응답 전체 - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");




                        String fail = "";


                        try {
                            fail = String.valueOf(response.get("fail"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(fail.equals("success")){
                            Toast.makeText(getApplicationContext(), "그룹 참여 완료 : " + fail, Toast.LENGTH_LONG).show();
                            group1.setText("그룹 : " + user_id);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "등록 실패. 오류 : " + fail, Toast.LENGTH_LONG).show();
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

    public void POST_BODY_JSON_CREATE(final String url, Context context, String groupname, String password, int user_id){
        //TODO 데이터 Request 객체 생성
        RequestQueue queue = Volley.newRequestQueue(context);

        //TODO 파라미터값 선언 실시
        JSONObject jsonBodyObj = new JSONObject();
        try{
            //" { \"age\" : 24, \"coin\" : 3, \"name\" : \"jisoo\"} ");
            jsonBodyObj.put("groupname", groupname);
            jsonBodyObj.put("password", password);
            jsonBodyObj.put("user_id", user_id);
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


                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() 메소드 : Volley POST_BODY_JSON 요청 응답]");
                        Log.d("","\n"+"["+"응답 전체 - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");

                        int id = 0;
                        String name = "";
                        String password = "";


                        try {
                            id = (int) response.get("id");
                            name = String.valueOf(response.get("name"));
                            password = String.valueOf(response.get("password"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(), "아이디는 " + id + "\n 패스워드는" + password, Toast.LENGTH_LONG).show();

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
