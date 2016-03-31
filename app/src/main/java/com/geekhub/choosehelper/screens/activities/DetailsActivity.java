package com.geekhub.choosehelper.screens.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Comment;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkComment;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.screens.fragments.AllComparesFragment;
import com.geekhub.choosehelper.ui.adapters.CommentsRecyclerViewAdapter;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseComparesManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.RealmChangeListener;

public class DetailsActivity extends BaseSignInActivity {

    @Bind(R.id.details_ll_main)
    RelativeLayout mLinearLayout;

    @Bind(R.id.toolbar_details)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_details)
    View mToolbarShadow;

    @Bind(R.id.details_swipe_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.details_rv)
    RecyclerView mRecyclerView;

    @Bind(R.id.details_et_comment_text)
    EditText mDetailsEtCommentText;

    private Snackbar mSnackbar;

    private Firebase mFirebase;
    private Firebase mFirebaseComments;

    private String mCompareId;

    private Compare mCompare;

    private RealmChangeListener mComparesListener = () -> {
        if (mCompare != null && mCompare.isLoaded()) {
            updateUi(mCompare);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setupToolbar();

        /** recycler view **/
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /** get current compare's id and create firebase reference **/
        if (getIntent() != null) {
            mCompareId = getIntent().getStringExtra(AllComparesFragment.INTENT_KEY_COMPARE_ID);
        }
        mFirebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_COMPARES)
                .child(mCompareId);
        mFirebaseComments = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_COMMENTS);

        /** get compare from database and network **/
        fetchCompareFromDb();

        if (Utils.hasInternet(getApplicationContext())) {
            fetchCompareFromNetwork();
        }

        /** swipe refresh layout **/
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (Utils.hasInternet(getApplicationContext())) {
                fetchCompareFromDb();
            } else {
                Toast.makeText(getApplicationContext(), R.string.toast_no_internet, Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @OnClick(R.id.details_btn_add_comment)
    public void onClick() {
        String commentText = mDetailsEtCommentText.getText().toString();
        if (!commentText.equals("")) {
            NetworkComment networkComment = new NetworkComment();
            networkComment.setDate(System.currentTimeMillis());
            networkComment.setCommentText(commentText);
            networkComment.setUserId(Prefs.getUserId());
            FirebaseComparesManager.addCommentToCompare(mCompareId, networkComment);
        } else {
            Toast.makeText(this, "You can't post empty comment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompare.removeChangeListener(mComparesListener);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

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
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_arrow_back_white));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    private void fetchCompareFromDb() {
        mCompare = DbComparesManager.getCompareById(mCompareId);
        mCompare.addChangeListener(mComparesListener);
    }

    private void fetchCompareFromNetwork() {
        mFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot compareSnapshot) {
                NetworkCompare networkCompare = compareSnapshot.getValue(NetworkCompare.class);
                new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                        .child(FirebaseConstants.FB_REFERENCE_USERS)
                        .child(networkCompare.getUserId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userSnapshot) {
                                Compare compare = ModelConverter.convertToCompare(networkCompare,
                                        compareSnapshot.getKey(),
                                        userSnapshot.getValue(NetworkUser.class),
                                        networkCompare.getUserId());
                                if (compare != null) {
                                    DbComparesManager.saveCompare(compare);
                                    updateUi(compare);
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                if (mSwipeRefreshLayout.isRefreshing()) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                                showSnackbar(firebaseError.getMessage() + " " + firebaseError.getCode());
                            }
                        });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                showSnackbar(firebaseError.getMessage() + " " + firebaseError.getCode());
            }
        });
    }

    private void updateUi(Compare compare) {
        CommentsRecyclerViewAdapter adapter = new CommentsRecyclerViewAdapter(compare/*,
                generateComments()*/);
        mRecyclerView.setAdapter(adapter);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showSnackbar(String message) {
        mSnackbar = Snackbar
                .make(mLinearLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", v -> fetchCompareFromNetwork());
        mSnackbar.show();
    }

    private List<Comment> generateComments() {
        List<Comment> comments = new ArrayList<>();
        User author = new User();
        author.setId("qesadasd");
        author.setFullName("Johny");
        author.setPhotoUrl("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcS-0d56tCE7lbXKopMQTh_ugToAx0R7-9v5iGXfKQe18MJ9-wi1");
        for (int i = 0; i < 10; i++) {
            Comment comment = new Comment();
            comment.setId(String.valueOf(i));
            comment.setDate(System.currentTimeMillis());
            comment.setCommentText("bla-bla-bla-bla-bla...");
            comment.setAuthor(author);
            comments.add(comment);
        }
        return comments;
    }
}
