package com.geekhub.choosehelper.ui.listeners;

import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

public interface OnSwitchChangeListener {

    void onSwitchChanged(SwitchCompat switchCompat, boolean isChecked, TextView tvStatus);

}
