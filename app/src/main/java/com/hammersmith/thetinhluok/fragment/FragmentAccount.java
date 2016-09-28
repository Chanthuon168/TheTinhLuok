package com.hammersmith.thetinhluok.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.Constant;
import com.hammersmith.thetinhluok.ContainerView;
import com.hammersmith.thetinhluok.MyApplication;
import com.hammersmith.thetinhluok.MyCommand;
import com.hammersmith.thetinhluok.PrefUtils;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.RoundedImageView;
import com.hammersmith.thetinhluok.model.User;
import com.joanzapata.iconify.widget.IconTextView;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.PhotoLoader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chan Thuon on 9/14/2016.
 */
public class FragmentAccount extends Fragment implements View.OnClickListener {
    private ImageView profile;
    GalleryPhoto galleryPhoto;
    private static final int SELECT_PHOTO = 100;
    private static String photoPath;
    private MyCommand myCommand;
    ArrayList<String> imageList = new ArrayList<>();
    private static String encoded;
    private User user;
    private EditText name, email, address, phone, phone2;
    private Context context;
    private User userPref;
    private ProgressDialog mProgressDialog, dialog;

    public FragmentAccount() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        user = PrefUtils.getCurrentUser(getActivity());
        profile = (ImageView) root.findViewById(R.id.profile);
        name = (EditText) root.findViewById(R.id.name);
        email = (EditText) root.findViewById(R.id.email);
        address = (EditText) root.findViewById(R.id.address);
        phone = (EditText) root.findViewById(R.id.phone);
        phone2 = (EditText) root.findViewById(R.id.phone2);
        profile.setOnClickListener(this);
        root.findViewById(R.id.lSave).setOnClickListener(this);
        galleryPhoto = new GalleryPhoto(getActivity());
        myCommand = new MyCommand(getActivity());
        getUser();
        showDialog();
        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
            case R.id.lSave:
                showProgressDialog();
                if (imageList.size() < 1) {
                    updateUser();
                } else {
                    uploadFile();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageList.clear();
        if (requestCode == SELECT_PHOTO && resultCode == getActivity().RESULT_OK) {
            galleryPhoto.setPhotoUri(data.getData());
            photoPath = galleryPhoto.getPath();
            imageList.add(photoPath);
            Log.d("path", photoPath);
            try {
                Bitmap bitmap = PhotoLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                profile.setImageBitmap(bitmap);
                encoded = getEncoded64ImageStringFromBitmap(bitmap);

            } catch (FileNotFoundException e) {
                Toast.makeText(getActivity(), "Error while loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile() {
        final String strName = name.getText().toString();
        final String strAddress = address.getText().toString();
        final String strPhone = phone.getText().toString();
        final String strPhone2 = phone2.getText().toString();
        final String strEmail = email.getText().toString();

        String url = ApiClient.BASE_URL + "upload/image";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String profileCode) {
                StringRequest userReq = new StringRequest(Request.Method.POST, Constant.URL_UPDATEUSER, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        imageList.clear();
                        PrefUtils.clearCurrentUser(getActivity());
                        try {
                            JSONObject obj = new JSONObject(s);
                            userPref = new User();
                            userPref.setName(obj.getString("name"));
                            userPref.setEmail(obj.getString("email"));
                            userPref.setSocialLink(obj.getString("social_link"));
                            userPref.setPhoto(obj.getString("photo"));
                            PrefUtils.setCurrentUser(userPref, getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hideProgressDialog();
                        dialogSuccess("Account was updated successfully");
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
                        params.put("name", strName);
                        params.put("email", strEmail);
                        params.put("address", strAddress);
                        params.put("phone", strPhone);
                        params.put("phone2", strPhone2);
                        params.put("social_link", user.getSocialLink());
                        params.put("image", ApiClient.BASE_URL + "images/" + profileCode);
                        return params;
                    }
                };
                int socketTimeout = 60000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                userReq.setRetryPolicy(policy);
                MyApplication.getInstance().addToRequestQueue(userReq);

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
        myCommand.execute();
    }

    private void getUser() {
        final StringRequest userReq = new StringRequest(Request.Method.POST, Constant.URL_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                hideDialog();
                try {
                    JSONObject obj = new JSONObject(s);
                    name.setText(obj.getString("name"));
                    email.setText(obj.getString("email"));
                    phone.setText(obj.getString("phone"));
                    phone2.setText(obj.getString("phone2"));
                    address.setText(obj.getString("address"));
                    Uri uri = Uri.parse(obj.getString("photo"));
                    context = profile.getContext();
                    Picasso.with(context).load(uri).into(profile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
//                        Toast.makeText(getActivity(), "" + volleyError.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("msg", volleyError.toString());
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

    private void updateUser() {
        final String strName = name.getText().toString();
        final String strAddress = address.getText().toString();
        final String strPhone = phone.getText().toString();
        final String strPhone2 = phone2.getText().toString();
        final String strEmail = email.getText().toString();
        StringRequest userReq = new StringRequest(Request.Method.POST, Constant.URL_UPDATEUSER, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                imageList.clear();
                PrefUtils.clearCurrentUser(getActivity());
                try {
                    JSONObject obj = new JSONObject(s);
                    userPref = new User();
                    userPref.setName(obj.getString("name"));
                    userPref.setEmail(obj.getString("email"));
                    userPref.setSocialLink(obj.getString("social_link"));
                    userPref.setPhoto(obj.getString("photo"));
                    PrefUtils.setCurrentUser(userPref, getActivity());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
                dialogSuccess("Account was updated successfully");
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
                params.put("name", strName);
                params.put("email", strEmail);
                params.put("address", strAddress);
                params.put("phone", strPhone);
                params.put("phone2", strPhone2);
                params.put("social_link", user.getSocialLink());
                params.put("image", user.getPhoto());
                return params;
            }
        };
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        userReq.setRetryPolicy(policy);
        MyApplication.getInstance().addToRequestQueue(userReq);
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

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Account updating...");
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

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

    private void showDialog(){
        if (dialog == null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(true);
        }

        dialog.show();
    }

    private void hideDialog(){
        if (dialog != null && dialog.isShowing()) {
            dialog.hide();
        }
    }
}
