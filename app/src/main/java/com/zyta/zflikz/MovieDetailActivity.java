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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyta.zflikz.model.Cast;
import com.zyta.zflikz.model.Credits;
import com.zyta.zflikz.model.CreditsAdapter;
import com.zyta.zflikz.model.Crew;
import com.zyta.zflikz.model.Genre;
import com.zyta.zflikz.model.MovieDetails;
import com.zyta.zflikz.model.ProductionAdapter;
import com.zyta.zflikz.model.ProductionCompany;
import com.zyta.zflikz.model.ProductionCountry;
import com.zyta.zflikz.model.SpokenLanguage;
import com.zyta.zflikz.utils.MovieAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieDetailActivity extends AppCompatActivity {
    private SQLiteDatabase mMovieDb;
    Cursor favoriteMovies;
    String isFavorite = "N";
    private Integer movieId = null;
    private Boolean video = null;
    private Double voteAverage = 0.0;
    private String title = null;
    private Double popularity = 0.0;
    private String posterPath = null;
    private String originalLanguage = null;
    private String originalTitle = null;
    private List<Integer> genreIds = null;
    private String backdropPath = null;
    private Boolean adult = null;
    private String overview = null;
    private String releaseDate = null;
    private Integer voteCount = null;
    private ArrayList<Crew> crewList = new ArrayList<>();
    private ArrayList<Cast> castList = new ArrayList<>();
    private ArrayList<ProductionCompany> productionCompanyList = new ArrayList<>();
    private ArrayList<ProductionCountry> productionCountryList = new ArrayList<>();
    private ArrayList<Genre> genreList = new ArrayList<>();
    private ArrayList<SpokenLanguage> spokenLanguagesList = new ArrayList<>();
    CreditsAdapter creditsAdapter;
    ProductionAdapter productionAdapter;
    //    MovieDetails movieDetails = new MovieDetails();
    ImageView posterImageView;
    ImageView backDropImageView;
    TextView movieNameTextView;
    TextView overviewTextView, ratingTextView, releaseDateTextView;
    CardView videoCardView, reviewCardView;
    ImageView recImageView;
    RecyclerView crewRecyclerView,prodrecyclerView;
    private static final String EXTRA_IMAGE = "com.antonioleiva.materializeyourapp.extraImage";
// FIXME: 03/07/18 set the linear layout to gone
    CardView productiCardView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Button converseButton;
        final FloatingActionButton favoriteFAB;
        final NestedScrollView scrollView;
        final FrameLayout frameLayout;
        final CoordinatorLayout linearLayout;


//        castList = new ArrayList<>();

        movieId = getIntent().getIntExtra("id", 0);
        System.out.println("movieId ---------------- " + movieId);

        creditsAdapter = new CreditsAdapter(this, castList);
        productionAdapter = new ProductionAdapter(this, productionCompanyList);

        recImageView = findViewById(R.id.back_temp);


        crewRecyclerView = findViewById(R.id.cast_recycler_view);
        prodrecyclerView = findViewById(R.id.production_recycler_view);
        productiCardView = findViewById(R.id.production_card_view);
        crewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        prodrecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        crewRecyclerView.setAdapter(creditsAdapter);
        prodrecyclerView.setAdapter(productionAdapter);
//        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);
        getAllMovieDetails();

        posterImageView = (ImageView) findViewById(R.id.poster);
        movieNameTextView = (TextView) findViewById(R.id.movie_name);
        overviewTextView = (TextView) findViewById(R.id.overview_label);
        ratingTextView = (TextView) findViewById(R.id.rating_label);
        releaseDateTextView = (TextView) findViewById(R.id.release_date_label);
        backDropImageView = (ImageView) findViewById(R.id.back_drop_image);
        favoriteFAB = (FloatingActionButton) findViewById(R.id.favorite_fab);
//        videoCardView = findViewById(R.id.video_card_view);
//        reviewCardView = findViewById(R.id.review_card_view);
        linearLayout = findViewById(R.id.complete_layout);

        converseButton = findViewById(R.id.con_button);


        backdropPath = getIntent().getStringExtra("backdrop_url");
        posterPath = getIntent().getStringExtra("poster_url");
        overview = getIntent().getStringExtra("overview");
        title = getIntent().getStringExtra("title");
        releaseDate = "Release Date : " + getIntent().getStringExtra("release_date");
        voteAverage = (getIntent().getExtras().getDouble("vote_average"));
        backdropPath = getIntent().getStringExtra("backdrop_url");


        movieNameTextView.setText(title);
        overviewTextView.setText(overview);
        releaseDateTextView.setText(releaseDate);
        ratingTextView.setText(String.valueOf(voteAverage));

        if (posterPath != null) {
            GlideApp.with(getApplicationContext()).load(posterPath).placeholder(R.drawable.zlikx_logo).into(posterImageView);
            GlideApp.with(getApplicationContext()).load((backdropPath == null) ? posterPath : backdropPath).placeholder(R.drawable.zlikx_logo).into(backDropImageView);
            GlideApp.with(getApplicationContext()).load((backdropPath == null) ? posterPath : backdropPath).placeholder(R.drawable.zlikx_logo).transform(new BlurTransformation(getApplicationContext())).into(recImageView);
        } else {
            GlideApp.with(getApplicationContext()).load(R.drawable.no_image_available).placeholder(R.drawable.zlikx_logo).into(posterImageView);
            GlideApp.with(getApplicationContext()).load((backdropPath == null) ? R.drawable.no_image_available : backdropPath).placeholder(R.drawable.zlikx_logo).into(backDropImageView);
            GlideApp.with(getApplicationContext()).load((backdropPath == null) ? R.drawable.no_image_available : backdropPath).placeholder(R.drawable.zlikx_logo).transform(new BlurTransformation(getApplicationContext())).into(recImageView);
        }
        System.out.println("titele here is : " + title);


        for (ProductionCompany companies : productionCompanyList) {
            System.out.println("Company in main are " + companies.getName());
        }
        getCredits();



        creditsAdapter.notifyDataSetChanged();

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




    private void getCredits() {

        System.out.println("Movie Id  : " + movieId);

        final Call<Credits> credits = MovieAPI.getService().getCredits(movieId, BuildConfig.TMDB_KEY);
        credits.enqueue(new Callback<Credits>() {
            @Override
            public void onResponse(Call<Credits> call, Response<Credits> response) {
                Credits credits = response.body();
                Log.e("test debug ", "onResponse: " + credits.getId());
                castList.addAll(credits.getCast());
                crewList.addAll(credits.getCrew());
                creditsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Credits> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void getAllMovieDetails() {
        System.out.println("getMovieDetails Movie Id  : " + movieId);

        final Call<MovieDetails> movieDetailsCall = MovieAPI.getService().getMovieDetails(movieId, BuildConfig.TMDB_KEY);
        movieDetailsCall.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                MovieDetails movieDetails = response.body();
                productionCompanyList.addAll(movieDetails.getProductionCompanies());
                productionAdapter.notifyDataSetChanged();
                productionCountryList.addAll(movieDetails.getProductionCountries());
                genreList.addAll(movieDetails.getGenres());
                spokenLanguagesList.addAll(movieDetails.getSpokenLanguages());

                Log.e("Movie Id received is :", "onResponse: "+ response.body().getId()+ "  Title : "+ response.body().getTitle());

                if (response.body().getProductionCompanies().isEmpty()) {
                    productiCardView.setVisibility(View.GONE);
                }

                for (int i = 0; i < productionCompanyList.size(); i++) {
                    System.out.println("path is :   http://image.tmdb.org/t/p/w185"  +productionCompanyList.get(i).getLogoPath());
                }

                for (ProductionCompany company: productionCompanyList) {
                    System.out.println("Production Company : "+company.getName() );
                }

                System.out.println("movieDetails : " + movieDetails.toString());
                overview = movieDetails.getOverview();
                releaseDate = movieDetails.getReleaseDate();
                voteAverage = movieDetails.getVoteAverage();

            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                System.out.println("Failure is : " + t.getMessage());
            }

        });

    }
}
