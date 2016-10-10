package com.hammersmith.thetinhluok.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.ApiInterface;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.ViewBannerGallery;
import com.hammersmith.thetinhluok.adapter.ProductAdapter;
import com.hammersmith.thetinhluok.adapter.PromotionAdapter;
import com.hammersmith.thetinhluok.model.Banner;
import com.hammersmith.thetinhluok.model.Product;
import com.hammersmith.thetinhluok.model.Promotion;
import com.joanzapata.iconify.widget.IconTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 9/9/2016.
 */
public class FragmentHome extends Fragment {
    private GridLayoutManager layoutManager;
    private ProductAdapter adapter;
    private Product product;
    private List<Product> products = new ArrayList<>();
    private Product promotion;
    private List<Product> promotions = new ArrayList<>();
    private RecyclerView recyclerView, recyclerViewPromotion;
    private PromotionAdapter promotionAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefresh;
    private int sizePromotion, sizeProduct;
    private ProgressDialog mProgressDialog;
    private int columns;
    private List<Banner> banners = new ArrayList<>();
    private ViewBannerGallery viewBannerGallery;
    private int sizeBanner;

    public FragmentHome() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_container_view, container, false);

        viewBannerGallery = (ViewBannerGallery) root.findViewById(R.id.viewBannerGallery);
        final ArrayList<ViewBannerGallery.BannerItem> listData = new ArrayList<ViewBannerGallery.BannerItem>();

        ApiInterface serviceBanner = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Banner>> callBanner = serviceBanner.getBanner();
        callBanner.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                banners = response.body();
                sizeBanner = banners.size();
                for (int i = 0; i < banners.size(); i++) {
                    listData.add(viewBannerGallery.new BannerItem(banners.get(i).getImage(), banners.get(i).getWebsite(), banners.get(i).getTitle()));
                }
                viewBannerGallery.flip(listData, true);
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {

            }
        });

//        listData.add(viewBannerGallery.new BannerItem("http://www.bdonlinemart.com/content/images/thumbs/0002578_electronics.jpeg", "http://www.bdonlinemart.com", "Electronic Shop"));
//        listData.add(viewBannerGallery.new BannerItem("http://www.sushmii.com/image/cache/data/Home%20page%20Banner/slide%203-960x400.jpg", "http://www.sushmii.com", "Jewellery Shop"));
//        listData.add(viewBannerGallery.new BannerItem("http://www.gobeautyvoice.com/images/beauty_banner11.jpg", "http://www.gobeautyvoice.com", "Beauty Shop"));
//        listData.add(viewBannerGallery.new BannerItem("https://s-media-cache-ak0.pinimg.com/736x/ae/8b/a7/ae8ba78b7be130e1afdf659947735128.jpg", "https://s-media-cache-ak0.pinimg.com", "Woman Fashion"));

//        viewBannerGallery.flip(listData, true);
        showProgressDialog();
        columns = getResources().getInteger(R.integer.number_column);

        swipeRefresh = (SwipeRefreshLayout) root.findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerViewPromotion = (RecyclerView) root.findViewById(R.id.recyclerViewPromotion);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPromotion.setLayoutManager(linearLayoutManager);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPromotion();
                refreshProduct();
                if (sizeBanner < 1) {
                    refreshBanner();
                }
            }
        });

        ApiInterface serviceSpecialPromotion = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Product>> callPromotion = serviceSpecialPromotion.getSpecialPromotion();
        callPromotion.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                promotions = response.body();
                sizePromotion = promotions.size();
                promotionAdapter = new PromotionAdapter(getActivity(), promotions);
                recyclerViewPromotion.setAdapter(promotionAdapter);
                recyclerViewPromotion.setHasFixedSize(true);
                promotionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        ApiInterface serviceRecentlyAdded = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Product>> callRecentlyAdded = serviceRecentlyAdded.getRecentlyAdded();
        callRecentlyAdded.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                products = response.body();
                sizeProduct = products.size();
                layoutManager = new GridLayoutManager(getActivity(), columns);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new ProductAdapter(getActivity(), products);
                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
                adapter.notifyDataSetChanged();
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    dialogExit("Are you sure want to exit this app?");
                    return true;
                } else {
                    return false;
                }
            }
        });

        return root;
    }

    private void refreshBanner() {
        final ArrayList<ViewBannerGallery.BannerItem> listData = new ArrayList<ViewBannerGallery.BannerItem>();
        ApiInterface serviceBanner = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Banner>> callBanner = serviceBanner.getBanner();
        callBanner.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                banners = response.body();
                for (int i = 0; i < banners.size(); i++) {
                    listData.add(viewBannerGallery.new BannerItem(banners.get(i).getImage(), banners.get(i).getWebsite(), banners.get(i).getTitle()));
                }
                viewBannerGallery.flip(listData, true);
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {

            }
        });
    }

    private void dialogExit(String strMessage) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View viewDialog = factory.inflate(R.layout.layout_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setView(viewDialog);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText(strMessage);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-times-circle-o}");
        TextView activate = (TextView) viewDialog.findViewById(R.id.ok);
        activate.setText("Exit");
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        viewDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void refreshPromotion() {
        ApiInterface serviceSpecialPromotion = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Product>> callPromotion = serviceSpecialPromotion.getSpecialPromotion();
        callPromotion.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                promotions = response.body();
                if (sizePromotion != promotions.size()) {
                    promotionAdapter = new PromotionAdapter(getActivity(), promotions);
                    recyclerViewPromotion.setAdapter(promotionAdapter);
                    recyclerViewPromotion.setHasFixedSize(true);
                    promotionAdapter.notifyDataSetChanged();
                }
                sizePromotion = promotions.size();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void refreshProduct() {
        ApiInterface serviceRecentlyAdded = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Product>> callRecentlyAdded = serviceRecentlyAdded.getRecentlyAdded();
        callRecentlyAdded.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                products = response.body();
                if (products.size() != sizeProduct) {
                    layoutManager = new GridLayoutManager(getActivity(), columns);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter = new ProductAdapter(getActivity(), products);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
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
