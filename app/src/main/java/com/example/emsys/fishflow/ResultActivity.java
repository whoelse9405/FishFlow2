package com.example.emsys.fishflow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {

    int imageViewWidth,imageViewHeight;     //이미지뷰 크기

    private FrameLayout resultLayout;           //결과화면 레이아웃
    private ImageButton helpButton;             //도움말 버튼
    private ImageButton backButton;             //뒤로가기 버튼
    private Button reportButton;                //신고 버튼

    //결과 화면
    private ImageView imageView;
    private TextView species;
    private TextView origin;
    private TextView fishInfoText;

    private ResultData resultData;
    private ResultSelectButton currentSelectButton=null;      //현재 선택된 버튼


    @Override
    protected  void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //결과 레이아웃
        resultLayout=(FrameLayout)findViewById(R.id.result_layout);

        //이미지뷰 세팅
        Intent intent = getIntent();
        String ImagePath = intent.getStringExtra("ImagePath");
        int ImageId = intent.getIntExtra("ImageId",0);  ImageId=1;
        Bitmap bitmap=null;
        if(ImagePath!=null){
            bitmap = BitmapFactory.decodeFile(ImagePath);
            imageView = (ImageView)findViewById(R.id.totalImageView);
            imageView.setImageBitmap(bitmap);
        }

        //서버에서 결과데이터 받기
        resultData = new ResultData(ImageId,bitmap,null);
        String url = "http://emsys.gonetis.com/fishflow/getResult.php";
        new HttpAsyncTask().execute(url);


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
                intent.putExtra("imageId",resultData.getImage().getId());
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

        if(resultData.getCropImages()==null) return;

        for(Fish fish: resultData.getCropImages()){
            ResultSelectButton button = new ResultSelectButton(this, fish);
            int startX = (int)(fish.getStartX()*imageViewWidth/100);
            int startY = (int)(fish.getStartY()*imageViewHeight/100);
            int width = (int)((fish.getEndX()-fish.getStartX())*imageViewWidth/100);
            int height = (int)((fish.getEndX()-fish.getStartX())*imageViewHeight/100);



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

    public ResultData getResultData() {
        return resultData;
    }

    public void setResultData(ResultData resultData) {
        this.resultData = resultData;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            String strUrl = params[0];

            try{
                Request request = new Request.Builder()
                        .url(strUrl)
                        .build();
                Response response = client.newCall(request).execute();

                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Fish>>(){}.getType();
                result = response.body().string();
                List<Fish> fishList = gson.fromJson(result,listType);
                resultData.setCropImages(fishList);
            }catch (IOException e){
                e.printStackTrace();
                Log.e("HttpAsyncTask ",e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!=null){
                Log.d("HttpAsyncTask ","result : "+s);
            }else {
                Log.d("HttpAsyncTask ","result fail : "+s);
            }
        }
    }
}


