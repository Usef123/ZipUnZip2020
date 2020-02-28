package com.naveed.zipunzip.Adapters;

import android.content.Context;
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
import com.naveed.zipunzip.Models.InternalStorageModelClass;
import com.naveed.zipunzip.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class InternalStorageListAdapter extends RecyclerView.Adapter<InternalStorageListAdapter.MyViewHolder> {

    ArrayList<InternalStorageModelClass> foldername;
    ArrayList<InternalStorageModelClass> TempArraylist;
    Context context;
    Onfolderlistner onfolderlistner;

    public InternalStorageListAdapter(ArrayList<InternalStorageModelClass> foldername, Context context, Onfolderlistner onfolderlistner) {
        this.foldername = foldername;
        this.TempArraylist = foldername;
        this.context = context;
        this.onfolderlistner = onfolderlistner;
        setHasStableIds(true);

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.foldername.setText(foldername.get(position).getName());

       if(foldername.get(position).getLocation().endsWith(".mp4") ||
                foldername.get(position).getLocation().endsWith(".MP4")||
                foldername.get(position).getLocation().endsWith(".MKV")||
                foldername.get(position).getLocation().endsWith(".mkv"))
        {
            File file = new File(foldername.get(position).getLocation());

            Glide.with(context).load(file).transition(withCrossFade()).thumbnail(0.1f).into(holder.img);
        }
       else if(foldername.get(position).getLocation().endsWith("jpg") ||
                foldername.get(position).getLocation().endsWith("png")||
                foldername.get(position).getLocation().endsWith("jpeg") ||
                foldername.get(position).getLocation().endsWith("JPG") ||
                foldername.get(position).getLocation().endsWith("PNG")||
                foldername.get(position).getLocation().endsWith("JPEG")
        )
        {
            File file = new File(foldername.get(position).getLocation());
            Picasso.get().load(Uri.fromFile(file)).centerCrop().resize(400,400).into(holder.img);

        }
       else
       {
           holder.img.setImageDrawable(foldername.get(position).getDrawable());
       }

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
            checkBox.setVisibility(View.GONE);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onfolderlistner.onClickListnerfolder(getAdapterPosition());
        }
    }
    public interface Onfolderlistner
    {
        public void onClickListnerfolder(int position);
    }
}
