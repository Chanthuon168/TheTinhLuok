package com.hammersmith.thetinhluok.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Product;

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

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewAdapter extends RecyclerView.ViewHolder {
        public MyViewAdapter(View itemView) {
            super(itemView);
        }
    }
}
