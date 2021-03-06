package com.zyta.zflikz;


import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class ThreeByFourImageView  extends ImageView {

    public ThreeByFourImageView(Context context) {
        super(context);
    }

    public ThreeByFourImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeByFourImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
//        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int width = getMeasuredWidth();
        int tempHeight  = height /5;
        tempHeight = tempHeight * 4;
        setMeasuredDimension(width, tempHeight);
    }
}
