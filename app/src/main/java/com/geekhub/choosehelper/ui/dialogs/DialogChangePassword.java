package com.geekhub.choosehelper.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.firebase.FirebaseUsersManager;

public class DialogChangePassword extends DialogFragment {

    public static final String BUNDLE_USER_EMAIL = "bundle_user_email";

    private String mEmail;

    public static DialogChangePassword newInstance(String email) {
        DialogChangePassword dialog = new DialogChangePassword();
        Bundle args = new Bundle();
        args.putString(BUNDLE_USER_EMAIL, email);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString(BUNDLE_USER_EMAIL);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_change_password_layout, null);
        EditText editText = (EditText) view.findViewById(R.id.et_old_password);
        EditText editText1 = (EditText) view.findViewById(R.id.et_new_password);
        EditText editText2 = (EditText) view.findViewById(R.id.et_repeat_new_password);
        builder.setView(view)
                .setPositiveButton(getString(R.string.dialog_btn_send_email), (dialog, id) -> {
                    String et = editText.getText().toString();
                    String et1 = editText1.getText().toString();
                    String et2 = editText2.getText().toString();
                    if (!et.equals(et1)) {
                        Toast.makeText(getContext(), R.string.toast_different_passwords, Toast.LENGTH_SHORT).show();
                    } else if (et1.equals("") || et2.equals("")) {
                        Toast.makeText(getContext(), R.string.toast_empty_fields, Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUsersManager.changePassword(getContext(), mEmail, et, et1);
                    }
                })
                .setNegativeButton(getString(R.string.dialog_btn_cancel), (dialog, id) -> {
                    DialogChangePassword.this.getDialog().cancel();
                });
        return builder.create();
    }
}
