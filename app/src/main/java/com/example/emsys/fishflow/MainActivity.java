package com.example.emsys.fishflow;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

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

    private FrameLayout cameraPreviewLayout;
    private static CameraPreview cameraPreview;
    private static Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 카메라 프리뷰를 전체화면으로 보여주기 위해 셋팅한다.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //세로 화면 설정
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 안드로이드 6.0 이상 버전에서는 권한 허가를 요청하고 권한을 받으면 초기화
        requestPermission();



        /////   카메라 프리뷰 설정  //////
        //카메라 객체를 받아서 다시 CameraPreview에 넣어줌
        cameraPreview=new CameraPreview(MainActivity.this,mCamera);

        //FrameLayout에 CameraPreview를 add
        cameraPreviewLayout=(FrameLayout)findViewById(R.id.cameraPreviewLayout);
        cameraPreviewLayout.addView(cameraPreview);




        /////   버튼 설정  //////
        //galleryButton
        galleryButton = (ImageButton) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_GALLERY_CODE);


                /*
                Intent intent = new Intent(Intent.ACTION_PICK);                     //갤러리 불러오기 인텐트
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);               //타입 결정
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");                                          //이미지 형태만 가져오기
                //startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_PICK_CODE);
                startActivityForResult(intent, REQ_GALLERY_CODE);
                */
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
                if(mCamera!=null)
                    mCamera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            //Bitmap bitmap = ConvertYuvByteArrayToBitmap(data, camera);

                            /*
                            if(data==null){
                                Toast.makeText(MainActivity.this, "Captured image is empty", Toast.LENGTH_LONG).show();
                                mCamera.startPreview();
                                return;
                            }else{
                                byte[] img = ConvertYuvByteArrayToBitmap(data,camera);

                                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                                intent.putExtra("cameraImage",img);
                                Log.e("send picture","data size : "+ img.length);
                                startActivity(intent);
                                //mCamera.startPreview();
                            }
                            */
                            try {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                String outUriStr = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Captured Image", "Captured Image using camera.");

                                if (outUriStr == null) {
                                    Log.d("SampleCapture", "Image insert failed.");
                                    return;
                                } else {
                                    Uri outUri = Uri.parse(outUriStr);
                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, outUri));
                                }
                                camera.startPreview();
                            }catch (Exception e){
                                Log.e("SampleCaptrue","Failed to insert image.",e);
                            }
                        }
                    });



            }
        });

    }

    //사진 데이터 형식 변환(YUV->bitmap)
    public static byte[] ConvertYuvByteArrayToBitmap(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();

        YuvImage image = new YuvImage(data, parameters.getPreviewFormat(), size.width, size.height, null);
        Log.e("MainActivity","get preview size : " +size.width+"x"+ size.height);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //이미지 크기 크롭
        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 20, out);

        byte[] imageBytes = out.toByteArray();
        return imageBytes;
        //return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }


    private void setInit(){
        // 카메라 객체를 R.layout.activity_main의 레이아웃에 선언한 SurfaceView에서 먼저 정의해야 함으로 setContentView 보다 먼저 정의한다.
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);
    }



    //권한 요청
    public boolean requestPermission(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET}, RESULT_PERMISSIONS);
            }else {
                setInit();
            }
        }else{  // version 6 이하일때
            setInit();
            //return true;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (RESULT_PERMISSIONS == requestCode) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions[0]==Manifest.permission.CAMERA) {
                // 카메라 권한 허가시
                setInit();
            } else {
                // 카메라 권한 거부시 재요청
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET}, RESULT_PERMISSIONS);
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