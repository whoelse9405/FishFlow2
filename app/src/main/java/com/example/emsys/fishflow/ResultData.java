package com.example.emsys.fishflow;

import android.graphics.Bitmap;

import java.sql.Time;
import java.util.List;

//결과 클래스
public class ResultData {
    public ImageData image;
    public List<Fish> cropImages;

    public ResultData(ImageData image, List<Fish> cropImages) {
        this.image = image;
        this.cropImages = cropImages;
    }

    public ResultData(int id, Bitmap data, List<Fish> cropImages) {
        this.image = new ImageData(id,data);
        this.cropImages = cropImages;
    }
}

//원본 이미지 클래스
class ImageData{
    public int id;          //원본 이미지 id
    public Bitmap data;     //원본 이미지
    public int width;       //원본 이미지 넓이
    public int height;      //원본 이미지 높이

    ImageData(int id, Bitmap data, int width,int height){
        this.id=id;
        this.data=data;
        this.width=width;
        this.height=height;
    }
    ImageData(int id, Bitmap data){
        this.id=id;
        this.data=data;
        this.width=data.getWidth();
        this.height=data.getHeight();
    }

    public int getId() {
        return id;
    }
    public Bitmap getData() {
        return data;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
}


//fish 테이블과 crop_iamge테이블 자연조인
//slect * from fish, crop_image where crop_image.fish_code=fish.fish_code;
class Fish{
    //크롭 정보(사각형)
    public int id;               //크롭 사진을 구별하는 식별자
    public float startX;          //시작 X좌표 비율
    public float startY;          //사작 Y좌표 비율
    public float endX;            //끝 X좌표 비율
    public float endY;            //끝 Y좌표 비율

    public boolean isError;    //오류여부(false=정상 true=비정상)

    //생선정보
    public int fishCode;       //생선 코드
    public String species;      //어종
    public String origin;       //원산지
    public String info;         //어종 설명, 정보

    public Fish(int id, float startX, float startY, float endX, float endY, boolean isError, int fishCode, String species, String origin, String info) {
        this.id = id;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.isError = isError;
        this.fishCode = fishCode;
        this.species = species;
        this.origin = origin;
        this.info = info;
    }

    public int getId() {
        return id;
    }
    public float getStartX() {
        return startX;
    }
    public float getStartY() {
        return startY;
    }
    public float getEndX() {
        return endX;
    }
    public float getEndY() {
        return endX;
    }
    public boolean isError() {
        return isError;
    }
    public int getFishCode() {
        return fishCode;
    }
    public String getSpecies() {
        return species;
    }
    public String getOrigin() {
        return origin;
    }
    public String getInfo() {
        return info;
    }
}