package com.hammersmith.thetinhluok.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Chan Thuon on 9/9/2016.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewAdapter> {
    private Context context;
    private Activity activity;
    private List<Product> products;
    private Product product;
    public ProductAdapter(Activity activity, List<Product> products){
        this.activity = activity;
        this.products = products;
    }
    @Override
    public MyViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product, parent, false);
        MyViewAdapter myViewHolder = new MyViewAdapter(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewAdapter holder, int position) {
        Uri uri = Uri.parse(ApiClient.BASE_URL + products.get(position).getImage());
        context = holder.image.getContext();
        Picasso.with(context).load(uri).into(holder.image);
        holder.price.setText("$"+products.get(position).getPrice());
        holder.discount.setText("("+products.get(position).getDiscount()+"% OFF)");
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewAdapter extends RecyclerView.ViewHolder {
        ImageView image;
        TextView price, discount;
        public MyViewAdapter(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            price = (TextView) itemView.findViewById(R.id.price);
            discount = (TextView) itemView.findViewById(R.id.discount);

        }
    }
}
