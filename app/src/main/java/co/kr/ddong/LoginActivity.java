package co.kr.ddong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class LoginActivity extends AppCompatActivity {
    private int dLoginFlag = 0;
    private int dCheckVersionFlag = 0;

    TextView introStartTV;
    ImageView introImage;
    //Button updateBt;
    //LinearLayout updateLL;

    private SharedPreferences preferences, accountpref;

    private String uId = null;
    private String uName = null;
    private String uEmail = null;
    private SignInButton signInButton;

    private FirebaseAuth mAuth = null;
    private FirebaseUser mUser = null;
    private GoogleSignInClient mGoogleSignInClient;
    //private static final int RC_SIGN_IN = 9001;

    ConnUpdateFrg connUpdate_frg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        FirebaseApp.initializeApp(this);

        introImage = (ImageView) findViewById(R.id.introImage);
        introStartTV = (TextView) findViewById(R.id.introStart);
        signInButton = (SignInButton) findViewById(R.id.googleLoginBtn);

        //updateBt = (Button) findViewById(R.id.updateBtn);
        //updateLL = (LinearLayout) findViewById(R.id.updateDialog);


        /*CheckClientVersion checkClientVersion = new CheckClientVersion();

        checkClientVersion.start();

        try{
            checkClientVersion.join();
        }catch (InterruptedException e){
        }*/
        dCheckVersionFlag = 0;
        if (dCheckVersionFlag == 0){
            Log.d(String.valueOf(this), "Client_version is current");


            signInButton.setVisibility(View.VISIBLE);

            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                Log.i(String.valueOf(LoginActivity.this), "mAuth is exist");
                //Toast.makeText(LoginActivity.this, "mAuth is exist", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(String.valueOf(LoginActivity.this), "mAuth is null");
                //Toast.makeText(LoginActivity.this, "mAuth is null", Toast.LENGTH_SHORT).show();
            }
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


            Intent signInIntent = mGoogleSignInClient.getSignInIntent();

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //signIn(signInIntent);
                    mUser = mAuth.getCurrentUser();
                    if (mUser != null) {
                        updateUI(mUser);
                        Log.d(String.valueOf(LoginActivity.this), "mUser is exist");
                        //Toast.makeText(LoginActivity.this, "mUser is exist", Toast.LENGTH_SHORT).show();
                    }else{
                        activityResultLauncher.launch(signInIntent);
                        //setResult(RESULT_OK, signInIntent);
                        //signIn(signInIntent);
                        Log.d(String.valueOf(LoginActivity.this), "mUser is null");
                        //Toast.makeText(LoginActivity.this, "mUser is null", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            introImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dLoginFlag = 1;
                    if (dLoginFlag == 1) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "서버 연결이 되지 않습니다. 관리자에게 문의하십시오.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        /*else{
            Log.d(String.valueOf(this), "Client_version is not current");
            connUpdate_frg = new ConnUpdateFrg();
            Log.d(String.valueOf(this), "11111111");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, connUpdate_frg).commit();
        }*/
    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("TAG", "result : " + result);

                    if (result.getResultCode() == RESULT_OK) {
                        Log.d(String.valueOf(LoginActivity.this), "Result = RESULT_OK START!!!!!!!!!!!");
                        //Toast.makeText(LoginActivity.this, "Result = RESULT_OK START!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
                        Intent signInIntent = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(signInIntent);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        }catch (ApiException e) {
                        }
                    }else {
                        Toast.makeText(LoginActivity.this, "구글 로그인 실패. 관리자에게 문의하십시오.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
    );


    /*
        private void signIn(Intent signInIntent) {
            Log.d(String.valueOf(LoginActivity.this), "signIn Start!!!!!!!!!!!!!");
            Intent signIntent = mGoogleSignInClient.getSignInIntent();

            setResult(RESULT_OK, signIntent);
        }

        private int signIn() {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
            return 0;
        }*/
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }*/
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(String.valueOf(LoginActivity.this), "GoogleSignInAccount account end");

                firebaseAuthWithGoogle(account);
                Log.d(String.valueOf(LoginActivity.this), "firebaseAuthWithGoogle end");

            } catch (ApiException e) {
                Log.d(String.valueOf(LoginActivity.this), "onActivityResult error");
            }
        }
    }*/
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(String.valueOf(LoginActivity.this), "firebaseAuthWithGoogle Start!!!!!!!!!!!!!");
        //Toast.makeText(LoginActivity.this, "firebaseAuthWithGoogle Start!!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "로그인 성공ㅋ", Toast.LENGTH_SHORT).show();
                            mUser = mAuth.getCurrentUser();
                            updateUI(mUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "로그인 실패하였습니다. 관리자에게 문의하십시오.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user) { //update ui code here
        Log.d(String.valueOf(LoginActivity.this), "updateUI Start!!!!!!!!!!!!!");
        //Toast.makeText(LoginActivity.this, "updateUI Start!!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
        ClientThread clientThread = new ClientThread();
        if (user != null) {
            uId = mUser.getUid();
            Log.i(String.valueOf(LoginActivity.this), "uId["+uId+"]");
            uName = mUser.getDisplayName();
            Log.i(String.valueOf(LoginActivity.this), "uName["+uName+"]");
            uEmail = mUser.getEmail();
            Log.i(String.valueOf(LoginActivity.this), "uEmail["+uEmail+"]");

            preferences = getSharedPreferences("KakaoAccount", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putInt("Type",1);
            editor.putString("ID", uId);
            editor.putString("Name", uName);
            editor.putString("EMAIL", uEmail);
            editor.commit();

            signInButton.setVisibility(View.INVISIBLE);
            introStartTV.setVisibility(View.VISIBLE);


            clientThread.start();

            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
            //finish();
        }else{
            Log.i(String.valueOf(LoginActivity.this), "[updateUI] FirebaseUser is null");
        }
    }
    class ClientThread extends Thread {

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
                            dLoginFlag = 1;
                            break;
                        }
                        else if (svrmsg.equals("2")){
                            Log.i(String.valueOf(this), "User Comeback");
                            Log.i(String.valueOf(this), "Login sucess");
                            dLoginFlag = 1;
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
                Toast.makeText(LoginActivity.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(LoginActivity.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    class CheckClientVersion extends Thread {

        public void run() {
            String host = "52.192.140.126";
            int port = 9999;

            int dFlag = 1;

            String client_ver = getString(R.string.app_ver);

            Log.i(String.valueOf(this), "check_Ver Thread Start");

            try {
                Socket socket = new Socket(host,port);
                Log.i(String.valueOf(this), "Make socket ["+host+"]["+port+"]");


                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                PrintWriter pw;
                BufferedReader reader;



                String msg = "0,"+ client_ver;

                pw=new PrintWriter(os);
                pw.println(msg);
                pw.flush();

                pw.println("done");
                pw.flush();

                reader = new BufferedReader(new InputStreamReader(is));

                while((msg = reader.readLine()) != null) {

                    Log.i(String.valueOf(this), "svrmsg is [" + msg + "]");
                    if (msg.equals("1")) {
                        Log.i(String.valueOf(this), "Current Client Version!!!");
                        dCheckVersionFlag = 0;
                        break;
                    }else {
                        Log.i(String.valueOf(this), "Client Version is not current version");
                        //dCheckVersionFlag = Integer.valueOf(msg);
                        dCheckVersionFlag = 0;
                        break;
                    }
                }

                pw.close();
                socket.close();

            } catch (UnknownHostException e) {
                Toast.makeText(LoginActivity.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(LoginActivity.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}