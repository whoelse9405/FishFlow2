package com.example.emsys.fishflow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class HelpActivity extends AppCompatActivity {

    ImageButton exitButton;

    @Override
    protected  void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(Color.parseColor("#ff7828"));
        }

        exitButton=(ImageButton)findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
