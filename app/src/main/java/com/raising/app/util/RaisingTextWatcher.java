package com.raising.app.util;

import android.text.Editable;
import android.text.TextWatcher;

public interface RaisingTextWatcher extends TextWatcher {

    @Override
    default void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    default void afterTextChanged(Editable s) {
        // do nothing
    }
}
