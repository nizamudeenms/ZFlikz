package com.zyta.zflikz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;


public class MovieDetailActivity extends AppCompatActivity {
    private SQLiteDatabase mMovieDb;
    Cursor favoriteMovies;
    String isFavorite = "N";
    String movieId = null;
    String posterUrl = null;
    String overview = null;
    String backdropUrl = null;
    String title = null;
    String releaseDate = null;
    String rating = null;
    private static final String EXTRA_IMAGE = "com.antonioleiva.materializeyourapp.extraImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ImageView posterImageView;
        final ImageView backDropImageView;
        TextView movieNameTextView;
        TextView overviewTextView, ratingTextView, releaseDateTextView;
        CardView videoCardView, reviewCardView;
        final FloatingActionButton favoriteFAB;
        final NestedScrollView scrollView;
        final FrameLayout frameLayout;
        final CoordinatorLayout linearLayout;

        final ImageView recImageView = findViewById(R.id.back_temp);

//        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);


        posterImageView = (ImageView) findViewById(R.id.poster);
        movieNameTextView = (TextView) findViewById(R.id.movie_name);
        overviewTextView = (TextView) findViewById(R.id.overview_label);
        ratingTextView = (TextView) findViewById(R.id.rating_label);
        releaseDateTextView = (TextView) findViewById(R.id.release_date_label);
        favoriteFAB = (FloatingActionButton) findViewById(R.id.favorite_fab);
//        videoCardView = findViewById(R.id.video_card_view);
//        reviewCardView = findViewById(R.id.review_card_view);
        linearLayout = findViewById(R.id.complete_layout);

        movieId = getIntent().getStringExtra("id");
        backdropUrl = getIntent().getStringExtra("backdrop_url");
        posterUrl = getIntent().getStringExtra("poster_url");
        overview = getIntent().getStringExtra("overview");
        title = getIntent().getStringExtra("title");
        releaseDate = "Release Date : " + getIntent().getStringExtra("release_date");
        rating = "Rating : " + getIntent().getStringExtra("vote_average");


        backDropImageView = (ImageView) findViewById(R.id.back_drop_image);
        backdropUrl = getIntent().getStringExtra("backdrop_url");
        RequestOptions glideoptions = new RequestOptions().placeholder(R.mipmap.ic_launcher);
        GlideApp.with(getApplicationContext()).load(posterUrl).placeholder(R.mipmap.ic_launcher).into(posterImageView);
        GlideApp.with(getApplicationContext()).load(backdropUrl).placeholder(R.mipmap.ic_launcher).into(backDropImageView);
        GlideApp.with(getApplicationContext()).load(backdropUrl).placeholder(R.mipmap.ic_launcher).transform(new BlurTransformation(getApplicationContext())).into(recImageView);

        movieNameTextView.setText(title);
        overviewTextView.setText(overview);
        releaseDateTextView.setText(releaseDate);
        ratingTextView.setText(rating);


    }
}
