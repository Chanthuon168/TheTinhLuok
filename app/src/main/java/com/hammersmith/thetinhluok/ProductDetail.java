package com.hammersmith.thetinhluok;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.hammersmith.thetinhluok.adapter.CommentAdapter;
import com.hammersmith.thetinhluok.adapter.ImageAdapter;
import com.hammersmith.thetinhluok.model.Comment;
import com.hammersmith.thetinhluok.model.Image;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

public class ProductDetail extends AppCompatActivity implements View.OnClickListener {
    private Image image;
    private List<Image> images = new ArrayList<>();
    private ImageAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private PopupWindow popWindow;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();
    private Comment comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        findViewById(R.id.l_report).setOnClickListener(this);
        findViewById(R.id.l_contact).setOnClickListener(this);
        findViewById(R.id.l_add_to_favorite).setOnClickListener(this);
        findViewById(R.id.l_comment).setOnClickListener(this);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new ImageAdapter(this, images);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        for (int i = 0; i < 5; i++) {
            image = new Image();
            image.setId(1);
            images.add(image);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.l_report:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "chanthuonsreng@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
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
                String phone = "0962008259";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
        viewDialog.findViewById(R.id.l_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = "0962008259";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
            }
        });
        viewDialog.findViewById(R.id.l_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "chanthuonsreng@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

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
}
