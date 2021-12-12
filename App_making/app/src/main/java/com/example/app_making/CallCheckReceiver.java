package com.example.app_making;

import android.app.Service;
import android.content.BroadcastReceiver;

import android.content.Context;

import android.content.Intent;

import android.content.SharedPreferences;
import android.media.AudioManager;

import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;

import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class CallCheckReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {


        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);




                String mState = String.valueOf(state);
                String contents;

                switch(state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Toast.makeText(context, "전화 수신상태가 아님", Toast.LENGTH_SHORT).show();
                        contents = "IDLE";
                        SharedPreferenceUtil.putSharedPreference(context, "Pause_INDEX", contents);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Toast.makeText(context, "전화 받음", Toast.LENGTH_SHORT).show();
                        contents = "OFFHOOK";
                        SharedPreferenceUtil.putSharedPreference(context, "Pause_INDEX", contents);
                        Log.d("HIHIHIHIHIHIHI", "onCallStateChanged: "+ SharedPreferenceUtil.getSharedPreference(context, "Pause_INDEX"));
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:

                        //Toast.makeText(context, "수신 전화번호, 전화벨 울림"+ PhoneNumberUtils.formatNumber(incomingNumber), Toast.LENGTH_SHORT).show();
                        // 처리하고자 하는 코드 추가하면 된다.
                        contents = "RING";
                        SharedPreferenceUtil.putSharedPreference(context, "Pause_INDEX", contents);


                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }





}
