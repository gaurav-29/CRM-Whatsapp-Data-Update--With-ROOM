package com.oceanmtech.crmwhatsappdataupdate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.oceanmtech.crmwhatsappdataupdate.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    RegisterActivity mContext = RegisterActivity.this;
    ActivityRegisterBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(mContext, R.layout.activity_register);

        onClickListeners();
    }

    private void onClickListeners() {
        mBinding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.etMobile.getText().toString().trim().equalsIgnoreCase("") ||
                        mBinding.etMobile.getText().toString().length() < 10)
                    Toast.makeText(mContext, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                else if (!Patterns.EMAIL_ADDRESS.matcher(mBinding.etEmail.getText().toString().trim()).matches() ||
                        mBinding.etEmail.getText().toString().trim().equalsIgnoreCase(""))
                    Toast.makeText(mContext, "Please enter valid email", Toast.LENGTH_SHORT).show();
                else if(mBinding.etPassword.getText().toString().trim().equalsIgnoreCase(""))
                    Toast.makeText(mContext, "Please enter valid password", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(mContext, "Sign up Successful", Toast.LENGTH_SHORT).show();
                    //SignUp();
                    Intent i = new Intent(mContext, LoginActivity.class);
                    startActivity(i);
                }
            }
        });
        mBinding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
            }
        });
    }
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}