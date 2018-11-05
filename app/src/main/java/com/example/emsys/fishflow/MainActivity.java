package com.example.emsys.fishflow;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

//import com.example.leedongjin_notebook.fishflow2.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    static final int REQ_GALLERY_CODE = 1;
    static final int REQ_CAMERA_CODE = 2;

    private static Context mContext;

    private static ImageButton galleryButton;
    private static ImageButton searchButton;
    private static ImageButton exitButton;
    private static ImageButton helpButton;

    private static FrameLayout cameraPreviewLayout;
    private static CameraPreview cameraPreview;
    private static Camera mCamera;
    private static int imageId;

    private static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 카메라 프리뷰를 전체화면으로 보여주기 위해 셋팅한다.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //세로 화면 설정
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setInit();

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
                startActivityForResult(intent, REQ_GALLERY_CODE);
            }
        });

        //helpButton
        helpButton = (ImageButton) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
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

                                //90도 회전
                                Matrix m = new Matrix();
                                m.setRotate(90,(float)bitmap.getWidth(),(float)bitmap.getHeight());     //회전 정보 입력
                                bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,false);    //90도 회전

                                ////파일로 저장
                                String ImagePath = StoreBitmap(bitmap,100);

                                //인텐트 작성
                                intent = new Intent(getApplicationContext(), ResultActivity.class);
                                intent.putExtra("ImagePath",ImagePath);
                                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

                                //서버 전송 후 결과화면으로 전환
                                String url = getString(R.string.server_url)+"postImage.php";
                                new postImageTask().execute(url,ImagePath);           //신고 데이터 서버로 전송

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    //bitmap 파일 저장
    public static String StoreBitmap(Bitmap bitmap, int quality){
        //파일로 저장
        File folder = null;
        String folderPath=null;

        //폴더경로 설정
        if ( !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            // SD카드가 마운트되어있지 않음
            folder = Environment.getRootDirectory();
        }else{
            // SD카드가 마운트되어있음
            //folder = Environment.getExternalStorageDirectory();
            folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }


        folderPath = folder.getAbsolutePath() + String.format("/FishFlow");
        folder=new File(folderPath);

        // 디렉토리가 존재하지 않으면 디렉토리 생성
        if ( !folder.exists() ){
            folder.mkdirs();
        }

        //파일명 설정
        Calendar cal = Calendar.getInstance();
        String filename=String.format("FF_%d%02d%02d_%02d%02d%02d.jpg", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

        //파일 저장
        File file = new File(folder.toString(), filename);
        FileOutputStream filestream = null;
        try {
            filestream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, filestream);
            filestream.flush();
            filestream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //MediaScannerConnection.scanFile(mContext,);

        //mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://" + Environment.getExternalStorageDirectory())));

        return file.toString();
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
            mContext=getApplicationContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);
    }

    //URI를 절대경로로 변환
    private String getRealPathFromURI(Uri contentURI) {

        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();

        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            //갤러리 선택사진 결과화면으로 전송
            case REQ_GALLERY_CODE: {
                Uri uri = data.getData();
                String ImagePath=getRealPathFromURI(uri);

                intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("ImagePath",ImagePath);

                //서버 전송
                String url = getString(R.string.server_url)+"postImage.php";
                new postImageTask().execute(url,ImagePath);           //신고 데이터 서버로 전송


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

    private class postImageTask extends AsyncTask<String, String, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            String strUrl = params[0];
            String imagePath = params[1];
            String imageName = imagePath.substring(imagePath.lastIndexOf("/")+1);
            File image = new File(imagePath);

            try{
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("uuid",Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))
                        .addFormDataPart("image", imageName, RequestBody.create(MediaType.parse("image/*"), image))
                        .build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(strUrl)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                result = response.body().string();
            }catch (IOException e){
                Log.e(TAG,e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(!s.equals("")){
                imageId=Integer.parseInt(s);
                intent.putExtra("ImageId",imageId);
                startActivity(intent);

                Toast.makeText(getApplicationContext(),"분석 완료하였습니다.",Toast.LENGTH_SHORT).show();
                Log.d(TAG,"okhttp result : "+s);
            }else {
                Toast.makeText(getApplicationContext(),"분석 실패하였습니다.",Toast.LENGTH_SHORT).show();
                Log.d(TAG,"okhttp result failed");
            }
        }
    }

}