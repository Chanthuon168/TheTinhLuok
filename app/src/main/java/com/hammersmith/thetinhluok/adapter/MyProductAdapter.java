package com.hammersmith.thetinhluok.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.ApiInterface;
import com.hammersmith.thetinhluok.PrefUtils;
import com.hammersmith.thetinhluok.ProductDetail;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Favorite;
import com.hammersmith.thetinhluok.model.MyProduct;
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
public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.MyViewHolder> {
    private User user;
    private MyProduct myProduct;
    private List<MyProduct> myProducts;
    private Context context;
    private Activity activity;

    public MyProductAdapter(Activity activity, List<MyProduct> myProducts) {
        this.activity = activity;
        this.myProducts = myProducts;
        user = PrefUtils.getCurrentUser(activity);
    }

    @Override
    public MyProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_my_product, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyProductAdapter.MyViewHolder holder, final int position) {
        final Uri uri = Uri.parse(ApiClient.BASE_URL + myProducts.get(position).getImage());
        context = holder.imageView.getContext();
        Picasso.with(context).load(uri).into(holder.imageView);
        holder.price.setText("USD $" + myProducts.get(position).getPrice());
        holder.iconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDelete(myProducts.get(position).getProId());
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNew = new Intent(activity, ProductDetail.class);
                intentNew.putExtra("pro_id", myProducts.get(position).getProId());
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intentNew);
            }
        });
    }

    private void dialogDelete(final int proID) {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View viewDialog = factory.inflate(R.layout.layout_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setView(viewDialog);
        TextView cancel = (TextView) viewDialog.findViewById(R.id.cancel);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        TextView ok = (TextView) viewDialog.findViewById(R.id.ok);
        message.setText("Are you sure want to delete this product from The TinhLuok?");
        icon.setText("{fa-trash-o}");
        ok.setText("Delete");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface serviceDeleteMyFavorite = ApiClient.getClient().create(ApiInterface.class);
                Call<List<MyProduct>> callDelete = serviceDeleteMyFavorite.deleteMyProduct(proID, user.getSocialLink());
                callDelete.enqueue(new Callback<List<MyProduct>>() {
                    @Override
                    public void onResponse(Call<List<MyProduct>> call, Response<List<MyProduct>> response) {
                        myProducts = response.body();
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<List<MyProduct>> call, Throwable t) {

                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return myProducts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView price;
        IconTextView iconDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            price = (TextView) itemView.findViewById(R.id.price);
            iconDelete = (IconTextView) itemView.findViewById(R.id.iconDelete);
        }
    }
}
