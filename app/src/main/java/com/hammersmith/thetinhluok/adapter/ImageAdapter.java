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
import com.hammersmith.thetinhluok.ContainerView;
import com.hammersmith.thetinhluok.ImageActivity;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Image;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Chan Thuon on 9/12/2016.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    private Image image;
    private List<Image> images;
    private Context context;
    private Activity activity;

    public ImageAdapter(Activity activity, List<Image> images) {
        this.activity = activity;
        this.images = images;
    }

    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product_detal, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.MyViewHolder holder, final int position) {
        Uri uri = Uri.parse(ApiClient.BASE_URL + images.get(position).getImage());
        context = holder.imageView.getContext();
        Picasso.with(context).load(uri).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ImageActivity.class);
                intent.putExtra("pro_id", images.get(position).getProId());
                intent.putExtra("position", position);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
