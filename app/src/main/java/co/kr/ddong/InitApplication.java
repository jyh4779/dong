package co.kr.ddong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class InitApplication extends AppCompatActivity {
    int dLoginFlag;

    TextView introStartTV;
    ImageView introImage, login_kakao;

    private SharedPreferences preferences, accountpref;

    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        ClientThread clientThread = new ClientThread();
/*
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                //이때 토큰이 전달이 되면 로그인이 성공한 것이고 토큰이 전달되지 않았다면 로그인 실패
                updateKakaoLoginUi();
                return null;
            }
        };*/

        signInButton = findViewById(R.id.googleLoginBtn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id2))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /*if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }*/

        //login_kakao = (ImageView) findViewById(R.id.btn_kakaoLogin);
        introImage = (ImageView) findViewById(R.id.introImage);
        introStartTV = (TextView) findViewById(R.id.introStart);
        introStartTV.setText("시작하려면 아무곳이나 누르세요.");

        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                signIn();
            }
        });

        //mAuth = FirebaseAuth.getInstance();

        login_kakao.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //카톡이 설치되어있는지 확인
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(InitApplication.this)){
                    //UserApiClient.getInstance().loginWithKakaoTalk(InitApplication.this, callback);
                    Log.i(String.valueOf(this), "Kakao callback end, SocketThread Start");
                    login_kakao.setVisibility(View.INVISIBLE);
                    introStartTV.setVisibility(View.VISIBLE);

                    introImage.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dLoginFlag = 0;
                            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                            clientThread.start();
                            if (dLoginFlag == 0) {
                                Toast.makeText(InitApplication.this, "로그인 성공.", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            } else {
                                Log.i(String.valueOf(this), "Login Thread Ret["+dLoginFlag+"]");
                                Toast.makeText(InitApplication.this, "로그인 실패하였습니다. 관리자에게 문의하십시오.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else { //카톡이 설치 안되어 있으면
                    //UserApiClient.getInstance().loginWithKakaoTalk(InitApplication.this, callback);
                    //Log.d(String.valueOf(this),String.valueOf(callback));
                    Toast.makeText(InitApplication.this, "카카오톡이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

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
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Snackbar.make(findViewById(R.id.intro_main), "Authentication Successed.", Snackbar.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.intro_main), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void updateKakaoLoginUi() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                preferences = getSharedPreferences("KakaoAccount", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                //로그인이 되어있으면
                if (user != null) {
                    editor.putInt("Type",1);
                    Log.d(String.valueOf(this),"invoke : id["+user.getId()+"]");
                    editor.putLong("ID", user.getId());
                    Log.d(String.valueOf(this),"invoke : nickname["+user.getKakaoAccount().getProfile().getNickname()+"]");
                    editor.putString("Name", user.getKakaoAccount().getProfile().getNickname());
                    Log.d(String.valueOf(this),"invoke : image["+user.getKakaoAccount().getProfile().getProfileImageUrl()+"]");
                    editor.putString("Image", user.getKakaoAccount().getProfile().getProfileImageUrl());
                    editor.commit();
                    Toast.makeText(InitApplication.this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show();

                } else {
                    //로그인이 안되어있을때
                    Toast.makeText(InitApplication.this, "로그인 실패하였습니다. 관리자에게 문의하십시오.", Toast.LENGTH_SHORT).show();
                    Log.d(String.valueOf(this),"로그인이 안되어 있넹");
                    editor.putInt("Type",2);
                }
                return null;
            }
        });
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
                            accountpref.getLong("ID",-1)+","+
                            accountpref.getString("Name","")+","+
                            accountpref.getString("Image","");

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
                Toast.makeText(InitApplication.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(InitApplication.this, "서버와 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
