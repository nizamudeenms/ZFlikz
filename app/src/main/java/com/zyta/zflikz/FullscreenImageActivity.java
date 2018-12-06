package com.zyta.zflikz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class FullscreenImageActivity extends AppCompatActivity {
    ArrayList<String> personImagePathArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_fullscreen_image);

//        SimpleDraweeView posterSimpleDraweeView;

        personImagePathArrayList = getIntent().getStringArrayListExtra("person_image_list");


//        posterSimpleDraweeView = findViewById(R.id.fullscreen_simple_drawee_view);


//        Uri uri = Uri.parse("android.resource://com.zyta.zflikz/drawable/loading_android");
//        InputStream stream = getContentResolver().openInputStream(uri);
//
//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setUri(uri)
//                .setAutoPlayAnimations(true)
//                .build();
//        posterSimpleDraweeView.setController(controller);

//        for (String path : personImagePathArrayList) {
//            posterSimpleDraweeView.setImageURI(path);
//        }


//        posterSimpleDraweeView.setImageURI(personImagePathArrayList.get(0));

        GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(getResources())
                .setFailureImage(R.drawable.no_image_available)
                .setProgressBarImage(new ProgressBarDrawable());


        ImageViewer.Builder builder = new ImageViewer.Builder<>(this, personImagePathArrayList)
                .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                .setStartPosition(0);

        builder.show();

    }
}
