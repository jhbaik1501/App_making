  package com.example.app_making;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telecom.Call;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Context;
import androidx.core.graphics.drawable.IconCompat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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


        StudyActivity studyActivity = new StudyActivity();
        if(Intent.ACTION_DATE_CHANGED.equals(intent.getAction()))
        {
            boolean ON = false;
            if (ON) {
                StudyActivity.Time_interrupt();
            }




            Log.d("\n어제날짜, 어제날짜", "\n\n\n\n\n바로바로바로\n" + "\n\n");
            String Time = readTime_yesterday(context);
            int Coin = (Time.charAt(0) - '0')* 10 + (Time.charAt(1) -'0');
            Toast.makeText(context, "공부시간이 멈췄습니다. 총 얻은 코인 수 " + Coin , Toast.LENGTH_SHORT).show();
            Coin += Integer.valueOf(readCoin(context));
            saveCoin(String.valueOf(Coin), context);



        }

    }

    public void saveCoin(String str, Context context){ // str은 코인정보.
        String filename;
        try {

            filename = "coin.txt";
            Log.d("=====", "\n\n\n========="+filename);
            FileOutputStream outfs = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outfs.write(str.getBytes());
            outfs.close();
            Toast.makeText(context,filename+"이 저장됨",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String readCoin(Context context) {
        String filename = "coin.txt";
        String coin=null;
        FileInputStream infs;
        try {
            infs= context.openFileInput(filename);
            byte txt[]=new byte[500];
            infs.read(txt);
            infs.close();
            coin=(new String(txt)).trim();

        } catch (FileNotFoundException e) {
            Log.d("코인", "코인 없음!");
            saveCoin("0", context);
            return "0";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coin;
    }

    String readTime_yesterday(Context context) {
        String filename = yesterday();
        String diaryStr=null;
        FileInputStream infs;
        try {
            infs= context.openFileInput(filename);
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
        else {
            day= day_;
        }
        int k = Integer.parseInt(day);
        k--;
        Log.d("TAG", "Yesterday: " +  year + "_" + month + "_" + k + ".txt" + "***********");
        return year + "_" + month + "_" + k + ".txt";
    }


}
