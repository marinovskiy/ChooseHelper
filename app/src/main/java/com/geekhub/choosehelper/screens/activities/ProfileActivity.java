package com.geekhub.choosehelper.screens.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import butterknife.Bind;

public class ProfileActivity extends BaseSignInActivity {

    private static final String TAG = "ProfileActivity";

    @Bind(R.id.toolbar_profile)
    Toolbar mToolbar;

    @Bind(R.id.profile_iv_avatar)
    ImageView mIvUserAvatar;

    @Bind(R.id.profile_test_image)
    ImageView mIvTestImg;

    @Bind(R.id.profile_tv_email)
    TextView mEmail;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mUser = DbUsersManager.getUserNotAsync(Prefs.getUserId());
        Glide.with(getApplicationContext())
                .load(mUser.getPhotoUrl())
                .into(mIvUserAvatar);
        mEmail.setText(mUser.getEmail());
        //AuthorizationUtil.getUserAsync(Prefs.getUserId());
        setupToolbar();
        /*User user = DbUsersManager.getUserAsync(Prefs.getUserId());
        if (user != null && user.isLoaded()) {
            Log.i("MainActivityTest", "setupNavHeader: " + user.getId());
            Log.i("MainActivityTest", "setupNavHeader: " + user.getFullName());
            Log.i("MainActivityTest", "setupNavHeader: " + user.getEmail());
            Log.i("MainActivityTest", "setupNavHeader: " + user.getPhotoUrl());
            Log.i("MainActivityTest", "setupNavHeader: " + user.getBirthday());
            Log.i("MainActivityTest", "setupNavHeader: " + user.getPlaceLive());
            Log.i("MainActivityTest", "setupNavHeader: " + user.getAbout());
        }*/
        /*Glide
                .with(this)
                .load(Prefs.getUserAvatarUrl())
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .into(mIvUserAvatar);*/
        final String[] img = {null};
        Firebase firebase = new Firebase("https://choosehelper.firebaseio.com/img");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "" + dataSnapshot.getValue());
                img[0] = String.valueOf(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i(TAG, "error: " + firebaseError);
            }
        });
        //mIvTestImg.setImageBitmap(Utils.convertStringToBitmap(img[0]));
        /*Glide
                .with(this)
                .load(Utils.convertStringToBitmap(img[0]))
                .into(mIvTestImg);*/

    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mUser.getFullName());
            getSupportActionBar().setHomeAsUpIndicator(ContextCompat
                    .getDrawable(getApplicationContext(), R.drawable.icon_arrow_back));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
