package com.hammersmith.thetinhluok;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hammersmith.thetinhluok.model.User;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 9/9/2016.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private User user;
    private EditText name, email, password, confirmPassword;
    private String strName, strEmail, strPassword, strConfirmPassword, strPhoto;
    private View view;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        view = findViewById(R.id.layout_register);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        name = (EditText) findViewById(R.id.input_username);
        email = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);
        confirmPassword = (EditText) findViewById(R.id.input_confirm_password);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                break;
            case R.id.register:
                strName = name.getText().toString();
                strEmail = email.getText().toString();
                strPassword = password.getText().toString();
                strConfirmPassword = confirmPassword.getText().toString();
                strPhoto = ApiClient.BASE_URL + "images/user.png";
                if (strName.equals("") || strName.length() < 6) {
                    Snackbar snackbar = Snackbar.make(view, "Username at least 6 characters", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (!isEmailValid(strEmail)) {
                    Snackbar snackbar = Snackbar.make(view, "Email is incorrect", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (!strPassword.equals(strConfirmPassword)) {
                    Snackbar snackbar = Snackbar.make(view, "Password is not match", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    userRegister(strName, strEmail, strPassword, strPhoto);
                }
                break;
        }
    }

    private void userRegister(String name, String email, String password, String photo) {
        showProgressDialog();
        user = new User(name, email, password, photo);
        ApiInterface serviceRegister = ApiClient.getClient().create(ApiInterface.class);
        Call<User> callRegister = serviceRegister.userRegister(user);
        callRegister.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                hideProgressDialog();
                if (user.getMsg().equals("Account has been registered")) {
                    Intent intentNew = new Intent(RegisterActivity.this, LoginActivity.class);
                    intentNew.putExtra("isNewItem", "register");
                    intentNew.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentNew);
                    finish();
                } else {
                    dialogMessage(user.getMsg());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Processing...");
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
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void dialogMessage(String strMessage) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View viewDialog = factory.inflate(R.layout.layout_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(viewDialog);
        viewDialog.findViewById(R.id.cancel).setVisibility(View.GONE);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText(strMessage);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-times-circle}");
        viewDialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
