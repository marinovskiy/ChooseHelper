package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.geekhub.choosehelper.R;

import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @OnClick(R.id.sign_in_tv)
    public void onClick() {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
    }
}
