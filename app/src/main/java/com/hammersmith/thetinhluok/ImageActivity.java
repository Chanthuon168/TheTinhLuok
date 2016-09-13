package com.hammersmith.thetinhluok;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hammersmith.thetinhluok.adapter.ViewPagerAdapter;
import com.hammersmith.thetinhluok.model.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ViewPagerAdapter mCustomPagerAdapter;
    private List<String> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mCustomPagerAdapter = new ViewPagerAdapter(this, images);
        mViewPager = (ViewPager) findViewById(R.id.myviewpager);
        mViewPager.setAdapter(mCustomPagerAdapter);
        images.add("https://lh3.googleusercontent.com/-aQ1kLcIWbMQ/AAAAAAAAAAI/AAAAAAAABN4/2ErazWzUyps/photo.jpg");
        images.add("https://graph.facebook.com/859616704172025/picture?type=large");
        images.add("http://192.168.1.114/online/images/user.png");
        mCustomPagerAdapter.notifyDataSetChanged();
    }
}
