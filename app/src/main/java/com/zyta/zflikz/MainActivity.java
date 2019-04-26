package com.zyta.zflikz;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.zyta.zflikz.model.MovieAdapter;
import com.zyta.zflikz.model.PostList;
import com.zyta.zflikz.model.Result;
import com.zyta.zflikz.utils.MovieAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private String mUsername;
    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.TwitterBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());
    ImageView profileImageImageView;
    TextView profileNameTextView, profileEmailTextView;
    private String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Result> movies = new ArrayList<>();
    private MovieAdapter mAdapter;
    private RecyclerView recyclerView;
    private AdView mAdView;


    private int PAGE_NO = 1;
    private int TOT_PAGES = 0;
    String ORG_LANG = null;
    final String PRIM_REL_YEAR = "2020";
    final String SORT_BY = "release_date.desc";


    Boolean isScrolling = false;

    int currentItems, totalItems, scrollOutItems;
    private String profileEmail, profileName;
    Uri profilePhotoUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.blackColorAccent40Percent));

        setSupportActionBar(toolbar);

        SharedPreferences myPrefs = getPreferences(MODE_PRIVATE);
        String langPreference = myPrefs.getString("ORG_LANG", "en");
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("ORG_LANG", langPreference);
        editor.commit();


//         RequestListener<Bitmap> requestListener = new RequestListener<Bitmap>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                // todo log exception to central service or something like that
//
//                // important to return false so the error placeholder can be placed
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                // everything worked out, so probably nothing to do
//                return false;
//            }
//        };

        ORG_LANG = langPreference;

        mFirebaseAuth = FirebaseAuth.getInstance();

        mUsername = ANONYMOUS;

        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    onSigninListener(user.getDisplayName());
                    Uri xx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

                    if (xx != null) {
                        for (UserInfo profile : user.getProviderData()) {
                            // Id of the provider (ex: google.com)
                            String providerId = profile.getProviderId();
                            // UID specific to the provider
                            String uid = profile.getUid();
                            // Name, email address, and profile photo Url
                            profileName = profile.getDisplayName();
                            profileEmail = profile.getEmail();
                            profilePhotoUrl = profile.getPhotoUrl();
                        }
                        GlideApp.with(getApplicationContext()).load(profilePhotoUrl).placeholder(R.mipmap.ic_launcher).error(R.drawable.zlikx_logo_bg_blur_grey).transform(new CircleCrop()).into(profileImageImageView);
                        profileNameTextView.setText(profileName);
                        profileEmailTextView.setText(profileEmail);
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
        profileImageImageView = headerLayout.findViewById(R.id.profile_image_view);
        profileNameTextView = headerLayout.findViewById(R.id.profile_name_text_view);
        profileEmailTextView = headerLayout.findViewById(R.id.profile_mail_text_view);


        movies = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new MovieAdapter(this, movies);

        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), getResources().getInteger(R.integer.grid_number_cols));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
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

        getData();

        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
            MobileAds.initialize(this, "ca-app-pub-1865534838493345~1681246593");
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

    }

//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            if (resultCode == RESULT_OK) {
//                // Sign-in succeeded, set up the UI
////                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
//            } else if (resultCode == RESULT_CANCELED) {
//                // Sign in was canceled by the user, finish the activity
//                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//    }

    private void languageSpinner() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Please select a Language");

        String[] listItems;
        listItems = getResources().getStringArray(R.array.pref_lang_option_labels);

        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (listItems[which]) {
                    case "English":
                        ORG_LANG = "en";
                        break;
                    case "Hindi":
                        ORG_LANG = "hi";
                        break;
                    case "Tamil":
                        ORG_LANG = "ta";
                        break;
                    case "Telugu":
                        ORG_LANG = "te";
                        break;
                    case "Malayalam":
                        ORG_LANG = "ml";
                        break;
                    case "Kannada":
                        ORG_LANG = "kn";
                        break;
                    case "Bengali":
                        ORG_LANG = "bn";
                        break;
                    case "Punjabi":
                        ORG_LANG = "pa";
                        break;
                    case "Nepali":
                        ORG_LANG = "ne";
                        break;
                    case "Gujarati":
                        ORG_LANG = "gu";
                        break;
                    case "Marathi":
                        ORG_LANG = "mr";
                        break;
                    case "Oriya":
                        ORG_LANG = "or";
                        break;
                }
                SharedPreferences myPrefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.putString("ORG_LANG", ORG_LANG);
                editor.commit();
                dialog.dismiss();
                finish();
                startActivity(getIntent());
                getData();
            }
        });
        builder.show();
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
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
//        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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
//            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
//            startActivity(startSettingsActivity);
//            finish();
            languageSpinner();
            return true;
        } else if (id == R.id.action_search) {
            Intent startSearchActivity = new Intent(getApplicationContext(), MovieSearchActivity.class);
            startActivity(startSearchActivity);
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

//        if (id == R.id.nav_settings) {
//            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
//            startActivity(startSettingsActivity);
////            this.finish();
//        } else
        if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Zlikx");
            intent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=com.google.android.apps.plus\n");
            startActivity(Intent.createChooser(intent, "choose one"));
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

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(MainActivity.this);
//        progressDialog.setMax(100);
        progressDialog.setMessage("Loading...");
//        progressDialog.setTitle("ProgressDialog bar example");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDialog.show();



        final Call<PostList> postList;
        if (ORG_LANG.equals("en")) {
            postList = MovieAPI.getService().getPopularMovies(BuildConfig.TMDB_KEY, PAGE_NO);
        } else {
            postList = MovieAPI.getService().getTrendingMovies(BuildConfig.TMDB_KEY, SORT_BY, PRIM_REL_YEAR, ORG_LANG, PAGE_NO);
        }


        postList.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
//                progressDialog.dismiss();

                PostList list = response.body();
                TOT_PAGES = list.getTotalPages();
                movies.addAll(list.getResults());
                mAdapter.notifyDataSetChanged();
                if (PAGE_NO <= TOT_PAGES) {
                    PAGE_NO = PAGE_NO + 1;
                }

            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
//                progressDialog.dismiss();

                if (t instanceof IOException) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_activity_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    Log.e(TAG, "onFailure: " + t.getMessage().toString());
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(MainActivity.this);
                    }

                    builder.setTitle("Internet Access Required")
                            .setMessage("Unable to Continue. Please Check Internet and try again\"")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_activity_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(MainActivity.this);
                    }
                    builder.setTitle("Internet Access Required")
                            .setMessage("Unable to Continue. Please Check Internet and try again\"")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
        progressDialog.dismiss();
    }

}
