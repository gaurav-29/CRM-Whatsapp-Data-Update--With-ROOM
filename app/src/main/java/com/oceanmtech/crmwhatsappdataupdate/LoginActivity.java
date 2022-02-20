package com.oceanmtech.crmwhatsappdataupdate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.oceanmtech.crmwhatsappdataupdate.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    LoginActivity mContext = LoginActivity.this;
    ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(mContext, R.layout.activity_login);

        onClickListeners();
    }

    private void onClickListeners() {

        mBinding.tvLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.etMobile.getText().toString().equalsIgnoreCase("") ||
                        mBinding.etMobile.getText().toString().length() < 10)
                {
                    Toast.makeText(mContext, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                }
                else if(mBinding.etPassword.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(mContext, "Please enter valid password", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mContext, "Log In Successful", Toast.LENGTH_SHORT).show();
                    //SignIn();
                    Intent i = new Intent(mContext, MainActivity.class);
                    startActivity(i);
                }
            }
        });

        mBinding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(mContext, RegisterActivity.class);
                    startActivity(i);
            }
        });
    }
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}