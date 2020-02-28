package com.naveed.zipunzip.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.naveed.zipunzip.Models.DisplayFilesModelClass;
import com.naveed.zipunzip.R;

import java.io.File;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class DisplayFilesADapterClass extends RecyclerView.Adapter<DisplayFilesADapterClass.MyViewHolder> {

    ArrayList<DisplayFilesModelClass> foldername;
    ArrayList<DisplayFilesModelClass> TempArraylist;
    Context context;
    boolean select = false;
    Onfolderlistner onfolderlistner;
    String check;

    public DisplayFilesADapterClass(ArrayList<DisplayFilesModelClass> foldername, Context context, Onfolderlistner onfolderlistner , String check) {
        this.foldername = foldername;
        this.TempArraylist = foldername;
        this.context = context;
        this.onfolderlistner = onfolderlistner;
        setHasStableIds(true);
        this.check = check;

    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.rcv_files_item_list , parent , false);
        return new MyViewHolder(v , onfolderlistner);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.foldername.setText(foldername.get(position).getName());
        if (check.equals("image"))
        {
            Glide.with(context).load(new File(foldername.get(position).getLocation()))
                    .transition(withCrossFade()).thumbnail(0.1f).into(holder.img);
        }
        else
        {
           holder.img.setImageDrawable(foldername.get(position).getItemIcon());
        }
        holder.itemView.setBackgroundColor(foldername.get(position).isSelected() ? Color.LTGRAY : Color.WHITE);
        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(foldername.get(position).isSelected());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!foldername.get(position).isSelected()) {
                    foldername.get(position).setSelected(true);
                    holder.checkBox.setVisibility(View.VISIBLE);
                    holder.checkBox.setChecked(foldername.get(position).isSelected());
                    select = true;
                    holder.itemView.setBackgroundColor(foldername.get(position).isSelected() ? Color.LTGRAY : Color.WHITE);
                }
                else
                {
                    foldername.get(position).setSelected(false);
                    holder.itemView.setBackgroundColor(foldername.get(position).isSelected() ? Color.LTGRAY : Color.WHITE);
                    holder.checkBox.setVisibility(View.VISIBLE);
                    holder.checkBox.setChecked(foldername.get(position).isSelected());
                }
            }
        });

        holder.checkBox.setChecked(foldername.get(position).isSelected());

    }


    @Override
    public int getItemCount() {
        return foldername.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView foldername;
        ImageView img , playicon;
        Onfolderlistner onfolderlistner;
        CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView, Onfolderlistner onfolderlistner) {
            super(itemView);
            foldername = itemView.findViewById(R.id.internal_foldername);
            foldername.setSelected(true);
            img = itemView.findViewById(R.id.img);
            playicon = itemView.findViewById(R.id.imgPlayVidesdasd);
            this.onfolderlistner = onfolderlistner;
            checkBox = itemView.findViewById(R.id.rcv_check);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onfolderlistner.onClickListner(getAdapterPosition());
        }
    }

    public interface Onfolderlistner
    {
        void onClickListner(int position);
    }

}
