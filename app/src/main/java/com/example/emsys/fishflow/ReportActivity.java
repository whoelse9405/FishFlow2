package com.example.emsys.fishflow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportActivity extends Activity {

    private static final String TAG = ReportData.class.getSimpleName();

    private Button cencleButton;
    private Button sendButton;
    private EditText titleEditText;
    private EditText contentsEditText;
    private Spinner spinner;

    ReportData reportData=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_report);

        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //position : 인덱스 번호
                //parent.getItemAtPosition(position) : 값
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        titleEditText =(EditText)findViewById(R.id.titleEditText);
        contentsEditText = (EditText)findViewById(R.id.contentsEditText);

        cencleButton=(Button)findViewById(R.id.cencleButton);
        cencleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendButton=(Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                final int imageId = intent.getIntExtra("imageId",0);
                final int cropId = intent.getIntExtra("cropId",0);
                if(imageId==0 || cropId==0){
                    Toast.makeText(getApplicationContext(),"생선을 선택해 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }


                String title = titleEditText.getText().toString();
                String contents = contentsEditText.getText().toString();

                if(title.equals("")){
                    //제목안씀
                    Toast.makeText(getApplicationContext(),"제목을 작성해 주세요.",Toast.LENGTH_SHORT).show();
                }else if(contents.equals("")) {
                    //내용 안씀
                    Toast.makeText(getApplicationContext(), "본문 내용을 작성해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    String reportClass = spinner.getSelectedItem().toString();
                    if(reportClass.equals("인식 오류")){
                        reportData=new ReportData(cropId,reportClass,title,contents);
                    }else{
                        reportData=new ReportData(reportClass,title,contents);
                    }
                    String url = getString(R.string.server_url)+"postReport.php";
                    new postReportTask().execute(url);           //신고 데이터 서버로 전송
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    private class postReportTask extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            String strUrl = params[0];

            try{
                RequestBody formBody = new FormBody.Builder()
                        .add("cropId", Integer.toString(reportData.getCropId()))
                        .add("reportClass", reportData.getReportClass())
                        .add("title", reportData.getTitle())
                        .add("contents", reportData.getContents())
                        .add("model", Build.MODEL)                                                                     //기기 모델명
                        .add("releaseVersion", Build.VERSION.RELEASE)                                                //os 버전
                        .add("uuid", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))     //안드로이드 고유값
                        .build();
                Request request = new Request.Builder()
                        .url(strUrl)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();

                result = response.body().string();
            }catch (IOException e){
                e.printStackTrace();
                Log.e(TAG,e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("true")){
                Toast.makeText(getApplicationContext(),"신고접수를 완료하였습니다.",Toast.LENGTH_SHORT).show();
                Log.d(TAG,"result : "+s);
            }else {
                Toast.makeText(getApplicationContext(),"신고접수를 실패하였습니다.",Toast.LENGTH_SHORT).show();
                Log.d(TAG,"result fail : "+s);
            }
        }
    }
}
