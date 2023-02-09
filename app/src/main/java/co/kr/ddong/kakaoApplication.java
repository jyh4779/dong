package co.kr.ddong;

import android.app.Application;

public class kakaoApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        // Kakao SDK 초기화
        //KakaoSdk.init(this,"@kakao_app_key");
    }
}
