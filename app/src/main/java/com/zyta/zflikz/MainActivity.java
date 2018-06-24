package com.zyta.zflikz;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zyta.zflikz.model.MovieAdapter;
import com.zyta.zflikz.model.PostList;
import com.zyta.zflikz.model.Result;
import com.zyta.zflikz.utils.MovieAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

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
    private ArrayList<Result> movies = new ArrayList<>();
    private MovieAdapter mAdapter;
    private RecyclerView recyclerView;


    final String GET_POPULAR = "popular";
    final String GET_TOP = "top_rated";
    final String GET_FAV = "favorite";
    public String sortBy = GET_POPULAR;
    private Parcelable listState;
    private int PAGE_NO = 1;
    private int TOT_PAGES = 0;
    String ORG_LANG = "te";
    final int PRIM_REL_YEAR = 2018;
    final String SORT_BY = "popularity.desc";

    private static final int PAGE_START = 1;


    Boolean isScrolling = false;


    int currentItems, totalItems, scrollOutItems;

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
                        RequestOptions glideoptions = new RequestOptions().placeholder(R.mipmap.ic_launcher).bitmapTransform(new CircleCrop());
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

        mAdapter = new MovieAdapter(this, movies);

        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), getResources().getInteger(R.integer.grid_number_cols));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = mLayoutManager.getChildCount();
                totalItems = mLayoutManager.getItemCount();
                scrollOutItems = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    getData();
                }

            }
        });
        setupSharedPreferences();
        getData();
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        loadColorFromPreferences(sharedPreferences);

        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadColorFromPreferences(SharedPreferences sharedPreferences) {
        ORG_LANG = sharedPreferences.getString("lang", "en");
//        getData();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadColorFromPreferences(sharedPreferences);

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_language) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
//            this.finish();
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


    private void getData() {

//        progress.setVisibility(View.VISIBLE);
        System.out.println("PAGE_NO : " + PAGE_NO);
        System.out.println("TOT_PAGES : " + TOT_PAGES);
        System.out.println("ORG_LANG : " + ORG_LANG);

//        if(PAGE_NO > TOT_PAGES){
//            return;
//        }
//        final Call<PostList> postList = MovieAPI.getService().getPopularMovies(BuildConfig.TMDB_KEY, PAGE_NO);
        final Call<PostList> postList = MovieAPI.getService().getTrendingMovies(BuildConfig.TMDB_KEY, SORT_BY, PRIM_REL_YEAR, ORG_LANG, PAGE_NO);
        postList.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                PostList list = response.body();
//                PAGE_NO = list.getPage();

                TOT_PAGES = list.getTotalPages();
                movies.addAll(list.getResults());
                mAdapter.notifyDataSetChanged();
                if (PAGE_NO <= TOT_PAGES) {
                    PAGE_NO = PAGE_NO + 1;
                    System.out.println("inside page no block");
                }
                System.out.println("PAGE NO at ENd : " + PAGE_NO);
                Toast.makeText(MainActivity.this, "Page NO : " + PAGE_NO, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
