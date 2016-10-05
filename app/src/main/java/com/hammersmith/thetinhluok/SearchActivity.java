package com.hammersmith.thetinhluok;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hammersmith.thetinhluok.adapter.ProductAdapter;
import com.hammersmith.thetinhluok.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editText;
    private ImageView clearText;
    private ProgressDialog mProgressDialog;
    private String str_search;

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private GridLayoutManager layoutManager;
    private List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final int columns = getResources().getInteger(R.integer.number_column);
        layoutManager = new GridLayoutManager(SearchActivity.this, columns);
        recyclerView.setLayoutManager(layoutManager);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        clearText = (ImageView) findViewById(R.id.search_clear);
        editText = (EditText) findViewById(R.id.editText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    str_search = editText.getText().toString();
                    str_search = str_search.replaceAll(" ", "_").toLowerCase();
                    showProgressDialog();
                    ApiInterface serviceFilter = ApiClient.getClient().create(ApiInterface.class);
                    Call<List<Product>> callFilter = serviceFilter.filterByName(str_search);
                    callFilter.enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                            products = response.body();
                            if (products.size() < 1) {
                                findViewById(R.id.txtFilter).setVisibility(View.VISIBLE);
                                adapter = new ProductAdapter(SearchActivity.this, products);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                findViewById(R.id.txtFilter).setVisibility(View.GONE);
                                adapter = new ProductAdapter(SearchActivity.this, products);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            hideProgressDialog();
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {

                        }
                    });

                    return true;
                }
                return false;
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(SearchActivity.this);
            mProgressDialog.setMessage("Filtering...");
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
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
