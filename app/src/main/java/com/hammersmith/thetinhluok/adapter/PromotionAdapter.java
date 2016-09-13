package com.hammersmith.thetinhluok.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.hammersmith.thetinhluok.ProductDetail;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Comment;
import com.hammersmith.thetinhluok.model.Product;
import com.hammersmith.thetinhluok.model.Promotion;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Chan Thuon on 9/9/2016.
 */
public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.MyViewAdapter> {
    private Context context;
    private Activity activity;
    private List<Promotion> promotions;
    private Promotion promotion;
    private PopupWindow popWindow;
    private ListView simpleList;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();
    private Comment comment;

    public PromotionAdapter(Activity activity, List<Promotion> promotions) {
        this.activity = activity;
        this.promotions = promotions;
    }

    @Override
    public MyViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_promotion, parent, false);
        MyViewAdapter myViewHolder = new MyViewAdapter(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewAdapter holder, int position) {
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ProductDetail.class));
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
        holder.iconLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.iconLove.setImageResource(R.drawable.heart);
            }
        });
    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }

    public void setSimpleList(RecyclerView recyclerView) {
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

    public class MyViewAdapter extends RecyclerView.ViewHolder {
        ImageView iconLove,iconComment;
        ImageView image;

        public MyViewAdapter(View itemView) {
            super(itemView);
            iconLove = (ImageView) itemView.findViewById(R.id.love);
            iconComment = (ImageView) itemView.findViewById(R.id.comment);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
