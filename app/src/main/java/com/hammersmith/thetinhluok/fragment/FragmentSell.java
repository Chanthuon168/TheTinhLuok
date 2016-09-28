package com.hammersmith.thetinhluok.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
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
import com.hammersmith.thetinhluok.ContainerView;
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
    private LinearLayout lCategory, lPhoto, lInformation, l_next;
    private PhotoAdapter photoAdapter;
    private static String strProcess = "category";
    private TextView txtProcess;
    private List<String> imgpath = new ArrayList<>();
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
    private EditText titleProduct, price, discount, size, color, description, name, email, phone, phone2, address;
    private CheckBox checkBox;
    private TextView read, next;
    private IconTextView iconNext;
    private User user;
    private ProgressDialog mProgressDialog;

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
        l_next = (LinearLayout) root.findViewById(R.id.l_next);
        address = (EditText) root.findViewById(R.id.address);
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

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    dialogExit("Are you sure want to cancel your selling?");
                    return true;
                } else {
                    return false;
                }
            }
        });

        l_next.setVisibility(View.GONE);

        return root;
    }

    @Override
    public void itemClicked(View view, int position) {
        lCategory.setVisibility(View.GONE);
        lPhoto.setVisibility(View.VISIBLE);
        strProcess = "photo";
        txtProcess.setText("2/3 Import your photo");
        catId = categories.get(position).getId();
        l_next.setVisibility(View.VISIBLE);
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
                    dialogExit("Are you sure want to cancel your selling?");
                } else if (strProcess.equals("photo")) {
                    lInformation.setVisibility(View.GONE);
                    lPhoto.setVisibility(View.GONE);
                    lCategory.setVisibility(View.VISIBLE);
                    strProcess = "category";
                    txtProcess.setText("1/3 Choose the category");
                    next.setText("Next ");
                    iconNext.setText("{fa-arrow-circle-right}");
                    l_next.setVisibility(View.GONE);
                    imgpath.clear();
                    photoAdapter.notifyDataSetChanged();
                } else if (strProcess.equals("info")) {
                    lCategory.setVisibility(View.GONE);
                    lPhoto.setVisibility(View.VISIBLE);
                    lInformation.setVisibility(View.GONE);
                    strProcess = "photo";
                    txtProcess.setText("2/3 Import your photo");
                    next.setText("Next ");
                    iconNext.setText("{fa-arrow-circle-right}");
                    l_next.setVisibility(View.VISIBLE);
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
                    l_next.setVisibility(View.VISIBLE);
                } else if (strProcess.equals("photo")) {
                    if (imgpath.size() < 1) {
                        dialogImport("Import your photos before continue!");
                    } else {
                        lCategory.setVisibility(View.GONE);
                        lPhoto.setVisibility(View.GONE);
                        lInformation.setVisibility(View.VISIBLE);
                        strProcess = "info";
                        txtProcess.setText("3/3 Enter the information");
                        next.setText("Sell ");
                        iconNext.setText("{fa-check-circle}");
                        l_next.setVisibility(View.VISIBLE);
                        getUser();
                    }
                } else if (strProcess.equals("info")) {
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
                        if (checkBox.isChecked()) {
                            uploadFile();
                        } else {
                            Snackbar snack = Snackbar.make(view, "Please agree term and condition.", Snackbar.LENGTH_LONG);
                            View v = snack.getView();
                            TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.RED);
                            snack.show();
                        }
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
            imageList.clear();
            for (int i = 0; i < images.size(); i++) {
                imgpath.add(images.get(i).path);
                imageList.add(images.get(i).path);
            }
            photoAdapter.notifyDataSetChanged();
            Log.d("image", "" + imageList);
        }
    }

    private void uploadFile() {
        showProgressDialog();
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
                        Log.d("response", response);
                        fileName.add(response);
                        Log.d("fileName", "" + fileName);
                        if (fileName.size() == imageList.size()) {
                            saveProduct();
                        }
//                        if (response.equals("uploaded_success")){
//                            sellProduct();
//                        }
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
                    address.setText(obj.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "" + volleyError.toString(), Toast.LENGTH_SHORT).show();
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
        activate.setText("Yes");
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strProcess = "category";
                startActivity(new Intent(getActivity(), ContainerView.class));
                getActivity().finish();
            }
        });
        TextView cancel = (TextView) viewDialog.findViewById(R.id.cancel);
        cancel.setText("No");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void dialogImport(String strMessage) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View viewDialog = factory.inflate(R.layout.layout_dialog_new, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setView(viewDialog);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText(strMessage);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-picture-o}");
        TextView cancel = (TextView) viewDialog.findViewById(R.id.cancel);
        cancel.setText("Yes");
        viewDialog.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void dialogSuccess(String strMessage) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View viewDialog = factory.inflate(R.layout.layout_dialog_new, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setView(viewDialog);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText(strMessage);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-check-circle-o}");
        TextView cancel = (TextView) viewDialog.findViewById(R.id.cancel);
        cancel.setText("SUCCESS");
        viewDialog.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ContainerView.class));
                getActivity().finish();
            }
        });

        dialog.show();
    }

    private void saveProduct() {
        final String strTitle = titleProduct.getText().toString();
        final String strPrice = price.getText().toString();
        final String strDiscount = discount.getText().toString();
        final String strSize = size.getText().toString();
        final String strColor = color.getText().toString();
        final String strDescription = description.getText().toString();
        final String strName = name.getText().toString();
        final String strEmail = email.getText().toString();
        final String strPhone = phone.getText().toString();
        final String strPhone2 = phone2.getText().toString();
        final String strAddress = address.getText().toString();
        StringRequest userReq = new StringRequest(Request.Method.POST, Constant.URL_SAVEPRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(final String proId) {
                for (final String nameList : fileName) {
                    StringRequest userReq = new StringRequest(Request.Method.POST, Constant.URL_SAVEIMAGE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            imageList.clear();
                            fileName.clear();
                            strProcess = "category";
                            hideProgressDialog();
                            dialogSuccess("Your product uploaded successfully");
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    Toast.makeText(getActivity(), " " + volleyError.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("pro_id", proId);
                            params.put("image", nameList);
                            return params;
                        }
                    };
                    int socketTimeout = 60000;
                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    userReq.setRetryPolicy(policy);
                    MyApplication.getInstance().addToRequestQueue(userReq);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), " " + volleyError.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cat_id", String.valueOf(catId));
                params.put("title", strTitle);
                params.put("image", fileName.get(0));
                params.put("price", strPrice);
                params.put("discount", strDiscount);
                params.put("size", strSize);
                params.put("color", strColor);
                params.put("description", strDescription);
                params.put("name", strName);
                params.put("email", strEmail);
                params.put("phone", strPhone);
                params.put("phone2", strPhone2);
                params.put("social_link", user.getSocialLink());
                params.put("address", strAddress);
                return params;
            }
        };
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        userReq.setRetryPolicy(policy);
        MyApplication.getInstance().addToRequestQueue(userReq);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Product uploading...");
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
