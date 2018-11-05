package com.example.emsys.fishflow;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;


import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG = SurfaceView.class.getSimpleName();

    private SurfaceHolder mHolder;
    private Camera mCamera;
    public List<Camera.Size> listPreviewSizes;
    private Camera.Size previewSize;
    //private Display display;

    // SurfaceView 생성자
    public CameraPreview(Context context, Camera camera){
        super(context);
        //display=((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay() ;
        this.mCamera=camera;
        mHolder = getHolder();       //getHolder를 넣어줌
        mHolder.addCallback(this);   //콜백을 아래에서 처리를 해줌 (this는 해당 클래스에서 처리를 함을 나타냄)
        // 카메라가 SurfaceView를  독점하기 위한 타입 설정
        // 버퍼를 사용하지않음
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    //  SurfaceView 생성시 호출
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            // 카메라 객체를 사용할 수 있게 연결한다.
            if(mCamera == null){
                mCamera = Camera.open();
            }
            //// 카메라 설정 ////
            Camera.Parameters parameters = mCamera .getParameters();

            //프리뷰 및 사진크기 설정
            parameters=setSize(parameters);

            // 카메라의 회전이 가로/세로일때 화면을 설정한다.
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                parameters.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }

            //자동 포커스 설정
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            //카메라객체에 설정한 옵션 전달
            mCamera.setParameters(parameters);

            //preview를 열어주고 이 surfaceHolder에서 처리함을 알려줌
            mCamera.setPreviewDisplay(mHolder);

            // 카메라 미리보기를 시작한다
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Failed to set camera preview.", e);
        }
    }

    // SurfaceView 의 크기가 바뀌면 호출
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

        // 카메라 객체를 사용할 수 있게 연결한다.
        if(mCamera == null){
            mCamera = Camera.open();
        }

        //// 카메라 설정 ////
        Camera.Parameters parameters = mCamera .getParameters();

        //프리뷰 및 사진크기 설정
        parameters=setSize(parameters);

        // 카메라의 회전이 가로/세로일때 화면을 설정한다.
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            parameters.setRotation(90);
            mCamera.setDisplayOrientation(90);
        } else {
            parameters.set("orientation", "landscape");
            parameters.setRotation(0);
            mCamera.setDisplayOrientation(0);
        }

        //parameters.setPreviewSize(width, height);     //카메라 프리뷰 크기 설정
        //parameters.setPictureSize(640, 480);          //사진 크기 설정


        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);       //자동 포커스 설정

        mCamera.setParameters(parameters);      //카메라객체에 설정한 옵션 전달
        mCamera.startPreview();                 // 카메라 미리보기를 시작한다
    }

    // SurfaceView가 종료시 호출
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera != null){
            // 카메라 미리보기를 종료한다.
            mCamera.stopPreview();
            // 메모리 해제
            mCamera.release();
            mCamera = null;
        }
    }

    // 사진을 찍을때 호출되는 함수 (스냅샷)
    public boolean capture(Camera.PictureCallback handler) {
        if (mCamera != null) {
            // 셔터후
            // Raw 이미지 생성후
            // JPE 이미지 생성후
            mCamera.takePicture(null, null, handler);
            return true;
        } else {
            return false;
        }
    }


    // 화면이 회전할 때 화면 사이즈를 구한다.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (listPreviewSizes != null) {
            previewSize = getPreviewSize(listPreviewSizes, width, height);
        }
    }
    public Camera.Size getPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }

    //카메라 비율 설정
    private Camera.Parameters setSize(Camera.Parameters parameters) {

        //Log.d("<<picture>>", "W:"+parameters.getPictureSize().width+"H:"+parameters.getPictureSize().height);   //지원하는 사진의 크기
        //Log.d("<<preview>>", "W:"+parameters.getPreviewSize().width+"H:"+parameters.getPreviewSize().height);   //지원하는 프리뷰의 크기

        int tempWidth = parameters.getPictureSize().width;      //사진 가로크기
        int tempHeight = parameters.getPictureSize().height;    //사진 세로크기
        int Result = 0;
        int Result2 = 0;
        int picSum = 0;
        int picSum2 = 0;
        int soin = 2;

        //사진 비율 구하기
        while(tempWidth >= soin && tempHeight >= soin){
            Result = tempWidth%soin;
            Result2 = tempHeight%soin;
            if(Result == 0 && Result2 == 0){
                picSum = tempWidth/soin;
                picSum2 = tempHeight/soin;
                //System.out.println("PictureWidth :"+tempWidth+"/"+soin+"결과:"+picSum+"나머지:"+Result);
                //System.out.println("PictureHeight :"+tempHeight+"/"+soin+"결과:"+picSum2+"나머지:"+Result2);
                tempWidth = picSum;
                tempHeight = picSum2;
            }else {
                soin++;
            }

        }
        //System.out.println("사진 비율 "+picSum+":"+picSum2);

        //카메라 사진 비율구하기
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();      //지원하는 모든 프리뷰 사이즈 얻기
        for (Camera.Size size : previewSizeList){
            tempWidth = size.width;
            tempHeight = size.height;
            Result = 0;
            Result2 = 0;
            int preSum = 0;
            int preSum2 = 0;
            soin = 2;

            while(tempWidth >= soin && tempHeight >= soin){
                Result = tempWidth%soin;
                Result2 = tempHeight%soin;
                if(Result == 0 && Result2 == 0){
                    preSum = tempWidth/soin;
                    preSum2 = tempHeight/soin;
                    //System.out.println("PreviewWidth :"+tempWidth+"/"+soin+"결과:"+preSum+"나머지:"+Result);
                    //System.out.println("PreviewHeight :"+tempHeight+"/"+soin+"결과:"+preSum2+"나머지:"+Result2);
                    tempWidth = preSum;
                    tempHeight = preSum2;
                }else {
                    soin++;
                }

            }
            //System.out.println("카메라 비율 "+preSum+":"+preSum2);

            if(picSum == preSum && picSum2 == preSum2){
                parameters.setPreviewSize(size.width, size.height);
                //System.out.println("설정된 비율 "+preSum+":"+preSum2);
                return parameters;
            }
        }

        //비율이 같지 않을때 알림
        //System.out.println("지원하는 사진 크기와 프리뷰의 비율이 없음");

        return parameters;
    }

}
