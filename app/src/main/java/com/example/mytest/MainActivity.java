package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    //레이아웃 넓이, 레이아웃 높이
    int width, height;
    //버튼 넓이, 버튼 높이, 버튼 X, 시작 버튼Y, 옵션 버튼Y, 글쎄 버튼Y
    int btnWidth, btnHeight, btnX, btnStartY, btnOptionY, btnGlsseY;

    Button btnStart;
    Button btnOption;
    Button btnGlsse;

    LinearLayout mainLayout;

    //InitApplication init = (InitApplication)getApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = (LinearLayout) findViewById(R.id.menu);

        btnStart = new Button(this);
        btnOption = new Button(this);
        btnGlsse = new Button(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        width = mainLayout.getWidth();
        height = mainLayout.getHeight();

        //init.setWidth(width);
        //init.setHeight(height);

        btnWidth = width/3;
        btnHeight = height/14;

        btnX = width/2-width/6;
        btnStartY = height/3;
        btnOptionY = btnStartY+height/12;
        btnGlsseY = btnOptionY+height/12;

        btnStart.setText("시작");
        btnStart.setX(btnX);
        btnStart.setY(btnStartY);
        mainLayout.addView(btnStart, new LinearLayout.LayoutParams(btnWidth, btnHeight));

        btnOption.setText("옵션");
        btnOption.setX(btnX);
        btnOption.setY(btnOptionY);
        mainLayout.addView(btnOption, new LinearLayout.LayoutParams(btnWidth, btnHeight));

        btnGlsse.setText("글쎄");
        btnGlsse.setX(btnX);
        btnGlsse.setY(btnGlsseY);
        mainLayout.addView(btnGlsse, new LinearLayout.LayoutParams(btnWidth, btnHeight));

        // 시작 버튼 클릭 이벤트
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), PlayActivity.class);
                startActivity(intent);
                mainLayout.removeView(btnStart);
                mainLayout.removeView(btnOption);
                mainLayout.removeView(btnGlsse);
            }
        });

        //옵션 버튼 클릭 이벤트
        btnOption.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view ) {
                setContentView(R.layout.intro);
            }
        });
    }
};




