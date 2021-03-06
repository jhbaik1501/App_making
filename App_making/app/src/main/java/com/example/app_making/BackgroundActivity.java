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
            Log.d("?????? -> ", "id.txt??? ??????");
        } catch (IOException e) {
            Log.d("?????? -> ", "id.txt??? ????????? ??????");
            e.printStackTrace();
        }
        id = temp;

        adapter = new ProductAdapter();

        adapter.addItem(new Product("picture 0", "??????", 0, 5, R.drawable.picture0));
        adapter.addItem(new Product("picture 1", "?????????????????????", 0, 10, R.drawable.picture1));
        adapter.addItem(new Product("picture 2", "?????????????????????", 0, 3, R.drawable.picture2));
        adapter.addItem(new Product("picture 3", "?????????????????????", 0, 5, R.drawable.picture3));
        adapter.addItem(new Product("picture 4", "?????????????????????", 0, 5, R.drawable.picture4));
        adapter.addItem(new Product("picture 5", "?????????????????????", 0, 5, R.drawable.picture5));
        adapter.addItem(new Product("picture 6", "?????????????????????", 0, 5, R.drawable.picture6));
        adapter.addItem(new Product("picture 7", "?????????????????????", 0, 10, R.drawable.picture7));


        recyclerView.setAdapter(adapter);

//        POST_BODY_JSON("http://10.0.2.2:8080/api/UseCoin", this, (long) 1, 5);

        adapter.setOnItemClickListener(new OnProductItemClickListener() {
            @Override
            public void onItemClick(ProductAdapter.ViewHolder holder, View view, int position) {
                Product item = (Product) adapter.getItem(position);
                Toast.makeText(getApplicationContext(), "????????? ?????? : " + item.getName(), Toast.LENGTH_LONG).show();
                int coin_count = Integer.parseInt(textView.getText().toString().replaceAll("[^0-9]", ""));
                if (coin_count < item.price){
                    Toast.makeText(getApplicationContext(), item.getName() + ", ????????? ???????????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                }
                else {


                    //dialog?????????

                    AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(BackgroundActivity.this);
                    myAlertBuilder.setTitle("??????");
                    myAlertBuilder.setMessage(item.getName() + "??? ?????????????????????????");
                    // ?????? ?????? (Ok ????????? Cancle ?????? )
                    myAlertBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int which){
                            // OK ????????? ????????? ??????
//                            saveCoin(String.valueOf(coin_count - item.price));

                            POST_BODY_JSON("http://10.0.2.2:8080/api/UseCoin", getApplicationContext(), id , item.price);

                            Toast.makeText(getApplicationContext(), item.getName() + " ?????? ??????", Toast.LENGTH_LONG).show();

                            SavePicture(item.getName());
                            textView.setText("????????? ??? : " + String.valueOf(coin_count - item.price));
                        }
                    });
                    myAlertBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Cancle ????????? ????????? ??????
                            Toast.makeText(getApplicationContext(),"?????? Cancle",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    myAlertBuilder.show();





                }
            }
        });

    }

    private void SavePicture(String picture) { // picture ????????? ?????? local???
        String filename;
        try {

            filename = "picture.txt";
            Log.d("=====", "\n\n\n========="+filename);
            FileOutputStream outfs = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
            outfs.write(picture.getBytes());
            outfs.close();
            //Toast.makeText(getApplicationContext(),filename+"??? ?????????",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onUserLeaveHint() { //??? ??? ?????? ????????? ???
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
        // ?????? id??? ??????????????? ?????? ?????? , ????????? ????????? id ?????? ???????????????.
        //TODO ????????? Request ?????? ??????
        RequestQueue queue = Volley.newRequestQueue(this);

        //TODO ??????????????? ?????? ??????
        Map<String, String> params = new HashMap<>(); //TODO {userId=1} ??????

//        params.put("userId", "1");
        params.put("userId", String.valueOf(id));

        //TODO ?????? Url ??? Data ?????? ??????
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
        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(url)+"]");
        Log.d("","\n"+"["+"?????? ??? - "+String.valueOf(params)+"]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(getUrl)+"]");
        Log.w("//===========//","================================================");
        Log.d("---","---");

        //TODO ????????? Response ?????? ??????


        final String requestBody = "";

        StringRequest request = new StringRequest(Request.Method.GET, getUrl,
                //TODO ????????? ?????? ?????? ??????
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");

                        int coin = 0;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            coin = (int) jsonObject.get("coin");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        textView.setText("????????? ??? : " + coin);

                    }
                },
                //TODO ????????? ?????? ?????? ?????? ??????
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("---","---");
                        Log.e("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(error.toString())+"]");
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
        //TODO ????????? Request ?????? ??????
        RequestQueue queue = Volley.newRequestQueue(context);

        //TODO ??????????????? ?????? ??????
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
        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() ????????? : Volley POST_BODY_JSON ?????? ??????]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(url)+"]");
        Log.d("","\n"+"["+"?????? ??? - "+String.valueOf(jsonBodyObj.toString())+"]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf("application/json")+"]");
        Log.w("//===========//","================================================");
        Log.d("---","---");

        //TODO ????????? Response ?????? ??????
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                //TODO ????????? ?????? ?????? ??????
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String str = "";
                        int possible = 1;
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() ????????? : Volley POST_BODY_JSON ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");


                        try {
                            str = String.valueOf(response.get("coin"));
                            possible = Integer.parseInt(String.valueOf(response.get("possible")));

                            if(possible == 0 ){
                                Toast.makeText(getApplicationContext(),"???????????? ????????????!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("pos, coin : ",possible + "---" + str);
                    }
                },
                //TODO ????????? ?????? ?????? ?????? ??????
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("---","---");
                        Log.e("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() ????????? : Volley POST_BODY_JSON ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(error.toString())+"]");
                        Log.e("//===========//","================================================");
                        Log.d("---","---");
                    }
                })
                //TODO ????????? ?????? ?????? ??? Body ????????? ????????? ?????? ??????
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
