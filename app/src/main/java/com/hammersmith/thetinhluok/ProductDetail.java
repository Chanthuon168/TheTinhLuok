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
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hammersmith.thetinhluok.adapter.CommentAdapter;
import com.hammersmith.thetinhluok.adapter.ImageAdapter;
import com.hammersmith.thetinhluok.model.Comment;
import com.hammersmith.thetinhluok.model.Image;
import com.hammersmith.thetinhluok.model.Product;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetail extends AppCompatActivity implements View.OnClickListener {
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
    private int proId;
    private LinearLayout l_sizeType, l_color, l_email, l_website, l_facebook, l_description;
    private TextView nameTop, priceTop, discountTop, name, price, discount, saving, pay, sizeType, color, ownerName, phone, email, website, facebook, description, txtSizeType;
    private ProgressDialog mProgressDialog;
    private Toolbar toolbar;
    private String strPhone, strEmail, strNumber, strTitle, strOwner, strDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
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
        l_sizeType = (LinearLayout) findViewById(R.id.l_sizeType);
        l_color = (LinearLayout) findViewById(R.id.l_color);
        l_email = (LinearLayout) findViewById(R.id.l_email);
        l_website = (LinearLayout) findViewById(R.id.l_website);
        l_facebook = (LinearLayout) findViewById(R.id.l_facebook);
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
        website = (TextView) findViewById(R.id.website);
        facebook = (TextView) findViewById(R.id.facebook);
        description = (TextView) findViewById(R.id.description);
        txtSizeType = (TextView) findViewById(R.id.txtSizeType);
        findViewById(R.id.l_comment).setOnClickListener(this);
        proId = getIntent().getIntExtra("pro_id", 0);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        showProgressDialog();
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
                if (product.getWebsite().equals("")) {
                    l_website.setVisibility(View.GONE);
                } else {
                    website.setText(product.getWebsite());
                }
                if (product.getFacebook().equals("")) {
                    l_facebook.setVisibility(View.GONE);
                } else {
                    facebook.setText(product.getFacebook());
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
                emailIntent.putExtra(Intent.EXTRA_TEXT,  "Dear Team The TinhLuok\n\n\tI want to report the product\n\n\tNumber "+strNumber+"\n\n\tTitle "+strTitle+"\n\n\tAdded by "+strOwner+"\n\n\tAdded date "+strDate+"\n\nThe reason ");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
            case R.id.l_contact:
                dialogContact();
                break;
            case R.id.l_add_to_favorite:

                break;
            case R.id.l_comment:
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null, false);
                RecyclerView recyclerViewComment = (RecyclerView) inflatedView.findViewById(R.id.commentsListView);
                Display display = getWindowManager().getDefaultDisplay();
                final Point size = new Point();
                display.getSize(size);
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                setSimpleList(recyclerViewComment);
                popWindow = new PopupWindow(inflatedView, width, height - 50, true);
                popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_bg));
                popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popWindow.setAnimationStyle(R.style.PopupAnimation);
                popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 100);
                break;
        }
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

    public void setSimpleList(RecyclerView recyclerView) {
        commentAdapter = new CommentAdapter(this, comments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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
