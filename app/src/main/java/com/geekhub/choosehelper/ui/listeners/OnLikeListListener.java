package com.geekhub.choosehelper.ui.listeners;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CheckBox;

public interface OnLikeListListener {

    void onLike(CardView mainView, CheckBox clickedCheckBox, CheckBox otherCheckBox,
                int position, int variantNumber);

}
