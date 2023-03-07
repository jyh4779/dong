package co.kr.ddong;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Record2Activity extends AppCompatActivity {
    int dLoginFlag;
    TextView score[][] = new TextView[10][3];

    Button mScore, fScore, wScore;

    scoreHandler scorehdl = new scoreHandler();
    scoreInitHandler scoreInithdl = new scoreInitHandler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.record);

        mScore = (Button)findViewById(R.id.myScore);
        fScore = (Button)findViewById(R.id.friendsScore);
        wScore = (Button)findViewById(R.id.worldScore);

        score[0][0] = (TextView) findViewById(R.id.oneScore);
        score[0][1] = (TextView) findViewById(R.id.oneTime);
        score[0][2] = (TextView) findViewById(R.id.oneName);

        score[1][0] = (TextView) findViewById(R.id.twoScore);
        score[1][1] = (TextView) findViewById(R.id.twoTime);
        score[1][2] = (TextView) findViewById(R.id.twoName);

        score[2][0] = (TextView) findViewById(R.id.threeScore);
        score[2][1] = (TextView) findViewById(R.id.threeTime);
        score[2][2] = (TextView) findViewById(R.id.threeName);

        score[3][0] = (TextView) findViewById(R.id.fourScore);
        score[3][1] = (TextView) findViewById(R.id.fourTime);
        score[3][2] = (TextView) findViewById(R.id.fourName);

        score[4][0] = (TextView) findViewById(R.id.fiveScore);
        score[4][1] = (TextView) findViewById(R.id.fiveTime);
        score[4][2] = (TextView) findViewById(R.id.fiveName);

        score[5][0] = (TextView) findViewById(R.id.sixScore);
        score[5][1] = (TextView) findViewById(R.id.sixTime);
        score[5][2] = (TextView) findViewById(R.id.sixName);

        score[6][0] = (TextView) findViewById(R.id.sevenScore);
        score[6][1] = (TextView) findViewById(R.id.sevenTime);
        score[6][2] = (TextView) findViewById(R.id.sevenName);

        score[7][0] = (TextView) findViewById(R.id.eightScore);
        score[7][1] = (TextView) findViewById(R.id.eightTime);
        score[7][2] = (TextView) findViewById(R.id.eightName);

        score[8][0] = (TextView) findViewById(R.id.nineScore);
        score[8][1] = (TextView) findViewById(R.id.nineTime);
        score[8][2] = (TextView) findViewById(R.id.nineName);

        score[9][0] = (TextView) findViewById(R.id.tenScore);
        score[9][1] = (TextView) findViewById(R.id.tenTime);
        score[9][2] = (TextView) findViewById(R.id.tenName);

        scorehdl = new scoreHandler();
        scoreInithdl = new scoreInitHandler();

        MyScoreThread clientThread = new MyScoreThread();

        clientThread.start();

        mScore.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view ) {
                MyScoreThread myScoreThread = new MyScoreThread();
                myScoreThread.start();
            }
        });

        wScore.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view ) {
                WorldScoreThread worldScoreThread = new WorldScoreThread();
                worldScoreThread.start();
            }
        });
    }

    class scoreInitHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(String.valueOf(this), "Make Score Init Start");

            for(int i=0;i<10;i++) {
                Log.i(String.valueOf(this), "Make Score Init i = "+i);
                score[i][0].setText(" ");
                score[i][1].setText(" ");
                score[i][2].setText(" ");
            }
        }
    }

    class scoreHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();

            int lank = bundle.getInt("LANK");
            String lankScore = bundle.getString("SCORE");
            String scoreTime = bundle.getString("SCORE_TIME");
            String name = bundle.getString("NAME");

            score[lank][0].setText(lankScore);
            score[lank][1].setText(scoreTime);
            score[lank][2].setText(name);
        }
    }

    class WorldScoreThread extends Thread {
        public void run() {
            //SharedPreferences accountpref;
            //String host = "52.199.43.97";
            String host = "52.192.140.126";
            //String host = "192.168.203.154";
            int port = 9999;

            String msg = null;
            String svrmsg = null;
            int dFlag = 1;

            //accountpref = getSharedPreferences("KakaoAccount", MODE_PRIVATE);
            //Log.i(String.valueOf(this), "Client Thread Start");

            Message initMsg = scoreInithdl.obtainMessage();
            Bundle bundleInit = new Bundle();

            bundleInit.putString("MSG", "INIT");

            initMsg.setData(bundleInit);
            scoreInithdl.sendMessage(initMsg);

            try {
                Socket socket = new Socket(host,port);
                Log.i(String.valueOf(this), "Make socket ["+host+"]["+port+"]");


                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                PrintWriter pw;
                BufferedReader reader;

                if(dFlag == 1){
                    msg = "4";

                    pw=new PrintWriter(os);
                    pw.println(msg);
                    pw.flush();

                    pw.println("done");
                    pw.flush();

                    reader = new BufferedReader(new InputStreamReader(is));
                    int i=0;

                    while((svrmsg = reader.readLine()) != null) {
                        Log.i(String.valueOf(this), "svrmsg is [" + svrmsg + "]");
                        String[] splitText = svrmsg.split(",");
                        if (splitText[0].equals("1")) {
                            Log.d(String.valueOf(this), "splitText[1] = "+splitText[1]);
                            Log.d(String.valueOf(this), "splitText[2] = "+splitText[2]);
                            Log.d(String.valueOf(this), "splitText[2] = "+splitText[3]);

                            Message message = scorehdl.obtainMessage();
                            Bundle bundle = new Bundle();

                            bundle.putInt("LANK", i);
                            bundle.putString("SCORE", splitText[1]);
                            bundle.putString("SCORE_TIME", splitText[2]);
                            bundle.putString("NAME", splitText[3]);

                            message.setData(bundle);
                            scorehdl.sendMessage(message);

                            i++;
                        } else if (splitText[0].equals("done")) {
                            Log.i(String.valueOf(this), "Account data receive done");
                            break;
                        } else {
                            Log.i(String.valueOf(this), "Account data receive Fail");
                            dLoginFlag = Integer.valueOf(svrmsg);
                            break;
                        }
                        if (i > 10) break;
                    }
                    pw.close();
                    socket.close();
                }
            } catch (UnknownHostException e) {
                Toast.makeText(Record2Activity.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(Record2Activity.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    class MyScoreThread extends Thread {
        public void run() {
            SharedPreferences accountpref;
            //String host = "52.199.43.97";
            String host = "52.192.140.126";
            //String host = "192.168.203.154";
            int port = 9999;

            String msg = null;
            String svrmsg = null;
            int dFlag = 1;

            accountpref = getSharedPreferences("KakaoAccount", MODE_PRIVATE);
            Log.i(String.valueOf(this), "Client Thread Start");

            Message initMsg = scoreInithdl.obtainMessage();
            Bundle bundleInit = new Bundle();

            bundleInit.putString("MSG", "INIT");

            initMsg.setData(bundleInit);
            scoreInithdl.sendMessage(initMsg);

            try {
                Socket socket = new Socket(host,port);
                Log.i(String.valueOf(this), "Make socket ["+host+"]["+port+"]");


                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                PrintWriter pw;
                BufferedReader reader;

                if(dFlag == 1){
                    if(accountpref.getInt("Type",2) == 2)  {
                        Log.i(String.valueOf(this), "No use Kakao Login");
                        Log.i(String.valueOf(this), "Client Thread End");
                        return;
                    }

                    msg = "3,"+
                            accountpref.getString("ID","-1");

                    pw=new PrintWriter(os);
                    pw.println(msg);
                    pw.flush();

                    pw.println("done");
                    pw.flush();

                    reader = new BufferedReader(new InputStreamReader(is));
                    int i=0;

                    while((svrmsg = reader.readLine()) != null) {
                        Log.i(String.valueOf(this), "svrmsg is [" + svrmsg + "]");
                        String[] splitText = svrmsg.split(",");
                        if (splitText[0].equals("1")) {
                            Log.d(String.valueOf(this), "splitText[1] = "+splitText[1]);
                            Log.d(String.valueOf(this), "splitText[2] = "+splitText[2]);
                            Log.d(String.valueOf(this), "splitText[2] = "+splitText[3]);

                            Message message = scorehdl.obtainMessage();
                            Bundle bundle = new Bundle();

                            bundle.putInt("LANK", i);
                            bundle.putString("SCORE", splitText[1]);
                            bundle.putString("SCORE_TIME", splitText[2]);
                            bundle.putString("NAME", splitText[3]);

                            message.setData(bundle);
                            scorehdl.sendMessage(message);

                            i++;
                        } else if (splitText[0].equals("done")) {
                            Log.i(String.valueOf(this), "Account data receive done");
                            break;
                        } else {
                            Log.i(String.valueOf(this), "Account data receive Fail");
                            dLoginFlag = Integer.valueOf(svrmsg);
                            break;
                        }
                        if (i > 10) break;
                    }
                    pw.close();
                    socket.close();
                }
            } catch (UnknownHostException e) {
                Toast.makeText(Record2Activity.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(Record2Activity.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}


