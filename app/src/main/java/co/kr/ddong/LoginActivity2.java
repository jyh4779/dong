package co.kr.ddong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class LoginActivity2  extends AppCompatActivity {
    int dLoginFlag;
    private SharedPreferences accountpref;

    Button loginBtn, createUserBtn;
    ImageView introImage;
    TextView introStartTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro2);

        loginBtn = (Button) findViewById(R.id.userLogin);
        createUserBtn = (Button) findViewById(R.id.newUserCreate);
        introImage = (ImageView) findViewById(R.id.introImage);
        introStartTV = (TextView) findViewById(R.id.introStart);

        accountpref = getSharedPreferences("KakaoAccount", MODE_PRIVATE);
        String uId = accountpref.getString("ID", "-1");

        uId = "-1";

        if (uId.equals("-1")) {
            Log.i(String.valueOf(this), "no Login Process Start");
            dLoginFlag = 0;
        } else {
            Log.i(String.valueOf(this), "Login Process Start");
            loginBtn.setVisibility(View.INVISIBLE);
            createUserBtn.setVisibility(View.INVISIBLE);
            introStartTV.setVisibility(View.VISIBLE);
            dLoginFlag = 1;
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LoginActivity3.class);
                launcher.launch(intent);
            }
        });

        introImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dLoginFlag == 1) {
                    Intent intent = new Intent(LoginActivity2.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult data)
                {
                    Log.d("TAG", "data : " + data);
                    if (data.getResultCode() == Activity.RESULT_OK)
                    {
                        Log.d(String.valueOf(this), "Login Return is RESULT_OK");
                        Intent intent = data.getData();
                        String result = intent.getStringExtra ("RESULT");
                        if(result.equals("OK")){
                            SharedPreferences.Editor editor = accountpref.edit();
                            editor.putString("ID", intent.getStringExtra("ID"));
                            editor.commit();

                            loginBtn.setVisibility(View.INVISIBLE);
                            createUserBtn.setVisibility(View.INVISIBLE);
                            dLoginFlag = 1;
                        }
                    }else if(data.getResultCode() == Activity.RESULT_CANCELED){
                        Log.d(String.valueOf(this), "Login Return is RESULT_CANCELED");
                    }
                }
            });
    class ClientLoginThread extends Thread {

        public void run() {
            //String host = "52.199.43.97";
            String host = "52.192.140.126";
            //String host = "192.168.203.154";
            int port = 9999;

            String msg = null;
            String svrmsg = null;
            int dFlag = 1;

            accountpref = getSharedPreferences("KakaoAccount", MODE_PRIVATE);
            Log.i(String.valueOf(this), "Client Thread Start");

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

                    msg = "1,"+
                            accountpref.getString("ID","-1")+","+
                            accountpref.getString("Name","")+","+
                            accountpref.getString("EMAIL","");

                    pw=new PrintWriter(os);
                    pw.println(msg);
                    pw.flush();

                    pw.println("done");
                    pw.flush();

                    reader = new BufferedReader(new InputStreamReader(is));

                    while((svrmsg = reader.readLine()) != null) {
                        Log.i(String.valueOf(this), "svrmsg is [" + svrmsg + "]");
                        if (svrmsg.equals("1")) {
                            Log.i(String.valueOf(this), "First Login User");
                            Log.i(String.valueOf(this), "Create User Data sucess");
                            break;
                        }
                        else if (svrmsg.equals("2")){
                            Log.i(String.valueOf(this), "User Comeback");
                            Log.i(String.valueOf(this), "Login sucess");
                            break;
                        }else {
                            Log.i(String.valueOf(this), "Account data send Fail");
                            dLoginFlag = Integer.valueOf(svrmsg);
                            break;
                        }
                    }
                    pw.close();
                    socket.close();
                }
            } catch (UnknownHostException e) {
                Toast.makeText(LoginActivity2.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(LoginActivity2.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}



