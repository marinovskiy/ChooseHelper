package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkLike;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.ui.adapters.ComparesAdapter;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;
import com.geekhub.choosehelper.utils.firebase.FirebaseLikesManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class SearchActivity extends BaseSignInActivity {

    @Bind(R.id.toolbar_search)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_search)
    View mToolbarShadow;

    @Bind(R.id.recycler_view_search)
    RecyclerView mRecyclerView;

    /**
     * firebase references and queries
     **/
    private Firebase mFirebaseCompares;
    private Firebase mFirebaseLikes;
    private Query mQueryCompares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupToolbar();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /** firebase references **/
        mFirebaseCompares = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_COMPARES);

        mFirebaseLikes = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_LIKES);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setFocusable(true);
        searchView.setIconified(true);
        searchView.setQueryHint("Search compares...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCompares(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //searchCompares(newText);
                return false;
            }
        });
        return true;
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
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //mToolbar.setNavigationIcon(R.drawable.icon_drawer);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    private void updateUi(List<Compare> compares) {
        ComparesAdapter adapter;
        if (mRecyclerView.getAdapter() == null) {
            adapter = new ComparesAdapter(compares.subList(0, compares.size() < 19
                    ? compares.size() : 19));
            mRecyclerView.setAdapter(adapter);

            /** click listener for details **/
            adapter.setOnItemClickListener((view, position) -> {
                Intent intent = new Intent(this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.INTENT_KEY_COMPARE_ID, compares.get(position).getId());
                startActivity(intent);
            });

            /** click listener for likes **/
            adapter.setOnLikeClickListener((mainView, clickedCheckBox, otherCheckBox,
                                            position, variantNumber) -> {
                if (!Utils.hasInternet(getApplicationContext())) {
                    clickedCheckBox.setChecked(false);
                    int newValue = Integer.parseInt(clickedCheckBox.getText().toString()) - 1;
                    clickedCheckBox.setText(String.valueOf(newValue));
                    Utils.showMessage(getApplicationContext(), getString(R.string.toast_no_internet));
                } else if (compares.get(position).getAuthor().getId().equals(Prefs.getUserId())) {
                    clickedCheckBox.setChecked(false);
                    int newValue = Integer.parseInt(clickedCheckBox.getText().toString()) - 1;
                    clickedCheckBox.setText(String.valueOf(newValue));
                    Utils.showMessage(getApplicationContext(), "You cannot like your own compares");
                } else {
                    Utils.blockViews(mainView, clickedCheckBox, otherCheckBox);
                    FirebaseLikesManager.updateLike(compares.get(position).getId(), variantNumber,
                            mainView, clickedCheckBox, otherCheckBox);
                }
            });

            /** click listener for popup menu **/
            adapter.setOnItemClickListenerPopup((view, position) -> {
                String compareId = compares.get(position).getId();
                if (compares.get(position).getAuthor().getId().equals(Prefs.getUserId())) {
                    Utils.showOwnerPopupMenu(getApplicationContext(), view, compareId);
                } else {
                    Utils.showUserPopupMenu(getApplicationContext(), view, compareId);
                }
            });

            /** click listener for author **/
            adapter.setOnItemClickListenerAuthor((view, position) -> {
                Intent userIntent = new Intent(this, ProfileActivity.class);
                userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID,
                        compares.get(position).getAuthor().getId());
                userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_NAME,
                        compares.get(position).getAuthor().getFullName());
                startActivity(userIntent);
            });
        } else {
            adapter = (ComparesAdapter) mRecyclerView.getAdapter();
            adapter.updateList(compares.subList(0, compares.size() < 19
                    ? compares.size() : 19));
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * get information about compare from firebase
     **/
    private void searchCompares(String query) {

        mQueryCompares = mFirebaseCompares.orderByChild(FirebaseConstants.FB_REF_QUESTION)
                .equalTo(query);

        mQueryCompares.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Compare> compares = new ArrayList<>();
                int snapshotSize = (int) dataSnapshot.getChildrenCount();
                if (snapshotSize == 0)
                    //hideRefreshing();
                    for (DataSnapshot compareSnapshot : dataSnapshot.getChildren()) {
                        NetworkCompare networkCompare = compareSnapshot.getValue(NetworkCompare.class);
                        fetchDetailsFromNetwork(compares, networkCompare,
                                compareSnapshot.getKey(), snapshotSize);
                    }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //hideRefreshing();
                Toast.makeText(getApplicationContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * get details information about compares from firebase
     **/
    private void fetchDetailsFromNetwork(List<Compare> compares, NetworkCompare networkCompare,
                                         String compareId, int size) {
        /** likes **/
        Query queryDetails = mFirebaseLikes.orderByChild(FirebaseConstants.FB_REF_COMPARE_ID)
                .equalTo(compareId);
        queryDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot likeSnapshot) {
                int likedVariant = -1;
                for (DataSnapshot snapshot : likeSnapshot.getChildren()) {
                    if (snapshot.getValue(NetworkLike.class).getUserId().equals(Prefs.getUserId())) {
                        likedVariant = snapshot.getValue(NetworkLike.class).getVariantNumber();
                    }
                }
                /** compare author **/
                final int tempLikedVariant = likedVariant;
                new Firebase(FirebaseConstants.FB_REF_MAIN)
                        .child(FirebaseConstants.FB_REF_USERS)
                        .child(networkCompare.getUserId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot authorSnapshot) {
                                compares.add(ModelConverter.convertToCompare(networkCompare,
                                        compareId,
                                        authorSnapshot.getValue(NetworkUser.class),
                                        networkCompare.getUserId(),
                                        tempLikedVariant));

                                if (compares.size() == size) {
                                    updateUi(compares);
//                                    hideRefreshing();
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
//                                hideRefreshing();
                                Toast.makeText(getApplicationContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
