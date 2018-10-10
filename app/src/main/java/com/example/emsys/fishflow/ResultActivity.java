package com.example.emsys.fishflow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    private ImageButton helpButton;
    private ImageButton backButton;
    private Button reportButton;
    private ImageView imageView;

    private String filePath;

    byte[] byteImage;
    @Override
    protected  void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //이미지뷰 세팅
        Intent intent = getIntent();
        byte[] byteImage = intent.getByteArrayExtra("Image");
        if(byteImage.length>0){
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(byteImage,0,byteImage.length);
            imageView = (ImageView)findViewById(R.id.totalImageView);
            //이미지뷰 최대 크기 조정
            int height = intent.getIntExtra("ImageHeight",0);
            int width = intent.getIntExtra("ImageWidth",0);
            imageView.setMaxWidth(height);
            imageView.setMaxHeight(width);
            imageView.setImageBitmap(bitmapImage);


        }


        helpButton = (ImageButton)findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            }
        });

        backButton=(ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        reportButton=(Button)findViewById(R.id.reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ReportActivity.class);
                startActivity(intent);
            }
        });

    }
}
