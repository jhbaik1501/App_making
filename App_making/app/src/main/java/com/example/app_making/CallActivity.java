package com.example.app_making;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CallActivity extends Activity {

    TextView callList;
    Button CallBtn;
    EditText phone_number;
    int isCall = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        callList = findViewById(R.id.call_list);
        CallBtn = findViewById(R.id.CallBtn);
        phone_number = findViewById(R.id.Phone_number);
        callList.setText(readCall());
    /*    retButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        CallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(phone_number.getText().length() == 11){

                    isCall = 1;
                    Editable editText = phone_number.getText();
                    editText.insert(3, "-");
                    editText.insert(8, "-");
                    Toast.makeText(getApplicationContext(), "phone_number=" + editText, Toast.LENGTH_SHORT).show();

                    String tel = "tel:" + editText;
                    saveCall(tel);
                    callList.setText(readCall());
                    sendCall(tel);


                }
                else {
                    Toast.makeText(getApplicationContext(), "제대로 된 폼으로 다시 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void sendCall(String Phone_number)
    {

        // 사용자의 OS 버전이 마시멜로우 이상인지 체크한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            /**
             * 사용자 단말기의 권한 중 "전화걸기" 권한이 허용되어 있는지 확인한다.
             * Android는 C언어 기반으로 만들어졌기 때문에 Boolean 타입보다 Int 타입을 사용한다.
             */
            int permissionResult = checkSelfPermission(Manifest.permission.CALL_PHONE);

            /**
             * 패키지는 안드로이드 어플리케이션의 아이디이다.
             * 현재 어플리케이션이 CALL_PHONE에 대해 거부되어있는지 확인한다.
             */
            if (permissionResult == PackageManager.PERMISSION_DENIED) {

                /**
                 * 사용자가 CALL_PHONE 권한을 거부한 적이 있는지 확인한다.
                 * 거부한적이 있으면 True를 리턴하고
                 * 거부한적이 없으면 False를 리턴한다.
                 */
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
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
                                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
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
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                }
            }
            // CALL_PHONE의 권한이 있을 때
            else {
                // 즉시 실행
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(Phone_number));
                startActivity(intent);
            }
        }
        // 마시멜로우 미만의 버전일 때
        else {
            // 즉시 실행
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(Phone_number));
            startActivity(intent);
        }

    }

    public void recvCall()
    {

        // 사용자의 OS 버전이 마시멜로우 이상인지 체크한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            /**
             * 사용자 단말기의 권한 중 "전화걸기" 권한이 허용되어 있는지 확인한다.
             * Android는 C언어 기반으로 만들어졌기 때문에 Boolean 타입보다 Int 타입을 사용한다.
             */
            int permissionResult = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);

            /**
             * 패키지는 안드로이드 어플리케이션의 아이디이다.
             * 현재 어플리케이션이 CALL_PHONE에 대해 거부되어있는지 확인한다.
             */
            if (permissionResult == PackageManager.PERMISSION_DENIED) {

                /**
                 * 사용자가 CALL_PHONE 권한을 거부한 적이 있는지 확인한다.
                 * 거부한적이 있으면 True를 리턴하고
                 * 거부한적이 없으면 False를 리턴한다.
                 */
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
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
                                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1000);
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
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1000);
                }
            }
            // CALL_PHONE의 권한이 있을 때
            else {
                // 즉시 실행
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-9061-8284"));
                startActivity(intent);
            }
        }
        // 마시멜로우 미만의 버전일 때
        else {
            // 즉시 실행
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-9061-8284"));
            startActivity(intent);
        }

    }


    public class PhoneStateCheckListener extends PhoneStateListener {

        MainActivity mainActivity;

        PhoneStateCheckListener(MainActivity _main) {

            mainActivity = _main;

        }

        @Override

        public void onCallStateChanged(int state, String incomingNumber) {

            if (state == TelephonyManager.CALL_STATE_IDLE) {

                Toast.makeText(mainActivity, "STATE_IDLE : Incoming number "

                        + incomingNumber, Toast.LENGTH_SHORT).show();

            } else if (state == TelephonyManager.CALL_STATE_RINGING) {

                Toast.makeText(mainActivity, "STATE_RINGING : Incoming number "

                        + incomingNumber, Toast.LENGTH_SHORT).show();

//수신 부분 입니다.

            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {

                Toast.makeText(mainActivity, "STATE_OFFHOOK : Incoming number "

                        + incomingNumber, Toast.LENGTH_SHORT).show();

            }

        }
    }


    public void saveCall(String str){ // str은 내용
        String filename;
        try {
            str += "\n" + readCall();
            filename = "Call.txt";
            FileOutputStream outfs = getApplication().openFileOutput(filename, Context.MODE_PRIVATE);
            outfs.write(str.getBytes());
            outfs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String readCall() {
        String filename = "Call.txt";
        String coin=null;
        FileInputStream infs;
        try {
            infs= getApplication().openFileInput(filename);
            byte txt[]=new byte[500];
            infs.read(txt);
            infs.close();
            coin=(new String(txt)).trim();

        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coin;
    }

    @Override
    protected void onUserLeaveHint() { //홈 키 혹은 작업탭 키
        super.onUserLeaveHint();
        if (isCall == 0){
            restart(this);
        }
        else {
            isCall =1;
        }

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
