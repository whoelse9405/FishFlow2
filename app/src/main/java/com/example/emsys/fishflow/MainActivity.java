package com.example.emsys.fishflow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{

    private static CameraPreview surfaceView;
    private SurfaceHolder holder;
    private static Button eixtButton;
    private static Button infoButton;
    private static Button photoButton;
    private static Button galleryButton;
    private static Camera mCamera;
    private int RESULT_PERMISSIONS=100;
    public static MainActivity getinstance;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //메인화면을 전체화면으로 세팅
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //카메라 권한 허가를 요청한다.
        requestPermissionCamera();
    }

    public static Camera getCamera(){
        return mCamera;
    }

    private void setInit(){
        getinstance = this;
        //카메라 객체를 R.layout.activity_main의 레이아웃에 선언한 서페이스뷰에서 먼저 정의해야 함으로
        mCamera = Camera.open();
        setContentView(R.layout.activity_main);

        //surfaceView를 상속받은 레이아웃을 정의한다.
        surfaceView=(CameraPreview)findViewById(R.id.surfaceView);

        //surfaceView 정의 - holder와 Callback을 정의한다.
        holder=surfaceView.getHolder();
        holder.addCallback(surfaceView);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public boolean requestPermissionCamera(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion>=Build.VERSION_CODES.M){                                                                                                                  //마시멜로우이후 버전이면
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){                           //카메라 권한허가가 되지않았다면
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},RESULT_PERMISSIONS);                  //
            }else{
                setInit();
            }
        }else{
            setInit();
            return true;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults){
        if(RESULT_PERMISSIONS==requestCode){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //권한 허가시
                setInit();
            }else{
                //권한 거부시
            }
            return;
        }
    }
}