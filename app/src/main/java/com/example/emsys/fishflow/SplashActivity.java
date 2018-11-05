package com.example.emsys.fishflow;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class SplashActivity extends AppIntro {

    private static final String TAG = SplashActivity.class.getSimpleName();
    final private int RESULT_PERMISSIONS = 100;

    //Fragment mSplash1 = new SplashFragment1();
    //Fragment mSplash2 = new SplashFragment2();
    //Fragment mSplash3 = new SplashFragment3();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(1000);
            prefs = getSharedPreferences("isFirstRun",MODE_PRIVATE);
            if(!prefs.getBoolean("isFirstRun",true)){
                startMainActivity();
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }




        addSlide(AppIntroFragment.newInstance("FishFlow","FishFlow는 인공지능 학습을 기반으로 간편하게 어종 및 생선의 원산지를 분석해주는 애플리케이션입니다.",R.mipmap.ic_launcher,ContextCompat.getColor(getApplicationContext(),R.color.appThemeColor)));
        addSlide(AppIntroFragment.newInstance("원클릭 분석 서비스, FishFlow","단 한번의 클릭으로 생선을 분석해보세요!",R.mipmap.ic_launcher,ContextCompat.getColor(getApplicationContext(),R.color.appThemeColor)));
        addSlide(AppIntroFragment.newInstance("진화하는 FishFlow","여러분의 신고를 통해 좀 더 정확한 결과를 가져다 드립니다!",R.mipmap.ic_launcher,ContextCompat.getColor(getApplicationContext(),R.color.appThemeColor)));


        // OPTIONAL METHODS
        // Override bar/separator color.
        //setBarColor(Color.parseColor("#3F51B5"));
        //setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        //showSkipButton(false);
        //setProgressButtonEnabled(false);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);

        //Set animator
        //setFadeAnimation();
        //setZoomAnimation();
        //setFlowAnimation();
        //setSlideOverAnimation();
        //setDepthAnimation();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // 안드로이드 6.0 이상 버전에서는 권한 허가를 요청하고 권한을 받으면 초기화
        requestPermission();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // 안드로이드 6.0 이상 버전에서는 권한 허가를 요청하고 권한을 받으면 초기화
        requestPermission();
    }

    @Override
    public void onSlideChanged( Fragment oldFragment, Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    //권한 요청
    public void requestPermission(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //아직 권한요청 수락하지 않음
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(this, "앱 실행을 위해서는 카메라 권한을 설정해야 합니다", Toast.LENGTH_LONG).show();
                }else{
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, RESULT_PERMISSIONS);
                }
            }else {
                //권한 요청 수락함
                prefs.edit().putBoolean("isFirstRun",false).apply();
                startMainActivity();

            }
        }else{
            // version 6 이하일때
            prefs.edit().putBoolean("isFirstRun",false).apply();
            startMainActivity();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (RESULT_PERMISSIONS == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions[0]==Manifest.permission.CAMERA) {
                // 카메라 권한 허가시
                prefs.edit().putBoolean("isFirstRun",false).apply();
                startMainActivity();
            } else {
                // 카메라 권한 거부시 재요청
                requestPermission();
            }
        }
    }
}
