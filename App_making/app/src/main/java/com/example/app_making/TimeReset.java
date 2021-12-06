package com.example.app_making;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Context;
import androidx.core.graphics.drawable.IconCompat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TimeReset extends BroadcastReceiver {
    public final static String MyAction = "com.example.broadcasttest.ACTION_MY_BROADCAST";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 전원연결 및 전원해제 시 Toast메시지를 띄운다



        if(Intent.ACTION_DATE_CHANGED.equals(intent.getAction()))
        {
            StudyActivity.Time_interrupt();


            Log.d("\n어제날짜, 어제날짜", "\n\n\n\n\n바로바로바로\n" + "\n\n");

            Toast.makeText(context, "공부시간이 멈췄습니다. 총 얻은 코인 수 " , Toast.LENGTH_SHORT).show();

        }

    }


}
