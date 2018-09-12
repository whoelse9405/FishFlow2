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
import android.util.Base64;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

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
                if(mCamera!=null){
                    // cameraView에 있는 capture() 메서드 실행
                    cameraPreview.capture(new Camera.PictureCallback() {

                        // JPEG 사진파일 생성후 호출됨
                        // 찍은 사진을 처리
                        // PictureCallback 인터페이스 에 있는 onPictureTaken() 메서드
                        // byte[] data - 사진 데이타
                        public void onPictureTaken(byte[] data, Camera camera) {
                            try {

                                // 사진데이타를 비트맵 객체로 저장
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                //포맷 변경 및 압축
                                bitmap.compress(Bitmap.CompressFormat.JPEG,10,outputStream);

                                //byteArray 및 bitmap 생성
                                byte[] byteArray=outputStream.toByteArray();
                                bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);


                                // 파일 생성
                                // 폴더 생성
                                // 사진 저장

                                // bitmap 이미지를 이용해 앨범에 저장
                                // 내용재공자를 통해서 앨범에 저장
                                String outUriStr = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"Captured Image","Captured Image using Camera.");
                                if (outUriStr == null) {
                                    Log.e("SampleCapture", "Image insert failed.");
                                    return;
                                } else {
                                    Uri outUri = Uri.parse(outUriStr);
                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,outUri));
                                    Log.d("SampleCapture", "Image insert success.");
                                }





                                //결과화면 전송
                                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                                intent.putExtra("Image",byteArray);
                                startActivity(intent);

                                // 다시 미리보기 화면 보여줌
                                camera.startPreview();
                            } catch (Exception e) {
                                Log.e("SampleCapture", "Failed to insert image.", e);
                            }
                        }
                    });
                }
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

    //(byte->bitmap)
    public static Bitmap ByteArrayToBitmap(byte[] byteArray,int quality){
        if(quality==100){
            return BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        }else{
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            byte[] compByte = BitmapToByteArray(bitmap,quality);
            return BitmapFactory.decodeByteArray(compByte,0,compByte.length);
        }
    }

    //(bitmap->byte)
    public static byte[] BitmapToByteArray(Bitmap bitmap,int quality){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //포맷 변경 및 압축
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,outputStream);
        byte[] byteArray=outputStream.toByteArray();
        return byteArray;
    }

    //byteArray를 Base64인코딩 후 utf-8로 인코딩
    public static String ByteArrayToBase64(byte[] byteArray){
        //base64 인코딩
        String image = Base64.encodeToString(byteArray,Base64.DEFAULT);

        //utf-8 인코딩
        String temp="";
        try{
            temp="&imagedevice="+ URLEncoder.encode(image,"utf-8");
        }catch(Exception e){
            Log.e("exception",e.toString());
        }

        return temp;
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
            //갤러리 선택사진 결과화면으로 전송
            case REQ_GALLERY_CODE: {
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                    byte[] byteArray = BitmapToByteArray(bitmap,50);
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("Image",byteArray);
                    startActivity(intent);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }catch (OutOfMemoryError e){
                    Toast.makeText(getApplicationContext(), "이미지 용량이 너무 큽니다.", Toast.LENGTH_SHORT).show();
                }
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