package com.geekhub.choosehelper.screens.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.network.NetworkVariant;
import com.geekhub.choosehelper.utils.FirebaseManager;
import com.geekhub.choosehelper.utils.Prefs;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCompareActivity extends BaseSignInActivity {

    @Bind(R.id.toolbar_add_compare)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_add_compare)
    View mToolbarShadow;

    @Bind(R.id.et_title)
    EditText mEtTitle;

    @Bind(R.id.et_first)
    EditText mEtFirst;

    @Bind(R.id.et_second)
    EditText mEtSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_compare);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.icon_arrow_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.btn_new)
    public void onClick() {
        String title = mEtTitle.getText().toString();
        String first = mEtFirst.getText().toString();
        String second = mEtSecond.getText().toString();
        NetworkVariant networkVariant1 = new NetworkVariant();
        networkVariant1.setImageUrl("");
        networkVariant1.setDescription(first);
        NetworkVariant networkVariant2 = new NetworkVariant();
        networkVariant2.setImageUrl(generateImage());
        networkVariant2.setDescription(second);
        List<NetworkVariant> variants = new ArrayList<>();
        variants.add(networkVariant1);
        variants.add(networkVariant2);
        FirebaseManager.addCompare(Prefs.getUserId(), "Alex M", title, variants, "5 Mar 2016");
    }

    private String generateImage() {
        Bitmap bmp =  BitmapFactory.decodeResource(getResources(),
                R.drawable.test_img);//your image
        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
        bmp.recycle();
        byte[] byteArray = bYtE.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
