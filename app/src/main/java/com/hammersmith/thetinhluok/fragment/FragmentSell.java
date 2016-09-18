package com.hammersmith.thetinhluok.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.adapter.CategoryAdapter;
import com.hammersmith.thetinhluok.adapter.PhotoAdapter;
import com.hammersmith.thetinhluok.model.Category;
import com.hammersmith.thetinhluok.model.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan Thuon on 9/14/2016.
 */
public class FragmentSell extends Fragment implements CategoryAdapter.ClickListener {
    private Category category;
    private List<Category> categories = new ArrayList<>();
    private RecyclerView recyclerViewCategory, recyclerViewPhoto;
    private CategoryAdapter categoryAdapter;
    private LinearLayoutManager layoutManager, layoutManagerPhoto;

    private Photo photo;
    private List<Photo> photos = new ArrayList<>();
    private PhotoAdapter photoAdapter;

    public FragmentSell() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sell, container, false);
        recyclerViewCategory = (RecyclerView) root.findViewById(R.id.recyclerViewCategory);
        recyclerViewPhoto = (RecyclerView) root.findViewById(R.id.recyclerViewPhoto);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManagerPhoto = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        recyclerViewCategory.setLayoutManager(layoutManager);
        recyclerViewCategory.setAdapter(categoryAdapter);
        categoryAdapter.setClickListener(this);
        for (int i = 0; i < 7; i++) {
            category = new Category();
            category.setId(1);
            category.setImage("logo");
            categories.add(category);
        }
        categoryAdapter.notifyDataSetChanged();

        photoAdapter = new PhotoAdapter(getActivity(),photos);
        recyclerViewPhoto.setLayoutManager(layoutManagerPhoto);
        recyclerViewPhoto.setAdapter(photoAdapter);
        for (int a = 0; a < 4; a++){
            photo = new Photo();
            photo.setImage("photo");
            photos.add(photo);
        }
        photoAdapter.notifyDataSetChanged();

        return root;
    }

    @Override
    public void itemClicked(View view, int position) {
        Toast.makeText(getActivity(), "" + categories.get(position).getImage(), Toast.LENGTH_SHORT).show();
    }
}
