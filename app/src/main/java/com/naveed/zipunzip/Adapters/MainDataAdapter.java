package com.naveed.zipunzip.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.naveed.zipunzip.R;
import com.naveed.zipunzip.Models.mainGridViewData;

import java.util.ArrayList;

public class MainDataAdapter extends RecyclerView.Adapter<MainDataAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<mainGridViewData> sensorList;
    onClickCardview onClickCardview;


    public MainDataAdapter(Context context, ArrayList<mainGridViewData> sensorList , onClickCardview onClickCardview) {
        this.context = context;
        this.sensorList = sensorList;
        this.onClickCardview = onClickCardview;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false);

        return new MyViewHolder(view , onClickCardview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.tvTitle.setText(sensorList.get(position).getItemTitle());
        holder.ivIcon.setImageDrawable(sensorList.get(position).getItemIcon());

    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivIcon;
        TextView tvTitle;
        View mView;
        onClickCardview onClickCardview;

        public MyViewHolder(@NonNull View itemView, MainDataAdapter.onClickCardview onClickCardview) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            mView = itemView;
            this.onClickCardview = onClickCardview;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickCardview.onCardViewClick(getAdapterPosition());
        }
    }

    public interface onClickCardview{
        public void onCardViewClick(int postion);
    }


}