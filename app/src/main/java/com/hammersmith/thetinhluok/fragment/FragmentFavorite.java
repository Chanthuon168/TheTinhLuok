package com.hammersmith.thetinhluok.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.ApiInterface;
import com.hammersmith.thetinhluok.PrefUtils;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.adapter.FavoriteAdapter;
import com.hammersmith.thetinhluok.model.Favorite;
import com.hammersmith.thetinhluok.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 9/14/2016.
 */
public class FragmentFavorite extends Fragment {
    private User user;
    private Favorite favorite;
    private List<Favorite> favorites = new ArrayList<>();
    private RecyclerView recyclerViewFavorite;
    private FavoriteAdapter favoriteAdapter;
    private LinearLayoutManager layoutManager;
    private ProgressDialog mProgressDialog;

    public FragmentFavorite() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        user = PrefUtils.getCurrentUser(getActivity());
        recyclerViewFavorite = (RecyclerView) root.findViewById(R.id.recyclerViewFavorite);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewFavorite.setLayoutManager(layoutManager);
        showProgressDialog();

        ApiInterface serviceFavorite = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Favorite>> callFavorite = serviceFavorite.getFavorite(user.getSocialLink());
        callFavorite.enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                favorites = response.body();
                if (favorites.size() < 1) {
                    root.findViewById(R.id.noFavorite).setVisibility(View.VISIBLE);
                } else {
                    favoriteAdapter = new FavoriteAdapter(getActivity(), favorites);
                    recyclerViewFavorite.setAdapter(favoriteAdapter);
                    favoriteAdapter.notifyDataSetChanged();
                }
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {

            }
        });

        return root;
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
