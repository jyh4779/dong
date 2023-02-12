package co.kr.ddong;

import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class DsClient {
    String sendString;

    class ClientThread extends Thread {
        @Override
        public void run() {
            String host = "52.199.43.97";
            int port = 9999;

            try {
                Socket socket = new Socket(host, port);

                ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
                outStream.writeObject("Hello");
                outStream.flush();
                Log.d("ClientStream","Sent to server");

                ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
                Object input = inStream.readObject();
                Log.d("ClientThread", "Received data : "+input);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
