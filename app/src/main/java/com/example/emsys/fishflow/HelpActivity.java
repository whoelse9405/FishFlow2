package com.example.emsys.fishflow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class HelpActivity extends AppCompatActivity {

    private static ImageButton exitButton;
    private static ImageView imageView;

    @Override
    protected  void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        imageView=(ImageView)findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.splash);

        exitButton=(ImageButton)findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
