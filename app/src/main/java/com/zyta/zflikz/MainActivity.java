package com.zyta.zflikz;

import android.app.SearchManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zyta.zflikz.controller.MovieController;
import com.zyta.zflikz.model.Movie;
import com.zyta.zflikz.model.MovieAdapter;
import com.zyta.zflikz.model.MovieContract;
import com.zyta.zflikz.model.MovieDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private String mUsername;
    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());
    ImageView profileImage;
    TextView profileName, profileEmail;
    private String TAG = MainActivity.class.getSimpleName();
    private static final String endpoint = "http://image.tmdb.org/t/p/";
    private ArrayList<Movie> movies;
    private MovieAdapter mAdapter;
    private RecyclerView recyclerView;
    private SQLiteDatabase mMovieDb;


    final String GET_POPULAR = "popular";
    final String GET_TOP = "top_rated";
    final String GET_FAV = "favorite";
    public String sortBy = GET_POPULAR;
    private Parcelable listState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setBackgroundColor(getResources().getColor(R.color.blackColorAccent40Percent));

        setSupportActionBar(toolbar);


        mFirebaseAuth = FirebaseAuth.getInstance();

        mUsername = ANONYMOUS;

        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this, "Signed in ", Toast.LENGTH_SHORT).show();
                    onSigninListener(user.getDisplayName());
                    Uri xx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                    if (xx != null) {
                        RequestOptions glideoptions = new RequestOptions().placeholder(R.mipmap.ic_launcher);
                        Glide.with(getApplicationContext()).load(xx).apply(glideoptions).into(profileImage);
                        profileName.setText(user.getDisplayName());
                        profileEmail.setText(user.getEmail());

                    }
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setTheme(R.style.ConnectionTheme)
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                    onSignOutListener();

                }
            }

        };

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        profileImage = headerLayout.findViewById(R.id.profile_image_view);
        profileName = headerLayout.findViewById(R.id.profile_name_text_view);
        profileEmail = headerLayout.findViewById(R.id.profile_mail_text_view);

        movies = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), getResources().getInteger(R.integer.grid_number_cols));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


        MovieDBHelper MovieDBHelper = new MovieDBHelper(this);
        mMovieDb = MovieDBHelper.getWritableDatabase();

        if (savedInstanceState != null) {
            sortBy = savedInstanceState.getString("loadList");
            listState = savedInstanceState.getParcelable("ListState");
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        } else {
            FetchMoviesTask fetchMovies = new FetchMoviesTask();
            fetchMovies.execute();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.sign_out_menu) {
//            AuthUI.getInstance().signOut(this);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_sign_out) {
            AuthUI.getInstance().signOut(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
    }

    private void onSigninListener(String displayName) {
        mUsername = displayName;
//        attachListener();
    }

    private void onSignOutListener() {
        mUsername = ANONYMOUS;
//        mMessageAdapter.clear();
//        detachListener();
    }


    private Cursor getPopularMovies() {
        return mMovieDb.query(
                MovieContract.MovieEntry.POPULAR_MOVIE_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Cursor getTopMovies() {
        return mMovieDb.query(
                MovieContract.MovieEntry.TOP_MOVIE_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }


    public class FetchMoviesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            final String FORECAST_BASE_URL = "https://api.themoviedb.org/3/movie/";

            String popular_url = FORECAST_BASE_URL + GET_POPULAR + "?api_key=" + BuildConfig.TMDB_KEY;
//            final String top_url = FORECAST_BASE_URL + GET_TOP + "?api_key=" + BuildConfig.TMDB_KEY;
            final String top_url = "https://api.themoviedb.org/3/discover/movie?api_key="+BuildConfig.TMDB_KEY+"&with_original_language=ta&sort_by=vote_average.desc";


            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                    popular_url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            movies.clear();
                            try {
                                JSONArray responseBundle = response.getJSONArray("results");
                                for (int j = 0; j < responseBundle.length(); j++) {
                                    JSONObject c = responseBundle.getJSONObject(j);
                                    String posterPath = c.getString("poster_path");
                                    String backdropPath = c.getString("backdrop_path");

                                    Movie movie = new Movie();
                                    movie.setPOSTER_PATH(endpoint + "original/" + posterPath);
                                    movie.setBACKDROP_PATH(endpoint + "w1280/" + backdropPath);
                                    movie.setID(c.getString("id"));
                                    movie.setOVERVIEW(c.getString("overview"));
                                    movie.setRELEASE_DATE(c.getString("release_date"));
                                    movie.setTITLE(c.getString("original_title"));
                                    movie.setVOTE_AVERAGE(c.getString("vote_average"));
                                    movies.add(movie);


                                    ContentValues cv = new ContentValues();
                                    cv.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, endpoint + "original/" + posterPath);
                                    cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, endpoint + "w1280/" + backdropPath);
                                    cv.put(MovieContract.MovieEntry.COLUMN_TITLE, c.getString("original_title"));
                                    cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, c.getString("id"));
                                    cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, c.getString("overview"));
                                    cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, c.getString("release_date"));
                                    cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, c.getString("vote_average"));
                                    mMovieDb.insert(MovieContract.MovieEntry.POPULAR_MOVIE_TABLE, null, cv);
                                }

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                            Cursor cPopularMovies = getPopularMovies();
                            cPopularMovies = getPopularMovies();
                            mAdapter = new MovieAdapter(getApplicationContext(), cPopularMovies);
                            recyclerView.setAdapter(mAdapter);
                            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.popular_sel_mes), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                            toast.show();
                            mAdapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    String message = null;
                    if (volleyError instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (volleyError instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (volleyError instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }

                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                    toast.show();
                    volleyError.printStackTrace();
                }
            });
            MovieController.getInstance().addToRequestQueue(req);

            JsonObjectRequest req2 = new JsonObjectRequest(Request.Method.GET,
                    top_url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
//                            Log.d(TAG, response.toString());
                            movies.clear();
                            try {
                                JSONArray responseBundle = response.getJSONArray("results");
                                for (int j = 0; j < responseBundle.length(); j++) {
                                    JSONObject c = responseBundle.getJSONObject(j);
                                    String posterPath = c.getString("poster_path");
                                    String backdropPath = c.getString("backdrop_path");

                                    Movie movie = new Movie();
                                    movie.setPOSTER_PATH(endpoint + "original/" + posterPath);
                                    movie.setBACKDROP_PATH(endpoint + "w1280/" + backdropPath);
                                    movie.setID(c.getString("id"));
                                    movie.setOVERVIEW(c.getString("overview"));
                                    movie.setRELEASE_DATE(c.getString("release_date"));
                                    movie.setTITLE(c.getString("original_title"));
                                    movie.setVOTE_AVERAGE(c.getString("vote_average"));
                                    movies.add(movie);

                                    ContentValues cv = new ContentValues();
                                    cv.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, endpoint + "original/" + posterPath);
                                    cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, endpoint + "w1280/" + backdropPath);
                                    cv.put(MovieContract.MovieEntry.COLUMN_TITLE, c.getString("original_title"));
                                    cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, c.getString("id"));
                                    cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, c.getString("overview"));
                                    cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, c.getString("release_date"));
                                    cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, c.getString("vote_average"));
                                    mMovieDb.insert(MovieContract.MovieEntry.TOP_MOVIE_TABLE, null, cv);
                                }

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    String message = null;
                    if (volleyError instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (volleyError instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (volleyError instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }


                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                    toast.show();
                    volleyError.printStackTrace();
                }
            });
            MovieController.getInstance().addToRequestQueue(req2);
            return null;
        }
    }

}
