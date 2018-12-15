package com.zyta.zflikz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class FullscreenImageActivity extends AppCompatActivity {
    ArrayList<String> personImagePathArrayList = new ArrayList<>();
    String postImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_fullscreen_image);

        SimpleDraweeView posterSimpleDraweeView;
        posterSimpleDraweeView = findViewById(R.id.fullscreen_simple_drawee_view);

        personImagePathArrayList = getIntent().getStringArrayListExtra("person_image_list");


        postImagePath = getIntent().getStringExtra("post_image_path");

        if (postImagePath != null) {
            GenericDraweeHierarchy hierarchy =
                    GenericDraweeHierarchyBuilder.newInstance(getResources())
                            .setFailureImage(R.drawable.no_image_available)
                            .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                            .setPlaceholderImage(R.drawable.zlikx_logo_bg_white)
                            .setPlaceholderImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                            .setProgressBarImage(new ProgressBarDrawable())
                            .build();
            posterSimpleDraweeView.setHierarchy(hierarchy);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(postImagePath)
                    .setAutoPlayAnimations(true)
                    .build();

            posterSimpleDraweeView.setHierarchy(hierarchy);
            posterSimpleDraweeView.setController(controller);
        }


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

        if (personImagePathArrayList != null) {
            GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(getResources())
                    .setFailureImage(R.drawable.no_image_available)
                    .setProgressBarImage(new ProgressBarDrawable());

            ImageViewer.Builder builder = new ImageViewer.Builder<>(this, personImagePathArrayList)
                    .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                    .setStartPosition(0);


            builder.show();
        }

    }
}
