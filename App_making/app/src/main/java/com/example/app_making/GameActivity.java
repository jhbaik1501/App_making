package com.example.app_making;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GameActivity extends Activity {
    Button button;
    EditText editText;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        button = findViewById(R.id.Save_button);
        editText = findViewById(R.id.editText);

        editText.setText(readMemo());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMemo(String.valueOf(editText.getText()));
                Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveMemo(String str){ // str은 내용
        String filename;
        try {
            filename = "memo.txt";
            FileOutputStream outfs = getApplication().openFileOutput(filename, Context.MODE_PRIVATE);
            outfs.write(str.getBytes());
            outfs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String readMemo() {
        String filename = "memo.txt";
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
