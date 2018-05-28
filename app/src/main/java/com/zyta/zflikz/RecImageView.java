package com.zyta.zflikz;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class RecImageView  extends ImageView {

    public RecImageView(Context context) {
        super(context);
    }

    public RecImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
//        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int width = getMeasuredWidth();
        setMeasuredDimension(width, height);
    }
}
