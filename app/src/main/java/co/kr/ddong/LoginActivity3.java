package co.kr.ddong;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class LoginActivity3 extends AppCompatActivity {
    private String ID = null;
    private String PW = null;

    private int dLoginFlag;

    EditText et_ID, et_PW;
    Button bt_ok, bt_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        et_ID = (EditText) findViewById(R.id.et_ID);
        et_PW = (EditText) findViewById(R.id.et_PW);

        bt_ok = (Button) findViewById(R.id.bt_login);
        bt_cancel = (Button) findViewById(R.id.bt_CANCEL);

        ClientLoginThread clientLoginThread = new ClientLoginThread();

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_ID.getText().equals(null)){
                    Toast.makeText(LoginActivity3.this, "ID를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    if(et_PW.getText().equals(null)){
                        Toast.makeText(LoginActivity3.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        ID = String.valueOf(et_ID.getText());
                        PW = String.valueOf(et_PW.getText());
                        Log.i(String.valueOf(this), "Client ID["+ID+"]");
                        Log.i(String.valueOf(this), "Client PassWord["+PW+"]");

                        clientLoginThread.start();

                        while(true){
                            if(dLoginFlag == 1){
                                Log.i(String.valueOf(this), "Server Login Success");
                                Intent intent = new Intent();
                                intent.putExtra("RESULT", "OK");
                                intent.putExtra("ID",ID);
                                intent.putExtra("PW",PW);
                                setResult(RESULT_OK, intent);
                                finish();
                            }else if(dLoginFlag == 2){
                                Toast.makeText(LoginActivity3.this, "로그인 실패. 아이디, 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(String.valueOf(this), "Login Cancel");
                Intent intent = new Intent();
                intent.putExtra("RESULT", "CANCEL");
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }
    class ClientLoginThread extends Thread {

        public void run() {
            //String host = "52.199.43.97";
            String host = "52.192.140.126";
            //String host = "192.168.203.154";
            int port = 9999;

            String msg = null;
            String svrmsg = null;
            int dFlag = 1;

            Log.i(String.valueOf(this), "Client Thread Start");

            try {
                Socket socket = new Socket(host,port);
                Log.i(String.valueOf(this), "Make socket ["+host+"]["+port+"]");


                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                PrintWriter pw;
                BufferedReader reader;

                if(dFlag == 1){
                    msg = "1,"+
                            ID+","+
                            PW;

                    pw=new PrintWriter(os);
                    pw.println(msg);
                    pw.flush();

                    pw.println("done");
                    pw.flush();

                    reader = new BufferedReader(new InputStreamReader(is));

                    while((svrmsg = reader.readLine()) != null) {
                        Log.i(String.valueOf(this), "svrmsg is [" + svrmsg + "]");
                        if (svrmsg.equals("1")) {
                            Log.i(String.valueOf(this), "User Comeback");
                            Log.i(String.valueOf(this), "Login sucess");
                            dLoginFlag = 1;
                            break;
                        }else {
                            Log.i(String.valueOf(this), "Account data send Fail");
                            dLoginFlag = 1;
                            break;
                        }
                    }
                    pw.close();
                    socket.close();
                }
            } catch (UnknownHostException e) {
                Toast.makeText(LoginActivity3.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(LoginActivity3.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}

