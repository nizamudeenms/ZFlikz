package com.zyta.zflikz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.zyta.zflikz.model.Backdrop;
import com.zyta.zflikz.model.Cast;
import com.zyta.zflikz.model.Credits;
import com.zyta.zflikz.model.CreditsAdapter;
import com.zyta.zflikz.model.Crew;
import com.zyta.zflikz.model.Genre;
import com.zyta.zflikz.model.ImageDetails;
import com.zyta.zflikz.model.MovieDetails;
import com.zyta.zflikz.model.Poster;
import com.zyta.zflikz.model.ProductionAdapter;
import com.zyta.zflikz.model.ProductionCompany;
import com.zyta.zflikz.model.ProductionCountry;
import com.zyta.zflikz.model.Review;
import com.zyta.zflikz.model.ReviewAdapter;
import com.zyta.zflikz.model.ReviewDetails;
import com.zyta.zflikz.model.SimiliarMovie;
import com.zyta.zflikz.model.SimiliarMovieAdapter;
import com.zyta.zflikz.model.SimiliarMovieDetails;
import com.zyta.zflikz.model.SpokenLanguage;
import com.zyta.zflikz.model.VideoAdapter;
import com.zyta.zflikz.model.VideoDetails;
import com.zyta.zflikz.model.VideoList;
import com.zyta.zflikz.utils.MovieAPI;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieDetailActivity extends AppCompatActivity {
    private Integer movieId = null;
    private Double voteAverage = 0.0;
    private String title = null;
    private String posterPath = null;
    private String backdropPath = null;
    private String overview = null;
    private String releaseDate = null;
    private ArrayList<Crew> crewList = new ArrayList<>();
    private ArrayList<Cast> castList = new ArrayList<>();
    private ArrayList<ProductionCompany> productionCompanyList = new ArrayList<>();
    private ArrayList<ProductionCountry> productionCountryList = new ArrayList<>();
    private ArrayList<Genre> genreList = new ArrayList<>();
    private ArrayList<SpokenLanguage> spokenLanguagesList = new ArrayList<>();
    private ArrayList<VideoList> videosListArrayList = new ArrayList<>();
    private ArrayList<String> videoUrlsArrayList = new ArrayList<>();
    private ArrayList<Review> reviewsList = new ArrayList<>();
    private ArrayList<SimiliarMovie> similiarMoviesList = new ArrayList<>();

    ArrayList<Backdrop> backdropArrayList = new ArrayList<>();
    ArrayList<String> backdropPathArrayList = new ArrayList<>();

    ArrayList<Poster> posterArrayList = new ArrayList<>();
    ArrayList<String> posterPathArrayList = new ArrayList<>();

    String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500";
    String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w342";

    CreditsAdapter creditsAdapter;
    ProductionAdapter productionAdapter;
    VideoAdapter videoAdapter;
    ReviewAdapter reviewAdapter;
    SimiliarMovieAdapter similiarMovieAdapter;
    ImageView backDropImageView;
    TextView movieNameTextView;
    TextView overviewTextView, ratingTextView, releaseDateTextView, completeCastTextView;
    CardView ratingCardView, releaseDateCardView, overviewCardView, productionCardView, castCardView, videoCardView, reviewCardView, similiarMovieCardView;
    ImageView recImageView,detailShareButtonImageView;
    RecyclerView crewRecyclerView, prodrecyclerView, videosRecyclerView, reviewsRecylerView, similiarMovieRecyclerView;
    final String FIRST_VIDEO_URL = "<body style=\"margin:0 0 0 0; padding:0 0 0 0; \"><iframe   style=\"width: 100%; height: 100%;\" frameborder=\"0\" framespacing=\"0\" src=\"https://www.youtube.com/embed/";
    final String SECOND_VIDEO_URL = "\"  allowfullscreen ></iframe></body>";
    String FINAL_URL = null;

    private AdView mAdView;

    SimpleDraweeView posterSimpleDraweeView;
    ImageOverlayView imageOverlayView;
    private String TAG = MovieDetailActivity.class.getSimpleName();
    private String newPosterPath;
    private String newBackdropPath;

    interface CastItemClicked {
        void castClicked(Bundle b);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_movie_detail);


        Button converseButton;

        handleIntent();

        creditsAdapter = new CreditsAdapter(this, castList);
        productionAdapter = new ProductionAdapter(this, productionCompanyList);
        videoAdapter = new VideoAdapter(this, videoUrlsArrayList);
        reviewAdapter = new ReviewAdapter(this, reviewsList);
        similiarMovieAdapter = new SimiliarMovieAdapter(this, similiarMoviesList);

        recImageView = findViewById(R.id.back_temp);
        detailShareButtonImageView = findViewById(R.id.detail_button_share);

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
        similiarMovieRecyclerView = findViewById(R.id.similiar_movie_recycler_view);
        similiarMovieCardView = findViewById(R.id.similiar_movie_card_view);

        posterSimpleDraweeView = findViewById(R.id.poster_simple_drawee_view);
        imageOverlayView = new ImageOverlayView(this);


        crewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        prodrecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        videosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        reviewsRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        similiarMovieRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        crewRecyclerView.setAdapter(creditsAdapter);
        videosRecyclerView.setAdapter(videoAdapter);
        prodrecyclerView.setAdapter(productionAdapter);
        reviewsRecylerView.setAdapter(reviewAdapter);
        similiarMovieRecyclerView.setAdapter(similiarMovieAdapter);
        getAllMovieDetails();

        movieNameTextView = (TextView) findViewById(R.id.movie_name);
        overviewTextView = (TextView) findViewById(R.id.overview_label);
        ratingTextView = (TextView) findViewById(R.id.rating_label);
        releaseDateTextView = (TextView) findViewById(R.id.release_date_label);
        completeCastTextView = (TextView) findViewById(R.id.complete_cast_text_view);
        backDropImageView = (ImageView) findViewById(R.id.back_drop_image);
//        favoriteFAB = (FloatingActionButton) findViewById(R.id.favorite_fab);

        converseButton = findViewById(R.id.con_button);

        GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(getResources())
                .setFailureImage(R.drawable.zlikx_logo_bg_blur_grey)
                .setProgressBarImage(new ProgressBarDrawable());


        getCredits();
        getVideo();
        getReviews();
        getSimiliarMovies();
        getImages();


        creditsAdapter.notifyDataSetChanged();
        videoAdapter.notifyDataSetChanged();

        converseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailActivity.this, ConverseActivity.class);
                intent.putExtra("movieId", movieId);
                intent.putExtra("backDropImagePath", backdropPath);
                MovieDetailActivity.this.startActivity(intent);
            }
        });

        posterSimpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posterArrayList.size() != 0) {
                    new ImageViewer.Builder<>(MovieDetailActivity.this, posterPathArrayList)
                            .setStartPosition(0)
                            .setOverlayView(imageOverlayView)
                            .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                            .show();
                } else {
                    Snackbar posterImagesSnackBar = Snackbar.make(getWindow().getDecorView(), "Poster Unavailable", Snackbar.LENGTH_LONG);
                    posterImagesSnackBar.show();
                }
            }
        });

        backDropImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backdropArrayList.size() != 0) {
                    new ImageViewer.Builder<>(MovieDetailActivity.this, backdropPathArrayList)
                            .setStartPosition(0)
                            .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                            .show();
                } else {
                    Snackbar backDropImagesSnackBar = Snackbar.make(getWindow().getDecorView(), "Backdrop Unavailable", Snackbar.LENGTH_LONG);
                    backDropImagesSnackBar.show();
                }
            }
        });

        completeCastTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Bundle creditsBundle = new Bundle();
//                creditsBundle.putParcelableArrayList("crewList",crewList);


                if (crewList.size() == 0 && castList.size() == 0) {
                    Snackbar creditsSnackbar = Snackbar.make(getWindow().getDecorView(), "Full Credits Unavailable", Snackbar.LENGTH_LONG);
                    creditsSnackbar.show();
                } else {
                    Intent fullCreditsView = new Intent(getApplicationContext(), FullCreditsActivity.class);
                    fullCreditsView.putExtra("type", "movie");
                    fullCreditsView.putExtra("cast_list", castList);
                    fullCreditsView.putExtra("crew_list", crewList);
                    startActivity(fullCreditsView);
                }
            }
        });

        detailShareButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Zlikx");

                String shareText = "Hi, Checkout this movie in Zlikx. https://zflikz.page.link/zlkx"+"?"+movieId;
                System.out.println("shareText = " + shareText);
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(intent, "Share with"));


            }
        });

        reviewAdapter.notifyDataSetChanged();

        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
            MobileAds.initialize(this, "ca-app-pub-1865534838493345~1681246593");
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent() {
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkAction != null) {
            System.out.println("appLinkData.getLastPathSegment() = " + appLinkData.getQuery());
            movieId = Integer.parseInt(appLinkData.getQuery());
                
        } else {
            movieId = getIntent().getIntExtra("id", 0);
        }
    }

    private void getSimiliarMovies() {

        final Call<SimiliarMovieDetails> similiarMovieCall = MovieAPI.getService().getSimiliarMovies(movieId, BuildConfig.TMDB_KEY);
        similiarMovieCall.enqueue(new Callback<SimiliarMovieDetails>() {
            @Override
            public void onResponse(Call<SimiliarMovieDetails> call, Response<SimiliarMovieDetails> response) {
                SimiliarMovieDetails similiarMovieDetails = response.body();

                similiarMoviesList.addAll(similiarMovieDetails.getSimiliarMovies());
                similiarMovieAdapter.notifyDataSetChanged();

                if (similiarMovieDetails.getSimiliarMovies().size() == 0) {
                    similiarMovieCardView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<SimiliarMovieDetails> call, Throwable t) {
                if (t instanceof IOException) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }

        });

    }

    private void getReviews() {

        final Call<ReviewDetails> reviews = MovieAPI.getService().getReviews(movieId, BuildConfig.TMDB_KEY);
        reviews.enqueue(new Callback<ReviewDetails>() {
            @Override
            public void onResponse(Call<ReviewDetails> call, Response<ReviewDetails> response) {
                ReviewDetails reviewDetails = response.body();

                Log.e("Movie Id received is :", "Reviews onResponse: " + response.body().getId() + "  Title : " + reviewDetails.getReviews().size());

                reviewsList.addAll(reviewDetails.getReviews());
                reviewAdapter.notifyDataSetChanged();

                if (reviewDetails.getReviews().size() == 0) {
                    reviewCardView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ReviewDetails> call, Throwable t) {
                if (t instanceof IOException) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            //TODO CHECK LATER FOR BACK BUTTON FUNC WORKS PROPERLY
//            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getCredits() {

        final Call<Credits> credits = MovieAPI.getService().getCredits(movieId, BuildConfig.TMDB_KEY);
        credits.enqueue(new Callback<Credits>() {
            @Override
            public void onResponse(Call<Credits> call, Response<Credits> response) {
                Credits credits = response.body();
                Log.e("test debug ", "onResponse: " + credits.getId());
                castList.addAll(credits.getCast());
                crewList.addAll(credits.getCrew());


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
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }

        });
    }

    private void getAllMovieDetails() {

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
                    Log.d(TAG, "production compaypath is :  " + productionCompanyList.get(i).getLogoPath());
                }

                for (ProductionCompany company : productionCompanyList) {
                    Log.d(TAG, "onResponse: " + company.getName());
                }

                overview = movieDetails.getOverview();
                releaseDate = movieDetails.getReleaseDate();
                voteAverage = movieDetails.getVoteAverage();
                title = movieDetails.getTitle();
                posterPath = movieDetails.getPosterPath();
                backdropPath = movieDetails.getBackdropPath();

                movieNameTextView.setText(title);


                if (overview == null || overview.isEmpty()) {
                    overviewCardView.setVisibility(View.GONE);
                } else {
                    overviewTextView.setText(overview);
                }
                if (releaseDate.isEmpty() || releaseDate == null) {
                    releaseDateCardView.setVisibility(View.GONE);
                } else {
                    try {
                        SimpleDateFormat fromSystem = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMMM-yyyy");

                        String reformattedStr = myFormat.format(fromSystem.parse(releaseDate));
                        releaseDate = getString(R.string.release_date) + reformattedStr;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    releaseDateTextView.setText(releaseDate);
                }
                if (voteAverage == 0.0 || voteAverage == null) {
                    ratingCardView.setVisibility(View.GONE);
                } else {
                    ratingTextView.setText(String.valueOf(voteAverage));
                }


                //TODO Change this condition according to null condiotion
                if (posterPath != null) {
                    newPosterPath = BACKDROP_BASE_URL + posterPath;
                }

                if (backdropPath != null) {
                    newBackdropPath = BACKDROP_BASE_URL + backdropPath;
                }


                if (newPosterPath != null) {
                    GlideApp.with(getApplicationContext()).load(newPosterPath).placeholder(R.drawable.zlikx_logo).into(posterSimpleDraweeView);
                    GlideApp.with(getApplicationContext()).load((backdropPath == null) ? newPosterPath : newBackdropPath).placeholder(R.drawable.zlikx_logo).into(backDropImageView);
                    GlideApp.with(getApplicationContext()).load((backdropPath == null) ? newPosterPath : newBackdropPath).placeholder(R.drawable.zlikx_logo).transform(new BlurTransformation(getApplicationContext())).into(recImageView);
                } else {
                    GlideApp.with(getApplicationContext()).load(R.drawable.zlikx_logo_bg_blur).placeholder(R.drawable.zlikx_logo).into(posterSimpleDraweeView);
                    GlideApp.with(getApplicationContext()).load((backdropPath == null) ? R.drawable.zlikx_logo_bg_blur_grey : newBackdropPath).placeholder(R.drawable.zlikx_logo).into(backDropImageView);
                    GlideApp.with(getApplicationContext()).load((backdropPath == null) ? R.drawable.zlikx_logo_bg_blur_grey : newBackdropPath).placeholder(R.drawable.zlikx_logo).transform(new BlurTransformation(getApplicationContext())).into(recImageView);
                }


            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {

                if (t instanceof IOException) {
//                    Toast.makeText(MovieDetailActivity.this, "No network ", Toast.LENGTH_SHORT).show();
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }

        });

    }

    private void getVideo() {

        final Call<VideoDetails> videoDetailsCall = MovieAPI.getService().getVideoDetails(movieId, BuildConfig.TMDB_KEY);
        videoDetailsCall.enqueue(new Callback<VideoDetails>() {
            @Override
            public void onResponse(Call<VideoDetails> call, Response<VideoDetails> response) {
                VideoDetails videoDetailsObj = response.body();
                Log.e("test debug ", "onResponse: " + videoDetailsObj.getId());
                videosListArrayList.addAll(videoDetailsObj.getVideoList());


                for (VideoList videoList : videosListArrayList) {
                    FINAL_URL = FIRST_VIDEO_URL + videoList.getKey() + SECOND_VIDEO_URL;
                    videoUrlsArrayList.add(FINAL_URL);
                }

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
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.complete_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }
        });
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
                    posterPathArrayList.add(POSTER_BASE_URL + poster.getFilePath());
                }
                for (Backdrop backdrop : backdropArrayList) {
                    backdropPathArrayList.add(POSTER_BASE_URL + backdrop.getFilePath());
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

}


