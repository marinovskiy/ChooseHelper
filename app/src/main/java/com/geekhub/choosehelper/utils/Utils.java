package com.geekhub.choosehelper.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.screens.activities.DetailsActivity;
import com.geekhub.choosehelper.screens.activities.EditCompareActivity;
import com.geekhub.choosehelper.screens.fragments.AllComparesFragment;
import com.geekhub.choosehelper.utils.firebase.FirebaseComparesManager;

public class Utils {

    /**
     * internet
     **/
    public static boolean hasInternet(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * dialogs
     **/
    public static void showPhotoPickerDialog(Context context,
                                             DialogInterface.OnClickListener onClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Photo")
                .setItems(R.array.photo_variants, onClickListener)
                .create();
        alertDialog.show();
    }

    public static void showCompareDeleteDialog(Context context,
                                               DialogInterface.OnClickListener onClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Delete compare?")
                .setNegativeButton("cancel", onClickListener)
                .setPositiveButton("delete", onClickListener)
                .create();
        alertDialog.show();
    }

    /**
     * other
     **/
    public static void showErrorMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static int getOtherVariantNumber(int currentVariantNumber) {
        return currentVariantNumber == 0 ? 1 : 0;
    }

    /**
     * popup menus
     **/
    public static void showUsualPopupMenu(Context context, Compare compare, View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_compare);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_details_compare:
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra(AllComparesFragment.INTENT_KEY_COMPARE_ID, compare.getId());
                    context.startActivity(intent);
                    return true;
                case R.id.action_share_compare:
                    //TODO share compare
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    public static void showOwnerPopupMenu(Context context, Compare compare, View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_compare_owner);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_details_compare:
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra(AllComparesFragment.INTENT_KEY_COMPARE_ID, compare.getId());
                    context.startActivity(intent);
                    return true;
                case R.id.action_share_compare:
                    //TODO share compare
                    return true;
                case R.id.action_edit_compare:
                    Intent intentEdit = new Intent(context, EditCompareActivity.class);
                    intentEdit.putExtra(AllComparesFragment.INTENT_KEY_COMPARE_ID, compare.getId());
                    context.startActivity(intentEdit);
                    return true;
                case R.id.action_delete_compare:
                    Utils.showCompareDeleteDialog(context, (dialog, which) -> {
                        switch (which) {
                            case -2:
                                dialog.cancel();
                                break;
                            case -1:
                                FirebaseComparesManager.deleteCompare(compare.getId());
                                break;
                        }
                    });
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    /**
     * methods for block and unblock views when like is transacting to server
     **/
    public static void blockViews(CardView cardView, CheckBox checkBox1, CheckBox checkBox2) {
        cardView.setClickable(false);
        checkBox1.setClickable(false);
        checkBox2.setClickable(false);
    }

    public static void unBlockViews(CardView cardView, CheckBox checkBox1, CheckBox checkBox2) {
        cardView.setClickable(true);
        checkBox1.setClickable(true);
        checkBox2.setClickable(true);
    }

    public static void blockViews(CheckBox checkBox1, CheckBox checkBox2) {
        checkBox1.setClickable(false);
        checkBox2.setClickable(false);
    }

    public static void unBlockViews(CheckBox checkBox1, CheckBox checkBox2) {
        checkBox1.setClickable(true);
        checkBox2.setClickable(true);
    }
}
