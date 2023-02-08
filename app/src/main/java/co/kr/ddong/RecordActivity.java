package co.kr.ddong;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RecordActivity extends AppCompatActivity {
    Button btOk;
    Button btReset;

    private SharedPreferences preferences, preftime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.record_table);

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
    public void CallScoreLank(){
        int dRecordScore, dScoreX;
        int dLankCount = 1;
        TextView score[][] = new TextView[5][2];
        preferences = getSharedPreferences("GameScore", MODE_PRIVATE);
        preftime = getSharedPreferences("GameTime", MODE_PRIVATE);

        score[0][0] = (TextView) findViewById(R.id.oneScore);
        score[0][1] = (TextView) findViewById(R.id.oneTime);

        score[1][0] = (TextView) findViewById(R.id.twoScore);
        score[1][1] = (TextView) findViewById(R.id.twoTime);

        score[2][0] = (TextView) findViewById(R.id.threeScore);
        score[2][1] = (TextView) findViewById(R.id.threeTime);

        score[3][0] = (TextView) findViewById(R.id.fourScore);
        score[3][1] = (TextView) findViewById(R.id.fourTime);

        score[4][0] = (TextView) findViewById(R.id.fiveScore);
        score[4][1] = (TextView) findViewById(R.id.fiveTime);

        dRecordScore = preferences.getInt(String.valueOf(dLankCount), 0);
        dScoreX = 0;
        while(!(dRecordScore == 0)){
            String sRecordTime = String.valueOf(dRecordScore);
            Log.d(String.valueOf(this), "No["+dLankCount+"] Score = "+preferences.getInt(String.valueOf(dLankCount),0));
            Log.d(String.valueOf(this), "No["+dLankCount+"] Time = "+preftime.getString(sRecordTime,""));
            score[dScoreX][0].setText(String.valueOf(dRecordScore));
            score[dScoreX][1].setText(preftime.getString(sRecordTime,""));
            dScoreX++;
            dLankCount++;
            dRecordScore = preferences.getInt(String.valueOf(dLankCount), 0);
        }
    }

    public void ScoreDataReset(){
        preferences = getSharedPreferences("GameScore", MODE_PRIVATE);
        preftime = getSharedPreferences("GameTime", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        SharedPreferences.Editor timeeditor = preftime.edit();
        editor.clear();
        editor.commit();
        timeeditor.clear();
        timeeditor.commit();
        Toast.makeText(this, "기록이 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

}