package com.example.emsys.fishflow;


import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class ResultSelectButton extends AppCompatButton {
    Fish fish;

    public ResultSelectButton(Context context, Fish fish) {
        // 소스상에서 생성할 때 쓰인다.
        super(context);
        this.fish = fish;
    }

    public ResultSelectButton(Context context, AttributeSet attrs, Fish fish) {
        // xml 을 통해 생성할 때 attribute 들이 attrs 로 넘어온다
        // 대부분 defStyle 이 있는 3번째 constructor 를 통한다.
        super(context, attrs);
        this.fish = fish;
    }

    public ResultSelectButton(Context context, AttributeSet attrs, int defStyleAttr, Fish fish) {
        // xml 을 통해 생성하면서 style 도 함께 적용되어 있다면 타게 되는 constructor. defStyle = 0 이면 no style 을 의미한다.
        super(context, attrs, defStyleAttr);
        this.fish = fish;
    }


    //View의 크기를 결정할 때 불리는 함수
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /*int widthMode = MeasureSpec.getMode( widthMeasureSpec );
        int width = 0;
        if ( widthMode == MeasureSpec.UNSPECIFIED )
            width = widthMeasureSpec;
        else if ( widthMode == MeasureSpec.AT_MOST )
            width = 100;
        else if ( widthMode == MeasureSpec.EXACTLY )
            width = MeasureSpec.getSize( widthMeasureSpec );

        int heightMode = MeasureSpec.getMode( heightMeasureSpec );
        int height = 0;
        if ( heightMode == MeasureSpec.UNSPECIFIED )
            height = heightMeasureSpec;
        else if ( heightMode == MeasureSpec.AT_MOST )
            height = 100;
        else if ( heightMode == MeasureSpec.EXACTLY )
            height = MeasureSpec.getSize( heightMeasureSpec );

        setMeasuredDimension( width, height );*/


    }

    //뷰의 좌표계가 정해진 단계로 좌표계를 바탕으로 뷰의 내용물을 배치하는 곳
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    //뷰의 좌표계가 정해진 단계로 좌표계를 바탕으로 뷰의 내용을 그리는 곳
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        super.onTouchEvent(event);

        return true;
    }

    public void setFish(Fish fish) {
        this.fish = fish;
    }
    public Fish getFish() {
        return fish;
    }
}
