package com.example.emsys.fishflow;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private Camera mCamera;
    public List<Camera.Size> listPreviewSizes;
    private Camera.Size previewSize;
    private Context context;

    //생성자

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.context=context;
        mCamera = MainActivity.getCamera();
        if(mCamera==null){
            mCamera=Camera.open();
        }
        listPreviewSizes=mCamera.getParameters().getSupportedPreviewSizes();
    }

    //surfaceView생성시 호출
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder){
        try{
            //카메라 객체를 사용할수 있게 연결한다.
            if(mCamera==null){
                mCamera=Camera.open();
            }

            //카메라 설정
            Camera.Parameters parameters = mCamera.getParameters();

            //카메라의 회전이 가로/세로일때 화면을 설정한다.
            if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
                parameters.set("orientation","portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            }else{
                parameters.set("orientation","landscape");
                mCamera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }
            //카메라 미리보기를 시작한다.
            mCamera.startPreview();

            //자동 포커스 설정
            mCamera.autoFocus(new Camera.AutoFocusCallback(){
              public void onAutoFocus(boolean success,Camera camera){
                  if(success){

                  }
              }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //surfaceView의 크기가 바뀌면 호출
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder,int i,int w,int h){
        //카메라 화면을 회전 할 때의 처리
        if(surfaceHolder.getSurface()==null){
            //프리뷰가 존재하지 않을때
            return;
        }
        //프리뷰를 다시 설정한다.
        try{
            mCamera.stopPreview();

            Camera.Parameters parameters = mCamera.getParameters();

            //화면 전환시 사진 회전 속성을 맞추기 위해 설정한다.
            int rotation = MainActivity.getinstance.getWindowManager().getDefaultDisplay().getRotation();
            switch (rotation){
                case Surface.ROTATION_0:{
                    mCamera.setDisplayOrientation(90);
                    parameters.setRotation(90);
                    break;
                }
                case Surface.ROTATION_90:{
                    mCamera.setDisplayOrientation(0);
                    parameters.setRotation(0);
                    break;
                }
                case Surface.ROTATION_180:{
                    mCamera.setDisplayOrientation(270);
                    parameters.setRotation(270);
                    break;
                }
                case Surface.ROTATION_270:{
                    mCamera.setDisplayOrientation(180);
                    parameters.setRotation(180);
                    break;
                }
            }

            //변경된 화면 넓이를 설정한다.
            parameters.setPreviewSize(previewSize.width,previewSize.height);
            mCamera.setParameters(parameters);

            //새로 변경된 설정으로 프리뷰를 시작한다.
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //surfaceView 종료시 호출
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){
        if(mCamera != null){
            //카메라 미리보기를 종료한다.
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }
    }

    //화면이 회전할 떄 화면 사이즈를 구한다.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        final int width = resolveSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(),heightMeasureSpec);
        setMeasuredDimension(width,height);

        if(listPreviewSizes!=null){
            previewSize = getPreviewSize(listPreviewSizes,width,height);
        }
    }

    public Camera.Size getPreviewSize(List<Camera.Size>sizes,int w,int h) {
        final double ASPECT_TOLERANCE =0.1;
        double targetRatio = (double)h/w;

        if(sizes==null) return null;

        Camera.Size optimalSize=null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for(Camera.Size size : sizes){
            double ratio = (double)size.width/size.height;
            if(Math.abs(ratio-targetRatio)>ASPECT_TOLERANCE)
                continue;

            if(Math.abs(size.height-targetHeight)<minDiff){
                optimalSize=size;
                minDiff= Math.abs(size.height-targetHeight);
            }
        }

        if(optimalSize==null){
            minDiff=Double.MAX_VALUE;
            for(Camera.Size size : sizes){
                if(Math.abs(size.height-targetHeight)<minDiff){
                    optimalSize=size;
                    minDiff = Math.abs(size.height-targetHeight);
                }
            }
        }

        return optimalSize;
    }
}
