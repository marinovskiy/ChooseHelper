package com.geekhub.choosehelper.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.firebase.FirebaseUsersManager;

public class DialogResetPassword extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_reset_password_layout, null);
        EditText editText = (EditText) view.findViewById(R.id.res_password_email);
        builder.setView(view)
                .setPositiveButton(getString(R.string.dialog_btn_send_email), (dialog, id) -> {
                    if (editText.getText().toString().equals("")) {
                        Toast.makeText(getContext(), R.string.toast_didnt_enter_email, Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUsersManager.resetPassword(getContext(), editText.getText().toString());
                    }
                })
                .setNegativeButton(getString(R.string.dialog_btn_cancel), (dialog, id) -> {
                    DialogResetPassword.this.getDialog().cancel();
                });
        return builder.create();
    }
}
