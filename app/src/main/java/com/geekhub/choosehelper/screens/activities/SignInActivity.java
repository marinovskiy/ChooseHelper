package com.geekhub.choosehelper.screens.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.screens.authentication.GooglePlusProfile;
import com.geekhub.choosehelper.screens.entities.UserProfile;
import com.geekhub.choosehelper.screens.keys.Key;
import com.google.android.gms.common.AccountPicker;

public class SignInActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final String[] ACCOUNT_TYPES = new String[]{"com.google"};
    private static final String GOOGLE_ACCOUNT_TYPE = "com.google";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button btnSignInGoogle = (Button) findViewById(R.id.sign_in_btn_google_plus);
        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUserAccount();
            }
        });
    }

//    @OnClick(R.id.sign_up_tv)
//    public void onClick() {
//        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
//    }

    private void pickUserAccount() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                ACCOUNT_TYPES, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);

        //  LOG
        Log.d(Key.LOG_TAG, "Started activity for result");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                String userEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                getUserProfile(new Account(userEmail, GOOGLE_ACCOUNT_TYPE));

                //  LOG
                Log.d(Key.LOG_TAG, "Done");
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.tip_incorrect_r_code,
                    Toast.LENGTH_SHORT).show();
        }
//        Intent intent = new Intent(this, ProfileActivity.class);
//        intent.putExtras(data);
//        startActivity(intent);
    }

    private UserProfile getUserProfile(Account account) {
        UserProfile userProfile = null;

        new GooglePlusProfile(this, account).execute();

        //  Return new user profile
        return userProfile;
    }
}
