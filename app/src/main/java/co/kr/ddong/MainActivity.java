package co.kr.ddong;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    //레이아웃 넓이, 레이아웃 높이
    int width, height;

    Button btnStart;
    Button btnOption;
    Button btnRecord;

    LinearLayout mainLayout;

    //InitApplication init = (InitApplication)getApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = (LinearLayout) findViewById(R.id.menu);

        btnStart = (Button) findViewById(R.id.start);
        btnOption = (Button) findViewById(R.id.option);
        btnRecord = (Button) findViewById(R.id.record);

        Log.d("getKeyHash", ""+getKeyHash(MainActivity.this));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        width = mainLayout.getWidth();
        height = mainLayout.getHeight();

        // 시작 버튼 클릭 이벤트
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), PlayActivity.class);
                startActivity(intent);
                }
        });

        //기록 버튼 클릭 이벤트
        btnRecord.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view ) {
                Intent intent= new Intent(getApplicationContext(), Record2Activity.class);
                startActivity(intent);
            }
        });
    }
    public static String getKeyHash(final Context context){
        PackageManager pm = context.getPackageManager();
        try{
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if(packageInfo == null)
                return null;

            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return android.util.Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                }catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }
            }
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
};




