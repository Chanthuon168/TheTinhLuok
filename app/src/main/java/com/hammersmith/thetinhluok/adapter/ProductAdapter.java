package com.hammersmith.thetinhluok.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.ApiInterface;
import com.hammersmith.thetinhluok.PrefUtils;
import com.hammersmith.thetinhluok.ProductDetail;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Comment;
import com.hammersmith.thetinhluok.model.Love;
import com.hammersmith.thetinhluok.model.Product;
import com.hammersmith.thetinhluok.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 9/9/2016.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewAdapter> {
    private User user;
    private Context context;
    private Activity activity;
    private List<Product> products;
    private Product product;
    private PopupWindow popWindow;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();
    private Comment comment;
    private String numLove;
    private Love love;

    public ProductAdapter(Activity activity, List<Product> products) {
        this.activity = activity;
        this.products = products;
        user = PrefUtils.getCurrentUser(activity);
    }

    @Override
    public MyViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product, parent, false);
        MyViewAdapter myViewHolder = new MyViewAdapter(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewAdapter holder, final int position) {
        Uri uri = Uri.parse(ApiClient.BASE_URL + products.get(position).getImage());
        context = holder.image.getContext();
        Picasso.with(context).load(uri).into(holder.image);
        holder.price.setText("$" + products.get(position).getPrice());
        holder.discount.setText("(" + products.get(position).getDiscount() + "% OFF)");
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNew = new Intent(activity, ProductDetail.class);
                intentNew.putExtra("pro_id", products.get(position).getId());
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intentNew);
            }
        });
        holder.iconComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null, false);
                RecyclerView recyclerViewComment = (RecyclerView) inflatedView.findViewById(R.id.commentsListView);
                Display display = activity.getWindowManager().getDefaultDisplay();
                final Point size = new Point();
                display.getSize(size);
                DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                setSimpleList(recyclerViewComment);
                popWindow = new PopupWindow(inflatedView, width, height - 50, true);
                popWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.popup_bg));
                popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popWindow.setAnimationStyle(R.style.PopupAnimation);
                popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 100);
            }
        });

        ApiInterface serviceCountLove = ApiClient.getClient().create(ApiInterface.class);
        Call<Love> callCount = serviceCountLove.getCountLove(products.get(position).getId());
        callCount.enqueue(new Callback<Love>() {
            @Override
            public void onResponse(Call<Love> call, Response<Love> response) {
                love = response.body();
                numLove = love.getCount();
                if (numLove.equals("no_love")) {
                    numLove = "";
                }
                holder.txtNumLove.setText(numLove);
            }

            @Override
            public void onFailure(Call<Love> call, Throwable t) {

            }
        });

        ApiInterface serviceLoveStatus = ApiClient.getClient().create(ApiInterface.class);
        Call<Love> callStatus = serviceLoveStatus.getLoveStatus(products.get(position).getId(), user.getSocialLink());
        callStatus.enqueue(new Callback<Love>() {
            @Override
            public void onResponse(Call<Love> call, Response<Love> response) {
                love = response.body();
                if (love.getStatus().equals("checked")) {
                    holder.iconLove.setImageResource(R.drawable.heart);
                } else {
                    holder.iconLove.setImageResource(R.drawable.heart_outline);
                }
            }

            @Override
            public void onFailure(Call<Love> call, Throwable t) {

            }
        });

        holder.iconLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                love = new Love(products.get(position).getId(), user.getSocialLink());
                ApiInterface serviceCreateLove = ApiClient.getClient().create(ApiInterface.class);
                Call<Love> callCreate = serviceCreateLove.createLove(love);
                callCreate.enqueue(new Callback<Love>() {
                    @Override
                    public void onResponse(Call<Love> call, Response<Love> response) {
                        love = response.body();
                        if (love.getStatus().equals("checked")) {
                            holder.iconLove.setImageResource(R.drawable.heart);
                        } else {
                            holder.iconLove.setImageResource(R.drawable.heart_outline);
                        }
                        if (love.getCount().equals("0")) {
                            holder.txtNumLove.setText("");
                        } else {
                            holder.txtNumLove.setText(love.getCount());
                        }
                    }

                    @Override
                    public void onFailure(Call<Love> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void setSimpleList(RecyclerView recyclerView) {
        commentAdapter = new CommentAdapter(activity, comments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setHasFixedSize(true);
        for (int i = 0; i < 10; i++) {
            comment = new Comment();
            comment.setId(1);
        }
        commentAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewAdapter extends RecyclerView.ViewHolder {
        ImageView image, iconLove, iconComment;
        TextView price, discount, txtNumLove, txtNumComment;

        public MyViewAdapter(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            price = (TextView) itemView.findViewById(R.id.price);
            discount = (TextView) itemView.findViewById(R.id.discount);
            iconLove = (ImageView) itemView.findViewById(R.id.love);
            iconComment = (ImageView) itemView.findViewById(R.id.comment);
            txtNumLove = (TextView) itemView.findViewById(R.id.txtNumLove);
            txtNumComment = (TextView) itemView.findViewById(R.id.txtNumComment);
        }
    }
}
