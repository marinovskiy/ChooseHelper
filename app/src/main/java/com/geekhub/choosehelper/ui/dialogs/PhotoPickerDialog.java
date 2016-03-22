package com.geekhub.choosehelper.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.geekhub.choosehelper.R;

/**
 * Created by Alex on 20.03.2016.
 */
public class PhotoPickerDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Photo")
                .setItems(R.array.photo_variants, (dialog, which) -> {
                    Toast.makeText(getContext(), "Position = " + which, Toast.LENGTH_SHORT).show();
                });
        return builder.create();
    }
}
