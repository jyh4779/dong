package com.example.mytest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecordActivity extends AppCompatActivity {
    Button btOk;
    Button btReset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.record);

        CallScoreLank();

        btOk = (Button) findViewById(R.id.ok);
        btReset = (Button) findViewById(R.id.reset);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScoreDataReset();
            }
        });
    }

    public void ScoreDataReset(){
        RoomDB database = RoomDB.getInstance(this);
        List<DDongData> dataList = database.mainDao().getAll();

        database.mainDao().reset(dataList);
        CallScoreLank();
    }

    public void CallScoreLank(){
        TextView score[][] = new TextView[5][3];

        score[0][0] = (TextView) findViewById(R.id.aName);
        score[0][1] = (TextView) findViewById(R.id.aScore);
        score[0][2] = (TextView) findViewById(R.id.aTime);

        score[1][0] = (TextView) findViewById(R.id.bName);
        score[1][1] = (TextView) findViewById(R.id.bScore);
        score[1][2] = (TextView) findViewById(R.id.bTime);

        score[2][0] = (TextView) findViewById(R.id.cName);
        score[2][1] = (TextView) findViewById(R.id.cScore);
        score[2][2] = (TextView) findViewById(R.id.cTime);

        score[3][0] = (TextView) findViewById(R.id.dName);
        score[3][1] = (TextView) findViewById(R.id.dScore);
        score[3][2] = (TextView) findViewById(R.id.dTime);

        score[4][0] = (TextView) findViewById(R.id.eName);
        score[4][1] = (TextView) findViewById(R.id.eScore);
        score[4][2] = (TextView) findViewById(R.id.eTime);

        Thread t = new Thread(() -> {
            RoomDB database = RoomDB.getInstance(this);
            List<DDongData> dataList = database.mainDao().getLank();

            int lank = 0;

            for(DDongData scoreLank : dataList){
                if (lank < 5) {
                    score[lank][0].setText(scoreLank.getNAME());
                    score[lank][1].setText(String.valueOf(scoreLank.getSCORE()));
                    score[lank][2].setText(scoreLank.getPLAY_TIME());
                    lank++;
                }
                else if(lank >= 5) break;
            }
            database.close();
        });
        t.start();
    }
}