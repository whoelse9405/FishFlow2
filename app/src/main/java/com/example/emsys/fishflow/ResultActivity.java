package com.example.emsys.fishflow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    int imageViewWidth,imageViewHeight;     //이미지뷰 크기

    private FrameLayout resultLayout;           //결과화면 레이아웃
    private ImageButton helpButton;             //도움말 버튼
    private ImageButton backButton;             //뒤로가기 버튼
    private Button reportButton;                //신고 버튼
    private ImageView imageView;                //결과 화면
    private TextView species;
    private TextView origin;
    private TextView fishInfoText;

    //private List<ResultSelectButton> resultSelectButton;
    //private ResultData resultData;
    private ResultSelectButton currentSelectButton=null;      //현재 선택된 버튼
    List<Fish>cropImages = new ArrayList<Fish>();
    int imageId;

    @Override
    protected  void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //결과 레이아웃
        resultLayout=(FrameLayout)findViewById(R.id.result_layout);

        //이미지뷰 세팅
        Intent intent = getIntent();
        String ImagePath = intent.getStringExtra("ImagePath");
        Bitmap bitmap=null;
        if(ImagePath!=null){
            bitmap = BitmapFactory.decodeFile(ImagePath);
            imageView = (ImageView)findViewById(R.id.totalImageView);
            imageView.setImageBitmap(bitmap);
        }


        //////결과 세팅
        //서버에서 결과데이터 받기
        imageId=1;
        //List<Fish>cropImages = new ArrayList<Fish>();
        cropImages.add(new Fish(1,0.0,0.0,50,50,false,1,"고등어","국산","국산 고등어입니다."));
        cropImages.add(new Fish(2,0,50,50,100,false,2,"고등어","노르웨이산","노르웨이산 고등어입니다."));
        cropImages.add(new Fish(3,50,0.0,100,50,false,3,"고등어","중국산","중국산 고등어입니다."));
        cropImages.add(new Fish(4,50,50,100,100,false,3,"고등어","중국산","중국산 고등어입니다."));
        cropImages.add(new Fish(5,25,25,75,75,false,3,"고등어","중국산","중국산 고등어입니다."));
        //resultData=new ResultData(imageId,bitmap,cropImages);




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
                intent.putExtra("imageId",imageId);
                intent.putExtra("cropId",currentSelectButton.getFish().getId());
                startActivity(intent);
            }
        });

    }

    //이미지뷰 크기 구하기 버튼 생성 및 설정
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        imageViewWidth = imageView.getWidth();
        imageViewHeight = imageView.getHeight();

        for(Fish fish: cropImages){
            ResultSelectButton button = new ResultSelectButton(this, fish);
            int startX = (int)(fish.startX*imageViewWidth/100);
            int startY = (int)(fish.startY*imageViewHeight/100);
            int width = (int)((fish.endX-fish.startX)*imageViewWidth/100);
            int height = (int)((fish.endX-fish.startX)*imageViewHeight/100);



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
                        if(currentSelectButton!=null)
                            currentSelectButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.result_no_selected_button));
                        v.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.result_selected_button));
                    } else {
                        if(currentSelectButton!=null)
                            currentSelectButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.result_no_selected_button));
                        v.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.result_selected_button));
                    }
                    //현재 선택된 버튼 업데이트
                    currentSelectButton=(ResultSelectButton)v;

                    species.setText(currentSelectButton.getFish().getSpecies());
                    origin.setText(currentSelectButton.getFish().getOrigin());
                    fishInfoText.setText(currentSelectButton.getFish().getInfo());
                }
            });
            resultLayout.addView(button);

            //위치(마진) 설정
            FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) button.getLayoutParams();
            mLayoutParams.setMargins(startX,startY,0,0);
            mLayoutParams.height=height;
            mLayoutParams.width=width;
            mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            button.setLayoutParams(mLayoutParams);
        }

    }
}
