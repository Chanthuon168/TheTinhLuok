package com.hammersmith.thetinhluok.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hammersmith.thetinhluok.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan Thuon on 9/13/2016.
 */
public class ViewPagerAdapter extends PagerAdapter {
    Context ssContext;
    private List<String> images = new ArrayList<>();

    public ViewPagerAdapter(Context ssContext, List<String> images) {
        this.ssContext = ssContext;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View ssView, Object ssObject) {
        return ssView == ((ImageView) ssObject);
    }

    @Override
    public Object instantiateItem(ViewGroup ssContainer, int ssPosition) {
        ImageView ssImageView = new ImageView(ssContext);
        ssImageView.setPadding(0, 0, 0, 0);
        ssImageView.setAdjustViewBounds(true);
        Uri uri = Uri.parse(images.get(ssPosition));
        ssContext = ssImageView.getContext();
        Picasso.with(ssContext).load(uri).into(ssImageView);

        ((ViewPager) ssContainer).addView(ssImageView, 0);
        return ssImageView;
    }

    @Override
    public void destroyItem(ViewGroup ssContainer, int ssPosition,
                            Object ssObject) {
        ((ViewPager) ssContainer).removeView((ImageView) ssObject);
    }
}
