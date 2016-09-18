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
import com.hammersmith.thetinhluok.adapter.FavoriteAdapter;
import com.hammersmith.thetinhluok.adapter.MyProductAdapter;
import com.hammersmith.thetinhluok.model.Favorite;
import com.hammersmith.thetinhluok.model.MyProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan Thuon on 9/14/2016.
 */
public class FragmentMyProduct extends Fragment {
    private MyProduct myProduct;
    private List<MyProduct> myProducts = new ArrayList<>();
    private RecyclerView recyclerViewMyProduct;
    private MyProductAdapter myProductAdapter;
    private GridLayoutManager layoutManager;

    public FragmentMyProduct() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_product, container, false);
        recyclerViewMyProduct = (RecyclerView) root.findViewById(R.id.recyclerViewMyProduct);
        layoutManager = new GridLayoutManager(getActivity(),2);
        myProductAdapter = new MyProductAdapter(getActivity(), myProducts);
        recyclerViewMyProduct.setLayoutManager(layoutManager);
        recyclerViewMyProduct.setAdapter(myProductAdapter);
        for (int i = 0; i < 10; i++) {
            myProduct = new MyProduct();
            myProduct.setImage("image");
            myProducts.add(myProduct);
        }
        myProductAdapter.notifyDataSetChanged();

        return root;
    }
}
