package co.kr.ddong;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class DsClient extends AppCompatActivity {
    private SharedPreferences preferences;

    class ClientThread extends Thread {
        public void run(int dFlag, String outputMsg) {
            String host = "52.199.43.97";
            int port = 9999;

            preferences = getSharedPreferences("KakaoAccount", MODE_PRIVATE);

            try {
                Socket socket = new Socket(host, port);
                OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream();

                if(dFlag == 1){
                    if(preferences.getInt("Type",0) == 2)  {
                        Log.i(String.valueOf(this), "No use Kakao Login");
                        Log.i(String.valueOf(this), "Client Thread End");
                        return;
                    }
                    outputMsg = preferences.getString("ID","")+","+
                            preferences.getString("Name","")+","+
                            preferences.getString("Image","");
                    os.write(outputMsg.getBytes());
                    os.flush();

                    byte[] data = new byte[16];
                    int n = is.read(data);
                    final String resultFromServer = new String(data,0,n);
                    Log.d(String.valueOf(this),"Server result = "+resultFromServer);

                    socket.close();
                }



            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
