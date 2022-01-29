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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BackgroundActivity extends Activity {

    RecyclerView recyclerView;
    ProductAdapter adapter;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        textView = findViewById(R.id.coin_textView);
        recyclerView = findViewById(R.id.recyclerView);


        if(readCoin().equals("0"))
            saveCoin("0");

        int coin_count= Integer.parseInt(readCoin()); //코인의 수

        textView.setText("코인의 수 : " + coin_count);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);


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

        adapter.setOnItemClickListener(new OnProductItemClickListener() {
            @Override
            public void onItemClick(ProductAdapter.ViewHolder holder, View view, int position) {
                Product item = (Product) adapter.getItem(position);
                Toast.makeText(getApplicationContext(), "선택된 제품 : " + item.getName(), Toast.LENGTH_LONG).show();
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
                            saveCoin(String.valueOf(coin_count - item.price));
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

    private void SavePicture(String picture) {
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


    public void saveCoin(String str ){ // str은 코인정보.
        String filename;
        try {

            filename = "coin.txt";
            Log.d("=====", "\n\n\n========="+filename);
            FileOutputStream outfs = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
            outfs.write(str.getBytes());
            outfs.close();
            //Toast.makeText(getApplicationContext(),filename+"이 저장됨",Toast.LENGTH_SHORT).show();
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
            infs= getApplicationContext().openFileInput(filename);
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
}
