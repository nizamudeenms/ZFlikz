package com.zyta.zflikz;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
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
import com.google.android.material.snackbar.Snackbar;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.zyta.zflikz.model.PersonCast;
import com.zyta.zflikz.model.PersonCastAdapter;
import com.zyta.zflikz.model.PersonCreditDetails;
import com.zyta.zflikz.model.PersonCrew;
import com.zyta.zflikz.model.PersonDetails;
import com.zyta.zflikz.model.PersonFullCreditsAdapter;
import com.zyta.zflikz.model.PersonImage;
import com.zyta.zflikz.model.PersonImageDetails;
import com.zyta.zflikz.utils.MovieAPI;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonActivity extends AppCompatActivity {
    TextView personNameTextView, personBiographyTextView, personBornOnTextView, personPlaceOfBirth, personKnownForTextView, completeFilmographyTextView;
    ImageView personImageView, personBackgroundImageView;
    //    LinearLayout personLinearLayout;
    CardView personCardView, personBioCardView;
    PersonCastAdapter personCastAdapter;
    RecyclerView personCreditsRecyclerView;
    ArrayList<PersonCast> castList = new ArrayList<>();
    ArrayList<PersonCrew> crewList = new ArrayList<>();
    private PersonFullCreditsAdapter personFullCreditsAdapter;
    ArrayList<PersonImage> personImageArrayList = new ArrayList<>();
    ArrayList<String> personImagePathArrayList = new ArrayList<>();
    String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500";


    int personId;
    String birthday, birthPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_person);

        personId = getIntent().getIntExtra("personId", 0);

        personCastAdapter = new PersonCastAdapter(this, castList);

        personImageView = findViewById(R.id.person_back_drop_image);
        personBackgroundImageView = findViewById(R.id.person_background_image_view);
        personNameTextView = findViewById(R.id.person_name_textview);
        personBiographyTextView = findViewById(R.id.person_biography_textview);
        personBornOnTextView = findViewById(R.id.person_born_on_text_view);
        personPlaceOfBirth = findViewById(R.id.person_birth_place_text_view);
        personKnownForTextView = findViewById(R.id.person_known_for_text_view);
        personCardView = findViewById(R.id.person_cast_card_view);
        personBioCardView = findViewById(R.id.person_bio_card_view);
        personCreditsRecyclerView = findViewById(R.id.person_cast_recycler_view);
        completeFilmographyTextView = findViewById(R.id.person_complete_cast_text_view);

        personCreditsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        personCreditsRecyclerView.setAdapter(personCastAdapter);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int tempHeight = height / 5;
        getPersonDetails();
        getPersonCredits();
        getPersonImages();


        completeFilmographyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (crewList.size() == 0 && castList.size() == 0) {
                    Snackbar creditsSnackbar = Snackbar.make(getWindow().getDecorView(), "Full Credits Unavailable", Snackbar.LENGTH_LONG);
                    creditsSnackbar.show();
                } else {
                    Intent fullCreditsView = new Intent(getApplicationContext(), FullCreditsActivity.class);
                    fullCreditsView.putExtra("type", "person");
                    fullCreditsView.putExtra("cast_list", castList);
                    fullCreditsView.putExtra("crew_list", crewList);
                    startActivity(fullCreditsView);
                }
            }
        });

        personImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new ImageViewer.Builder<>(PersonActivity.this, personImagePathArrayList)
//                        .setStartPosition(0)
//                        .show();

                if (personImagePathArrayList.size() != 0) {
                    GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(getResources())
                            .setFailureImage(R.drawable.zlikx_logo_bg_blur_grey)
                            .setProgressBarImage(new ProgressBarDrawable());

                    ImageViewer.Builder builder = new ImageViewer.Builder<>(PersonActivity.this, personImagePathArrayList)
                            .setStartPosition(0)
                            .setCustomDraweeHierarchyBuilder(hierarchyBuilder);
                    builder.show();
                } else {
                    Snackbar backDropImagesSnackBar = Snackbar.make(getWindow().getDecorView(), "Image Unavailable", Snackbar.LENGTH_LONG);
                    backDropImagesSnackBar.show();
                }

//                if (crewList.size() == 0 && castList.size() == 0) {
//                    Snackbar creditsSnackbar = Snackbar.make(getWindow().getDecorView(), "Full Credits Unavailable", Snackbar.LENGTH_LONG);
//                    creditsSnackbar.show();
//                } else {
//                    Intent fullscreenImageIntent = new Intent(getApplicationContext(), FullscreenImageActivity.class);
//                    fullscreenImageIntent.putExtra("type", "person");
//                    fullscreenImageIntent.putExtra("person_image_list", personImagePathArrayList);
////                    fullscreenImageIntent.putExtra("crew_list", crewList);
//                    startActivity(fullscreenImageIntent);
//                }
            }
        });
    }

    private void getPersonDetails() {

        final Call<PersonDetails> personDetailsCall = MovieAPI.getService().getPersonDetails(personId, BuildConfig.TMDB_KEY);
        personDetailsCall.enqueue(new Callback<PersonDetails>() {
            @Override
            public void onResponse(Call<PersonDetails> call, Response<PersonDetails> response) {
                PersonDetails personDetails = response.body();

                try {
                    birthday = personDetails.getBirthday();
                    if (birthday != null) {
                        SimpleDateFormat fromSystem = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMMM-yyyy");

                        String reformattedStr = myFormat.format(fromSystem.parse(personDetails.getBirthday()));
                        birthday = getString(R.string.born_on) + reformattedStr;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                personNameTextView.setText(personDetails.getName());


                if (birthday == null || birthday.isEmpty()) {
                    personBornOnTextView.setVisibility(View.GONE);
                } else {
                    personBornOnTextView.setText(birthday);
                }

                if (personDetails.getPlaceOfBirth() == null || personDetails.getPlaceOfBirth().isEmpty()) {
                    personPlaceOfBirth.setVisibility(View.GONE);
                } else {
                    birthPlace = getString(R.string.born_place) + personDetails.getPlaceOfBirth();
                    personPlaceOfBirth.setText(birthPlace);
                }

                if (personDetails.getBiography() == null || personDetails.getBiography().isEmpty()) {
                    personBiographyTextView.setVisibility(View.GONE);
                    personBioCardView.setVisibility(View.GONE);

                } else {
                    personBiographyTextView.setText(personDetails.getBiography());
                }

                if (personDetails.getKnownForDepartment() == null || personDetails.getKnownForDepartment().isEmpty()) {
                    personKnownForTextView.setVisibility(View.GONE);
                } else {
                    personKnownForTextView.setText(getString(R.string.known_for) + personDetails.getKnownForDepartment());
                }

                if (personDetails.getProfilePath() != null) {
                    GlideApp.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w500" + personDetails.getProfilePath()).centerCrop().error(R.drawable.person_placeholder).into(personImageView);
                    GlideApp.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w500" + personDetails.getProfilePath()).centerCrop().transform(new BlurTransformation(getApplicationContext())).error(R.drawable.person_placeholder).into(personBackgroundImageView);
                }
            }

            @Override
            public void onFailure(Call<PersonDetails> call, Throwable t) {
                if (t instanceof IOException) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.person_coordinator_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.person_coordinator_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }
        });

    }

    private void getPersonCredits() {


        final Call<PersonCreditDetails> credits = MovieAPI.getService().getPersonCredits(personId, BuildConfig.TMDB_KEY);

        credits.enqueue(new Callback<PersonCreditDetails>() {
            @Override
            public void onResponse(Call<PersonCreditDetails> call, Response<PersonCreditDetails> response) {
                PersonCreditDetails personCreditDetails = response.body();
                Log.e("test debug ", "onResponse: " + personCreditDetails.getId());
                castList.addAll(personCreditDetails.getPersonCast());
                crewList.addAll(personCreditDetails.getPersonCrew());


                if (response.body().getPersonCast().isEmpty()) {
                    personCardView.setVisibility(View.GONE);
                }

                personCastAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<PersonCreditDetails> call, Throwable t) {
                if (t instanceof IOException) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.person_coordinator_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.person_coordinator_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }
        });
    }

    private void getPersonImages() {

        final Call<PersonImageDetails> imageDetails = MovieAPI.getService().getPersonImageDetails(personId, BuildConfig.TMDB_KEY);
        imageDetails.enqueue(new Callback<PersonImageDetails>() {
            @Override
            public void onResponse(Call<PersonImageDetails> call, Response<PersonImageDetails> response) {
                PersonImageDetails imagesObject = response.body();
                Log.e("get Image details", "onResponse: " + imagesObject.getId());

                personImageArrayList.addAll(imagesObject.getPersonImage());

                for (PersonImage personImage : personImageArrayList) {
                    personImagePathArrayList.add(POSTER_BASE_URL + personImage.getFilePath());
                }
            }

            @Override
            public void onFailure(Call<PersonImageDetails> call, Throwable t) {
                if (t instanceof IOException) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.person_coordinator_layout), "No Network", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.person_coordinator_layout), "Error Occurred", Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                }
            }
        });
    }
}
