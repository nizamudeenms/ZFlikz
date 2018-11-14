package com.zyta.zflikz;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyta.zflikz.model.Cast;
import com.zyta.zflikz.model.Credits;
import com.zyta.zflikz.model.CreditsAdapter;
import com.zyta.zflikz.model.Crew;
import com.zyta.zflikz.model.Genre;
import com.zyta.zflikz.model.MovieDetails;
import com.zyta.zflikz.model.ProductionAdapter;
import com.zyta.zflikz.model.ProductionCompany;
import com.zyta.zflikz.model.ProductionCountry;
import com.zyta.zflikz.model.Review;
import com.zyta.zflikz.model.ReviewAdapter;
import com.zyta.zflikz.model.ReviewDetails;
import com.zyta.zflikz.model.SpokenLanguage;
import com.zyta.zflikz.model.VideoAdapter;
import com.zyta.zflikz.model.VideoDetails;
import com.zyta.zflikz.model.VideoList;
import com.zyta.zflikz.utils.MovieAPI;

import java.io.IOException;
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
    private ArrayList<VideoList> videosListArrayList = new ArrayList<>();
    private ArrayList<String> videoUrlsArrayList = new ArrayList<>();
    private ArrayList<Review> reviewsList = new ArrayList<>();

    CreditsAdapter creditsAdapter;
    ProductionAdapter productionAdapter;
    VideoAdapter videoAdapter;
    ReviewAdapter reviewAdapter;
    //    MovieDetails movieDetails = new MovieDetails();
    ImageView posterImageView;
    ImageView backDropImageView;
    TextView movieNameTextView;
    TextView overviewTextView, ratingTextView, releaseDateTextView, completeCastTextView;
    CardView ratingCardView, releaseDateCardView, overviewCardView, productionCardView, castCardView, videoCardView,reviewCardView;
    ImageView recImageView;
    RecyclerView crewRecyclerView, prodrecyclerView, videosRecyclerView, reviewsRecylerView;
    final String FIRST_VIDEO_URL = "<body style=\"margin:0 0 0 0; padding:0 0 0 0; \"><iframe   style=\"width: 100%; height: 100%;\" frameborder=\"0\" framespacing=\"0\" src=\"https://www.youtube.com/embed/";
    final String SECOND_VIDEO_URL = "\"  allowfullscreen ></iframe></body>";
    String FINAL_URL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Button converseButton;
        final FloatingActionButton favoriteFAB;
        final NestedScrollView scrollView;
        final FrameLayout frameLayout;
        final CoordinatorLayout linearLayout;


        movieId = getIntent().getIntExtra("id", 0);
        System.out.println("movieId ---------------- " + movieId);

        creditsAdapter = new CreditsAdapter(this, castList);
        productionAdapter = new ProductionAdapter(this, productionCompanyList);
        videoAdapter = new VideoAdapter(this, videoUrlsArrayList);
        reviewAdapter = new ReviewAdapter(this,reviewsList);

        recImageView = findViewById(R.id.back_temp);


        crewRecyclerView = findViewById(R.id.cast_recycler_view);
        prodrecyclerView = findViewById(R.id.production_recycler_view);
        videosRecyclerView = findViewById(R.id.videos_recycler_view);
        productionCardView = findViewById(R.id.production_card_view);
        castCardView = findViewById(R.id.cast_card_view);
        ratingCardView = findViewById(R.id.rating_card_view);
        overviewCardView = findViewById(R.id.overview_card_view);
        releaseDateCardView = findViewById(R.id.release_date_card_view);
        videoCardView = findViewById(R.id.video_card_view);
        reviewsRecylerView = findViewById(R.id.reviews_recycler_view);
        reviewCardView = findViewById(R.id.review_card_view);

        crewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        prodrecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        videosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        reviewsRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        crewRecyclerView.setAdapter(creditsAdapter);
        videosRecyclerView.setAdapter(videoAdapter);
        prodrecyclerView.setAdapter(productionAdapter);
        reviewsRecylerView.setAdapter(reviewAdapter);
//        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);
        getAllMovieDetails();

        posterImageView = (ImageView) findViewById(R.id.poster);
        movieNameTextView = (TextView) findViewById(R.id.movie_name);
        overviewTextView = (TextView) findViewById(R.id.overview_label);
        ratingTextView = (TextView) findViewById(R.id.rating_label);
        releaseDateTextView = (TextView) findViewById(R.id.release_date_label);
        completeCastTextView = (TextView) findViewById(R.id.complete_cast_text_view);
        backDropImageView = (ImageView) findViewById(R.id.back_drop_image);
        favoriteFAB = (FloatingActionButton) findViewById(R.id.favorite_fab);
//      videoCardView = findViewById(R.id.video_card_view);
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

        if (overview == null || overview.isEmpty()) {
            System.out.println("overview = " + overview);
            overviewCardView.setVisibility(View.GONE);
        } else {
            overviewTextView.setText(overview);
        }
        if (releaseDate.isEmpty() || releaseDate == null) {
            System.out.println("releaseDate = " + releaseDate);
            releaseDateCardView.setVisibility(View.GONE);
        } else {
            releaseDateTextView.setText(releaseDate);
        }
        if (voteAverage == 0.0 || voteAverage == null) {
            System.out.println("voteAverage = " + voteAverage);
            ratingCardView.setVisibility(View.GONE);
        } else {
            ratingTextView.setText(String.valueOf(voteAverage));
        }

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
        getVideo();
        getReviews();


        creditsAdapter.notifyDataSetChanged();
        videoAdapter.notifyDataSetChanged();
//        reviewAdapter.notifyDataSetChanged();

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

        posterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("posterPath = " + posterPath);
                if (posterPath != null) {
                    Intent view = new Intent(getApplicationContext(), ImagesActivity.class);
                    view.putExtra("image_type", "poster");
                    view.putExtra("movie_id", movieId);
                    startActivity(view);
                } else {
                    Snackbar mySnackbar = Snackbar.make(getWindow().getDecorView(), "Poster Unavailable", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }
        });

        backDropImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("backdropPath = " + backdropPath);
                if (backdropPath != null) {
                    Intent view = new Intent(getApplicationContext(), ImagesActivity.class);
                    view.putExtra("image_type", "backdrop");
                    view.putExtra("movie_id", movieId);
                    startActivity(view);
                } else {
                    Snackbar mySnackbar = Snackbar.make(getWindow().getDecorView(), "Image Unavailable", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }
        });

        completeCastTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("crewList = " + crewList.size());
                System.out.println("castList = " + castList.size());


                if (crewList.size() == 0 && castList.size() == 0) {
                    Snackbar creditsSnackbar = Snackbar.make(getWindow().getDecorView(), "Full Credits Unavailable", Snackbar.LENGTH_LONG);
                    creditsSnackbar.show();
                } else {
                    Intent fullCreditsView = new Intent(getApplicationContext(), FullCreditsActivity.class);
                    fullCreditsView.putExtra("cast_list", castList);
                    fullCreditsView.putExtra("crew_list", crewList);
                    startActivity(fullCreditsView);
                }
            }
        });
        reviewAdapter.notifyDataSetChanged();

    }

    private void getReviews() {

        System.out.println("Inside Get Reviews : Movie Id  : " + movieId);

        final Call<ReviewDetails> reviews = MovieAPI.getService().getReviews(movieId, BuildConfig.TMDB_KEY);
        reviews.enqueue(new Callback<ReviewDetails>() {
            @Override
            public void onResponse(Call<ReviewDetails> call, Response<ReviewDetails> response) {
                ReviewDetails reviewDetails = response.body();

                System.out.println("reviewDetails : "+ reviewDetails.getId());

                Log.e("Movie Id received is :", "Reviews onResponse: " + response.body().getId() + "  Title : " + reviewDetails.getReviews().size());

                reviewsList.addAll(reviewDetails.getReviews());
                reviewAdapter.notifyDataSetChanged();


                System.out.println("reviewsList.size() = " + reviewsList.size());

                if (reviewDetails.getReviews().size() == 0) {
                    reviewCardView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ReviewDetails> call, Throwable t) {
                if (t instanceof IOException) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    System.out.println("Failure is : " + t.getMessage());
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    System.out.println("Failure is : " + t.getMessage());
                }
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            System.out.println("back pressed and exited");
            finish();
        }
        return super.onKeyDown(keyCode, event);
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

                System.out.println("castList.size() = " + castList.size());

                if (response.body().getCast().isEmpty()) {
                    castCardView.setVisibility(View.GONE);
                }

                creditsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Credits> call, Throwable t) {
                if (t instanceof IOException) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    System.out.println("Failure is : " + t.getMessage());
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    System.out.println("Failure is : " + t.getMessage());
                }
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

                Log.e("Movie Id received is :", "onResponse: " + response.body().getId() + "  Title : " + response.body().getTitle());

                if (response.body().getProductionCompanies().isEmpty()) {
                    productionCardView.setVisibility(View.GONE);
                }

                for (int i = 0; i < productionCompanyList.size(); i++) {
                    System.out.println("path is :   http://image.tmdb.org/t/p/w185" + productionCompanyList.get(i).getLogoPath());
                }

                for (ProductionCompany company : productionCompanyList) {
                    System.out.println("Production Company : " + company.getName());
                }

                System.out.println("movieDetails : " + movieDetails.toString());
                overview = movieDetails.getOverview();
                releaseDate = movieDetails.getReleaseDate();
                voteAverage = movieDetails.getVoteAverage();

            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {

                if (t instanceof IOException) {
//                    Toast.makeText(MovieDetailActivity.this, "No network ", Toast.LENGTH_SHORT).show();
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    System.out.println("Failure is : " + t.getMessage());
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    System.out.println("Failure is : " + t.getMessage());


                }
            }

        });

    }

    private void getVideo() {

        System.out.println("get Video Movie Id  : " + movieId);

        final Call<VideoDetails> videoDetailsCall = MovieAPI.getService().getVideoDetails(movieId, BuildConfig.TMDB_KEY);
        videoDetailsCall.enqueue(new Callback<VideoDetails>() {
            @Override
            public void onResponse(Call<VideoDetails> call, Response<VideoDetails> response) {
                VideoDetails videoDetailsObj = response.body();
                Log.e("test debug ", "onResponse: " + videoDetailsObj.getId());
                videosListArrayList.addAll(videoDetailsObj.getVideoList());


                for (VideoList videoList : videosListArrayList) {
                    FINAL_URL = FIRST_VIDEO_URL + videoList.getKey() + SECOND_VIDEO_URL;
                    System.out.println("FINAL_URL = " + FINAL_URL);
                    videoUrlsArrayList.add(FINAL_URL);
                }
                System.out.println("videoUrlsArrayList.size() = " + videoUrlsArrayList.size());

                if (response.body().getVideoList().isEmpty()) {
                    videoCardView.setVisibility(View.GONE);
                }

                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<VideoDetails> call, Throwable t) {
                if (t instanceof IOException) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    System.out.println("Failure is : " + t.getMessage());
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    System.out.println("Failure is : " + t.getMessage());
                }
            }
        });
    }
}


