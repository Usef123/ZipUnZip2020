package com.naveed.zipunzip.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class DisplayZIPADapterClass extends RecyclerView.Adapter<DisplayZIPADapterClass.MyViewHolder> {

    ArrayList<DisplayFilesModelClass> foldername;
    ArrayList<DisplayFilesModelClass> TempArraylist;
    Context context;
    Onfolderlistner1 onfolderlistner;
    String check;

    public DisplayZIPADapterClass(ArrayList<DisplayFilesModelClass> foldername, Context context, Onfolderlistner1 onfolderlistner ) {
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
        View v = inflater.inflate(R.layout.rcv_zipfiles_item_list , parent , false);
        return new MyViewHolder(v , onfolderlistner);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.foldername.setText(foldername.get(position).getName());

           holder.img.setImageDrawable(foldername.get(position).getItemIcon());

    }


    @Override
    public int getItemCount() {
        return foldername.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView foldername;
        ImageView img , playicon;
        Onfolderlistner1 onfolderlistner;
        public MyViewHolder(@NonNull View itemView, Onfolderlistner1 onfolderlistner) {
            super(itemView);
            foldername = itemView.findViewById(R.id.internal_foldername);
            foldername.setSelected(true);
            img = itemView.findViewById(R.id.img);
            playicon = itemView.findViewById(R.id.imgPlayVidesdasd);
            this.onfolderlistner = onfolderlistner;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onfolderlistner.onClickListner(getAdapterPosition());
        }
    }

    public interface Onfolderlistner1
    {
        public void onClickListner(int position);
    }
}
