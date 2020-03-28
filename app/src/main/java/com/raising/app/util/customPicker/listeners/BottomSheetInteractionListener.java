package com.raising.app.util.customPicker.listeners;

import android.view.View;

public interface BottomSheetInteractionListener {

  void initiateUi(View view);

  void setCustomStyle(View view);

  void setSearchEditText();

  void setupRecyclerView(View view);
}
