package com.example.emsys.fishflow;

import android.graphics.Bitmap;

import java.sql.Time;
import java.util.List;

//결과 클래스
public class ResultData {
    private ImageData image;
    private List<Fish> cropImages;

    public ResultData(ImageData image, List<Fish> cropImages) {
        this.image = image;
        this.cropImages = cropImages;
    }

    public ResultData(int id, Bitmap data, List<Fish> cropImages) {
        this.image = new ImageData(id,data);
        this.cropImages = cropImages;
    }

    public ImageData getImage() {
        return image;
    }

    public void setImage(ImageData image) {
        this.image = image;
    }

    public List<Fish> getCropImages() {
        return cropImages;
    }

    public void setCropImages(List<Fish> cropImages) {
        this.cropImages = cropImages;
    }
}

//원본 이미지 클래스
class ImageData{
    private int id;          //원본 이미지 id
    private Bitmap data;     //원본 이미지
    private int width;       //원본 이미지 넓이
    private int height;      //원본 이미지 높이

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


//fish 테이블과 crop_iamge 테이블 자연조인
//slect * from fish, crop_image where crop_image.fish_code=fish.fish_code;
class Fish{
    //크롭 정보(사각형)
    private int id;                  //크롭 사진을 구별하는 식별자
    private int pid;                 //원본사진 id
    private double startX;          //시작 X좌표 비율
    private double startY;          //사작 Y좌표 비율
    private double endX;            //끝 X좌표 비율
    private double endY;            //끝 Y좌표 비율
    private boolean isError;        //오류여부(false=정상 true=비정상)

    //생선정보
    private int fishCode;           //생선 코드
    private String species;          //어종
    private String origin;           //원산지
    private String info;             //어종 설명, 정보

    public Fish(int id, int pid, double startX, double startY, double endX, double endY, boolean isError, int fishCode, String species, String origin, String info) {
        this.id = id;
        this.pid = pid;
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

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public int getFishCode() {
        return fishCode;
    }

    public void setFishCode(int fishCode) {
        this.fishCode = fishCode;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Fish{" +
                "id=" + id +
                ", pid=" + pid +
                ", startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                ", isError=" + isError +
                ", fishCode=" + fishCode +
                ", species='" + species + '\'' +
                ", origin='" + origin + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}