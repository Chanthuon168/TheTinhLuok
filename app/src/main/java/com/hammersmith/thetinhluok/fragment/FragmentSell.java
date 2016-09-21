package com.hammersmith.thetinhluok.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.ApiInterface;
import com.hammersmith.thetinhluok.Constant;
import com.hammersmith.thetinhluok.MyApplication;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.adapter.CategoryAdapter;
import com.hammersmith.thetinhluok.adapter.GridviewImage;
import com.hammersmith.thetinhluok.adapter.PhotoAdapter;
import com.hammersmith.thetinhluok.model.Category;
import com.hammersmith.thetinhluok.model.Love;
import com.hammersmith.thetinhluok.model.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan Thuon on 9/14/2016.
 */
public class FragmentSell extends Fragment implements CategoryAdapter.ClickListener, View.OnClickListener {
    private Category category;
    private List<Category> categories = new ArrayList<>();
    private RecyclerView recyclerViewCategory, recyclerViewPhoto;
    private CategoryAdapter categoryAdapter;
    private LinearLayoutManager layoutManager, layoutManagerPhoto;
    private LinearLayout lCategory, lPhoto, lInformation;
    private Photo photo;
    private List<Photo> photos = new ArrayList<>();
    private PhotoAdapter photoAdapter;
    private static String strProcess = "category";
    private TextView txtProcess;
    private ImageView imageView;
    private LinearLayout linearLayout1;
    private List<String> imgpath = new ArrayList<>();
    private GridView gridView;
    private GridviewImage gridAdapter;

    int socketTimeout = 60000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);


    public FragmentSell() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sell, container, false);
        imageView = (ImageView) root.findViewById(R.id.image);
        root.findViewById(R.id.l_back).setOnClickListener(this);
        root.findViewById(R.id.l_next).setOnClickListener(this);
        root.findViewById(R.id.lImportPhoto).setOnClickListener(this);
        txtProcess = (TextView) root.findViewById(R.id.txtProcessing);
        lCategory = (LinearLayout) root.findViewById(R.id.l_category);
        lPhoto = (LinearLayout) root.findViewById(R.id.l_photo);
        lInformation = (LinearLayout) root.findViewById(R.id.l_information);
        recyclerViewCategory = (RecyclerView) root.findViewById(R.id.recyclerViewCategory);
//        recyclerViewPhoto = (RecyclerView) root.findViewById(R.id.recyclerViewPhoto);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManagerPhoto = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        recyclerViewCategory.setLayoutManager(layoutManager);
        recyclerViewCategory.setAdapter(categoryAdapter);
        categoryAdapter.setClickListener(this);

        filterCategory();

        gridView = (GridView) root.findViewById(R.id.grid_view);
        gridAdapter = new GridviewImage(getActivity(), imgpath);
        gridView.setAdapter(gridAdapter);

        return root;
    }

    @Override
    public void itemClicked(View view, int position) {
        lCategory.setVisibility(View.GONE);
        lPhoto.setVisibility(View.VISIBLE);
        strProcess = "photo";
    }

    private void filterCategory() {
        if (categories.size() <= 0) {
            JsonArrayRequest fieldReq = new JsonArrayRequest(Constant.URL_CATEGORY, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            category = new Category();
                            category.setId(obj.getInt("id"));
                            category.setImage(obj.getString("image"));
                            category.setTitle(obj.getString("name"));
                            categories.add(category);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getActivity(), volleyError + "", Toast.LENGTH_SHORT).show();
                }
            });
            fieldReq.setRetryPolicy(policy);
            MyApplication.getInstance().addToRequestQueue(fieldReq);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.l_back:
                if (strProcess.equals("category")) {
                    Toast.makeText(getActivity(), "close", Toast.LENGTH_SHORT).show();
                } else if (strProcess.equals("photo")) {
                    lInformation.setVisibility(View.GONE);
                    lPhoto.setVisibility(View.GONE);
                    lCategory.setVisibility(View.VISIBLE);
                    strProcess = "category";
                    txtProcess.setText("1/3 Choose the category");
                } else if (strProcess.equals("info")) {
                    lCategory.setVisibility(View.GONE);
                    lPhoto.setVisibility(View.VISIBLE);
                    lInformation.setVisibility(View.GONE);
                    strProcess = "photo";
                    txtProcess.setText("2/3 Import your photo");
                }
                break;
            case R.id.l_next:
                if (strProcess.equals("category")) {
                    lPhoto.setVisibility(View.VISIBLE);
                    lCategory.setVisibility(View.GONE);
                    lInformation.setVisibility(View.GONE);
                    strProcess = "photo";
                    txtProcess.setText("2/3 Import your photo");
                } else if (strProcess.equals("photo")) {
                    lCategory.setVisibility(View.GONE);
                    lPhoto.setVisibility(View.GONE);
                    lInformation.setVisibility(View.VISIBLE);
                    strProcess = "info";
                    txtProcess.setText("3/3 Enter the information");
                } else if (strProcess.equals("info")) {
                    Toast.makeText(getActivity(), "finish", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.lImportPhoto:
                Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 4);
                startActivityForResult(intent, Constants.REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == resultCode && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            imgpath.clear();
            for (int i = 0, l = images.size(); i < l; i++) {
                imgpath.add(images.get(i).path);
            }
            gridAdapter.notifyDataSetChanged();
        }
    }
}
