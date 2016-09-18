package com.hammersmith.thetinhluok.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Category;
import com.hammersmith.thetinhluok.model.Photo;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

/**
 * Created by Chan Thuon on 9/18/2016.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> {
    private ClickListener clickListener;
    private Context context;
    private Activity activity;
    private Photo photo;
    private List<Photo> photos;

    public PhotoAdapter(Activity activity, List<Photo> photos) {
        this.activity = activity;
        this.photos = photos;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        IconTextView icon;
        TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getLayoutPosition());
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_photo, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public interface ClickListener {
        public void itemClicked(View view, int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
