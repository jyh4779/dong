package co.kr.ddong;

import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;

public class PlayActivity extends AppCompatActivity implements View.OnTouchListener {
    int width, height;
    int zolaHeight, zolaWidth, zolaX, zolaY;
    int score = 0;
    int dungCount = 0;
    int hdlCount = 0;
    int downspeed = 20;
    int level = 1;
    int dSvrRet = 0;

    boolean stopFlag = false;

    float touchX;

    public ImageView zola;
    public TextView scoreText;
    public TextView endScore;
    public TextView lvBoard;
    Button endBt;
    ImageView dung[] = new ImageView[30];
    dungHandler hdl[] = new dungHandler[30];
    dungHandler lvup = new dungHandler();

    private SharedPreferences preferences, preftime, accountpref;

    ConstraintLayout PlayLayout;
    LinearLayout EndLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);

        PlayLayout = (ConstraintLayout) findViewById(R.id.play);
        EndLayout = (LinearLayout) findViewById(R.id.endLayout);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        zolaInit();

        scoreText = (TextView) findViewById(R.id.jumsu);
        scoreText.setText(String.valueOf(score));

        endScore = (TextView) findViewById(R.id.endScore);
        lvBoard = (TextView) findViewById(R.id.levelBoard);

        PlayLayout.setOnTouchListener(this);

        while(hdlCount < 30) {
            dung[hdlCount] = new ImageView(this);
            hdl[hdlCount] = new dungHandler();
            hdlCount++;
        }
        lvup = new dungHandler();

        ScoreThread scoreThread = new ScoreThread();
        CountThread thread = new CountThread();
        thread.start();

        endBt = (Button) findViewById(R.id.endButton);



        endBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                scoreThread.start();
                finish();
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int w = ((ViewGroup) zola.getParent()).getWidth() - zola.getWidth();
        //Log.i("onTouch", "w = "+String.valueOf(w));

        if(!stopFlag) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchX = event.getX();
                //Log.d("onTouch", "ACTION_DOWN, touchX = "+touchX);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                //Log.d("onTouch", "ACTION_MOVE");
                touchX = event.getX();
                if (touchX > 0 && touchX < w) {
                    if(touchX > zola.getX())zola.setImageResource(R.drawable.zola_left);
                    else if(touchX < zola.getX())zola.setImageResource(R.drawable.zola_right);
                    else if(touchX == zola.getX())zola.setImageResource(R.drawable.zolaman);
                    zola.setX(touchX);
                }
                //Log.d("onTouch", "ACTION_MOVE, touchX = "+touchX);
                //Log.d("onTouch", "ACTION_MOVE, zolaX = "+zola.getX());
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                zola.setImageResource(R.drawable.zolaman);
            }
        }
        return false;
    }

    private class CountThread extends Thread {
        public void run() {
            while(!stopFlag){
                if(dungCount < 30) {
                    Log.i(String.valueOf(this), "dungCount["+dungCount+"] Start!!");
                    DongThread thread = new DongThread(dungCount);
                    thread.start();
                }
                else if(dungCount >= 30) {
                    Log.i(String.valueOf(this), "dungCount is 30");
                    Log.i(String.valueOf(this), "Return to 0");
                    dungCount = -1;
                    level+=1;

                    Message message = lvup.obtainMessage();
                    Bundle bundle = new Bundle();

                    bundle.putInt("msgCode", 3);
                    message.setData(bundle);

                    lvup.sendMessage(message);
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dungCount++;
            }
        }
    }

    private class DongThread extends Thread {
        ImageView threadDong;
        int threadDungCount;
        public DongThread(int count) {
            // 초기화 작업
            threadDungCount = count;
            threadDong = dung[threadDungCount];
            Random random = new Random();
            float randomNum= random.nextInt(width + 1);

            int dungWidth = zolaWidth;
            int dungHeight = zolaHeight;
            float dungX = randomNum;
            float dungY = -dungHeight;

            Message message = hdl[threadDungCount].obtainMessage();
            Bundle bundle = new Bundle();

            bundle.putInt("dungInitCount",threadDungCount);
            bundle.putInt("dungWidth",dungWidth);
            bundle.putInt("dungHeight",dungHeight);
            bundle.putFloat("dungX",dungX);
            bundle.putFloat("dungY",dungY);

            message.setData(bundle);
            hdl[threadDungCount].sendMessage(message);
        }
        @WorkerThread
        public void run() {
            // 스레드에게 수행시킬 동작들 구현
            int dungY = (int) threadDong.getY();
            Log.i(String.valueOf(this), "run, dungY = "+dungY);
            int dungSpeed = downspeed;

            while (!stopFlag) {
                Message message = hdl[threadDungCount].obtainMessage();
                Bundle bundle = new Bundle();

                dungY += dungSpeed;
                //Log.i(String.valueOf(this), "run, dungY = "+dungY);

                if (dungY < height-(int)threadDong.getHeight()) {
                    bundle.putInt("msgCode", 1);
                    bundle.putInt("dungSetCount", threadDungCount);
                    bundle.putInt("setY", dungY);

                    message.setData(bundle);

                    hdl[threadDungCount].sendMessage(message);
                    //Log.i(String.valueOf(this), "run, sendMessage Code[1], dungCount[" + threadDungCount+"]");

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    bundle.putInt("msgCode", 2);
                    bundle.putInt("dungEndCount", threadDungCount);
                    message.setData(bundle);
                    hdl[threadDungCount].sendMessage(message);
                    Log.i(String.valueOf(this), "run, sendMessage Code[2], dungCount[" + threadDungCount+"]");

                    return;
                }
            }
        }
    }

    class dungHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            int msgCode = bundle.getInt("msgCode");
            //Log.i(String.valueOf(this), "dungHandler, Receive Message Code["+msgCode+"]");

            switch(msgCode) {
                case 0:
                    int dungWidth = bundle.getInt("dungWidth");
                    int dungHeight = bundle.getInt("dungHeight");
                    float dungInitX = bundle.getFloat("dungX");
                    float dungInitY = bundle.getFloat("dungY");
                    int dungInitCount = bundle.getInt("dungInitCount");
                    int dungRandomImage = Math.floorMod((int)dungInitX,3);
                    //Log.i(String.valueOf(this), "dungHandler, Receive DungCount["+dungInitCount+"]");

                    dung[dungCount].setX(dungInitX);
                    dung[dungCount].setY(dungInitY);
                    if(dungRandomImage == 0) dung[dungCount].setImageResource(R.drawable.ddong1);
                    else if(dungRandomImage == 1) dung[dungCount].setImageResource(R.drawable.ddong2);
                    else if(dungRandomImage == 2) dung[dungCount].setImageResource(R.drawable.ddong3);
                    else dung[dungCount].setImageResource(R.drawable.ddong);
                    Log.i(String.valueOf(this), "dungHandler, ImageInitial dung["+dungInitCount+"]");

                    PlayLayout.addView(dung[dungInitCount], new ConstraintLayout.LayoutParams(dungWidth, dungHeight));
                    Log.i(String.valueOf(this), "dungHandler, ImageAddview"+dungInitX+", "+dungInitY);

                case 1:
                    int dungSetCount = bundle.getInt("dungSetCount");
                    int dungY = bundle.getInt("setY");

                    dung[dungSetCount].setY(dungY);
                    //Log.i(String.valueOf(this), "dungHandler, dungCount[" + dungSetCount+"]");
                    //Log.i(String.valueOf(this), "dungHandler, setY = " + dungY);

                    if (dungY+dung[dungSetCount].getHeight()-60 >= zolaY) {
                        Log.i("dungHandler", "case 1: check Game End!");
                        Log.i("checkEnd", "zolaY = "+zolaY);
                        Log.i("checkEnd", "dungY = "+dungY+", "+dung[dungSetCount].getHeight());

                        checkEnd(dungSetCount);
                    }
                    break;
                case 2:
                    int dungEndCount = bundle.getInt("dungEndCount");

                    score += 1;
                    scoreText.setText(String.valueOf(score));
                    Log.i(String.valueOf(this), "dungHandler, dung[ = " + dungEndCount+"] is Over!");
                    Log.i(String.valueOf(this), "dungHandler, Score = "+score);

                    dung[dungEndCount].setY(0);

                    PlayLayout.removeView(dung[dungEndCount]);
                    Log.i(String.valueOf(this), "dungHandler, dung[ = " + dungEndCount+"] remove");
                    break;
                case 3:
                    lvBoard.setText(String.valueOf(level)+"단계");
                    downspeed += 5;
                    break;
            }
        }
    }
    void checkEnd (int dungCount){
        float dXStart = dung[dungCount].getX();
        float dXEnd = dXStart+dung[dungCount].getWidth();
        float zXStart = zola.getX();
        float zXEnd = zXStart+zola.getWidth();

        if (zXStart >= dXStart) {
            if (zXStart <= dXEnd) {
                Log.i("checkEnd", "dung hit zola. It's End!");
                Log.i("checkEnd", "zolaX = "+zXStart+", "+zXEnd);
                Log.i("checkEnd", "dungX = "+dXStart+", "+dXEnd);

                EndLayout.setVisibility(View.VISIBLE);
                endScore.setText("점수 : "+String.valueOf(score));

                stopFlag = true;

            }
        }
        else if (zXStart<= dXStart ){
            if (zXEnd >= dXStart){
                Log.i("checkEnd", "dung hit zola. It's End!");
                Log.i("checkEnd", "zolaX = "+zXStart+", "+zXEnd);
                Log.i("checkEnd", "dungX = "+dXStart+", "+dXEnd);
                EndLayout.setVisibility(View.VISIBLE);
                endScore.setText("점수 : "+String.valueOf(score));

                stopFlag = true;
            }
        }
    }
    class ScoreThread extends Thread {
        public void run() {
            String host = "52.192.140.126";
            int port = 9999;

            String msg = null;

            accountpref = getSharedPreferences("KakaoAccount", MODE_PRIVATE);
            Log.i(String.valueOf(this), "Score Send Thread Start");

            try {
                Log.i(String.valueOf(this), "Start socket [" + host + "][" + port + "]");
                Socket socket = new Socket(host, port);
                Log.i(String.valueOf(this), "Make socket [" + host + "][" + port + "]");


                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                PrintWriter pw;
                BufferedReader reader;

                msg = "2," +
                        accountpref.getString("ID", "-1") + "," +
                        String.valueOf(score);
                Log.i(String.valueOf(this), "Client MSG [" + msg + "]");

                pw = new PrintWriter(os);
                pw.println(msg);
                pw.flush();

                pw.println("done");
                pw.flush();

                reader = new BufferedReader(new InputStreamReader(is));

                while ((msg = reader.readLine()) != null) {
                    Log.i(String.valueOf(this), "svrmsg is [" + msg + "]");
                    if (msg.equals("1")) {
                        Log.i(String.valueOf(this), "Server return Success");
                        break;
                    } else {
                        Log.i(String.valueOf(this), "Score Data send Fail");
                        dSvrRet = Integer.valueOf(msg);
                        score_local_save();
                        break;
                    }
                }
                pw.close();
                socket.close();
            } catch (UnknownHostException e) {
                Toast.makeText(PlayActivity.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(PlayActivity.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    void score_local_save(){
        int n = 0;
        String getTime = null;

        preferences = getSharedPreferences("GameScore", MODE_PRIVATE);
        preftime = getSharedPreferences("GameTime", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        SharedPreferences.Editor timeeditor = preftime.edit();

        while(true){
            if(preferences.getInt(String.valueOf(n),-1) == -1){
                Log.i(String.valueOf(this), "Score Index["+n+"] is empty");
                break;
            }
            Log.i(String.valueOf(this), "Score Index["+n+"] is "+preferences.getInt(String.valueOf(n),-1));
            n++;
        }
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat("MM/dd hh:mm");
            getTime = sdf.format(date);
        }
        editor.putInt(String.valueOf(n),score);
        timeeditor.putString(String.valueOf(score),getTime);

        editor.commit();
        timeeditor.commit();
        return;
    }
    /*
    private void save_values() {
        int dNewScoreLank;
        int dPutInt;
        int dBeforeScoreLank = 5;

        String getTime = null;

        dNewScoreLank = check_score_empty();
        if(dNewScoreLank == 6){
            Log.i(String.valueOf(this), score+ " is lose");
            return;
        }

        preferences = getSharedPreferences("GameScore", MODE_PRIVATE);
        preftime = getSharedPreferences("GameTime", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        SharedPreferences.Editor timeeditor = preftime.edit();

        while(dBeforeScoreLank >= dNewScoreLank){
            if(dBeforeScoreLank > dNewScoreLank){
                if(!(preferences.getInt(String.valueOf(dBeforeScoreLank),0)==0)) {
                    dPutInt = dBeforeScoreLank - 1;
                    editor.putInt(String.valueOf(dBeforeScoreLank), preferences.getInt(String.valueOf(dPutInt), -1));
                }
                dBeforeScoreLank--;
            }
            else if(dBeforeScoreLank == dNewScoreLank){
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat("MM/dd hh:mm");
                    getTime = sdf.format(date);
                }

                editor.putInt(String.valueOf(dBeforeScoreLank),score);
                timeeditor.putString(String.valueOf(score),getTime);
                break;
            }
        }
        editor.commit();
        timeeditor.commit();
        return;
    }
    public int check_score_empty(){
        int dScoreLank = 1;
        preferences = getSharedPreferences("GameScore", MODE_PRIVATE);
        while (dScoreLank < 6) {
            if (preferences.getInt(String.valueOf(dScoreLank), -1) == -1) {
                Log.i(String.valueOf(this), "Lank[" + dScoreLank + "] is Empty");
                return dScoreLank;
            }
            else if (preferences.getInt(String.valueOf(dScoreLank), -1) >= score){
                Log.i(String.valueOf(this), "Lank[" + dScoreLank + "] is win");
                dScoreLank++;
            }
            else if (preferences.getInt(String.valueOf(dScoreLank), -1) < score){
                Log.i(String.valueOf(this), score+ " is better than Lank["+dScoreLank+"]");
                return dScoreLank;
            }
        }
        return dScoreLank;
    }*/
    public void zolaInit(){
        width = PlayLayout.getWidth();
        //Log.i("onWidowsFocusChanged","width = "+String.valueOf(width));
        height = PlayLayout.getHeight();
        //Log.i("onWidowsFocusChanged","height = "+String.valueOf(height));

        zolaWidth = width/8;
        //Log.i("onWidowsFocusChanged","zolaWidth = "+String.valueOf(zolaWidth));
        zolaHeight = height/9;
        //Log.i("onWidowsFocusChanged","zolaHeight = "+String.valueOf(zolaHeight));
        zolaX = width/2-zolaWidth/8;
        //Log.i("onWidowsFocusChanged","zolaX = "+String.valueOf(zolaX));
        zolaY = height-zolaHeight;
        //Log.i("onWidowsFocusChanged","zolaY = "+String.valueOf(zolaY));

        //zola = new ImageView(this);
        zola = (ImageView)findViewById(R.id.zola);
        //zola.setX(zolaX);
        //zola.setY(zolaY);
        zola.getLayoutParams().width=zolaWidth;
        zola.getLayoutParams().height=zolaHeight;
        //zola.setImageResource(R.drawable.zolaman);
        //PlayLayout.addView(zola, new ConstraintLayout.LayoutParams(zolaWidth, zolaHeight));
        Log.i(String.valueOf(this), "add zola"+zolaX+", "+zolaY);
    }
}
