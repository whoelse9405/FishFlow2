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
        Log.e("resultActivity","start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        Intent intent = getIntent();
        Toast.makeText(ResultActivity.this, " "+intent.getByteArrayExtra("cameraImage").length, Toast.LENGTH_SHORT).show();
        //filePath = intent.getStringExtra("FilePath");
        if(intent.getByteArrayExtra("cameraImage").length>0){
            byte[] byteImage = getIntent().getByteArrayExtra("cameraImage");
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(byteImage,0,byteImage.length);

            imageView = (ImageView)findViewById(R.id.imageView);
            imageView.setMaxWidth(1920);
            imageView.setMaxHeight(1080);
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
