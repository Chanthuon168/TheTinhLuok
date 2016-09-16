package com.hammersmith.thetinhluok;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hammersmith.thetinhluok.adapter.ImageAdapter;
import com.hammersmith.thetinhluok.adapter.ViewPagerAdapter;
import com.hammersmith.thetinhluok.model.Image;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ViewPagerAdapter mCustomPagerAdapter;
    private List<Image> images = new ArrayList<>();
    private int proId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mViewPager = (ViewPager) findViewById(R.id.myviewpager);
        proId = getIntent().getIntExtra("pro_id", 0);
        ApiInterface serviceImage = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Image>> callImage = serviceImage.getImage(proId);
        callImage.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                images = response.body();
                mCustomPagerAdapter = new ViewPagerAdapter(ImageActivity.this, images);
                mViewPager.setAdapter(mCustomPagerAdapter);
                mCustomPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {

            }
        });
    }
}
