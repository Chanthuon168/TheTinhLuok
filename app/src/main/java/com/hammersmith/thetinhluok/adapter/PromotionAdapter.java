package com.hammersmith.thetinhluok.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.ApiInterface;
import com.hammersmith.thetinhluok.LoginActivity;
import com.hammersmith.thetinhluok.PrefUtils;
import com.hammersmith.thetinhluok.ProductDetail;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Comment;
import com.hammersmith.thetinhluok.model.Love;
import com.hammersmith.thetinhluok.model.Product;
import com.hammersmith.thetinhluok.model.Promotion;
import com.hammersmith.thetinhluok.model.User;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 9/9/2016.
 */
public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.MyViewAdapter> {
    private User user;
    private Context context;
    private Activity activity;
    private List<Product> promotions;
    private Product promotion;
    private PopupWindow popWindow;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();
    private Comment comment;
    private Comment comm;
    private String numLove, numComment;
    private Love love;

    public PromotionAdapter(Activity activity, List<Product> promotions) {
        this.activity = activity;
        this.promotions = promotions;
        user = PrefUtils.getCurrentUser(activity);
    }

    @Override
    public MyViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_promotion, parent, false);
        MyViewAdapter myViewHolder = new MyViewAdapter(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewAdapter holder, final int position) {
        Uri uri = Uri.parse(ApiClient.BASE_URL + promotions.get(position).getImage());
        context = holder.image.getContext();
        Picasso.with(context).load(uri).into(holder.image);
        holder.price.setText("$" + promotions.get(position).getPrice());
        holder.discount.setText("(" + promotions.get(position).getDiscount() + "% OFF)");

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNew = new Intent(activity, ProductDetail.class);
                intentNew.putExtra("pro_id", promotions.get(position).getId());
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intentNew);
            }
        });
        holder.iconComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null, false);
                final RecyclerView recyclerViewComment = (RecyclerView) inflatedView.findViewById(R.id.commentsListView);
                final EditText writeComment = (EditText) inflatedView.findViewById(R.id.writeComment);
                final ImageView iconSend = (ImageView) inflatedView.findViewById(R.id.iconSend);
                final LinearLayout lNoComment = (LinearLayout) inflatedView.findViewById(R.id.lNoComment);
                final LinearLayout lDialog = (LinearLayout) inflatedView.findViewById(R.id.lDialog);
                Display display = activity.getWindowManager().getDefaultDisplay();
                final Point size = new Point();
                display.getSize(size);
                DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                recyclerViewComment.setLayoutManager(layoutManager);

                ApiInterface serviceComment = ApiClient.getClient().create(ApiInterface.class);
                final Call<List<Comment>> callComment = serviceComment.getComment(promotions.get(position).getId());
                callComment.enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                        comments = response.body();
                        lDialog.setVisibility(View.GONE);
                        if (comments.size() < 1) {
                            lNoComment.setVisibility(View.VISIBLE);
                        } else {
                            lNoComment.setVisibility(View.GONE);
                            commentAdapter = new CommentAdapter(activity, comments);
                            recyclerViewComment.setAdapter(commentAdapter);
                            commentAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Comment>> call, Throwable t) {

                    }
                });
                popWindow = new PopupWindow(inflatedView, width, height - 50, true);
                popWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.popup_bg));
                popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popWindow.setAnimationStyle(R.style.PopupAnimation);
                popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 100);
                iconSend.setEnabled(false);
                writeComment.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.toString().trim().length() == 0) {
                            iconSend.setEnabled(false);
                            iconSend.setImageResource(R.drawable.ic_content_unsend);
                        } else {
                            iconSend.setEnabled(true);
                            iconSend.setImageResource(R.drawable.ic_content_send);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                iconSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (comments.size() < 1) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentDateTime = dateFormat.format(new Date());
                            comment = new Comment(promotions.get(position).getId(), user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
                            ApiInterface serviceCreateComment = ApiClient.getClient().create(ApiInterface.class);
                            Call<Comment> callCreate = serviceCreateComment.createComment(comment);
                            callCreate.enqueue(new Callback<Comment>() {
                                @Override
                                public void onResponse(Call<Comment> call, Response<Comment> response) {
                                    ApiInterface serviceComment = ApiClient.getClient().create(ApiInterface.class);
                                    final Call<List<Comment>> callComment = serviceComment.getComment(promotions.get(position).getId());
                                    callComment.enqueue(new Callback<List<Comment>>() {
                                        @Override
                                        public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                                            comments = response.body();
                                            lDialog.setVisibility(View.GONE);
                                            if (comments.size() < 1) {
                                                lNoComment.setVisibility(View.VISIBLE);
                                            } else {
                                                lNoComment.setVisibility(View.GONE);
                                                commentAdapter = new CommentAdapter(activity, comments);
                                                recyclerViewComment.setAdapter(commentAdapter);
                                                commentAdapter.notifyDataSetChanged();
                                            }
                                            lNoComment.setVisibility(View.GONE);

                                        }

                                        @Override
                                        public void onFailure(Call<List<Comment>> call, Throwable t) {

                                        }
                                    });

                                    ApiInterface serviceCountComment = ApiClient.getClient().create(ApiInterface.class);
                                    Call<Comment> callCountComment = serviceCountComment.getCountComment(promotions.get(position).getId());
                                    callCountComment.enqueue(new Callback<Comment>() {
                                        @Override
                                        public void onResponse(Call<Comment> call, Response<Comment> response) {
                                            comment = response.body();
                                            numComment = comment.getCount();
                                            if (numComment.equals("no_comment")) {
                                                numComment = "";
                                            }
                                            holder.txtNumComment.setText(numComment);
                                        }

                                        @Override
                                        public void onFailure(Call<Comment> call, Throwable t) {

                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<Comment> call, Throwable t) {

                                }
                            });
                        } else {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentDateTime = dateFormat.format(new Date());
                            comment = new Comment(promotions.get(position).getId(), user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
                            ApiInterface serviceCreateComment = ApiClient.getClient().create(ApiInterface.class);
                            Call<Comment> callCreate = serviceCreateComment.createComment(comment);
                            callCreate.enqueue(new Callback<Comment>() {
                                @Override
                                public void onResponse(Call<Comment> call, Response<Comment> response) {
                                    lNoComment.setVisibility(View.GONE);
                                    comment = response.body();
                                    comm = new Comment();
                                    comm.setComment(comment.getComment());
                                    comm.setProfile(comment.getProfile());
                                    comm.setCreateAt(comment.getCreateAt());
                                    comm.setName(comment.getName());
                                    comm.setLastMessage(comment.getLastMessage());
                                    comments.add(comm);
                                    commentAdapter.notifyDataSetChanged();
                                    if (commentAdapter.getItemCount() > 1) {
                                        recyclerViewComment.getLayoutManager().smoothScrollToPosition(recyclerViewComment, null, commentAdapter.getItemCount() - 1);
                                    }

                                    ApiInterface serviceCountComment = ApiClient.getClient().create(ApiInterface.class);
                                    Call<Comment> callCountComment = serviceCountComment.getCountComment(promotions.get(position).getId());
                                    callCountComment.enqueue(new Callback<Comment>() {
                                        @Override
                                        public void onResponse(Call<Comment> call, Response<Comment> response) {
                                            comment = response.body();
                                            numComment = comment.getCount();
                                            if (numComment.equals("no_comment")) {
                                                numComment = "";
                                            }
                                            holder.txtNumComment.setText(numComment);
                                        }

                                        @Override
                                        public void onFailure(Call<Comment> call, Throwable t) {

                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<Comment> call, Throwable t) {

                                }
                            });
                        }
                        writeComment.setText("");
                    }
                });
            }
        });

        ApiInterface serviceCountLove = ApiClient.getClient().create(ApiInterface.class);
        Call<Love> callCount = serviceCountLove.getCountLove(promotions.get(position).getId());
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

        ApiInterface serviceCountComment = ApiClient.getClient().create(ApiInterface.class);
        Call<Comment> callCountComment = serviceCountComment.getCountComment(promotions.get(position).getId());
        callCountComment.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                comment = response.body();
                numComment = comment.getCount();
                if (numComment.equals("no_comment")) {
                    numComment = "";
                }
                holder.txtNumComment.setText(numComment);
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {

            }
        });

        ApiInterface serviceLoveStatus = ApiClient.getClient().create(ApiInterface.class);
        Call<Love> callStatus = serviceLoveStatus.getLoveStatus(promotions.get(position).getId(), user.getSocialLink());
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
                love = new Love(promotions.get(position).getId(), user.getSocialLink());
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

    @Override
    public int getItemCount() {
        return promotions.size();
    }

    public class MyViewAdapter extends RecyclerView.ViewHolder {
        ImageView iconLove, iconComment, image;
        TextView price, discount, txtNumLove, txtNumComment;


        public MyViewAdapter(View itemView) {
            super(itemView);
            iconLove = (ImageView) itemView.findViewById(R.id.love);
            iconComment = (ImageView) itemView.findViewById(R.id.comment);
            image = (ImageView) itemView.findViewById(R.id.image);
            price = (TextView) itemView.findViewById(R.id.price);
            discount = (TextView) itemView.findViewById(R.id.discount);
            txtNumLove = (TextView) itemView.findViewById(R.id.txtNumLove);
            txtNumComment = (TextView) itemView.findViewById(R.id.txtNumComment);
        }
    }
}
