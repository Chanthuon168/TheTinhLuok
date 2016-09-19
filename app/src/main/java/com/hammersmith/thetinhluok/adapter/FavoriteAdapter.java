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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.ApiInterface;
import com.hammersmith.thetinhluok.ImageActivity;
import com.hammersmith.thetinhluok.PrefUtils;
import com.hammersmith.thetinhluok.ProductDetail;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Favorite;
import com.hammersmith.thetinhluok.model.Image;
import com.hammersmith.thetinhluok.model.User;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 9/12/2016.
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {
    private Favorite favorite;
    private List<Favorite> favorites;
    private Context context;
    private Activity activity;
    private User user;

    public FavoriteAdapter(Activity activity, List<Favorite> favorites) {
        this.activity = activity;
        this.favorites = favorites;
        user = PrefUtils.getCurrentUser(activity);
    }

    @Override
    public FavoriteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_favorite, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteAdapter.MyViewHolder holder, final int position) {
        final Uri uri = Uri.parse(ApiClient.BASE_URL + favorites.get(position).getImage());
        context = holder.imageView.getContext();
        Picasso.with(context).load(uri).into(holder.imageView);
        holder.title.setText(favorites.get(position).getTitle());
        holder.price.setText("$" + favorites.get(position).getPrice());
        holder.discount.setText("(" + favorites.get(position).getDiscount() + "% OFF)");
        holder.createAt.setText(favorites.get(position).getCreateAt());
        Double proPrice = Double.valueOf(favorites.get(position).getPrice());
        Double proDiscount = Double.valueOf(favorites.get(position).getDiscount());
        Double saving = proPrice * proDiscount / 100;
        Double proPay = proPrice - saving;
        holder.pricePay.setText("$" + proPay);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNew = new Intent(activity, ProductDetail.class);
                intentNew.putExtra("pro_id", favorites.get(position).getProId());
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intentNew);
            }
        });

        holder.lFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNew = new Intent(activity, ProductDetail.class);
                intentNew.putExtra("pro_id", favorites.get(position).getProId());
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intentNew);
            }
        });

        holder.iconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface serviceDeleteFavorite = ApiClient.getClient().create(ApiInterface.class);
                Call<List<Favorite>> callDelete = serviceDeleteFavorite.deleteFavorite(favorites.get(position).getId(), user.getSocialLink());
                callDelete.enqueue(new Callback<List<Favorite>>() {
                    @Override
                    public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                        favorites = response.body();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<Favorite>> call, Throwable t) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, price, discount, pricePay, createAt;
        LinearLayout lFavorite;
        IconTextView iconDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            discount = (TextView) itemView.findViewById(R.id.discount);
            pricePay = (TextView) itemView.findViewById(R.id.price_pay);
            createAt = (TextView) itemView.findViewById(R.id.createdAt);
            lFavorite = (LinearLayout) itemView.findViewById(R.id.l_favorite);
            iconDelete = (IconTextView) itemView.findViewById(R.id.iconDelete);
        }
    }
}
