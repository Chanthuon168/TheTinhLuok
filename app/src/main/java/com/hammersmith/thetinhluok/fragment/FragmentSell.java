package com.hammersmith.thetinhluok.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.ApiInterface;
import com.hammersmith.thetinhluok.Constant;
import com.hammersmith.thetinhluok.MyApplication;
import com.hammersmith.thetinhluok.MyCommand;
import com.hammersmith.thetinhluok.PrefUtils;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.adapter.CategoryAdapter;
import com.hammersmith.thetinhluok.adapter.GridviewImage;
import com.hammersmith.thetinhluok.adapter.PhotoAdapter;
import com.hammersmith.thetinhluok.model.Category;
import com.hammersmith.thetinhluok.model.Love;
import com.hammersmith.thetinhluok.model.Photo;
import com.hammersmith.thetinhluok.model.User;
import com.joanzapata.iconify.widget.IconTextView;
import com.kosalgeek.android.photoutil.PhotoLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chan Thuon on 9/14/2016.
 */
public class FragmentSell extends Fragment implements CategoryAdapter.ClickListener, View.OnClickListener {
    private Category category;
    private List<Category> categories = new ArrayList<>();
    private RecyclerView recyclerViewCategory, recyclerViewPhoto;
    private CategoryAdapter categoryAdapter;
    private LinearLayoutManager layoutManager;
    private GridLayoutManager layoutManagerPhoto;
    private LinearLayout lCategory, lPhoto, lInformation;
    private PhotoAdapter photoAdapter;
    private static String strProcess = "category";
    private TextView txtProcess;
    private List<String> imgpath = new ArrayList<>();
    private String pathImage = "'['";
    private List<String> fileName = new ArrayList<>();
    private int catId;
    int socketTimeout = 60000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    private MyCommand myCommand;
    ArrayList<String> imageList = new ArrayList<>();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private EditText titleProduct, price, discount, size, color, description, name, email, phone, phone2, website, facebook;
    private CheckBox checkBox;
    private TextView read, next;
    private IconTextView iconNext;
    private User user;

    public FragmentSell() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sell, container, false);
        user = PrefUtils.getCurrentUser(getActivity());
        root.findViewById(R.id.l_back).setOnClickListener(this);
        root.findViewById(R.id.l_next).setOnClickListener(this);
        root.findViewById(R.id.lImportPhoto).setOnClickListener(this);
        titleProduct = (EditText) root.findViewById(R.id.titleProduct);
        price = (EditText) root.findViewById(R.id.price);
        discount = (EditText) root.findViewById(R.id.discount);
        size = (EditText) root.findViewById(R.id.size);
        color = (EditText) root.findViewById(R.id.color);
        description = (EditText) root.findViewById(R.id.description);
        name = (EditText) root.findViewById(R.id.name);
        email = (EditText) root.findViewById(R.id.email);
        phone = (EditText) root.findViewById(R.id.phone);
        phone2 = (EditText) root.findViewById(R.id.phone2);
        website = (EditText) root.findViewById(R.id.website);
        facebook = (EditText) root.findViewById(R.id.facebook);
        checkBox = (CheckBox) root.findViewById(R.id.checkbox);
        read = (TextView) root.findViewById(R.id.read);
        next = (TextView) root.findViewById(R.id.next);
        iconNext = (IconTextView) root.findViewById(R.id.iconNext);
        txtProcess = (TextView) root.findViewById(R.id.txtProcessing);
        lCategory = (LinearLayout) root.findViewById(R.id.l_category);
        lPhoto = (LinearLayout) root.findViewById(R.id.l_photo);
        lInformation = (LinearLayout) root.findViewById(R.id.l_information);
        recyclerViewCategory = (RecyclerView) root.findViewById(R.id.recyclerViewCategory);
        recyclerViewPhoto = (RecyclerView) root.findViewById(R.id.recyclerViewPhoto);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManagerPhoto = new GridLayoutManager(getActivity(), 2);
        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        recyclerViewCategory.setLayoutManager(layoutManager);
        recyclerViewCategory.setAdapter(categoryAdapter);
        categoryAdapter.setClickListener(this);
        myCommand = new MyCommand(getActivity());
        verifyStoragePermissions(getActivity());
        filterCategory();

        photoAdapter = new PhotoAdapter(getActivity(), imgpath);
        recyclerViewPhoto.setLayoutManager(layoutManagerPhoto);
        recyclerViewPhoto.setAdapter(photoAdapter);


        return root;
    }

    @Override
    public void itemClicked(View view, int position) {
        lCategory.setVisibility(View.GONE);
        lPhoto.setVisibility(View.VISIBLE);
        strProcess = "photo";
        txtProcess.setText("2/3 Import your photo");
        catId = categories.get(position).getId();
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
                    next.setText("Next ");
                    iconNext.setText("{fa-arrow-circle-right}");
                } else if (strProcess.equals("info")) {
                    lCategory.setVisibility(View.GONE);
                    lPhoto.setVisibility(View.VISIBLE);
                    lInformation.setVisibility(View.GONE);
                    strProcess = "photo";
                    txtProcess.setText("2/3 Import your photo");
                    next.setText("Next ");
                    iconNext.setText("{fa-arrow-circle-right}");
                }
                break;
            case R.id.l_next:
                if (strProcess.equals("category")) {
                    lPhoto.setVisibility(View.VISIBLE);
                    lCategory.setVisibility(View.GONE);
                    lInformation.setVisibility(View.GONE);
                    strProcess = "photo";
                    txtProcess.setText("2/3 Import your photo");
                    next.setText("Next ");
                    iconNext.setText("{fa-arrow-circle-right}");
                } else if (strProcess.equals("photo")) {
                    lCategory.setVisibility(View.GONE);
                    lPhoto.setVisibility(View.GONE);
                    lInformation.setVisibility(View.VISIBLE);
                    strProcess = "info";
                    txtProcess.setText("3/3 Enter the information");
                    next.setText("Sell ");
                    iconNext.setText("{fa-check-circle}");
                    getUser();
                } else if (strProcess.equals("info")) {
//                    uploadFile();
                    if (name.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Seller name required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (phone.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Phone number required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (titleProduct.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Product title required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (price.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Price required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (description.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Description required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else {
                        sellProduct();
                    }
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
            fileName.clear();
            imageList.clear();
            String path;
            String temPath = "";
            for (int i = 0; i < images.size(); i++) {
                imgpath.add(images.get(i).path);
                fileName.add(images.get(i).name);
                imageList.add(images.get(i).path);
                path = '"' + images.get(i).path + '"';
                temPath = path + ',' + path;
            }
            pathImage = '[' + temPath + ']';
            photoAdapter.notifyDataSetChanged();
            Log.d("filename", "" + fileName);
            Log.d("image", "" + imageList);
        }
    }

    private void uploadFile() {
        for (String imagePath : imageList) {
            try {
                Bitmap bitmap = PhotoLoader.init().from(imagePath).requestSize(512, 512).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                final String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                String url = ApiClient.BASE_URL + "upload.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error while uploading image", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("image", encoded);
                        return params;
                    }
                };
                myCommand.add(stringRequest);
            } catch (FileNotFoundException e) {
                Toast.makeText(getContext(), "Error while loading image", Toast.LENGTH_SHORT).show();
            }
        }
        myCommand.execute();
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void sellProduct() {
        String strTitle = titleProduct.getText().toString();
        String strPrice = price.getText().toString();
        String strDiscount = discount.getText().toString();
        String strSize = size.getText().toString();
        String strColor = color.getText().toString();
        String strDescription = description.getText().toString();
        String strName = name.getText().toString();
        String strEmail = email.getText().toString();
        String strPhone = phone.getText().toString();
        String strPhone2 = phone2.getText().toString();
        String strWebsite = website.getText().toString();
        String strFacebook = facebook.getText().toString();
    }

    private void getUser() {
        final StringRequest userReq = new StringRequest(Request.Method.POST, Constant.URL_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj = new JSONObject(s);
                    name.setText(obj.getString("name"));
                    email.setText(obj.getString("email"));
                    phone.setText(obj.getString("phone"));
                    phone2.setText(obj.getString("phone2"));
                    website.setText(obj.getString("website"));
                    facebook.setText(obj.getString("facebook"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "history " + volleyError.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("social_link", user.getSocialLink());
                return params;
            }
        };
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        userReq.setRetryPolicy(policy);
        MyApplication.getInstance().addToRequestQueue(userReq);
    }
}
