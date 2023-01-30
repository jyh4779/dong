package com.example.mytest;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InitApplication extends AppCompatActivity {
    TextView introStartTV;
    ImageView introImage;

    myDBHelper myDBHelper;
    SQLiteDatabase sqlDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        //sqlDB = myDBHelper.getWritableDatabase();

        introImage = (ImageView) findViewById(R.id.introImage);
        introStartTV = (TextView) findViewById(R.id.introStart);
        introStartTV.setVisibility(View.VISIBLE);

        introImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void onStart() {
        super.onStart();
    }

    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "dongDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE TB_DONG_SCORE ( IND INTEGER PRIMARY KEY,NAME CHAR(10)," +
                    "SCORE INTEGER, GAME_TIME DATE);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS TB_DONG_SCORE");
            onCreate(db);

        }
    }
}
