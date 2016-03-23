package com.geekhub.choosehelper.screens.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.db.DbUsersManager;

import butterknife.Bind;

public class ProfileActivity extends BaseSignInActivity {

    private static final String TAG = "ProfileActivity";

    @Bind(R.id.profile_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.profile_iv_avatar)
    ImageView mIvUserAvatar;

    @Bind(R.id.profile_tv_username)
    TextView mUsername;

    @Bind(R.id.profile_tv_email)
    TextView mEmail;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get user profile info
        mUser = DbUsersManager.getUserNotAsync(Prefs.getUserId());

        Glide.with(getApplicationContext())
                .load(mUser.getPhotoUrl())
                .into(mIvUserAvatar);
        mEmail.setText(mUser.getEmail());
        mUsername.setText(mUser.getFullName());

        // Init toolbar
        setupToolbar();

        /*final String[] img = {null};
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
        });*/
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

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.toolbar_title);
            getSupportActionBar().setHomeAsUpIndicator(ContextCompat
                    .getDrawable(getApplicationContext(), R.drawable.icon_arrow_back));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
