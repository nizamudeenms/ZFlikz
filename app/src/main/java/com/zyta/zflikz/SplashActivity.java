package com.zyta.zflikz;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

public class SplashActivity extends AppCompatActivity {


    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        LottieAnimationView lottieAnimationView = new LottieAnimationView(this);
//        lottieAnimationView.findViewById(R.id.animation_view);
////        lottieAnimationView.setImageAssetsFolder("raw.images/");
//        lottieAnimationView.setAnimation(R.raw.loading);

//       new LottieComposition().ima


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
        System.out.println("SplashActivity.onCreate");
    }


    //TODO Change this later
    @RequiresApi(28)
    private static class OnUnhandledKeyEventListenerWrapper implements View.OnUnhandledKeyEventListener {
        private ViewCompat.OnUnhandledKeyEventListenerCompat mCompatListener;



        OnUnhandledKeyEventListenerWrapper(ViewCompat.OnUnhandledKeyEventListenerCompat listener) {
            System.out.println("OnUnhandledKeyEventListenerWrapper.OnUnhandledKeyEventListenerWrapper");
            this.mCompatListener = listener;
        }

        public boolean onUnhandledKeyEvent(View v, KeyEvent event) {
            System.out.println("OnUnhandledKeyEventListenerWrapper.onUnhandledKeyEvent");
            return this.mCompatListener.onUnhandledKeyEvent(v, event);
        }
    }
}
