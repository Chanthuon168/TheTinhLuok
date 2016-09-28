package com.hammersmith.thetinhluok.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.hammersmith.thetinhluok.adapter.MyProductAdapter;
import com.hammersmith.thetinhluok.model.Favorite;
import com.hammersmith.thetinhluok.model.MyProduct;
import com.hammersmith.thetinhluok.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 9/14/2016.
 */
public class FragmentMyProduct extends Fragment {
    private User user;
    private MyProduct myProduct;
    private List<MyProduct> myProducts = new ArrayList<>();
    private RecyclerView recyclerViewMyProduct;
    private MyProductAdapter myProductAdapter;
    private GridLayoutManager layoutManager;
    private ProgressDialog mProgressDialog;

    public FragmentMyProduct() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_my_product, container, false);
        user = PrefUtils.getCurrentUser(getActivity());
        recyclerViewMyProduct = (RecyclerView) root.findViewById(R.id.recyclerViewMyProduct);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerViewMyProduct.setLayoutManager(layoutManager);
        showProgressDialog();

        ApiInterface serviceMyProduct = ApiClient.getClient().create(ApiInterface.class);
        Call<List<MyProduct>> callMyProduct = serviceMyProduct.getMyProduct(user.getSocialLink());
        callMyProduct.enqueue(new Callback<List<MyProduct>>() {
            @Override
            public void onResponse(Call<List<MyProduct>> call, Response<List<MyProduct>> response) {
                myProducts = response.body();
                if (myProducts.size() < 1){
                    root.findViewById(R.id.noMyProduct).setVisibility(View.VISIBLE);
                }else {
                    myProductAdapter = new MyProductAdapter(getActivity(), myProducts);
                    recyclerViewMyProduct.setAdapter(myProductAdapter);
                    myProductAdapter.notifyDataSetChanged();
                }
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<MyProduct>> call, Throwable t) {

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
