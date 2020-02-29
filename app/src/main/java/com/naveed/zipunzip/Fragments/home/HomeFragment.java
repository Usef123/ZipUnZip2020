package com.naveed.zipunzip.Fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.naveed.zipunzip.Activities.DisplayAllFilesActivity;
import com.naveed.zipunzip.Adapters.MainDataAdapter;
import com.naveed.zipunzip.R;
import com.naveed.zipunzip.Models.mainGridViewData;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.naveed.zipunzip.Activities.DisplayAllFilesActivity.counter;

public class HomeFragment extends Fragment implements MainDataAdapter.onClickCardview {

    TextView occupiedSpaceText = null;
    TextView freeSpaceText;
    ProgressBar progressIndicator;
    ArrayList<mainGridViewData> buttonlist;
    RecyclerView mainRecylcerView;
    float totalSpace;
    float occupiedSpace;
    float freeSpace1 ;
    static int NumberOfCoulmns = 3;
    DecimalFormat outputFormat ;
    CardView cv;

    InterstitialAd interstitialAd;

    public void reqNewInterstitial() {
        interstitialAd = new InterstitialAd(getActivity());
        interstitialAd.setAdUnitId(getResources().getString(R.string.Interstitial_ID));
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }
    AdView adView1;

    private void BannerAd(View root) {
        adView1 = root.findViewById(R.id.adViewhome);
        AdRequest adrequest = new AdRequest.Builder()
                .build();
        adView1.loadAd(adrequest);
        adView1.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                adView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int error) {
                adView1.setVisibility(View.GONE);
            }

        });
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        IntializeViews(root);
        DisplayingStorageOfDevice();
        reqNewInterstitial();
        BannerAd(root);

        Log.d("ad", "onCreateView: ");

        GridLayoutManager manager = new GridLayoutManager(getActivity(), NumberOfCoulmns);
        mainRecylcerView.setLayoutManager(manager);
        buttonlist = new ArrayList<mainGridViewData>();
        addDataToArray();
        MainDataAdapter mainDataAdapter = new MainDataAdapter(getActivity(), buttonlist ,this);
        mainRecylcerView.setAdapter(mainDataAdapter);

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (interstitialAd.isLoaded()&&counter%3 == 0) {

                            interstitialAd.show();

                    } else {
                        reqNewInterstitial();
                        String name = "";
                        Intent intent = new Intent(getActivity(), DisplayAllFilesActivity.class);
                        intent.putExtra("category", "Internal Storage");
                        intent.putExtra("loc" , "");
                        startActivity(intent);

                    }
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {

                            reqNewInterstitial();
                            String name = "";
                            Intent intent = new Intent(getActivity(), DisplayAllFilesActivity.class);
                            intent.putExtra("category", "Internal Storage");
                            intent.putExtra("loc" , "");
                            startActivity(intent);

                        }
                    });


            }
        });

        return root;
    }

    private void DisplayingStorageOfDevice()
    {
        totalSpace = DeviceMemory.getInternalStorageSpace();
        occupiedSpace = DeviceMemory.getInternalUsedSpace();
        freeSpace1 = DeviceMemory.getInternalFreeSpace();
        outputFormat = new DecimalFormat("#.##");


        if (null != occupiedSpaceText) {
            occupiedSpaceText.setText("Occupied "+ outputFormat.format(occupiedSpace) + " GB");
        }

        if (null != freeSpaceText) {
            freeSpaceText.setText("Free Space "+outputFormat.format(freeSpace1) + " GB");
        }

        if (null != progressIndicator) {
            progressIndicator.setMax((int) totalSpace);
            progressIndicator.setProgress((int)occupiedSpace);
        }
    }

    void IntializeViews(View root)
    {
        occupiedSpaceText = root.findViewById(R.id.occupiedSpace);
        freeSpaceText = root.findViewById(R.id.freeSpace);
        progressIndicator = root.findViewById(R.id.indicator);
        mainRecylcerView = root.findViewById(R.id.categoriesRecyclerview);

        cv = root.findViewById(R.id.cv);
    }

    @Override
    public void onCardViewClick(final int postion) {


            if (interstitialAd.isLoaded() && counter%3 == 0) {
                    interstitialAd.show();
            }
            else {
                reqNewInterstitial();
                String name = buttonlist.get(postion).getItemTitle();
                Intent intent = new Intent(getActivity(), DisplayAllFilesActivity.class);
                intent.putExtra("category", name);
                intent.putExtra("loc" , "");
                startActivity(intent);

            }
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {

                    reqNewInterstitial();
                    String name = buttonlist.get(postion).getItemTitle();
                    Intent intent = new Intent(getActivity(), DisplayAllFilesActivity.class);
                    intent.putExtra("category", name);
                    intent.putExtra("loc" , "");
                    startActivity(intent);

                }
            });


    }

    public static class DeviceMemory {

        public static float getInternalStorageSpace() {
            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            float total = ((float)statFs.getBlockCount() * statFs.getBlockSize()) / 1073741824;
            return total;
        }

        public static float getInternalFreeSpace() {
            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            float free  = ((float)statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1073741824;
            return free;
        }

        public static float getInternalUsedSpace() {
            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());

            float total = ((float)statFs.getBlockCount() * statFs.getBlockSize()) / 1073741824;
            float free  = ((float)statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1073741824;
            float busy  = total - free;
            return busy;
        }
    }

    private void addDataToArray() {

        //adding Sensor Title and Icon to arrayList
        mainGridViewData obj0 = new mainGridViewData();
        obj0.setItemIcon((ContextCompat.getDrawable(getActivity(), R.drawable.jpgimageicon)));
        obj0.setItemTitle(getResources().getString(R.string.image));
        buttonlist.add(obj0);

        mainGridViewData obj1 = new mainGridViewData();
        obj1.setItemIcon(ContextCompat.getDrawable(getActivity(), R.drawable.mp4imageicon));
        obj1.setItemTitle(getResources().getString(R.string.video));
        buttonlist.add(obj1);

        mainGridViewData obj2 = new mainGridViewData();
       obj2.setItemIcon(ContextCompat.getDrawable(getActivity(), R.drawable.mp3imageicon));
        obj2.setItemTitle(getResources().getString(R.string.audio));
        buttonlist.add(obj2);

        mainGridViewData obj3 = new mainGridViewData();
        obj3.setItemIcon(ContextCompat.getDrawable(getActivity(), R.drawable.pdfimageicon));
        obj3.setItemTitle(getResources().getString(R.string.pdffiles));
        buttonlist.add(obj3);

        mainGridViewData obj4 = new mainGridViewData();
        obj4.setItemIcon(ContextCompat.getDrawable(getActivity(), R.drawable.xlxsimageicon));
        obj4.setItemTitle(getResources().getString(R.string.xlxs));
        buttonlist.add(obj4);

        mainGridViewData obj5 = new mainGridViewData();
        obj5.setItemIcon(ContextCompat.getDrawable(getActivity(), R.drawable.txtimageicon));
        obj5.setItemTitle(getResources().getString(R.string.txt));
        buttonlist.add(obj5);

        mainGridViewData obj7 = new mainGridViewData();
        obj7.setItemIcon(ContextCompat.getDrawable(getActivity(), R.drawable.apkimageicon));
        obj7.setItemTitle(getResources().getString(R.string.apk));
        buttonlist.add(obj7);

        mainGridViewData obj8 = new mainGridViewData();
        obj8.setItemIcon(ContextCompat.getDrawable(getActivity(), R.drawable.wordimageicon));
        obj8.setItemTitle(getResources().getString(R.string.docsfiles));
        buttonlist.add(obj8);

        mainGridViewData obj9 = new mainGridViewData();
        obj9.setItemIcon(ContextCompat.getDrawable(getActivity(), R.drawable.pptimageicon));
        obj9.setItemTitle(getResources().getString(R.string.pptfiles));
        buttonlist.add(obj9);

    }

}
