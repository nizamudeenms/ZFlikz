package com.zyta.zflikz;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class MovieDetailActivity extends AppCompatActivity {
    private SQLiteDatabase mMovieDb;
    Cursor favoriteMovies;
    String isFavorite = "N";
    private Integer movieId = null;
    private Boolean video = null;
    private Double voteAverage = null;
    private String title = null;
    private Double popularity = null;
    private String posterPath = null;
    private String originalLanguage = null;
    private String originalTitle = null;
    private List<Integer> genreIds = null;
    private String backdropPath = null;
    private Boolean adult = null;
    private String overview = null;
    private String releaseDate = null;
    private Integer voteCount = null;

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
        Button converseButton;
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

        converseButton = findViewById(R.id.con_button);

        movieId = getIntent().getIntExtra("id", 0);
        System.out.println("movieId ---------------- " + movieId);

        backdropPath = getIntent().getStringExtra("backdrop_url");
        posterPath = getIntent().getStringExtra("poster_url");
        overview = getIntent().getStringExtra("overview");
        title = getIntent().getStringExtra("title");
        releaseDate = "Release Date : " + getIntent().getStringExtra("release_date");
        voteAverage = (getIntent().getExtras().getDouble("vote_average"));


        backDropImageView = (ImageView) findViewById(R.id.back_drop_image);
        backdropPath = getIntent().getStringExtra("backdrop_url");
        if (posterPath != null) {
            GlideApp.with(getApplicationContext()).load(posterPath).placeholder(R.drawable.zlikx_logo).into(posterImageView);
            GlideApp.with(getApplicationContext()).load((backdropPath == null) ? posterPath : backdropPath).placeholder(R.drawable.zlikx_logo).into(backDropImageView);
            GlideApp.with(getApplicationContext()).load((backdropPath == null) ? posterPath : backdropPath).placeholder(R.drawable.zlikx_logo).transform(new BlurTransformation(getApplicationContext())).into(recImageView);
        } else {
            GlideApp.with(getApplicationContext()).load(R.drawable.no_image_available).placeholder(R.drawable.zlikx_logo).into(posterImageView);
            GlideApp.with(getApplicationContext()).load((backdropPath == null) ? R.drawable.no_image_available : backdropPath).placeholder(R.drawable.zlikx_logo).into(backDropImageView);
            GlideApp.with(getApplicationContext()).load((backdropPath == null) ? R.drawable.no_image_available : backdropPath).placeholder(R.drawable.zlikx_logo).transform(new BlurTransformation(getApplicationContext())).into(recImageView);
        }
//        GlideApp.with(getApplicationContext()).load((backdropPath == null) ? posterPath : backdropPath).placeholder(R.mipmap.ic_launcher).transform(new BlurTransformation(getApplicationContext())).into(recImageView);
//        GlideApp.with(getApplicationContext()).load((backdropPath == null) ? posterPath : backdropPath).placeholder(R.mipmap.ic_launcher).transform(new BlurTransformation(getApplicationContext())).into(backDropImageView);

        movieNameTextView.setText(title);
        overviewTextView.setText(overview);
        releaseDateTextView.setText(releaseDate);
        ratingTextView.setText(voteAverage.toString());


        converseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailActivity.this, ConverseActivity.class);
                System.out.println("movieId ::::::::::: " + movieId);
                intent.putExtra("movieId", movieId);
                intent.putExtra("backDropImagePath", backdropPath);
                MovieDetailActivity.this.startActivity(intent);
            }
        });

    }
}
