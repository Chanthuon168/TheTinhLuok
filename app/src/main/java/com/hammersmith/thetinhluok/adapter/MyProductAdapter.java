package com.hammersmith.thetinhluok.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Favorite;
import com.hammersmith.thetinhluok.model.MyProduct;

import java.util.List;

/**
 * Created by Chan Thuon on 9/12/2016.
 */
public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.MyViewHolder> {
    private MyProduct myProduct;
    private List<MyProduct> myProducts;
    private Context context;
    private Activity activity;

    public MyProductAdapter(Activity activity, List<MyProduct> myProducts) {
        this.activity = activity;
        this.myProducts = myProducts;
    }

    @Override
    public MyProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_my_product, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyProductAdapter.MyViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return myProducts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
