package com.example.emsys.fishflow;

import android.graphics.Bitmap;

import java.sql.Time;
import java.util.List;

//결과 클래스
public class ResultData {
    public ImageData image;
    public List<Fish> cropImages;

}

//원본 이미지 클래스
class ImageData{
    public int id;          //원본 이미지 id
    public Bitmap data;     //원본 이미지
    public int date;        //
}

class Fish{
    //생선정보
    public int fishCode;        //생선 코드
    public String specied;      //어종
    public String origin;       //원산지
    public String info;         //어종 설명, 정보

    //위치 정보(사각형)
    public int startX;          //시작 X좌표
    public int startY;          //사작 Y좌표
    public int width;           //가로 길이
    public int height;          //세로 길이
}