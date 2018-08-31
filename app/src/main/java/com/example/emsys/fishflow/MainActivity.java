package com.example.emsys.fishflow;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

//import com.example.leedongjin_notebook.fishflow2.R;

public class MainActivity extends AppCompatActivity {

    static final int REQ_GALLERY_CODE = 1;
    static final int REQ_CAMERA_CODE = 2;
    static final int REQ_IMAGE_CROP = 3;
    static final int REQ_PERMISSION_CAMERA = 4;
    private final int RESULT_PERMISSIONS = 100;

    private static ImageButton galleryButton;
    private static ImageButton searchButton;
    private static ImageButton exitButton;
    private static ImageButton helpButton;

    private static CameraPreview surfaceView;
    private SurfaceHolder holder;
    private static Camera mCamera;
    public static MainActivity getInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#ff7828"));
        }

        // 카메라 프리뷰를  전체화면으로 보여주기 위해 셋팅한다.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 안드로이드 6.0 이상 버전에서는 CAMERA 권한 허가를 요청한다.
        requestPermissionCamera();


        //galleryButton
        galleryButton = (ImageButton) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);                     //갤러리 불러오기 인텐트
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);               //타입 결정
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");                                          //이미지 형태만 가져오기
                //startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_PICK_CODE);
                startActivityForResult(intent, REQ_GALLERY_CODE);
            }
        });

        //helpButton
        helpButton = (ImageButton) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            }
        });

        //exitButton
        exitButton = (ImageButton) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //searchButton
        searchButton = (ImageButton)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(intent);
            }
        });

    }

    public static Camera getCamera(){
        return mCamera;
    }
    private void setInit(){
        getInstance = this;

        // 카메라 객체를 R.layout.activity_main의 레이아웃에 선언한 SurfaceView에서 먼저 정의해야 함으로 setContentView 보다 먼저 정의한다.
        mCamera = Camera.open();

        setContentView(R.layout.activity_main);

        // SurfaceView를 상속받은 레이아웃을 정의한다.
        surfaceView = (CameraPreview)findViewById(R.id.cameraView);


        // SurfaceView 정의 - holder와 Callback을 정의한다.
        holder = surfaceView.getHolder();
        holder.addCallback(surfaceView);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    public boolean requestPermissionCamera(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, RESULT_PERMISSIONS);
            }else {
                setInit();
            }
        }else{  // version 6 이하일때
            setInit();
            //return true;
        }

        return true;
    }

    //권한
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (RESULT_PERMISSIONS == requestCode) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허가시
                setInit();
            } else {
                // 권한 거부시
            }
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case REQ_GALLERY_CODE: {
                //imageView = (ImageView) findViewById(R.id.imageView);
                //imageView.setImageURI(data.getData());
                break;
            }
            case REQ_CAMERA_CODE: {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(imageBitmap);
                break;
            }
        }

    }
}