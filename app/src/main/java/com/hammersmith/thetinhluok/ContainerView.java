package com.hammersmith.thetinhluok;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.hammersmith.thetinhluok.fragment.FragmentAbout;
import com.hammersmith.thetinhluok.fragment.FragmentAccount;
import com.hammersmith.thetinhluok.fragment.FragmentFavorite;
import com.hammersmith.thetinhluok.fragment.FragmentHome;
import com.hammersmith.thetinhluok.fragment.FragmentLanguage;
import com.hammersmith.thetinhluok.fragment.FragmentMyProduct;
import com.hammersmith.thetinhluok.fragment.FragmentProduct;
import com.hammersmith.thetinhluok.fragment.FragmentSell;
import com.hammersmith.thetinhluok.model.User;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

public class ContainerView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentHome fragmentHome;
    private TextView name, email;
    private RoundedImageView profile;
    private User user;
    private Context context = ContainerView.this;
    private View mHeaderView;
    private Boolean helper_home = false;
    private Boolean helper_product = false;
    private Boolean helper_sell = false;
    private Boolean helper_account = false;
    private Boolean helper_my_product = false;
    private Boolean helper_favorite = false;
    private Boolean helper_about = false;
    private Boolean helper_language = false;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_view);
        user = PrefUtils.getCurrentUser(ContainerView.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            initScreen();
            helper_home = true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mHeaderView = navigationView.getHeaderView(0);
        name = (TextView) mHeaderView.findViewById(R.id.name);
        email = (TextView) mHeaderView.findViewById(R.id.email);
        profile = (RoundedImageView) mHeaderView.findViewById(R.id.profile);
        name.setText(user.getName());
        email.setText(user.getEmail());
        Uri uri = Uri.parse(user.getPhoto());
        context = profile.getContext();
        Picasso.with(context).load(uri).into(profile);


        Menu m = navigationView.getMenu();
        m.findItem(R.id.nav_home).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_home));
        m.findItem(R.id.nav_products).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_list));
        m.findItem(R.id.nav_sell).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_camera));
        m.findItem(R.id.nav_account).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_user));
        m.findItem(R.id.nav_my_product).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_tags));
        m.findItem(R.id.nav_favorite).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_star_half_o));
        m.findItem(R.id.nav_logout).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sign_out));
        m.findItem(R.id.nav_about).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_info));
        m.findItem(R.id.nav_language).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_language));

    }

    private void initScreen() {
        fragmentHome = new FragmentHome();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_framelayout, fragmentHome);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.container_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            toolbar.setTitle("The TinhLuok");
            helper_product = false;
            helper_sell = false;
            helper_account = false;
            helper_my_product = false;
            helper_favorite = false;
            helper_about = false;
            helper_language = false;
            if (helper_home == false) {
                fragmentHome = new FragmentHome();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container_framelayout, fragmentHome);
                transaction.addToBackStack(null);
                transaction.commit();
                helper_home = true;
            }

        } else if (id == R.id.nav_products) {
            toolbar.setTitle("Product");
            helper_home = false;
            helper_sell = false;
            helper_account = false;
            helper_my_product = false;
            helper_favorite = false;
            helper_about = false;
            helper_language = false;
            if (helper_product == false) {
                FragmentProduct fragmentProduct = new FragmentProduct();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container_framelayout, fragmentProduct);
                transaction.addToBackStack(null);
                transaction.commit();
                helper_product = true;
            }

        } else if (id == R.id.nav_sell) {
            toolbar.setTitle("Sell product");
            helper_home = false;
            helper_product = false;
            helper_account = false;
            helper_my_product = false;
            helper_favorite = false;
            helper_about = false;
            helper_language = false;
            if (helper_sell == false) {
                FragmentSell fragmentSell = new FragmentSell();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container_framelayout, fragmentSell);
                transaction.addToBackStack(null);
                transaction.commit();
                helper_sell = true;
            }

        } else if (id == R.id.nav_account) {
            toolbar.setTitle("My account");
            helper_home = false;
            helper_product = false;
            helper_sell = false;
            helper_my_product = false;
            helper_favorite = false;
            helper_about = false;
            helper_language = false;
            if (helper_account == false) {
                FragmentAccount fragmentAccount = new FragmentAccount();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container_framelayout, fragmentAccount);
                transaction.addToBackStack(null);
                transaction.commit();
                helper_account = true;
            }

        } else if (id == R.id.nav_my_product) {
            toolbar.setTitle("My products");
            helper_home = false;
            helper_product = false;
            helper_account = false;
            helper_sell = false;
            helper_favorite = false;
            helper_about = false;
            helper_language = false;
            if (helper_my_product == false) {
                FragmentMyProduct fragmentMyProduct = new FragmentMyProduct();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container_framelayout, fragmentMyProduct);
                transaction.addToBackStack(null);
                transaction.commit();
                helper_my_product = true;
            }

        } else if (id == R.id.nav_favorite) {
            toolbar.setTitle("My Favorite");
            helper_home = false;
            helper_product = false;
            helper_account = false;
            helper_my_product = false;
            helper_sell = false;
            helper_about = false;
            helper_language = false;
            if (helper_favorite == false) {
                FragmentFavorite fragmentFavorite = new FragmentFavorite();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container_framelayout, fragmentFavorite);
                transaction.addToBackStack(null);
                transaction.commit();
                helper_favorite = true;
            }
            helper_favorite = true;

        } else if (id == R.id.nav_logout) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            PrefUtils.clearCurrentUser(ContainerView.this);
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(ContainerView.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_about) {
            toolbar.setTitle("About us");
            helper_home = false;
            helper_product = false;
            helper_account = false;
            helper_my_product = false;
            helper_favorite = false;
            helper_sell = false;
            helper_language = false;
            if (helper_about == false) {
                FragmentAbout fragmentAbout = new FragmentAbout();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container_framelayout, fragmentAbout);
                transaction.addToBackStack(null);
                transaction.commit();
                helper_about = true;
            }

        } else if (id == R.id.nav_language) {
            toolbar.setTitle("Language");
            helper_home = false;
            helper_product = false;
            helper_account = false;
            helper_my_product = false;
            helper_favorite = false;
            helper_about = false;
            helper_sell = false;
            if (helper_language == false) {
                FragmentLanguage fragmentLanguage = new FragmentLanguage();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container_framelayout, fragmentLanguage);
                transaction.addToBackStack(null);
                transaction.commit();
                helper_language = true;
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
