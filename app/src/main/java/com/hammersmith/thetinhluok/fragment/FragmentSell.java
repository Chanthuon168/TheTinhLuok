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
    private List<String> images = new ArrayList<>();
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
    private ProgressDialog mProgressDialog, dialog;

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
        root.findViewById(R.id.read).setOnClickListener(this);
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
        showDialog();
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
                    hideDialog();
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
            case R.id.read:
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View viewDialog = factory.inflate(R.layout.dialog, null);
                final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                dialog.setView(viewDialog);
                TextView message = (TextView) viewDialog.findViewById(R.id.message);
                message.setText("What is a Privacy Policy\n" +
                        "\n" +
                        "A Privacy Policy is the legal statement that specifies what the business owner does with the personal data collected from users, along with how the data is processed and why.\n" +
                        "\n" +
                        "In 1968, Council of Europe did studies on the threat of the Internet expansion as they were concerned with the effects of technology on human rights. This lead to the development of policies that were to be developed to protect personal data.\n" +
                        "\n" +
                        "This marks the start of what we know now as a “Privacy Policy”. While the name “Privacy Policy” refers to the legal agreement, the concept of privacy and protecting user data is closely related.\n" +
                        "\n" +
                        "This agreement can also be known under these names:\n" +
                        "\n" +
                        "Privacy Statement\n" +
                        "Privacy Notice\n" +
                        "Privacy Information\n" +
                        "Privacy Page\n" +
                        "The Privacy Policy can be used for both your website and mobile app if it’s adapted to include the platforms your business operates on.\n" +
                        "\n" +
                        "The contents of a Privacy Policy may differ from one country to another, depending on the country legislation, but most privacy laws identify the following critical points that a business must comply with when dealing with personal data:\n" +
                        "\n" +
                        "Notice. Data collectors (meaning, you or your company) must make clear what they are doing with the personal information from users before gathering it.\n" +
                        "Choice. The companies collecting the data must respect the choices of users on what information to provide and how personal that provided information will be.\n" +
                        "Access. Users should be able to view or contest the accuracy of personal data collected by the company.\n" +
                        "Security. The companies are entirely responsible for the accuracy and security (keeping it properly away from unauthorized eyes and hands) of the collected personal information.\n" +
                        "This means that a “Privacy Policy” serves as a way to inform users how their personal information will be used, along with how the information will be collected and who has access to it.\n" +
                        "\n" +
                        "Who needs a Privacy Policy\n" +
                        "\n" +
                        "Any entity (company or individual) that collects or uses personal information from users will need a Privacy Policy.\n" +
                        "\n" +
                        "A Privacy Policy is required regardless of the type of platform your business operates on or what kind of industry you are in:\n" +
                        "\n" +
                        "Web sites\n" +
                        "WordPress blogs, or any other platforms: Joomla!, Drupal etc.\n" +
                        "E-commerce stores\n" +
                        "Mobile apps. Not having a Privacy Policy can be a reason for rejection during the app review.\n" +
                        "A Privacy Policy is required for all iOS apps. Section 17 of “Apple’s App Store Review Guidelines” and the “iOS Developer Program License” require developers with apps that collect personal information from users to have this legal agreement.");
                dialog.show();
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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] byteFormat = stream.toByteArray();
                final String encoded = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

                String url = ApiClient.BASE_URL + "upload/image";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        fileName.add(response);
                        images.add(response);
                        if (fileName.size() == imageList.size()) {
                            saveProduct();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialogError("Error while uploading product");
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
                dialogError("Error while uploading product");
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
                pushNotification();
                startActivity(new Intent(getActivity(), ContainerView.class));
                getActivity().finish();
            }
        });

        dialog.show();
    }

    private void dialogError(String strMessage) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View viewDialog = factory.inflate(R.layout.layout_dialog_new, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setView(viewDialog);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText(strMessage);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-exclamation-circle}");
        TextView cancel = (TextView) viewDialog.findViewById(R.id.cancel);
        cancel.setText("Try again");
        viewDialog.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                uploadFile();
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

    private void showDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(true);
        }

        dialog.show();
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.hide();
        }
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
        hideDialog();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void pushNotification() {
        StringRequest userReq = new StringRequest(Request.Method.POST, Constant.URL_PUSH_NOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(final String proId) {

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), " " + volleyError.toString(), Toast.LENGTH_SHORT).show();
                        Log.w("volleyError", "" + volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("message", user.getName() + " recently added new product");
                params.put("image", ApiClient.BASE_URL + "images/" + images.get(0));
                return params;
            }
        };
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        userReq.setRetryPolicy(policy);
        MyApplication.getInstance().addToRequestQueue(userReq);
    }
}
