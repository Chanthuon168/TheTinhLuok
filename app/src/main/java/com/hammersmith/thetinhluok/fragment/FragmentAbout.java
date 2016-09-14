package com.hammersmith.thetinhluok.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hammersmith.thetinhluok.R;

/**
 * Created by Chan Thuon on 9/14/2016.
 */
public class FragmentAbout extends Fragment{

    public FragmentAbout(){}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about,container,false);

        return root;
    }
}
