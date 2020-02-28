package com.naveed.zipunzip.Fragments.ZipFiles;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.naveed.zipunzip.Activities.DisplayAllFilesActivity;
import com.naveed.zipunzip.Adapters.DisplayZIPADapterClass;
import com.naveed.zipunzip.Models.DisplayFilesModelClass;
import com.naveed.zipunzip.R;
import com.rahman.dialog.Activity.SmartDialog;
import com.rahman.dialog.ListenerCallBack.SmartDialogClickListener;
import com.rahman.dialog.Utilities.SmartDialogBuilder;
import com.wang.avi.AVLoadingIndicatorView;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import ir.mahdi.mzip.zip.ZipArchive;

public class ZipFileFragment extends Fragment implements DisplayZIPADapterClass.Onfolderlistner1 {

    RecyclerView recyclerView;
    ArrayList<DisplayFilesModelClass> Modelclasslist;
    DisplayZIPADapterClass adapter;
    ProgressBar progressBar;
    View layoutdialog;
    File zipFile , targetDirectory;
    String zipfilelocation , targetdirectorylocation;

    AVLoadingIndicatorView avi;


    Button cancel , extracthere , extractto , setpasswordbtn;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_zipfiles, container, false);

        recyclerView = root.findViewById(R.id.zipfiles_RecyclerView);
        progressBar = root.findViewById(R.id.progressbarrzipfiles);
        layoutdialog = root.findViewById(R.id.layoutDialog);
        cancel = root.findViewById(R.id.btncancel);
        extracthere = root.findViewById(R.id.btnextracthere);
        extractto = root.findViewById(R.id.btnextractto);



        avi = root.findViewById(R.id.avi);
        avi.hide();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutdialog.setVisibility(View.GONE);

            }
        });

        extracthere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layoutdialog.setVisibility(View.GONE);


                new SmartDialogBuilder(getActivity())
                        .setTitle("Smart Dialog")
                        .setSubTitle("This is the alert dialog to showing alert to user")
                        .setCancalable(true)
                         //set sub title font
                        .setNegativeButtonHide(true) //hide cancel button
                        .setPositiveButton("OK", new SmartDialogClickListener() {
                            @Override
                            public void onClick(SmartDialog smartDialog) {
                                new ZipAsyncTask().execute();
                                smartDialog.dismiss();
                            }
                        }).build().show();


            }
        });

        extractto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        Modelclasslist = new ArrayList<>();
        walkdirRAR(Environment.getExternalStorageDirectory());
        new videoAsyncTask().execute();
        return root;
    }

    private class videoAsyncTask extends AsyncTask<String,String,String> {


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
                    //Do something after 100ms
                    progressBar.setVisibility(View.GONE );
                    getAllCompressedFiles();
                }
            }, 1000);
            super.onPostExecute(s);
        }@Override
        protected String doInBackground(String... strings) {


            return null;
        }
    }

    private class ZipAsyncTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
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
                    recyclerView.setAlpha(1);
                    layoutdialog.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"Successfull Unziped" ,Toast.LENGTH_LONG).show();
                    getAllCompressedFiles();
                }
            }, 2000);
            super.onPostExecute(s);
        }@Override
        protected String doInBackground(String... strings) {

            ZipArchive zipArchive = new ZipArchive();
            zipArchive.unzip(zipfilelocation , targetdirectorylocation ,"");
            return null;
        }
    }
    private void getAllCompressedFiles() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new DisplayZIPADapterClass(Modelclasslist , getContext() , this);
        recyclerView.setAdapter(adapter);
    }
    public void walkdirRAR(File dir) {
        String zippatteren = ".zip";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkdirRAR(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(zippatteren))
                    {
                        if(checkPasswordProtectection(listFile[i].getAbsolutePath())) {
                            DisplayFilesModelClass object = new DisplayFilesModelClass();
                            object.setName(listFile[i].getName());
                            object.setLocation(listFile[i].getAbsolutePath());
                            object.setItemIcon(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.zipimageicon));
                            // object.setSize(String.valueOf(listFile[i].length() / 1024));
                            Modelclasslist.add(object);
                        }
                    }
                }
            }
        }
    }

    private boolean checkPasswordProtectection(String absolutePath) {

        try {
            net.lingala.zip4j.core.ZipFile zipFile = new ZipFile(absolutePath);
            
            if (zipFile.isEncrypted()) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }


    @Override
    public void onClickListner(final int position) {


        recyclerView.setAlpha((float) 0.1);

        zipFile = new File(Modelclasslist.get(position).getLocation());
        targetDirectory = zipFile.getParentFile();

        zipfilelocation = Modelclasslist.get(position).getLocation();
        targetdirectorylocation = zipFile.getParent();
        final FlatDialog flatDialog = new FlatDialog(getActivity());
        flatDialog.setTitle("Please Select")
                .setTitleColor(getResources().getColor(R.color.grayas))
                .setFirstButtonText("Extract At File location")
                .setSecondButtonText("Select Location to Extract")
                .setThirdButtonText("Cancel")
                .setBackgroundColor(getResources().getColor(R.color.white))
                .setFirstButtonColor(getResources().getColor(R.color.grayd))
                .setSecondButtonColor(getResources().getColor(R.color.grayd))
                .setThirdButtonColor(getResources().getColor(R.color.grayd))
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new SmartDialogBuilder(getActivity())
                                .setTitle("Successfully UnZipped")
                                .setSubTitle("Please Check Folder With App name In Internal Storage \n\n" + Modelclasslist.get(position).getLocation())
                                .setCancalable(true)
                                //set sub title font
                                .setNegativeButtonHide(true) //hide cancel button
                                .setPositiveButton("OK", new SmartDialogClickListener() {
                                    @Override
                                    public void onClick(SmartDialog smartDialog) {
                                        new ZipAsyncTask().execute();
                                        smartDialog.dismiss();


                                    }
                                }).build().show();
                        flatDialog.dismiss();

                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = "";
                        Intent intent = new Intent(getActivity(), DisplayAllFilesActivity.class);
                        intent.putExtra("category", "Internal Storage");
                        intent.putExtra("loc" , zipfilelocation);
                        startActivity(intent);
                    }
                }).withThirdButtonListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flatDialog.dismiss();
                recyclerView.setAlpha(1);
            }
        }).show();


    }

}
