package com.hammersmith.thetinhluok.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.ImageActivity;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Favorite;
import com.hammersmith.thetinhluok.model.Image;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Chan Thuon on 9/12/2016.
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {
    private Favorite favorite;
    private List<Favorite> favorites;
    private Context context;
    private Activity activity;

    public FavoriteAdapter(Activity activity, List<Favorite> favorites) {
        this.activity = activity;
        this.favorites = favorites;
    }

    @Override
    public FavoriteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_favorite, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteAdapter.MyViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
