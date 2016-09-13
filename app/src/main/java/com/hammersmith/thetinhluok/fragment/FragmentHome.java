package com.hammersmith.thetinhluok.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.ViewBannerGallery;
import com.hammersmith.thetinhluok.adapter.ProductAdapter;
import com.hammersmith.thetinhluok.adapter.PromotionAdapter;
import com.hammersmith.thetinhluok.model.Product;
import com.hammersmith.thetinhluok.model.Promotion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan Thuon on 9/9/2016.
 */
public class FragmentHome extends Fragment {
    private GridLayoutManager layoutManager;
    private ProductAdapter adapter;
    private Product product;
    private List<Product> products = new ArrayList<>();
    private Promotion promotion;
    private List<Promotion> promotions = new ArrayList<>();
    private RecyclerView recyclerView, recyclerViewPromotion;
    private PromotionAdapter promotionAdapter;
    private LinearLayoutManager linearLayoutManager;

    public FragmentHome() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_container_view, container, false);

        ViewBannerGallery viewBannerGallery = (ViewBannerGallery) root.findViewById(R.id.viewBannerGallery);
        ArrayList<ViewBannerGallery.BannerItem> listData = new ArrayList<ViewBannerGallery.BannerItem>();
        listData.add(viewBannerGallery.new BannerItem("http://www.angkorfocus.com/userfiles/banner_phnom_penh_heart_city.jpg", "http://www.angkorfocus.com", "Phnom Penh"));
        listData.add(viewBannerGallery.new BannerItem("http://www.angkorfocus.com/userfiles/banner_siem_reap_angkor_wat(1).jpg", "http://www.angkorfocus.com", "Angkor Wat"));
        listData.add(viewBannerGallery.new BannerItem("http://www.angkorfocus.com/userfiles/banner_beach-break-sihanoukville.jpg", "http://www.angkorfocus.com", "Sihanoukville"));
        listData.add(viewBannerGallery.new BannerItem("http://www.angkorfocus.com/userfiles/banner_kep_city.jpg", "http://www.angkorfocus.com", "Kep"));
        viewBannerGallery.flip(listData, true);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerViewPromotion = (RecyclerView) root.findViewById(R.id.recyclerViewPromotion);
        promotionAdapter = new PromotionAdapter(getActivity(), promotions);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPromotion.setLayoutManager(linearLayoutManager);
        recyclerViewPromotion.setAdapter(promotionAdapter);
        recyclerViewPromotion.setHasFixedSize(true);
        for (int a = 0; a < 5; a++) {
            promotion = new Promotion();
            promotion.setId(1);
            promotions.add(promotion);
        }
        promotionAdapter.notifyDataSetChanged();

        layoutManager = new GridLayoutManager(getActivity(), 2);
        adapter = new ProductAdapter(getActivity(), products);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        for (int i = 0; i < 10; i++) {
            product = new Product();
            product.setId(1);
            products.add(product);
        }
        adapter.notifyDataSetChanged();

        return root;
    }
}
