package com.example.emsys.fishflow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class Splash extends Activity{
    @Override
    protected  void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

        finish();
    }
}