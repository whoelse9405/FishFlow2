package com.example.emsys.fishflow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class ReportActivity extends Activity {

    private Button cencleButton;
    private Button sendButton;
    private EditText titleEditText;
    private EditText contentsEditText;
    private Spinner spinner;

    private ReportData reportData;

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

                String title = titleEditText.getText().toString();
                String contents = contentsEditText.getText().toString();

                if(title.equals("")){
                    //제목안씀
                    Toast.makeText(getApplicationContext(),"제목을 작성해주세요.",Toast.LENGTH_SHORT).show();
                }else if(contents.equals("")) {
                    //내용 안씀
                    Toast.makeText(getApplicationContext(), "본문 내용을 작성해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    String reportClass = spinner.getSelectedItem().toString();
                    //int cropId;
                    Toast.makeText(getApplicationContext(), reportClass, Toast.LENGTH_SHORT).show();
                    reportData=new ReportData(1,reportClass,title,contents);
                }

                //데이터 서버로 보내기
                //Gson gson = new Gson();
                //String jsonData = gson.toJson()
                finish();
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

    /*
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
    */
}
