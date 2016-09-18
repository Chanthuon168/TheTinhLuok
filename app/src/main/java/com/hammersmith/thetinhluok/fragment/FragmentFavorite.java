package com.hammersmith.thetinhluok.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.adapter.FavoriteAdapter;
import com.hammersmith.thetinhluok.model.Favorite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan Thuon on 9/14/2016.
 */
public class FragmentFavorite extends Fragment {
    private Favorite favorite;
    private List<Favorite> favorites = new ArrayList<>();
    private RecyclerView recyclerViewFavorite;
    private FavoriteAdapter favoriteAdapter;
    private LinearLayoutManager layoutManager;
    public FragmentFavorite() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerViewFavorite = (RecyclerView) root.findViewById(R.id.recyclerViewFavorite);
        layoutManager = new LinearLayoutManager(getActivity());
        favoriteAdapter = new FavoriteAdapter(getActivity(),favorites);
        recyclerViewFavorite.setLayoutManager(layoutManager);
        recyclerViewFavorite.setAdapter(favoriteAdapter);
        for (int i = 0; i < 10; i++){
            favorite = new Favorite();
            favorite.setImage("image");
            favorites.add(favorite);
        }
        favoriteAdapter.notifyDataSetChanged();

        return root;
    }
}
