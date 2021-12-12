package com.example.app_making;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SmsReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;

        String PhoneNumber = "";
        String contents = "";
        if (bundle != null)
        {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                PhoneNumber += msgs[i].getOriginatingAddress();

                contents += msgs[i].getMessageBody().toString();

            }
            Toast.makeText(context, "Num" + PhoneNumber+ " contents" + contents, Toast.LENGTH_SHORT).show();

            //initSMS(context);
            saveSMS(PhoneNumber, contents, context);
        }
    }

    public void initSMS(Context context){
        String filename = "SMSList.txt";
        String save = "";
        FileOutputStream outfs;
        try {
            outfs = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outfs.write(save.getBytes());
            outfs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveSMS(String Number, String contents, Context context){ // Number은 수신온 전화번호.
        String filename;
        try {

            String str= readSMS(context);

            String a = "<!#!" + Number + "<!#!" + contents + str ;
            String save = "";
            String []tokens=a.split("<!#!");
            if(tokens.length >40){
                for (int i = 1; i< 41; i++){
                    save += "<!#!" + tokens[i];
                }
            }
            else{
                save = a;
            }
            Toast.makeText(context, "\n\n\n ======" + save +"\n\n\n", Toast.LENGTH_SHORT).show();

            filename = "SMSList.txt";

            FileOutputStream outfs = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outfs.write(save.getBytes());
            outfs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String readSMS(Context context) {
        String filename = "SMSList.txt";
        String coin = null;
        FileInputStream infs;
        try {
            infs= context.openFileInput(filename);
            byte txt[] = new byte[500];
            infs.read(txt);
            infs.close();
            coin=(new String(txt)).trim();

        } catch (FileNotFoundException e) {
            Log.d("SMS정보", "SMS정보 없음!");
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coin;
    }
}



