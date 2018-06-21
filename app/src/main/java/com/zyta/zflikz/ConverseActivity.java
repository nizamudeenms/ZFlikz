package com.zyta.zflikz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zyta.zflikz.model.ConversationAdapter;
import com.zyta.zflikz.model.ConversationMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConverseActivity extends AppCompatActivity {
    private static final String TAG = "ConverseActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    Date simpleDate = new Date();
    DateFormat df6 = new SimpleDateFormat("E, MMM dd yyyy HH:mm:ss");
    String postDate = df6.format(simpleDate);


    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;
    private String mUsername;
    Uri mUserProfileImage;

    private Integer movieId;
    private String backDropImagePath;
    private RecyclerView conRecyclerView;
    ConversationAdapter conversationAdapter;
    ImageView backDropImage;


    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    ChildEventListener mChildEventListener;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    private static final String FREINDLY_MSG_LENGTH_KEY = "friendly-msg-key";
    private ArrayList<ConversationMessage> ConversationMessagesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converse);


        mUsername = ANONYMOUS;
        movieId = getIntent().getIntExtra("movieId", 0);
        backDropImagePath = getIntent().getStringExtra("backDropImagePath");
        getmUsername();
        System.out.println("movieId ::::::::::: " + movieId);

        backDropImage = findViewById(R.id.converse_activity_backdrop);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("movies").child(movieId.toString());
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("movie_chat_photos");

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        GlideApp.with(getApplicationContext()).load(backDropImagePath).placeholder(R.mipmap.ic_launcher).transform(new BlurTransformation(getApplicationContext())).into(backDropImage);

        conRecyclerView = (RecyclerView) findViewById(R.id.con_recycler_view);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);

        conversationAdapter = new ConversationAdapter(this, ConversationMessagesList);

        conRecyclerView = findViewById(R.id.con_recycler_view);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        conRecyclerView.setLayoutManager(mLayoutManager);
        conRecyclerView.setAdapter(conversationAdapter);
        conRecyclerView.setItemAnimator(new DefaultItemAnimator());
        conRecyclerView.setHasFixedSize(true);
        conRecyclerView.smoothScrollToPosition(conRecyclerView.getAdapter().getItemCount());
        conRecyclerView.addItemDecoration(new DividerItemDecoration(ConverseActivity.this,
                DividerItemDecoration.VERTICAL));


        attachListener();

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Your Logic here
                ConversationMessagesList.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    ConversationMessage cMessage = eventSnapshot.getValue(ConversationMessage.class);
                    Log.e("DATA", "" + cMessage);
                    ConversationMessagesList.add(cMessage);
                    conRecyclerView.smoothScrollToPosition(conRecyclerView.getAdapter().getItemCount());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
                mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/jpeg");
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                    }
                });
            }
        });


        // Enable Send button when there's text to send
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

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                ConversationMessage ConversationMessage = new ConversationMessage(movieId.toString(), postDate, mMessageEditText.getText().toString(), mUsername,mUserProfileImage.toString());
                mDatabaseReference.push().setValue(ConversationMessage);
                attachListener();
                // Clear input box
                mMessageEditText.setText("");
            }
        });
    }

    public String getmUsername() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUsername = user.getDisplayName();
        mUserProfileImage = user.getPhotoUrl();
        return mUsername;
    }


    void attachListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ConversationMessage friendlyMessage = dataSnapshot.getValue(ConversationMessage.class);
                    ConversationMessagesList.add(friendlyMessage);
                    conversationAdapter.notifyDataSetChanged();
                    conRecyclerView.smoothScrollToPosition(conRecyclerView.getAdapter().getItemCount());

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

}
