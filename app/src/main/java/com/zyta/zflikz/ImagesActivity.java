package com.zyta.zflikz;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
    private ArrayList<Backdrop> backdropArrayList = new ArrayList<>();
    private ArrayList<Poster> posterArrayList = new ArrayList<>();
    ImagesAdapter imagesAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        viewPager = findViewById(R.id.view_pager);


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

        System.out.println("Movie Id  : " + movieId);

        final Call<ImageDetails> imageDetails = MovieAPI.getService().getImageDetails(movieId, BuildConfig.TMDB_KEY);
        imageDetails.enqueue(new Callback<ImageDetails>() {
            @Override
            public void onResponse(Call<ImageDetails> call, Response<ImageDetails> response) {
                ImageDetails imagesObject = response.body();
                Log.e("get Image details", "onResponse: " + imagesObject.getId());
                backdropArrayList.addAll(imagesObject.getBackdrops());
                posterArrayList.addAll(imagesObject.getPosters());

                System.out.println("posterArrayList = " + posterArrayList.size());
//                for (Poster poster : posterArrayList) {
//                    System.out.println("poster = " + poster.getFilePath());
//                }
                System.out.println("backdropArrayList = " + backdropArrayList.size());

                imagesAdapter = new ImagesAdapter(posterArrayList, getApplicationContext());
                imagesAdapter.setOnLayoutSwipedListener(ImagesActivity.this);
                viewPager.setAdapter(imagesAdapter);
                imagesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<ImageDetails> call, Throwable t) {
                if (t instanceof IOException) {
                    System.out.println("Failure is : " + t.getMessage());
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.images_activity_frame_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                } else {
                    System.out.println("Failure is : " + t.getMessage());
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
