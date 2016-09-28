package com.hammersmith.thetinhluok;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hammersmith.thetinhluok.adapter.CommentAdapter;
import com.hammersmith.thetinhluok.adapter.ImageAdapter;
import com.hammersmith.thetinhluok.model.Comment;
import com.hammersmith.thetinhluok.model.Favorite;
import com.hammersmith.thetinhluok.model.Image;
import com.hammersmith.thetinhluok.model.Love;
import com.hammersmith.thetinhluok.model.Product;
import com.hammersmith.thetinhluok.model.User;
import com.joanzapata.iconify.widget.IconTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetail extends AppCompatActivity implements View.OnClickListener {
    private User user;
    private Favorite favorite;
    private Image image;
    private Product product;
    private List<Image> images = new ArrayList<>();
    private ImageAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private PopupWindow popWindow;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();
    private Comment comment;
    private Comment comm;
    private int proId;
    private LinearLayout l_sizeType, l_color, l_email, l_description, lAddress;
    private TextView txtAddToFavorite, nameTop, priceTop, discountTop, name, price, discount, saving, pay, sizeType, color, ownerName, phone, email, website, facebook, description, txtSizeType;
    private ProgressDialog mProgressDialog;
    private Toolbar toolbar;
    private String strPhone, strEmail, strNumber, strTitle, strOwner, strDate;
    private Love love;
    private String numLove, numComment;
    private ImageView iconLove, iconComment;
    private TextView txtLove, txtNumLove, txtNumComment, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        user = PrefUtils.getCurrentUser(ProductDetail.this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.l_report).setOnClickListener(this);
        findViewById(R.id.l_contact).setOnClickListener(this);
        findViewById(R.id.l_add_to_favorite).setOnClickListener(this);
        findViewById(R.id.l_love).setOnClickListener(this);
        iconLove = (ImageView) findViewById(R.id.iconLove);
        iconComment = (ImageView) findViewById(R.id.iconComment);
        txtLove = (TextView) findViewById(R.id.txtLove);
        txtNumLove = (TextView) findViewById(R.id.txtNumLove);
        txtNumComment = (TextView) findViewById(R.id.txtNumComment);
        l_sizeType = (LinearLayout) findViewById(R.id.l_sizeType);
        l_color = (LinearLayout) findViewById(R.id.l_color);
        l_email = (LinearLayout) findViewById(R.id.l_email);
        l_description = (LinearLayout) findViewById(R.id.l_description);
        nameTop = (TextView) findViewById(R.id.name_top);
        priceTop = (TextView) findViewById(R.id.price_top);
        discountTop = (TextView) findViewById(R.id.discount_top);
        name = (TextView) findViewById(R.id.name);
        price = (TextView) findViewById(R.id.price);
        discount = (TextView) findViewById(R.id.discount);
        saving = (TextView) findViewById(R.id.saving);
        pay = (TextView) findViewById(R.id.pay);
        sizeType = (TextView) findViewById(R.id.sizeType);
        color = (TextView) findViewById(R.id.color);
        ownerName = (TextView) findViewById(R.id.ownerName);
        phone = (TextView) findViewById(R.id.phone);
        email = (TextView) findViewById(R.id.email);
        description = (TextView) findViewById(R.id.description);
        txtSizeType = (TextView) findViewById(R.id.txtSizeType);
        txtAddToFavorite = (TextView) findViewById(R.id.txtAddToFavorite);
        lAddress = (LinearLayout) findViewById(R.id.lAddress);
        address = (TextView) findViewById(R.id.address);
        findViewById(R.id.l_comment).setOnClickListener(this);
        proId = getIntent().getIntExtra("pro_id", 0);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        showProgressDialog();

        ApiInterface serviceCountLove = ApiClient.getClient().create(ApiInterface.class);
        Call<Love> callCount = serviceCountLove.getCountLove(proId);
        callCount.enqueue(new Callback<Love>() {
            @Override
            public void onResponse(Call<Love> call, Response<Love> response) {
                love = response.body();
                numLove = love.getCount();
                if (numLove.equals("no_love")) {
                    numLove = "";
                }
                txtNumLove.setText(numLove);
            }

            @Override
            public void onFailure(Call<Love> call, Throwable t) {

            }
        });

        ApiInterface serviceCountComment = ApiClient.getClient().create(ApiInterface.class);
        Call<Comment> callCountComment = serviceCountComment.getCountComment(proId);
        callCountComment.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                comment = response.body();
                numComment = comment.getCount();
                if (numComment.equals("no_comment")) {
                    numComment = "";
                }
                txtNumComment.setText(numComment);
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {

            }
        });

        ApiInterface serviceLoveStatus = ApiClient.getClient().create(ApiInterface.class);
        Call<Love> callStatus = serviceLoveStatus.getLoveStatus(proId, user.getSocialLink());
        callStatus.enqueue(new Callback<Love>() {
            @Override
            public void onResponse(Call<Love> call, Response<Love> response) {
                love = response.body();
                if (love.getStatus().equals("checked")) {
                    iconLove.setImageResource(R.drawable.heart_white);
                    txtLove.setText("Liked");
                } else {
                    iconLove.setImageResource(R.drawable.heart_outline_white);
                    txtLove.setText("Like");
                }
            }

            @Override
            public void onFailure(Call<Love> call, Throwable t) {

            }
        });

        ApiInterface serviceFavoriteStatus = ApiClient.getClient().create(ApiInterface.class);
        Call<Favorite> callStatusFavorite = serviceFavoriteStatus.getFavoriteStatus(proId, user.getSocialLink());
        callStatusFavorite.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                favorite = response.body();
                txtAddToFavorite.setText(favorite.getMsg());
            }

            @Override
            public void onFailure(Call<Favorite> call, Throwable t) {

            }
        });

        ApiInterface serviceImage = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Image>> callImage = serviceImage.getImage(proId);
        callImage.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                images = response.body();
                adapter = new ImageAdapter(ProductDetail.this, images);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {

            }
        });
        ApiInterface serviceProductDetail = ApiClient.getClient().create(ApiInterface.class);
        Call<Product> callProductDetail = serviceProductDetail.getProductDetail(proId);
        callProductDetail.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                product = response.body();
                toolbar.setTitle(product.getCatName());
                strPhone = product.getPhone();
                strEmail = product.getEmail();
                strNumber = product.getProId();
                strTitle = product.getName();
                strOwner = product.getOwnerName();
                strDate = product.getCreatedAt();
                Double proPrice = Double.valueOf(product.getPrice());
                Double proDiscount = Double.valueOf(product.getDiscount());
                if (proDiscount != 0) {
                    priceTop.setPaintFlags(priceTop.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                nameTop.setText(product.getName());
                priceTop.setText("$" + proPrice);
                discountTop.setText("(" + proDiscount + "% OFF)");
                name.setText(product.getName());
                price.setText("$" + proPrice);
                discount.setText("(" + proDiscount + "% OFF)");
                Double proSaving = proPrice * proDiscount / 100;
                Double proPay = proPrice - proSaving;
                saving.setText("$" + proSaving);
                pay.setText("$" + proPay);
                if (!product.getSize().equals("") && !product.getType().equals("")) {
                    txtSizeType.setText("Size/Type");
                    sizeType.setText(product.getSize() + " / " + product.getType());
                } else if (!product.getType().equals("")) {
                    txtSizeType.setText("Type");
                    sizeType.setText(product.getType());
                } else if (!product.getSize().equals("")) {
                    txtSizeType.setText("Size");
                    sizeType.setText(product.getSize());
                } else {
                    l_sizeType.setVisibility(View.GONE);
                }
                if (product.getColor().equals("")) {
                    l_color.setVisibility(View.GONE);
                } else {
                    color.setText(product.getColor());
                }
                if (product.getDescription().equals("")) {
                    l_description.setVisibility(View.GONE);
                } else {
                    description.setText(product.getDescription());
                }
                ownerName.setText(product.getOwnerName());
                if (product.getPhone2().equals("")) {
                    phone.setText(product.getPhone());
                } else {
                    phone.setText(product.getPhone() + "/" + product.getPhone2());
                }
                if (product.getEmail().equals("")) {
                    l_email.setVisibility(View.GONE);
                } else {
                    email.setText(product.getEmail());
                }
                if (product.getAddress().equals("")) {
                    lAddress.setVisibility(View.GONE);
                } else {
                    address.setText(product.getAddress());
                }

                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.l_report:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", strEmail, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report The Product");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Team The TinhLuok\n\n\tI want to report the product\n\n\tNumber " + strNumber + "\n\n\tTitle " + strTitle + "\n\n\tAdded by " + strOwner + "\n\n\tAdded date " + strDate + "\n\nThe reason ");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
            case R.id.l_contact:
                dialogContact();
                break;
            case R.id.l_add_to_favorite:
                createFavorite();
                break;
            case R.id.l_love:
                love = new Love(proId, user.getSocialLink());
                ApiInterface serviceCreateLove = ApiClient.getClient().create(ApiInterface.class);
                Call<Love> callCreate = serviceCreateLove.createLove(love);
                callCreate.enqueue(new Callback<Love>() {
                    @Override
                    public void onResponse(Call<Love> call, Response<Love> response) {
                        love = response.body();
                        if (love.getStatus().equals("checked")) {
                            iconLove.setImageResource(R.drawable.heart_white);
                            txtLove.setText("Liked");
                        } else {
                            iconLove.setImageResource(R.drawable.heart_outline_white);
                            txtLove.setText("Like");
                        }
                        if (love.getCount().equals("0")) {
                            txtNumLove.setText("");
                        } else {
                            txtNumLove.setText(love.getCount());
                        }
                    }

                    @Override
                    public void onFailure(Call<Love> call, Throwable t) {

                    }
                });
                break;
            case R.id.l_comment:
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null, false);
                final RecyclerView recyclerViewComment = (RecyclerView) inflatedView.findViewById(R.id.commentsListView);
                final EditText writeComment = (EditText) inflatedView.findViewById(R.id.writeComment);
                final ImageView iconSend = (ImageView) inflatedView.findViewById(R.id.iconSend);
                final LinearLayout lNoComment = (LinearLayout) inflatedView.findViewById(R.id.lNoComment);
                final LinearLayout lDialog = (LinearLayout) inflatedView.findViewById(R.id.lDialog);
                Display display = getWindowManager().getDefaultDisplay();
                final Point size = new Point();
                display.getSize(size);
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetail.this);
                recyclerViewComment.setLayoutManager(layoutManager);

                ApiInterface serviceComment = ApiClient.getClient().create(ApiInterface.class);
                final Call<List<Comment>> callComment = serviceComment.getComment(proId);
                callComment.enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                        comments = response.body();
                        lDialog.setVisibility(View.GONE);
                        if (comments.size() < 1) {
                            lNoComment.setVisibility(View.VISIBLE);
                        } else {
                            lNoComment.setVisibility(View.GONE);
                            commentAdapter = new CommentAdapter(ProductDetail.this, comments);
                            recyclerViewComment.setAdapter(commentAdapter);
                            commentAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Comment>> call, Throwable t) {

                    }
                });
                popWindow = new PopupWindow(inflatedView, width, height - 50, true);
                popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_bg));
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
                            comment = new Comment(proId, user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
                            ApiInterface serviceCreateComment = ApiClient.getClient().create(ApiInterface.class);
                            Call<Comment> callCreate = serviceCreateComment.createComment(comment);
                            callCreate.enqueue(new Callback<Comment>() {
                                @Override
                                public void onResponse(Call<Comment> call, Response<Comment> response) {
                                    ApiInterface serviceComment = ApiClient.getClient().create(ApiInterface.class);
                                    final Call<List<Comment>> callComment = serviceComment.getComment(proId);
                                    callComment.enqueue(new Callback<List<Comment>>() {
                                        @Override
                                        public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                                            comments = response.body();
                                            lDialog.setVisibility(View.GONE);
                                            if (comments.size() < 1) {
                                                lNoComment.setVisibility(View.VISIBLE);
                                            } else {
                                                lNoComment.setVisibility(View.GONE);
                                                commentAdapter = new CommentAdapter(ProductDetail.this, comments);
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
                                    Call<Comment> callCountComment = serviceCountComment.getCountComment(proId);
                                    callCountComment.enqueue(new Callback<Comment>() {
                                        @Override
                                        public void onResponse(Call<Comment> call, Response<Comment> response) {
                                            comment = response.body();
                                            numComment = comment.getCount();
                                            if (numComment.equals("no_comment")) {
                                                numComment = "";
                                            }
                                            txtNumComment.setText(numComment);
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
                            comment = new Comment(proId, user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
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
                                    Call<Comment> callCountComment = serviceCountComment.getCountComment(proId);
                                    callCountComment.enqueue(new Callback<Comment>() {
                                        @Override
                                        public void onResponse(Call<Comment> call, Response<Comment> response) {
                                            comment = response.body();
                                            numComment = comment.getCount();
                                            if (numComment.equals("no_comment")) {
                                                numComment = "";
                                            }
                                            txtNumComment.setText(numComment);
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
                break;
        }
    }

    private void createFavorite() {
        favorite = new Favorite(proId, user.getSocialLink());
        ApiInterface serviceCreateFavorite = ApiClient.getClient().create(ApiInterface.class);
        Call<Favorite> callFavorite = serviceCreateFavorite.createFavorite(favorite);
        callFavorite.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                favorite = response.body();
                txtAddToFavorite.setText(favorite.getMsg());
            }

            @Override
            public void onFailure(Call<Favorite> call, Throwable t) {

            }
        });
    }

    private void dialogContact() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View viewDialog = factory.inflate(R.layout.layout_dialog_contact, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(viewDialog);
        TextView activate = (TextView) viewDialog.findViewById(R.id.ok);
        LinearLayout layoutEmail = (LinearLayout) viewDialog.findViewById(R.id.l_email);
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        viewDialog.findViewById(R.id.cancel).setVisibility(View.GONE);
        viewDialog.findViewById(R.id.l_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", strPhone, null));
                startActivity(intent);
            }
        });
        viewDialog.findViewById(R.id.l_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", strPhone, null)));
            }
        });
        if (strEmail.equals("")) {
            layoutEmail.setVisibility(View.GONE);
        } else {
            layoutEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", strEmail, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });
        }
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(ProductDetail.this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }
}
