package co.kr.ddong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class InitApplication extends AppCompatActivity {
    TextView introStartTV;
    ImageView introImage, login_kakao;

    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                //이때 토큰이 전달이 되면 로그인이 성공한 것이고 토큰이 전달되지 않았다면 로그인 실패
                updateKakaoLoginUi();
                return null;
            }
        };

        login_kakao = (ImageView) findViewById(R.id.btn_kakaoLogin);
        introImage = (ImageView) findViewById(R.id.introImage);
        introStartTV = (TextView) findViewById(R.id.introStart);
        introStartTV.setText("시작하려면 아무곳이나 누르세요.");
        //introStartTV.setVisibility(View.VISIBLE);

        login_kakao.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //카톡이 설치되어있는지 확인
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(InitApplication.this)){
                    UserApiClient.getInstance().loginWithKakaoTalk(InitApplication.this, callback);
                } else { //카톡이 설치 안되어 있으면
                    UserApiClient.getInstance().loginWithKakaoTalk(InitApplication.this, callback);
                }
            }
        });

        DsClient dsClient = new DsClient();
        dsClient.ClientThread.start(1,"");

        introImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

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
                } else {
                    //로그인이 안되어있을때
                    Log.d(String.valueOf(this),"로그인이 안되어 있넹");
                    editor.putInt("Type",2);
                }
                return null;
            }
        });
    }

}
