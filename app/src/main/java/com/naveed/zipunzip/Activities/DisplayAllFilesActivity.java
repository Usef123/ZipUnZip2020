package com.naveed.zipunzip.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.TokenWatcher;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;
import com.naveed.zipunzip.Adapters.DisplayFilesADapterClass;
import com.naveed.zipunzip.Adapters.InternalStorageListAdapter;
import com.naveed.zipunzip.Fragments.ZipFiles.ZipFileFragment;
import com.naveed.zipunzip.Models.DisplayFilesModelClass;
import com.naveed.zipunzip.Models.InternalStorageModelClass;
import com.naveed.zipunzip.R;
import com.rahman.dialog.Activity.SmartDialog;
import com.rahman.dialog.ListenerCallBack.SmartDialogClickListener;
import com.rahman.dialog.Utilities.SmartDialogBuilder;
import com.wang.avi.AVLoadingIndicatorView;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import ir.mahdi.mzip.zip.ZipArchive;

public class DisplayAllFilesActivity extends AppCompatActivity implements DisplayFilesADapterClass.Onfolderlistner,
        InternalStorageListAdapter.Onfolderlistner {

    RecyclerView recyclerView;
    DisplayFilesADapterClass adapter;
    InternalStorageListAdapter ISadapter;
    String name;

    MenuItem archive;

    public static final File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "ZipUnZip");

    ArrayList<InternalStorageModelClass> fileList;
    ArrayList<DisplayFilesModelClass> ModelClasslist;
    ArrayList<String> SelectedItemLocationArrayList;
    ArrayList<DisplayFilesModelClass> SelectedItemArrayList;

    AVLoadingIndicatorView progressBar;
    String previouspath;
    File root;
    View bottomsheetLayout;
    View bottomsheetmakezipfiles;

    Button cancelbtn, extractbtn;
    Button cancelbtnzip, addfiletozipbtn;
    //FolderListAdapter adapter;
    String category;
    InterstitialAd interstitialAd;

    public static int counter = 0;
    int colorDailogbtn = 0;
    String location;
    AVLoadingIndicatorView avi;
    Color color;

    public void reqNewInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.Interstitial_ID));
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }
    AdView adView1;

    private void BannerAd() {
        adView1 = findViewById(R.id.adViewlight);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_files);
        recyclerView = findViewById(R.id.allfiles_RecyclerView);
        progressBar = findViewById(R.id.allfilesProgressbar);
        bottomsheetLayout = findViewById(R.id.bottomsheetlayout);
        bottomsheetmakezipfiles = findViewById(R.id.dialogmakezipfile);
        cancelbtn = findViewById(R.id.cancelExtracting);
        extractbtn = findViewById(R.id.startextracting);
        cancelbtnzip = findViewById(R.id.cancelzipfilebtn);
        addfiletozipbtn = findViewById(R.id.makezipfilebtn);
        avi = findViewById(R.id.avidisplayallfiles);
        avi.setIndicatorColor(getResources().getColor(R.color.colorPrimary));
        avi.hide();
        reqNewInterstitial();
        BannerAd();
        SelectedItemLocationArrayList= new ArrayList<>();
        SelectedItemArrayList = new ArrayList<>();

        Intent i = getIntent();
        category = i.getStringExtra("category");
        location = i.getStringExtra("loc");
        if (location.equals("")) {
            bottomsheetLayout.setVisibility(View.GONE);
        } else {
            bottomsheetLayout.setVisibility(View.VISIBLE);
        }
        ModelClasslist = new ArrayList<>();
        fileList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.displayallfilestoolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switch (category) {
            case "PPT Files":
                ModelClasslist = new ArrayList<>();
                new PPTAsyncTask().execute();
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E65C39")));
                addfiletozipbtn.setBackground(getDrawable(R.drawable.bgbuttonppt));
                progressBar.setIndicatorColor(getColor(R.color.PPT));
                colorDailogbtn = getColor(R.color.PPT);

                break;
            case "Word Files":
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0E86FE")));
                ModelClasslist = new ArrayList<>();
                progressBar.setIndicatorColor(getColor(R.color.DOCX));
                addfiletozipbtn.setBackground(getDrawable(R.drawable.bgbuttonword));
                new WordAsyncTask().execute();
                colorDailogbtn = getColor(R.color.DOCX);
                break;
            case "PDF Files":
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F26D6D")));
                ModelClasslist = new ArrayList<>();
                progressBar.setIndicatorColor(getColor(R.color.PDF));
                addfiletozipbtn.setBackground(getDrawable(R.drawable.bgbuttonpdf));
                colorDailogbtn = getColor(R.color.PDF);
                new PDfAsyncTask().execute();
                break;
            case "Excel Files":
                ModelClasslist = new ArrayList<>();
                new XLXSAsyncTask().execute();
                progressBar.setIndicatorColor(getColor(R.color.EXCEL));
                addfiletozipbtn.setBackground(getDrawable(R.drawable.bgbuttonexcel));
                colorDailogbtn = getColor(R.color.EXCEL);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2BCD86")));
                break;
            case "Audio Files":
                getAllAudios();
                progressBar.setIndicatorColor(getColor(R.color.AUDIO));
                addfiletozipbtn.setBackground(getDrawable(R.drawable.bgbuttonaudio));
                colorDailogbtn = getColor(R.color.AUDIO);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6AA34E")));
                break;
            case "Video Files":
                getAllVideos();
                progressBar.setIndicatorColor(getColor(R.color.MP4));
                addfiletozipbtn.setBackground(getDrawable(R.drawable.bgbuttonvideo));
                colorDailogbtn = getColor(R.color.MP4);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D99255")));
                break;
            case "Image Files":
                getAllImages();
                progressBar.setIndicatorColor(getColor(R.color.IMAGE));
                addfiletozipbtn.setBackground(getDrawable(R.drawable.bgbuttonimages));
                colorDailogbtn = getColor(R.color.IMAGE);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#CC97AA")));
                break;
            case "APK Files":
                ModelClasslist = new ArrayList<>();
                progressBar.setIndicatorColor(getColor(R.color.APK));
                addfiletozipbtn.setBackground(getDrawable(R.drawable.bgbuttonapk));
                colorDailogbtn = getColor(R.color.APK);
                new APKAsyncTask().execute();

                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2CBFB1")));
                break;
            case "Text Files":
                ModelClasslist = new ArrayList<>();
                new TEXTAsyncTask().execute();
                colorDailogbtn = getColor(R.color.TEXT);
                addfiletozipbtn.setBackground(getDrawable(R.drawable.bgbuttontext));
                progressBar.setIndicatorColor(getColor(R.color.TEXT));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#29ABE2")));
                break;
            case "Internal Storage":
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFA000")));
                getInternalStorage();
                break;

        }

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheetLayout.setVisibility(View.GONE);
            }
        });

        extractbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new UNZipAsyncTask().execute();
                ISadapter.notifyDataSetChanged();
                bottomsheetLayout.setVisibility(View.GONE);

            }
        });

        cancelbtnzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheetmakezipfiles.setVisibility(View.GONE);
            }
        });
        addfiletozipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FlatDialog flatDialog = new FlatDialog(DisplayAllFilesActivity.this);
                flatDialog.setTitle("Please Enter File Name")
                        .setTitleColor(getResources().getColor(R.color.grayas))
                        .setFirstTextFieldHint("e.g ZipFileImage")
                        .setFirstTextFieldHintColor(getResources().getColor(R.color.grayas))
                        .setFirstTextFieldBorderColor(getResources().getColor(R.color.colorPrimaryDark))
                        .setFirstButtonText("Ok")
                        .setFirstTextFieldTextColor(getResources().getColor(R.color.grayas))
                        .setSecondButtonText("Cancel")
                        .setBackgroundColor(getResources().getColor(R.color.white))
                        .setFirstButtonColor(colorDailogbtn)
                        .setSecondButtonColor(colorDailogbtn)
                        .withFirstButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                View parentLayout = findViewById(android.R.id.content);
                                Snackbar.make(parentLayout, "Please Wait Do Not Close App", Snackbar.LENGTH_LONG)
                                        .setAction("CLOSE", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        })
                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                        .show();



                                flatDialog.dismiss();
                                progressBar.hide();
                                name = flatDialog.getFirstTextField();
                                avi.hide();
                                new ZipAsyncTask().execute();
                            }
                        }).withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flatDialog.dismiss();
                        progressBar.hide();
                        avi.hide();
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        counter++;
        reqNewInterstitial();


    }
    void unSelecting()
    {
        for (int i = 0 ; i< SelectedItemArrayList.size() ; i++)
        {
            SelectedItemArrayList.get(i).setSelected(false);
        }
    }
    private class ZipAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setClickable(true);
            recyclerView.setAlpha((float) 0.2);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    if (SelectedItemLocationArrayList.size() > 0) {
                        try {
                            zipper(SelectedItemLocationArrayList, name);

                            new SmartDialogBuilder(DisplayAllFilesActivity.this)
                                    .setTitle("Successfully Zipped")
                                    .setSubTitle("Please Check Folder With App name In Internal Storage \n\n" +
                                            Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name))
                                    .setCancalable(false)
                                    .setNegativeButtonHide(true) //hide cancel button
                                    .setPositiveButton("OK", new SmartDialogClickListener() {
                                        @Override
                                        public void onClick(SmartDialog smartDialog) {
                                            smartDialog.dismiss();
                                            progressBar.hide();
                                            recyclerView.setAlpha(1);
                                            unSelecting();
                                            adapter.notifyDataSetChanged();
                                        }
                                    }).build().show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else
                    {
                        new SmartDialogBuilder(DisplayAllFilesActivity.this)
                                .setTitle("Zip Failed")
                                .setSubTitle("Please Select File ")
                                .setCancalable(false)
                                .setNegativeButtonHide(true) //hide cancel button
                                .setPositiveButton("OK", new SmartDialogClickListener() {
                                    @Override
                                    public void onClick(SmartDialog smartDialog) {
                                        smartDialog.dismiss();
                                    }
                                }).build().show();
                    }

                }
            }, 2000);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            addingToZipFile();
            return null;
        }
    }

    private class UNZipAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            avi.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    avi.hide();
                    new SmartDialogBuilder(DisplayAllFilesActivity.this)
                            .setTitle("Successfully UnZipped")
                            .setCancalable(true)
                            .setNegativeButtonHide(true) //hide cancel button
                            .setPositiveButton("OK", new SmartDialogClickListener() {
                                @Override
                                public void onClick(SmartDialog smartDialog) {

                                    smartDialog.dismiss();
                                }
                            }).build().show();
                    recyclerView.setAlpha(1);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DisplayAllFilesActivity.this, "Successfull Unziped", Toast.LENGTH_LONG).show();
                    finish();
                }
            }, 2000);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            ZipArchive zipArchive = new ZipArchive();
            zipArchive.unzip(location, previouspath, "");
            return null;
        }
    }

    private void getInternalStorage() {

        fileList = new ArrayList<>();
        root = new File(String.valueOf(Environment.getExternalStorageDirectory()));
        previouspath = root.getAbsolutePath();
        ListDir(root);
        progressBar.hide();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        ISadapter = new InternalStorageListAdapter(fileList, this, this);
        recyclerView.setAdapter(ISadapter);

    }

    public void ListDir(File f) {
        File[] listFile = f.listFiles();


        if (listFile != null) {
            fileList.clear();
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    InternalStorageModelClass objects = new InternalStorageModelClass();

                    objects.setName(listFile[i].getName());
                    objects.setLocation(listFile[i].getAbsolutePath());
                    objects.setType(getfileExtension(listFile[i]));
                    objects.setDrawable(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.foldericon));
                    fileList.add(objects);

                } else if (listFile[i].getName().endsWith(".pdf")) {
                    //Do what ever u want
                    InternalStorageModelClass object = new InternalStorageModelClass();
                    object.setName(listFile[i].getName().toString());
                    object.setLocation(listFile[i].getAbsolutePath());
                    object.setDrawable(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.pdfimageicon));

                    object.setType(".pdf");
                    fileList.add(object);

                } else if (listFile[i].getName().endsWith(".docx")) {

                    InternalStorageModelClass object = new InternalStorageModelClass();
                    object.setName(listFile[i].getName().toString());
                    object.setDrawable(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.wordimageicon));
                    object.setLocation(listFile[i].getAbsolutePath());
                    object.setType(".docx");
                    fileList.add(object);
                } else if (listFile[i].getName().endsWith(".txt")) {

                    InternalStorageModelClass object = new InternalStorageModelClass();
                    object.setDrawable(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.txtimageicon));
                    object.setName(listFile[i].getName().toString());
                    object.setLocation(listFile[i].getAbsolutePath());
                    object.setType(".txt");
                    fileList.add(object);

                } else if (listFile[i].getName().endsWith(".mp4") ||
                        listFile[i].getName().endsWith(".mkv")) {

                    InternalStorageModelClass object = new InternalStorageModelClass();
                    object.setName(listFile[i].getName().toString());
                    object.setLocation(listFile[i].getAbsolutePath());
                    object.setDrawable(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.mp4imageicon));
                    object.setType("mp4");
                    fileList.add(object);
                } else if (listFile[i].getName().endsWith(".mp3")) {
                    InternalStorageModelClass object = new InternalStorageModelClass();
                    object.setName(listFile[i].getName().toString());
                    object.setDrawable(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.mp3icon));
                    object.setLocation(listFile[i].getAbsolutePath());
                    object.setType(".mp3");
                    fileList.add(object);
                } else if (listFile[i].getName().endsWith(".apk")) {
                    InternalStorageModelClass object = new InternalStorageModelClass();
                    object.setDrawable(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.apkimageicon));
                    object.setName(listFile[i].getName().toString());
                    object.setLocation(listFile[i].getAbsolutePath());
                    object.setType(".apk");
                    //object.setSize(String.valueOf(listFile[i].length()/1024));
                    fileList.add(object);
                } else if (listFile[i].getName().endsWith(".jpg") || listFile[i].getName().endsWith(".jpeg")
                        || listFile[i].getName().endsWith(".png")) {

                    InternalStorageModelClass object = new InternalStorageModelClass();
                    object.setName(listFile[i].getName().toString());
                    object.setLocation(listFile[i].getAbsolutePath());
                    //  object.setSize(String.valueOf(listFile[i].length()/1024));
                    object.setType("image");
                    fileList.add(object);
                } else if (listFile[i].getName().endsWith(".pptx") || listFile[i].getName().endsWith(".ppt")) {
                    InternalStorageModelClass object = new InternalStorageModelClass();
                    object.setDrawable(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.pptimageicon));
                    object.setName(listFile[i].getName().toString());
                    object.setLocation(listFile[i].getAbsolutePath());
                    // object.setSize(String.valueOf(listFile[i].length()/1024));
                    object.setType(".pptx");
                    fileList.add(object);
                } else if (listFile[i].getName().endsWith(".rar") || listFile[i].getName().endsWith(".zip")) {
                    InternalStorageModelClass object = new InternalStorageModelClass();
                    object.setDrawable(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.rarimageicon));
                    object.setName(listFile[i].getName().toString());
                    object.setLocation(listFile[i].getAbsolutePath());
                    // object.setSize(String.valueOf(listFile[i].length()/1024));
                    object.setType(".pptx");
                    fileList.add(object);
                }


            }
        }
    }

    private String getfileExtension(File file) {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension = mimeTypeMap.getFileExtensionFromUrl(contentResolver.getType(Uri.fromFile(file)));
        return extension;
    }

    public void addingToZipFile() {
        for (int i = 0 ; i < ModelClasslist.size() ; i++)
        {
            if (ModelClasslist.get(i).isSelected())
            {
                SelectedItemLocationArrayList.add(ModelClasslist.get(i).getLocation());
                SelectedItemArrayList.add(ModelClasslist.get(i));
            }
        }
    }

    @Override
    public void onClickListnerfolder(int position) {
        File file = new File(fileList.get(position).getLocation());
        previouspath = file.getAbsolutePath();
        if (file.isDirectory()) {
            ListDir(file);
            ISadapter.notifyDataSetChanged();
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    private class myAsyncTask extends android.os.AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            progressBar.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    progressBar.hide();

                }
            }, 500);
            super.onPostExecute(s);


        }

        @Override
        protected String doInBackground(String... strings) {

            getImages();
            return null;
        }
    }

    public void getImages() {

        ContentResolver contentResolver = Objects.requireNonNull(getContentResolver());

        Uri songsUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


        Cursor songCursor = contentResolver.query(songsUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int name = songCursor.getColumnIndex(MediaStore.Images.Media.TITLE);
            int location = songCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int sizes = songCursor.getColumnIndex(MediaStore.Images.Media.SIZE);


            do {
                String currentTitle = songCursor.getString(name);
                String Location = songCursor.getString(location);
                String size = songCursor.getString(sizes);
                DisplayFilesModelClass object = new DisplayFilesModelClass();
                object.setLocation(Location);
                object.setName(currentTitle);
                ModelClasslist.add(object);
            } while (songCursor.moveToNext());
        }
    }

    private void getAllImages() {
        ModelClasslist = new ArrayList<>();
        new myAsyncTask().execute();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new DisplayFilesADapterClass(ModelClasslist, this, this, "image");
        recyclerView.setAdapter(adapter);
    }

    ////////////////////////////////////////////////////////////////////////
    private class videoAsyncTask extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {

            progressBar.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms

                    progressBar.hide();
                }
            }, 500);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            getVideos();
            return null;
        }
    }

    public void getVideos() {
        ContentResolver contentResolver = getContentResolver();

        Uri songsUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songsUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songtitle = songCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            int songlocation = songCursor.getColumnIndex(MediaStore.Video.Media.DATA);

            do {
                String currentTitle = songCursor.getString(songtitle);
                String currentLocation = songCursor.getString(songlocation);
                DisplayFilesModelClass object = new DisplayFilesModelClass();
                object.setName(currentTitle);
                object.setLocation(currentLocation);
                ModelClasslist.add(object);
            } while (songCursor.moveToNext());
        }
    }

    private void getAllVideos() {


        ModelClasslist = new ArrayList<>();
        new videoAsyncTask().execute();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new DisplayFilesADapterClass(ModelClasslist, this, this, "image");
        recyclerView.setAdapter(adapter);
    }

    //////////////////////////////////////////////////////////////////
    private class audioAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setAdapter(adapter);
                }
            }, 500);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            getAudios();
            return null;
        }
    }

    public void getAudios() {
        ContentResolver contentResolver = getContentResolver();

        Uri songsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songsUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songtitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            int songlocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentTitle = songCursor.getString(songtitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songlocation);
                String currentid = songCursor.getString(0);

                DisplayFilesModelClass object = new DisplayFilesModelClass();
                object.setLocation(currentLocation);
                object.setName(currentTitle);
                object.setItemIcon(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.mp3imageicon));
                ModelClasslist.add(object);


            } while (songCursor.moveToNext());
        }


    }

    private void getAllAudios() {
        ModelClasslist = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new DisplayFilesADapterClass(ModelClasslist, this, this, "");
        new audioAsyncTask().execute();

    }

    ///////////////////////////////////////////////////////////////
    private void getAllPdf() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new DisplayFilesADapterClass(ModelClasslist, this, this, "");
        recyclerView.setAdapter(adapter);
    }

    public void walkdirPDF(File dir) {
        String pdfPattern = ".pdf";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkdirPDF(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)) {
                        //Do what ever u want
                        DisplayFilesModelClass object = new DisplayFilesModelClass();
                        object.setName(listFile[i].getName().toString());
                        object.setLocation(listFile[i].getAbsolutePath());
                        object.setItemIcon(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.pdfimageicon));
                        // object.setSize(String.valueOf(listFile[i].length() / 1024));
                        ModelClasslist.add(object);


                    }
                }
            }
        }
    }

    private class PDfAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getAllPdf();
                    progressBar.setVisibility(View.GONE);
                }
            }, 500);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            walkdirPDF(Environment.getExternalStorageDirectory());
            return null;
        }
    }

    ////////////////////////////////////////////
    public void walkDirWord(File dir) {
        String pdfPattern = ".docx";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkDirWord(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)) {
                        //Do what ever u want
                        DisplayFilesModelClass object = new DisplayFilesModelClass();
                        object.setName(listFile[i].getName().toString());
                        object.setLocation(listFile[i].getAbsolutePath());
                        object.setItemIcon(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.wordimageicon));
                        // object.setSize(String.valueOf(listFile[i].length() / 1024));
                        ModelClasslist.add(object);

                    }
                }
            }
        }
    }

    private void getAllWord() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new DisplayFilesADapterClass(ModelClasslist, this, this, "");
        recyclerView.setAdapter(adapter);
    }

    private class WordAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getAllWord();
                    progressBar.setVisibility(View.GONE);
                }
            }, 500);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            walkDirWord(Environment.getExternalStorageDirectory());
            return null;
        }
    }

    /////////////////////////////////////////////////////////////////////
    public void walkDirppt(File dir) {
        String pdfPattern = ".pptx";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkDirppt(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)) {
                        //Do what ever u want
                        DisplayFilesModelClass object = new DisplayFilesModelClass();
                        object.setName(listFile[i].getName().toString());
                        object.setLocation(listFile[i].getAbsolutePath());
                        object.setItemIcon(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.pdfimageicon));
                        // object.setSize(String.valueOf(listFile[i].length() / 1024));
                        ModelClasslist.add(object);

                    }
                }
            }
        }
    }

    private void getPPTFiles() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new DisplayFilesADapterClass(ModelClasslist, this, this, "");
        recyclerView.setAdapter(adapter);

    }

    private class PPTAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPPTFiles();
                    progressBar.setVisibility(View.GONE);
                }
            }, 500);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            walkDirppt(Environment.getExternalStorageDirectory());
            return null;
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    public void walkDirTXT(File dir) {
        String pdfPattern = ".txt";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkDirTXT(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)) {
                        //Do what ever u want
                        DisplayFilesModelClass object = new DisplayFilesModelClass();
                        object.setName(listFile[i].getName().toString());
                        object.setLocation(listFile[i].getAbsolutePath());
                        object.setItemIcon(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.txtimageicon));
                        // object.setSize(String.valueOf(listFile[i].length() / 1024));
                        ModelClasslist.add(object);

                    }
                }
            }
        }
    }

    private void getAllText() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new DisplayFilesADapterClass(ModelClasslist, this, this, "");
        recyclerView.setAdapter(adapter);
    }

    private class TEXTAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getAllText();
                    progressBar.setVisibility(View.GONE);
                }
            }, 500);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            walkDirTXT(Environment.getExternalStorageDirectory());
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////
    public void walkDirApk(File dir) {
        String pdfPattern = ".apk";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkDirApk(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)) {
                        //Do what ever u want
                        DisplayFilesModelClass object = new DisplayFilesModelClass();
                        object.setName(listFile[i].getName().toString());
                        object.setLocation(listFile[i].getAbsolutePath());
                        object.setItemIcon(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.apkimageicon));
                        // object.setSize(String.valueOf(listFile[i].length() / 1024));
                        ModelClasslist.add(object);

                    }
                }
            }
        }
    }

    private void getAllAPK() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new DisplayFilesADapterClass(ModelClasslist, this, this, "");
        recyclerView.setAdapter(adapter);
    }

    private class APKAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getAllAPK();
                    progressBar.setVisibility(View.GONE);
                }
            }, 500);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            walkDirApk(Environment.getExternalStorageDirectory());
            return null;
        }
    }

    ////////////////////////////////////////////////////////////////////
    public void walkDirxlxs(File dir) {
        String pdfPattern = ".xlxs";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkDirxlxs(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)) {
                        //Do what ever u want
                        DisplayFilesModelClass object = new DisplayFilesModelClass();
                        object.setName(listFile[i].getName().toString());
                        object.setLocation(listFile[i].getAbsolutePath());
                        object.setItemIcon(ContextCompat.getDrawable(DisplayAllFilesActivity.this, R.drawable.xlxsimageicon));
                        // object.setSize(String.valueOf(listFile[i].length() / 1024));
                        ModelClasslist.add(object);

                    }
                }
            }
        }
    }

    private void getAllXLXS() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new DisplayFilesADapterClass(ModelClasslist, this, this, "");
        recyclerView.setAdapter(adapter);
    }

    private class XLXSAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    getAllXLXS();
                }
            }, 500);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            walkDirxlxs(Environment.getExternalStorageDirectory());
            return null;
        }
    }

    /////////////////////////////////////////////////////////////////////
    @Override
    public void onClickListner(int position) {

        Toast.makeText(this, ModelClasslist.get(position).getName(), Toast.LENGTH_SHORT).show();


    }


    public String zipper(ArrayList<String> allFiles, String zipFileName) throws IOException {

        String timeStampOfZipFile = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        mediaStorageDir.mkdirs();
        String zippath = mediaStorageDir.getAbsolutePath() + "/" + zipFileName + timeStampOfZipFile + ".zip";
        try {
            if (new File(zippath).exists()) {
                new File(zippath).delete();
            }
            //new File(zipFileName).delete(); // Delete if exists
            net.lingala.zip4j.core.ZipFile zipFile = new ZipFile(zippath);
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            if (allFiles.size() > 0) {
                for (String fileName : allFiles) {
                    File file = new File(fileName);
                    zipFile.addFile(file, zipParameters);
                }
            }


        } catch (net.lingala.zip4j.exception.ZipException e) {
            e.printStackTrace();
        }
        return zippath;
    }


    @Override
    public void onBackPressed() {
        if (category.equals("Internal Storage")) {

            if (previouspath.equals(root.getAbsolutePath())) {
                super.onBackPressed();
            } else {

                previouspath = previouspath.substring(0, previouspath.lastIndexOf("/"));
                File f = new File(previouspath);
                getSupportActionBar().setTitle(f.getName());
                ListDir(f);
                ISadapter.notifyDataSetChanged();
            }
        } else {
            super.onBackPressed();
            reqNewInterstitial();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (category.equals("Internal Storage")) {

        }
        else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.appbar_menu, menu);
            archive = menu.findItem(R.id.search_option);

            archive.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    bottomsheetmakezipfiles.setVisibility(View.VISIBLE);
                    //Toast.makeText(DisplayAllFilesActivity.this,, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

        return true;
    }
}


