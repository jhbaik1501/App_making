package com.example.app_making;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SMSActivity extends Activity {

    Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;
    RecyclerView recyclerView;
    CustomerAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        btnSendSMS = (Button) findViewById(R.id.button);
        txtPhoneNo = (EditText) findViewById(R.id.phone);
        txtMessage = (EditText) findViewById(R.id.message);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CustomerAdapter();

        String[] readSMS = readSMS();


        for (int i =1; i < readSMS.length ; i+=2) {

            adapter.addItem(new Customer(readSMS[i], readSMS[i + 1], R.drawable.customer));
        } //추가 하기 메일 전화번호하고 메일 내용


        recyclerView.setAdapter(adapter);



        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String phoneNo = txtPhoneNo.getText().toString();
                String message = txtMessage.getText().toString();
                if (phoneNo.length()>0 && message.length()>0)
                    sendSMS2(phoneNo, message);
                else
                    Toast.makeText(getBaseContext(), "Please enter both phone number and message.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // SMS를 전송하는 과정을 모니터링하고 싶다면
    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    // 모니터링 안하고 발송을 원한다면 아래 함수를 이용
    private void __sendSMS(String phoneNumber, String message)
    {

        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, SMSActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
    }

    public void sendSMS2(String Phone_number, String message)
    {

        // 사용자의 OS 버전이 마시멜로우 이상인지 체크한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = checkSelfPermission(Manifest.permission.SEND_SMS);


            if (permissionResult == PackageManager.PERMISSION_DENIED) {

                /**
                 * 사용자가 CALL_PHONE 권한을 거부한 적이 있는지 확인한다.
                 * 거부한적이 있으면 True를 리턴하고
                 * 거부한적이 없으면 False를 리턴한다.
                 */
                if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("이 기능을 사용하기 위해서는 단말기의 \"전화걸기\" 권한이 필요합니다. 계속 하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    /**
                                     * 새로운 인스턴스(onClickListener)를 생성했기 때문에
                                     * 버전체크를 다시 해준다.
                                     */
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        // CALL_PHONE 권한을 Android OS에 요청한다.
                                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1000);
                                    }
                                }
                            })
                            .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "기능을 취소했습니다", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();
                }
                // 최초로 권한을 요청할 때
                else {
                    // CALL_PHONE 권한을 Android OS에 요청한다.
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1000);
                }
            }
            // CALL_PHONE의 권한이 있을 때
            else {
                //Toast.makeText(getApplicationContext(), "ㅎㅇㅋ1", Toast.LENGTH_SHORT).show();
                // 즉시 실행
                __sendSMS(Phone_number, message);

            }
        }
        // 마시멜로우 미만의 버전일 때
        else {
            // 즉시 실행
            //Toast.makeText(getApplicationContext(), "ㅎㅇㅋ2", Toast.LENGTH_SHORT).show();
            __sendSMS(Phone_number, message);
        }

    }


    String[] readSMS() {
        String filename = "SMSList.txt";
        String[] k = new String[3];
        k[0] = "";
        k[1] = "아무 내용이 없습니다.";
        k[2] = "";
        String A;
        FileInputStream infs;
        try {
            infs= openFileInput(filename);
            byte txt[] = new byte[500];
            infs.read(txt);
            infs.close();
            A =(new String(txt)).trim();
            String[] SMS = A.split("<!#!");
            return SMS;

        } catch (FileNotFoundException e) {
            Log.d("SMS정보", "SMS정보 없음!");
            return k;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return k;
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


