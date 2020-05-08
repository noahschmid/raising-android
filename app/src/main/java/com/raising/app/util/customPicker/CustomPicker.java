package com.raising.app.util.customPicker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.raising.app.R;
import com.raising.app.util.customPicker.listeners.BottomSheetInteractionListener;
import com.raising.app.util.customPicker.listeners.OnCustomPickerListener;
import com.raising.app.util.customPicker.listeners.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CustomPicker implements LifecycleObserver, BottomSheetInteractionListener {
    private final String TAG = "CustomPicker";
    public static final int SORT_BY_NONE = 0;
    public static final int SORT_BY_NAME = 1;
    public static final int THEME_NEW = 2;
    private int theme;

    private int style;
    private Context context;
    private int sortBy = SORT_BY_NONE;
    private OnCustomPickerListener onCustomPickerListener;
    private boolean canSearch = true;

    private List<? extends PickerItem> items;
    private EditText searchEditText;
    private RecyclerView countriesRecyclerView;
    private LinearLayout rootView;
    private int textColor;
    private int hintColor;
    private int backgroundColor;
    private int searchIconId;
    private Drawable searchIcon;
    private CustomPickerAdapter adapter;
    private List<PickerItem> searchResults;
    private Dialog dialog;
    private boolean multiSelect;
    private Button cancelButton, okButton;
    private List<PickerItem> result;

    private CustomPicker() {
    }

    public List<PickerItem> getResult() {
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    CustomPicker(Builder builder) {
        sortBy = builder.sortBy;
        if (builder.onCustomPickerListener != null) {
            onCustomPickerListener = builder.onCustomPickerListener;
        }
        style = builder.style;
        context = builder.context;
        canSearch = builder.canSearch;
        theme = builder.theme;
        multiSelect = builder.multiSelect;
        dialog = null;
        items = builder.items;
    }

    // region Listeners
    private void sortCountries(@NonNull List<PickerItem> items) {
        Collections.sort(items, new Comparator<PickerItem>() {
            @Override
            public int compare(PickerItem item1, PickerItem item2) {
                return item1.getName().trim().compareToIgnoreCase(item2.getName().trim());
            }
        });
    }
    // endregion

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    /**
     * Set checked items by a list
     *
     * @param selected
     */
    public void setSelected(List<PickerItem> selected) {
        selected.forEach(sel -> {
            for (PickerItem item : items) {
                if (sel.getId() == item.getId()) {
                    item.setChecked(true);
                    break;
                }
            }
        });
    }

    /**
     * Set checked items by a list
     *
     * @param selectedIds
     */
    public void setSelectedById(List<Long> selectedIds) {
        selectedIds.forEach(sel -> {
            for (PickerItem item : items) {
                if (sel == item.getId()) {
                    item.setChecked(true);
                    break;
                }
            }
        });
    }

    public void showDialog(@NonNull FragmentActivity activity) {
        showDialog(activity, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d(TAG, "onDismiss: Dialog dismissed");
            }
        });

    }

    // region Utility Methods
    public void showDialog(@NonNull FragmentActivity activity, DialogInterface.OnDismissListener dismissListener) {
        activity.getLifecycle().addObserver(this);
        dialog = new Dialog(activity);
        dialog.setOnDismissListener(dismissListener);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.item_custom_picker, null);
        initiateUi(dialogView);
        setCustomStyle(dialogView);
        setSearchEditText();
        setupRecyclerView(dialogView);
        dialog.setContentView(dialogView);

        okButton = dialogView.findViewById(R.id.picker_ok);
        cancelButton = dialogView.findViewById(R.id.picker_cancel);

        if (!multiSelect) {
            okButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        }

        cancelButton.setOnClickListener(v -> dismiss());

        okButton.setOnClickListener(v -> {
            result = new ArrayList<>();
            items.forEach(item -> {
                if (item.isChecked()) {
                    result.add(item);
                }
            });
            dismiss();
        });

        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(params);
            if (theme == THEME_NEW) {
                Drawable background =
                        ContextCompat.getDrawable(context, R.drawable.ic_dialog_new_background);
                if (background != null) {
                    background.setColorFilter(
                            new PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP));
                }
                rootView.setBackgroundDrawable(background);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
        dialog.show();
    }

    public List<? extends PickerItem> getItems() {
        return items;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void dismissDialogs() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public boolean instanceRunning() {
        return dialog != null;
    }

    @Override
    public void setupRecyclerView(View sheetView) {
        searchResults = new ArrayList<>();
        searchResults.addAll(items);
        adapter = new CustomPickerAdapter(sheetView.getContext(), searchResults,
                new OnItemClickListener() {
                    @Override
                    public void onItemClicked(PickerItem item) {
                        if (onCustomPickerListener != null) {
                            onCustomPickerListener.onSelectItem(item);
                            if (dialog != null && !multiSelect) {
                                dialog.dismiss();
                            }
                            dialog = null;
                            textColor = 0;
                            hintColor = 0;
                            backgroundColor = 0;
                            searchIconId = 0;
                            searchIcon = null;
                        }
                    }
                },
                textColor, multiSelect);
        countriesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(sheetView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        countriesRecyclerView.setLayoutManager(layoutManager);
        countriesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setSearchEditText() {
        if (canSearch) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Intentionally Empty
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Intentionally Empty
                }

                @Override
                public void afterTextChanged(Editable searchQuery) {
                    search(searchQuery.toString());
                }
            });
            searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    InputMethodManager imm = (InputMethodManager) searchEditText.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                    }
                    return true;
                }
            });
        } else {
            searchEditText.setVisibility(View.GONE);
        }
    }

    private void search(String searchQuery) {
        searchResults.clear();
        for (PickerItem item : items) {
            if (item.getName().toLowerCase(Locale.ENGLISH).contains(searchQuery.toLowerCase())) {
                searchResults.add(item);
            }
        }
        sortCountries(searchResults);
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("ResourceType")
    @Override
    public void setCustomStyle(View sheetView) {
        if (style != 0) {
            int[] attrs =
                    {
                            android.R.attr.textColor, android.R.attr.textColorHint, android.R.attr.background,
                            android.R.attr.drawable
                    };
            TypedArray ta = sheetView.getContext().obtainStyledAttributes(style, attrs);
            textColor = ta.getColor(0, Color.BLACK);
            hintColor = ta.getColor(1, Color.GRAY);
            backgroundColor = ta.getColor(2, Color.WHITE);
            searchIconId = ta.getResourceId(3, R.drawable.ic_search);
            searchEditText.setTextColor(textColor);
            searchEditText.setHintTextColor(hintColor);
            searchIcon = ContextCompat.getDrawable(searchEditText.getContext(), searchIconId);
            if (searchIconId == R.drawable.ic_search) {
                searchIcon.setColorFilter(new PorterDuffColorFilter(hintColor, PorterDuff.Mode.SRC_ATOP));
            }
            searchEditText.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, null, null);
            rootView.setBackgroundColor(backgroundColor);
            ta.recycle();
        }
    }

    @Override
    public void initiateUi(View sheetView) {
        searchEditText = sheetView.findViewById(R.id.country_code_picker_search);
        countriesRecyclerView = sheetView.findViewById(R.id.countries_recycler_view);
        rootView = sheetView.findViewById(R.id.rootView);
    }
    // endregion

    public void setOnCustomPickerListener(OnCustomPickerListener onCustomPickerListener) {
        this.onCustomPickerListener = onCustomPickerListener;
    }

    // endregion

    // region Builder
    public static class Builder {
        private Context context;
        private int sortBy = SORT_BY_NONE;
        private boolean canSearch = true;
        private OnCustomPickerListener onCustomPickerListener;
        private int style;
        private int theme = THEME_NEW;
        private boolean multiSelect = false;
        private ArrayList<? extends PickerItem> items = new ArrayList<>();

        public Builder with(@NonNull Context context) {
            this.context = context;
            return this;
        }

        public Builder style(@NonNull @StyleRes int style) {
            this.style = style;
            return this;
        }

        public Builder sortBy(@NonNull int sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder setItems(ArrayList<? extends PickerItem> items) {
            this.items = items;
            return this;
        }

        public Builder listener(@NonNull OnCustomPickerListener onCustomPickerListener) {
            this.onCustomPickerListener = onCustomPickerListener;
            return this;
        }

        public Builder canSearch(@NonNull boolean canSearch) {
            this.canSearch = canSearch;
            return this;
        }

        public Builder multiSelect(@NonNull boolean multiSelect) {
            this.multiSelect = multiSelect;
            return this;
        }

        public Builder theme(@NonNull int theme) {
            this.theme = theme;
            return this;
        }

        public CustomPicker build() {
            return new CustomPicker(this);
        }
    }
    // endregion

    public static class NameComparator implements Comparator<PickerItem> {
        @Override
        public int compare(PickerItem country, PickerItem nextCountry) {
            return country.getName().compareToIgnoreCase(nextCountry.getName());
        }
    }
    // endregion
}
