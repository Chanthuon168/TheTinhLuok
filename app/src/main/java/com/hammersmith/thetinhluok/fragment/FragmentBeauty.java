package com.hammersmith.thetinhluok.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.ApiInterface;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.adapter.ProductAdapter;
import com.hammersmith.thetinhluok.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 9/16/2016.
 */
public class FragmentBeauty extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private GridLayoutManager layoutManager;
    private List<Product> products = new ArrayList<>();
    private Product product;
    private SwipeRefreshLayout swipeRefresh;
    private int sizeProduct;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_woman, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        swipeRefresh = (SwipeRefreshLayout) root.findViewById(R.id.swiperefresh);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
//        showProgressDialog();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshProduct();
            }
        });

        ApiInterface serviceProduct = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Product>> callProduct = serviceProduct.getProduct(6);
        callProduct.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                products = response.body();
                sizeProduct = products.size();
                adapter = new ProductAdapter(getActivity(), products);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
//                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });

        return root;
    }

    private void refreshProduct() {
        ApiInterface serviceProduct = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Product>> callProduct = serviceProduct.getProduct(6);
        callProduct.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                products = response.body();
                if (sizeProduct != products.size()) {
                    adapter = new ProductAdapter(getActivity(), products);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                sizeProduct = products.size();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
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
