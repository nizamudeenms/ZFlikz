package com.zyta.zflikz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zyta.zflikz.model.ConversationMessage;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImageSelectionActivity extends AppCompatActivity {
    ListView selectedPhotosList;
    ImageView activityBackDropImageView;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    Date simpleDate = new Date();
    DateFormat df6 = new SimpleDateFormat("E, MMM dd yyyy HH:mm:ss");
    String postDate = df6.format(simpleDate);
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private LinearLayout imageSelLinearLayout;
    private EditText mMessageEditText;
    private Button mSendButton;
    private String mUsername;
    private ProgressBar imageSelProgressBar;
    Uri mUserProfileImage;

    private Integer movieId;
    private String backDropImagePath;
    private String postImageUrl;
    private Uri localUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);

        movieId = getIntent().getIntExtra("movieId", 0);
        backDropImagePath = getIntent().getStringExtra("backDropImagePath");
        activityBackDropImageView = findViewById(R.id.image_sel_activity_backdrop);

        mMessageEditText = (EditText) findViewById(R.id.image_sel_edit_text);
        mSendButton = (Button) findViewById(R.id.image_sel_send_button);
        imageSelLinearLayout = findViewById(R.id.sel_image_linear_layout);
        imageSelProgressBar = findViewById(R.id.img_sel_progress_bar);
        imageSelProgressBar.setVisibility(View.INVISIBLE);


        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("movie_chat_photos");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("movies").child(movieId.toString());

        getmUsername();
        GlideApp.with(getApplicationContext()).load(backDropImagePath).placeholder(R.drawable.zlikx_logo).transform(new BlurTransformation(getApplicationContext())).into(activityBackDropImageView);


        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        selectedPhotosList = findViewById(R.id.sel_image_list_view);
        Pix.start(ImageSelectionActivity.this, 100, 1);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                System.out.println("postImage URL : " + postImageUrl);
                System.out.println("localUri URL : " + localUri);
//                StorageReference storageReference = mStorageReference.child(postImageUrl);

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);

                final StorageReference photoRef = mStorageReference.child(movieId.toString()).child(localUri.getLastPathSegment());

                UploadTask uploadTask = photoRef.putFile(localUri);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        imageSelProgressBar.setVisibility(View.VISIBLE);

                        imageSelLinearLayout.setVisibility(View.INVISIBLE);

                    }
                });
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        Log.e("photoref ", "photoRef.getDownloadUrl() - " + photoRef.getDownloadUrl());
                        // Continue with the task to get the download URL
                        return photoRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.e("downloadUri ", "downloadUri  : " + downloadUri);
                            ConversationMessage ConversationMessage = new ConversationMessage(movieId.toString(), postDate, mMessageEditText.getText().toString(), mUsername, mUserProfileImage.toString(), downloadUri.toString());
                            mDatabaseReference.push().setValue(ConversationMessage);
                            imageSelProgressBar.setVisibility(View.INVISIBLE);
                            finish();
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });


//                photoRef.putFile(localUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        postImageUrl = photoRef.getDownloadUrl().toString();
////                        taskSnapshot.ge
////                        postImageUrl = url.toString();
//                    }
//
//                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        Log.e("Tesst","postImage Url "+postImageUrl);
//                        ConversationMessage ConversationMessage = new ConversationMessage(movieId.toString(), postDate, mMessageEditText.getText().toString(), mUsername, mUserProfileImage.toString(), postImageUrl);
//                        mDatabaseReference.push().setValue(ConversationMessage);
//                        finish();
//                    }
//                });

            }
        });

    }


//    final StorageReference ref = storageRef.child("images/mountains.jpg");
//    UploadTask uploadTask = ref.putFile(file);
//
//    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//        @Override
//        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//            if (!task.isSuccessful()) {
//                throw task.getException();
//            }
//
//            // Continue with the task to get the download URL
//            return ref.getDownloadUrl();
//        }
//    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//        @Override
//        public void onComplete(@NonNull Task<Uri> task) {
//            if (task.isSuccessful()) {
//                Uri downloadUri = task.getResult();
//            } else {
//                // Handle failures
//                // ...
//            }
//        }
//    });


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("val", "requestCode ->  " + requestCode + "  resultCode " + resultCode);

        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    Log.e("returnValue", " ->  " + returnValue.toString());
//                    Log.e("data", " ->  " + data.getData().toString());

//                    ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.content_listview_selected_images,returnValue);
//                    adapter.add(returnValue);

                    selectedPhotosList.setAdapter(
                            new ImageListAdapter(
                                    ImageSelectionActivity.this,
                                    returnValue
                            )
                    );

//                    localUri = Uri.parse(returnValue.get(0));
                    localUri = Uri.fromFile(new File(returnValue.get(0)));
                    postImageUrl = returnValue.toString();
                    mSendButton.setEnabled(true);

//            StorageReference storageReference = mStorageReference.child(data.getData().getLastPathSegment());
//            storageReference.putFile(data.getData()).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Task<Uri> url = taskSnapshot.getStorage().getDownloadUrl();
//                    postImageUrl = url.toString();
//
//                }
//            });
//                    for (String s : returnValue) {
//                        Log.e("val", " ->  " + s);
//                    }
                } else if(resultCode == Activity.RESULT_CANCELED){
                    finish();
                }
            }
            break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(ImageSelectionActivity.this, 100, 1);
                } else {
                    Toast.makeText(ImageSelectionActivity.this, "Please approve permissions to select Image", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public String getmUsername() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUsername = user.getDisplayName();
        mUserProfileImage = user.getPhotoUrl();
        return mUsername;
    }
}

class ImageListAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private ArrayList<String> imageUrls;

    public ImageListAdapter(Context context, ArrayList<String> imageUrls) {
        super(context, R.layout.content_listview_selected_images, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.content_listview_selected_images, parent, false);
        }

        GlideApp
                .with(context)
                .load(imageUrls.get(position))
                .into((ImageView) convertView);

        return convertView;
    }
}
