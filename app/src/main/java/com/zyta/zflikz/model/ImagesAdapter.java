package com.zyta.zflikz.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.R;

import java.util.ArrayList;

import ua.zabelnikov.swipelayout.layout.frame.SwipeableLayout;
import ua.zabelnikov.swipelayout.layout.listener.OnLayoutPercentageChangeListener;
import ua.zabelnikov.swipelayout.layout.listener.OnLayoutSwipedListener;

public class ImagesAdapter extends PagerAdapter {

    public final ArrayList<String> items;
    private final Context mContext;
    private OnLayoutSwipedListener onLayoutSwipedListener;
    private String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500";
    private String posterPath = null;

    public ImagesAdapter(ArrayList<String> items, Context context) {
        this.items = items;
        this.mContext = context;
    }

    public void setOnLayoutSwipedListener(OnLayoutSwipedListener onLayoutSwipedListener) {
        this.onLayoutSwipedListener = onLayoutSwipedListener;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        String imageUrl = items.get(position);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.view_pager_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.fullscreen_image_view);

        posterPath = POSTER_BASE_URL + imageUrl;

        GlideApp.with(mContext).load(posterPath).placeholder(R.drawable.zlikx_logo).into(imageView);

        final View colorFrame = view.findViewById(R.id.color_container);
        SwipeableLayout swipeableLayout = (SwipeableLayout) view.findViewById(R.id.swipeableLayout);

        swipeableLayout.setOnLayoutPercentageChangeListener(new OnLayoutPercentageChangeListener() {
            private float lastAlpha = 1.0f;

            @Override
            public void percentageY(float percentage) {
                float alphaCorrector = percentage / 2;
                AlphaAnimation alphaAnimation = new AlphaAnimation(lastAlpha, 1 - alphaCorrector);
                alphaAnimation.setDuration(300);
                colorFrame.startAnimation(alphaAnimation);
                lastAlpha = 1 - alphaCorrector;
            }
        });
        swipeableLayout.setOnSwipedListener(new OnLayoutSwipedListener() {
            @Override
            public void onLayoutSwiped() {
                if (onLayoutSwipedListener != null) {
                    onLayoutSwipedListener.onLayoutSwiped();
                }
            }
        });


        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
