package com.zyta.zflikz;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.zyta.zflikz.model.Backdrop;
import com.zyta.zflikz.model.ImageDetails;
import com.zyta.zflikz.model.ImagesAdapter;
import com.zyta.zflikz.model.Poster;
import com.zyta.zflikz.utils.MovieAPI;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.zabelnikov.swipelayout.layout.listener.OnLayoutSwipedListener;

public class ImagesActivity extends AppCompatActivity implements OnLayoutSwipedListener {
    int movieId = 0;
    String imageType = null;
    private ArrayList<Backdrop> backdropArrayList = new ArrayList<>();
    private ArrayList<String> backdropPathArrayList = new ArrayList<>();
    private ArrayList<Poster> posterArrayList = new ArrayList<>();
    private ArrayList<String> posterPathArrayList = new ArrayList<>();
    ImagesAdapter imagesAdapter;
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        viewPager = findViewById(R.id.view_pager);

        imageType = getIntent().getStringExtra("image_type");
        movieId = getIntent().getIntExtra("movie_id", 0);

        getImages();

//
//        swipeableLayout.setOnLayoutPercentageChangeListener(new OnLayoutPercentageChangeListener() {
//            private float lastAlpha = 1.0f;
//
//            @Override
//            public void percentageY(float percentage) {
//                float alphaCorrector = percentage / 3;
//                AlphaAnimation alphaAnimation = new AlphaAnimation(lastAlpha, 1 - alphaCorrector);
//                alphaAnimation.setDuration(300);
//                colorFrame.startAnimation(alphaAnimation);
//                lastAlpha = 1 - alphaCorrector;
//            }
//        });
//        swipeableLayout.setOnSwipedListener(new OnLayoutSwipedListener() {
//            @Override
//            public void onLayoutSwiped() {
//                ImagesActivity.this.finish();
//            }
//        });
//
//        swipeableLayout.setLayoutShiftListener(new LayoutShiftListener() {
//            @Override
//            public void onLayoutShifted(float positionX, float positionY, boolean isTouched) {
//                Log.e("Swipelayout", "isTouched = " + isTouched);
//            }
//        });

    }


    private void getImages() {

        final Call<ImageDetails> imageDetails = MovieAPI.getService().getImageDetails(movieId, BuildConfig.TMDB_KEY);
        imageDetails.enqueue(new Callback<ImageDetails>() {
            @Override
            public void onResponse(Call<ImageDetails> call, Response<ImageDetails> response) {
                ImageDetails imagesObject = response.body();
                Log.e("get Image details", "onResponse: " + imagesObject.getId());
                backdropArrayList.addAll(imagesObject.getBackdrops());
                posterArrayList.addAll(imagesObject.getPosters());

                for (Poster poster : posterArrayList) {
                    posterPathArrayList.add(poster.getFilePath());
                }
                for (Backdrop backdrop : backdropArrayList) {
                    backdropPathArrayList.add(backdrop.getFilePath());
                }

                if (imageType.equals("poster")) {
                    imagesAdapter = new ImagesAdapter(posterPathArrayList, getApplicationContext());
                    imagesAdapter.setOnLayoutSwipedListener(ImagesActivity.this);
                    viewPager.setAdapter(imagesAdapter);
                    imagesAdapter.notifyDataSetChanged();
                }else if(imageType.equals("backdrop")){
                    imagesAdapter = new ImagesAdapter(backdropPathArrayList, getApplicationContext());
                    imagesAdapter.setOnLayoutSwipedListener(ImagesActivity.this);
                    viewPager.setAdapter(imagesAdapter);
                    imagesAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<ImageDetails> call, Throwable t) {
                if (t instanceof IOException) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.images_activity_frame_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.images_activity_frame_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }
        });
    }

    @Override
    public void onLayoutSwiped() {
        finish();
    }
}
