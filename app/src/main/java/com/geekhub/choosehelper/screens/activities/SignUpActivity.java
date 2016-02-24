package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Bundle;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.BaseSignInActivity;

import butterknife.OnClick;

public class SignUpActivity extends BaseSignInActivity {

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
