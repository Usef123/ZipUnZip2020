package com.naveed.zipunzip.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.naveed.zipunzip.R;

public class SplashScreenActivity extends AppCompatActivity {
    InterstitialAd interstitialAd;
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final TextView textView = findViewById(R.id.textwaitin);

        reqNewInterstitial();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    reqNewInterstitial();
                    //main work

                    finish();
                    Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(mainIntent);

                }
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {

                        reqNewInterstitial();
                        //main work

                        finish();
                        Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(mainIntent);

                    }
                });


            }
        },5000);

    }
    public void reqNewInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.Interstitial_ID));
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

}
