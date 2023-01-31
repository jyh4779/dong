package com.example.mytest;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    List<DDongData> dataList = new ArrayList<>();
    RoomDB database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(String.valueOf(this),"Start RecordActivity");

        setContentView(R.layout.record);
        Log.i(String.valueOf(this),"Load record.xml");


        database = RoomDB.getInstance(this);
        dataList = database.mainDao().getAll();

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

        RoomDB database = RoomDB.getInstance(this);
        DDongData data = new DDongData();

        int lank = 0;

        while(lank < 5) {
            score[lank][0].setText(data.getNAME());
            score[lank][1].setText(data.getSCORE());
            score[lank][2].setText(data.getPLAY_TIME());
            lank++;
        }

    }
}
    /*
    private void load_values() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = ContactDBCtrct.SQL_SELECT;
        Log.i(String.valueOf(this),"Query = "+query);
        Cursor cursor = db.rawQuery(query, null);

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

        if (cursor.moveToFirst()) {
            int lank = 0;
            int value;
            String data;
            while(lank < 5) {
                // NAME 값 가져오기.
                data = cursor.getString(0);
                Log.i(String.valueOf(this),"Name["+lank+"] = "+data);
                score[lank][0].setText(data);

                // SCORE 값 가져오기.
                value = cursor.getInt(1);
                Log.i(String.valueOf(this),"SCORE["+lank+"] = "+String.valueOf(value));
                score[lank][1].setText(value);

                // SCORE_TIME 값 가져오기.
                data = cursor.getString(2);
                Log.i(String.valueOf(this),"TIME["+lank+"] = "+data);
                score[lank][2].setText(data);

                lank++;
            }
        }
        cursor.close();
    }*/

