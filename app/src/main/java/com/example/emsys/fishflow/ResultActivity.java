package com.example.emsys.fishflow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private ImageButton helpButton;             //도움말 버튼
    private ImageButton backButton;             //뒤로가기 버튼
    private Button reportButton;                //신고 버튼
    private ImageView imageView;                //결과 화면
    private TextView species;
    private TextView origin;
    private TextView fishInfoText;

    private List<Button> resultSelectButton;
    private ResultData resultData;
    private Button currentSelectButton;      //현재 선택된 버튼
    private int currentSelectFish;           //현재 선택된 생선정보 인덱스

    int imageViewWidth,imageViewHeight;     //이미지뷰 크기


    @Override
    protected  void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //이미지뷰 세팅
        Intent intent = getIntent();
        String ImagePath = intent.getStringExtra("ImagePath");

        if(ImagePath!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(ImagePath);
            imageView = (ImageView)findViewById(R.id.totalImageView);
            imageView.setImageBitmap(bitmap);
        }


        //////결과 세팅
        //서버에서 결과데이터 받기
        int imageId=1;

        for(Fish fish: resultData.cropImages){
            Button button = new Button(this);
            int startX=(int)((float)fish.startX*imageViewWidth/100);
            int startY=(int)((float)fish.startY*imageViewHeight/100);
            int width = (int)((float)(fish.endX-fish.startX)*imageViewWidth/100);
            int height = (int)((float)(fish.endX-fish.startX)*imageViewHeight/100);

            //크기 설정
            button.setWidth(width);
            button.setHeight(height);

            //버튼 테마적용
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(ContextCompat.getDrawable(this, R.drawable.result_no_selected_button));
            } else {
                button.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.result_no_selected_button));
            }

            //클릭 이벤트 설정
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //버튼 색 변경
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        currentSelectButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.result_no_selected_button));
                        v.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.result_no_selected_button));
                    } else {
                        currentSelectButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.result_no_selected_button));
                        v.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.result_no_selected_button));
                    }
                    //현재 선택된 버튼 업데이트
                    currentSelectButton=(Button)v;
                    //currentSelectFish=v.get

                    //species.setText(fish.getSpecies());
                    //species.setText(fish.getSpecies());
                    //species.setText(fish.getSpecies());
                }
            });

            resultSelectButton.add(button);
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
            public void     onClick(View view) {
                finish();
            }
        });

        species=(TextView)findViewById(R.id.species);

        origin=(TextView)findViewById(R.id.origin);

        fishInfoText=(TextView)findViewById(R.id.fishInfoText);

        reportButton=(Button)findViewById(R.id.reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ReportActivity.class);
                startActivity(intent);
            }
        });

    }

    //이미지뷰 크기 구하기
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        imageViewWidth = imageView.getWidth();
        imageViewHeight = imageView.getHeight();
    }
}
