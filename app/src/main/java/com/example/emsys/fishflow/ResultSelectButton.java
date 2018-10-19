package com.example.emsys.fishflow;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ResultSelectButton extends Button {
    Fish fish;

    public ResultSelectButton(Context context) {
        super(context);
    }

    public ResultSelectButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResultSelectButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        width = Math.min(width, height);
        height = width;

        setMeasuredDimension(width, height);
    }

    public void setFish(Fish fish) {
        this.fish = fish;
    }
    public Fish getFish() {
        return fish;
    }
}
