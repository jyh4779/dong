package co.kr.ddong;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

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

        //옵션 버튼 클릭 이벤트
        btnRecord.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view ) {
                Intent intent= new Intent(getApplicationContext(), RecordActivity.class);
                startActivity(intent);
            }
        });
    }
};




