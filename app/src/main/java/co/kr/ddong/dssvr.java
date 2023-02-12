package co.kr.ddong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class dssvr {

    public final static int SERVER_PORT=9999;
    public static void main(String[] ar) {
        ServerSocket ss=null;
        try {
            ss=new ServerSocket(SERVER_PORT);

        }catch(IOException e) {
            e.printStackTrace();
        }

        while(true) {
            try {
                System.out.println("Waiting connection...");
                Socket s=ss.accept();
                System.out.println("[ Connection Info ]");
                System.out.println("client address:"+s.getInetAddress());
                System.out.println("client port:"+s.getPort());
                System.out.println("my port:"+s.getLocalPort());

                new ServerThread(s).start;

                PrintWriter pw=new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                pw.println("Hello!! From server");

                pw.close();
                s.close();
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
class ServerThread extends Thread{

    private Socket s;
    private BufferedReader br;
    private PrintWriter pw;
    public ServerThread(Socket s) {
        this.s=s;
        try {
            br=new BufferedReader(new InputStreamReader(s.getInputStream()));	//Socket으로 Read용 Stream
            pw=new PrintWriter(new OutputStreamWriter(s.getOutputStream()));	//Socket으로 Write용 Stream
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {

        while(true) {
            String received;
            try {
                received = br.readLine();	//1. 받고
                System.out.println("server received :"+received);
                if(received==null||received.equals("quit")) {	//quit 또는 q가 오면 종료
                    if(br!=null) br.close();
                    if(pw!=null) pw.close();
                    if(s!=null) s.close();
                    return;
                }

                pw.println("Server Got Your Message : "+received);	//2. 보냄
                pw.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
