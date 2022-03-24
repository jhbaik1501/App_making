package com.example.app_making;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.HashMap;
import java.util.Map;

public class BackgroundActivity extends Activity {

    RecyclerView recyclerView;
    ProductAdapter adapter;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        textView = findViewById(R.id.coin_textView);
        recyclerView = findViewById(R.id.RecyclerView);


        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

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
            GET("http://10.0.2.2:8080/api/UserCheck", Integer.parseInt( rep )  );
        } catch (FileNotFoundException e) {
            Log.d("에러 -> ", "id.txt가 없음");
        } catch (IOException e) {
            Log.d("에러 -> ", "id.txt에 내용이 없음");
            e.printStackTrace();
        }
        id = temp;

        adapter = new ProductAdapter();

        adapter.addItem(new Product("picture 0", "리본", 0, 5, R.drawable.picture0));
        adapter.addItem(new Product("picture 1", "헤지스레이디스", 0, 10, R.drawable.picture1));
        adapter.addItem(new Product("picture 2", "헤지스레이디스", 0, 3, R.drawable.picture2));
        adapter.addItem(new Product("picture 3", "헤지스레이디스", 0, 5, R.drawable.picture3));
        adapter.addItem(new Product("picture 4", "헤지스레이디스", 0, 5, R.drawable.picture4));
        adapter.addItem(new Product("picture 5", "헤지스레이디스", 0, 5, R.drawable.picture5));
        adapter.addItem(new Product("picture 6", "헤지스레이디스", 0, 5, R.drawable.picture6));
        adapter.addItem(new Product("picture 7", "헤지스레이디스", 0, 10, R.drawable.picture7));


        recyclerView.setAdapter(adapter);

//        POST_BODY_JSON("http://10.0.2.2:8080/api/UseCoin", this, (long) 1, 5);

        adapter.setOnItemClickListener(new OnProductItemClickListener() {
            @Override
            public void onItemClick(ProductAdapter.ViewHolder holder, View view, int position) {
                Product item = (Product) adapter.getItem(position);
                Toast.makeText(getApplicationContext(), "선택된 제품 : " + item.getName(), Toast.LENGTH_LONG).show();
                int coin_count = Integer.parseInt(textView.getText().toString().replaceAll("[^0-9]", ""));
                if (coin_count < item.price){
                    Toast.makeText(getApplicationContext(), item.getName() + ", 코인이 부족하여 구매가 불가합니다.", Toast.LENGTH_LONG).show();
                }
                else {


                    //dialog만들기

                    AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(BackgroundActivity.this);
                    myAlertBuilder.setTitle("구매");
                    myAlertBuilder.setMessage(item.getName() + "을 구매하시겠습니까?");
                    // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
                    myAlertBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int which){
                            // OK 버튼을 눌렸을 경우
//                            saveCoin(String.valueOf(coin_count - item.price));

                            POST_BODY_JSON("http://10.0.2.2:8080/api/UseCoin", getApplicationContext(), id , item.price);

                            Toast.makeText(getApplicationContext(), item.getName() + " 구매 완료", Toast.LENGTH_LONG).show();

                            SavePicture(item.getName());
                            textView.setText("코인의 수 : " + String.valueOf(coin_count - item.price));
                        }
                    });
                    myAlertBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Cancle 버튼을 눌렸을 경우
                            Toast.makeText(getApplicationContext(),"구매 Cancle",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    myAlertBuilder.show();





                }
            }
        });

    }

    private void SavePicture(String picture) { // picture 정보는 그냥 local에
        String filename;
        try {

            filename = "picture.txt";
            Log.d("=====", "\n\n\n========="+filename);
            FileOutputStream outfs = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
            outfs.write(picture.getBytes());
            outfs.close();
            //Toast.makeText(getApplicationContext(),filename+"이 저장됨",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

                        int coin = 0;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            coin = (int) jsonObject.get("coin");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        textView.setText("코인의 수 : " + coin);

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

    public void POST_BODY_JSON(final String url, Context context, Long id, int useCoin){
        //TODO 데이터 Request 객체 생성
        RequestQueue queue = Volley.newRequestQueue(context);

        //TODO 파라미터값 선언 실시
        JSONObject jsonBodyObj = new JSONObject();
        try{
            //" { \"age\" : 24, \"coin\" : 3, \"name\" : \"jisoo\"} ");
            jsonBodyObj.put("userId", id);
            jsonBodyObj.put("useCoin", useCoin);
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


                        try {
                            str = String.valueOf(response.get("coin"));
                            possible = Integer.parseInt(String.valueOf(response.get("possible")));

                            if(possible == 0 ){
                                Toast.makeText(getApplicationContext(),"구매할수 없습니다!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("pos, coin : ",possible + "---" + str);
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
