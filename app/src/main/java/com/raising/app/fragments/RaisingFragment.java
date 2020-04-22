package com.raising.app.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.raising.app.MainActivity;
import com.raising.app.R;
import com.raising.app.models.Account;
import com.raising.app.models.Model;
import com.raising.app.models.NotificationSettings;
import com.raising.app.models.ViewState;
import com.raising.app.models.leads.Lead;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.Resources;
import com.raising.app.util.SimpleMessageDialog;
import com.raising.app.util.ToastHandler;
import com.raising.app.viewModels.AccountViewModel;
import com.raising.app.viewModels.ResourcesViewModel;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class RaisingFragment extends Fragment {
    final private String TAG = "RaisingFragment";

    final private String deviceEndpoint = ApiRequestHandler.getDomain() + "device";

    protected View loadingPanel;
    protected FrameLayout overlayLayout;
    protected AccountViewModel accountViewModel;
    protected ResourcesViewModel resourcesViewModel;
    protected Resources resources;
    protected Account currentAccount;
    private int processesLoading = 0;

    protected void onAccountUpdated() {
    }

    protected void onResourcesLoaded() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingPanel = getLayoutInflater().inflate(R.layout.view_loading_panel, null);
        overlayLayout = view.findViewById(R.id.overlay_layout);

        if (overlayLayout != null) {
            overlayLayout.setFocusable(false);
            overlayLayout.setClickable(false);
            overlayLayout.addView(loadingPanel);
            loadingPanel.setVisibility(View.GONE);
        }
        accountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);
        accountViewModel.getAccount().observe(getViewLifecycleOwner(), account -> {
            currentAccount = account;
        });
        currentAccount = accountViewModel.getAccount().getValue();
        accountViewModel.getViewState().observe(getViewLifecycleOwner(), viewState -> {
            Log.d(TAG, "onViewCreated: ViewState: " + viewState.toString());
            switch (viewState) {
                case LOADING:
                    showLoadingPanel();
                    break;
                case RESULT:
                case CACHED:
                    dismissLoadingPanel();
                    break;
                case UPDATED:
                    currentAccount = accountViewModel.getAccount().getValue();
                    onAccountUpdated();
                    break;

                case ERROR:
                    ToastHandler toastHandler = new ToastHandler(getContext());
                    toastHandler.showToast(getString(R.string.generic_error_title), Toast.LENGTH_LONG);
                    accountViewModel.loadAccount();
                    break;
            }
        });

        resourcesViewModel = ViewModelProviders.of(getActivity()).get(ResourcesViewModel.class);
        resourcesViewModel.getResources().observe(getViewLifecycleOwner(), resources -> {
            this.resources = resources;
        });
        resourcesViewModel.getViewState().observe(getViewLifecycleOwner(), viewState -> {
            processViewState(viewState);
        });
        resources = resourcesViewModel.getResources().getValue();

        processViewState(resourcesViewModel.getViewState().getValue());
        processViewState(accountViewModel.getViewState().getValue());
    }


    protected void processViewState(ViewState viewState) {
        switch (viewState) {
            case LOADING:
                showLoadingPanel();
                break;
            case RESULT:
            case CACHED:
                dismissLoadingPanel();
                onResourcesLoaded();
                break;
        }
    }

    /**
     * Load profile image into image view
     *
     * @param id        id of the profile image
     * @param imageView where to load the image into
     */
    protected void loadProfileImage(long id, ImageView imageView) {
        Glide
                .with(InternalStorageHandler.getContext())
                .load(ApiRequestHandler.getDomain() + "media/profilepicture/" + id)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_person_24dp)
                .into(imageView);
    }

    /**
     * Change from the current fragment to the next
     *
     * @param fragment The fragment, that should be displayed next
     * @author Lorenz Caliezi 09.03.2020
     */
    protected void changeFragment(Fragment fragment) {
        try {
            getActivitiesFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (NullPointerException e) {
            Log.e("RaisingFragment", "Error while changing Fragment: " + e.getMessage());
        }
    }

    /**
     * Display a generic "oops something went wrong" message
     */
    public void displayGenericError() {
        showSimpleDialog(getString(R.string.generic_error_title),
                getString(R.string.generic_error_text));
    }

    /**
     * Change from the current fragment to the next
     *
     * @param fragment The fragment, that should be displayed next
     * @param name     The transaction name
     * @author Lorenz Caliezi 09.03.2020
     */
    protected void changeFragment(Fragment fragment, String name) {
        try {
            getActivitiesFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(name)
                    .commit();
        } catch (NullPointerException e) {
            Log.e("RaisingFragment", "Error while changing Fragment: " +
                    e.getMessage());
        }
    }

    /**
     * Clear all fragments on the backstack and replace fragment container with new fragment
     *
     * @param fragment the fragment to display next
     */
    protected void clearBackstackAndReplace(RaisingFragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    /**
     * Creates an easy to read string representation of large numbers
     *
     * @param amount The number, that should be converted to the easier string
     * @return The string representation of the number
     */
    public String amountToString(int amount) {
        String unit = "";
        String[] units = getResources().getStringArray(R.array.revenue_units);
        String currency = getResources().getString(R.string.currency);
        int i = 0;
        while (Math.log10(amount) >= 3 && i < units.length) {
            amount /= 1000;
            unit = units[i];
            ++i;
        }
        return currency + " " + amount + unit;
    }


    /**
     * Call {@link com.raising.app.MainActivity#hideBottomNavigation(boolean)}
     *
     * @param isHidden if true, the bottomNavigation should be invisible,
     *                 if false, the bottomNavigation should be visible
     * @author Lorenz Caliezi 06.03.2020
     */
    protected void hideBottomNavigation(boolean isHidden) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null)
            activity.hideBottomNavigation(isHidden);
    }

    /**
     * Call {@link com.raising.app.MainActivity#customizeActionBar(String, boolean)}
     *
     * @param title          The title of the action bar
     * @param showBackButton true, if back button should be active
     *                       false, if there is no back button
     */
    protected void customizeAppBar(String title, boolean showBackButton) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null)
            activity.customizeActionBar(title, showBackButton);
    }

    /**
     * Call {@link com.raising.app.MainActivity#setActionBarMenu(boolean)}
     *
     * @param setMenu true, if menu should be visible
     *                false, to dismiss menu
     */
    protected void setActionBarMenu(boolean setMenu) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setActionBarMenu(setMenu);
        }
    }

    /**
     * This methods retrieves an instance the SupportFragmentManager of the underlying activity
     *
     * @return Instance of SupportFragmentManager of used Activity
     * @author Lorenz Caliezi 09.03.2020
     */
    protected FragmentManager getActivitiesFragmentManager() {
        try {
            return getParentFragmentManager();
        } catch (NullPointerException e) {
            Log.e("RaisingFragment", "Could not get activities fragment manager: " +
                    e.toString());
        }
        return null;
    }

    /**
     * Opens a simple dialog, which can only be accepted
     *
     * @param dialogTitle   The title of the simple message dialog
     * @param dialogMessage The message, that is to be displayed
     * @author Lorenz Caliezi 09.03.2020
     */
    protected void showSimpleDialog(String dialogTitle, String dialogMessage) {
        SimpleMessageDialog dialog =
                new SimpleMessageDialog().newInstance(dialogTitle, dialogMessage);
        dialog.show(getActivitiesFragmentManager(), "dialog");
    }

    /**
     * Prepares the TextInputLayouts where a word limiter is needed
     *
     * @param textLayout   The layout that has the limiter
     * @param textInput    The input of the layout with the limiter
     * @param WORD_MAXIMUM The limit of words, that the layout allows
     * @param currentText  The current text of the text view
     * @author Lorenz Caliezi 18.03.2020
     */
    protected void prepareRestrictedTextLayout(final TextInputLayout textLayout, final EditText textInput, final int WORD_MAXIMUM, String currentText) {
        if (currentText == null || currentText.equals(" ")) {
            textLayout.setHelperText(0 + "/" + WORD_MAXIMUM);
        } else {
            String[] currentTextArray = splitStringIntoWords(currentText);
            textLayout.setHelperText(currentTextArray.length + "/" + WORD_MAXIMUM);
        }

        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = textInput.getText().toString();
                String[] textArray = splitStringIntoWords(text);
                textLayout.setHelperText(textArray.length + "/" + WORD_MAXIMUM);

                if (textArray.length > WORD_MAXIMUM) {
                    textLayout.setError(getString(R.string.register_error_word_limit_overflow));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Creates a String array containing seperate words from a given String
     *
     * @param text The text that should be split into words
     * @return The array containing the words
     */
    protected String[] splitStringIntoWords(String text) {
        text.replace("\n", " ");
        return text.split(" ");
    }

    /**
     * Leaves the currentFragment and removes currentFragment from the backstack
     * Currently only works, if currentFragment is on top of the stack
     *
     * @param currentFragment The fragment that is to be removed
     * @author Lorenz Caliezi 23.03.2020
     */
    protected void popCurrentFragment(Fragment currentFragment) {
        FragmentManager fragmentManager = getActivitiesFragmentManager();
        fragmentManager.beginTransaction().remove(currentFragment);
        fragmentManager.popBackStackImmediate();
        accountViewModel.updateCompleted();
    }

    /**
     * Create checkbox group out of array list
     *
     * @param list   the items to add
     * @param layout where to add to
     */
    protected void setupCheckboxes(ArrayList<? extends Model> list, LinearLayout layout) {
        list.forEach(item -> {
            CheckBox cb = new CheckBox(getContext());
            cb.setText(item.getName());
            cb.setContentDescription(String.valueOf(item.getId()));
            layout.addView(cb);
        });
    }

    /**
     * Add radio boxes to radio group out of array list
     *
     * @param list  the items to add
     * @param group where to add to
     */
    protected void setupRadioGroup(ArrayList<? extends Model> list, RadioGroup group) {
        list.forEach(item -> {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(item.getName());
            rb.setContentDescription(String.valueOf(item.getId()));
            group.addView(rb);
        });
    }

    /**
     * Tick checkbox with given id
     *
     * @param layout
     * @param id
     */
    protected void tickCheckbox(LinearLayout layout, long id) {
        for (int i = 0; i < layout.getChildCount(); ++i) {
            CheckBox cb = (CheckBox) layout.getChildAt(i);
            if (Long.parseLong((String) cb.getContentDescription()) == id) {
                cb.setChecked(true);
            }
        }
    }

    /**
     * Tick radio button with given id
     *
     * @param group
     * @param id
     */
    protected void tickRadioButton(RadioGroup group, long id) {
        for (int i = 0; i < group.getChildCount(); ++i) {
            RadioButton rb = (RadioButton) group.getChildAt(i);
            if (Long.parseLong((String) rb.getContentDescription()) == id) {
                rb.setChecked(true);
            }
        }
    }

    /**
     * Get selected checkboxes of a layout
     *
     * @param layout
     * @return
     */
    protected ArrayList<Long> getSelectedCheckboxIds(LinearLayout layout) {
        ArrayList<Long> results = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); ++i) {
            View v = layout.getChildAt(i);
            if (((CheckBox) v).isChecked() && ((String) ((CheckBox) v).getContentDescription()).length() > 0) {
                results.add(Long.parseLong((String) ((CheckBox) v).getContentDescription()));
            }
        }

        return results;
    }

    /**
     * Get selected radio button item and return id of the element
     *
     * @param group the radio group
     * @return id of the selected element
     */
    protected Long getSelectedRadioId(RadioGroup group) {
        ArrayList<Long> results = new ArrayList<>();
        for (int i = 0; i < group.getChildCount(); ++i) {
            View v = group.getChildAt(i);
            if (((RadioButton) v).isChecked() &&
                    ((String) ((RadioButton) v).getContentDescription()).length() > 0) {
                return Long.parseLong((String) ((RadioButton) v).getContentDescription());
            }
        }

        return -1l;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissLoadingPanel();
    }

    /**
     * Show year picker
     *
     * @param title      title of the picker
     * @param inputField the field to print the output to
     */
    protected void showYearPicker(String title, EditText inputField) {
        Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) { // on date set }
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(Calendar.JULY).setMinYear(1900)
                .setActivatedYear(today.get(Calendar.YEAR))
                .setMaxYear(today.get(Calendar.YEAR) + 200)
                .setMinMonth(Calendar.FEBRUARY)
                .setTitle(title)
                .showYearOnly()
                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                    @Override
                    public void onYearChanged(int selectedYear) {
                        inputField.setText(String.valueOf(selectedYear));
                    }
                })
                .build()
                .show();

        inputField.setText(String.valueOf(today.get(Calendar.YEAR)));
    }

    protected void showLoadingPanel() {
        Log.d(TAG, "showLoadingPanel: called");
        if (overlayLayout == null) {
            Log.e("RaisingFragment", "No overlay layout found!");
            return;
        }
        ++processesLoading;

        if (processesLoading > 1) {
            return;
        }

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loadingPanel.setVisibility(View.VISIBLE);
    }

    protected void dismissLoadingPanel() {
        Log.d(TAG, "dismissLoadingPanel: called");
        --processesLoading;
        if (loadingPanel == null || processesLoading != 0) {
            if (processesLoading < 0)
                processesLoading = 0;
            return;
        }

        loadingPanel.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    protected void cacheNotificationSettings(ArrayList<NotificationSettings> settings) {
        try {
            InternalStorageHandler.saveObject(settings,
                    "notificationSettings_" + AuthenticationHandler.getId());
        } catch (Exception e) {
            Log.e(TAG, "Error caching settings: " + e.getMessage());
        }
    }

    protected ArrayList<NotificationSettings> getCachedNotificationSettings() {
        try {
            if (InternalStorageHandler.exists("notificationSettings_" + AuthenticationHandler.getId())) {
                Log.d(TAG, "getCachedNotificationSettings: loaded cached settings");
                return (ArrayList<NotificationSettings>) InternalStorageHandler.loadObject(
                        "notificationSettings_" + AuthenticationHandler.getId());
            } else {
                Log.d(TAG, "getCachedNotificationSettings: No cached settings available");
            }
        } catch (Exception e) {

        }
        return null;
    }

    void prepareDeviceForNotifications() {
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        String device = "ANDROID";
        Log.d(TAG, "prepareDeviceForNotifications: DeviceToken: " + deviceToken);
        ArrayList<String> notificationStrings = new ArrayList<>();
        if (getCachedNotificationSettings() != null) {
            getCachedNotificationSettings().forEach(notificationSettings -> {
                Log.d(TAG, "prepareDeviceForNotifications: Notification Setting: " + notificationSettings.name());
                notificationStrings.add(notificationSettings.name());
            });
        } else {
            notificationStrings.add(NotificationSettings.NEVER.name());
        }

        Log.d(TAG, "prepareDeviceForNotifications: Endpoint" + deviceEndpoint);

        //TODO: perform backend request
        /* Example request
        {
            "token": "asdf",
            "device": "ANDROID",
            "notificationTypes":["MATCHLIST", "REQUEST"]
}
         */
    }
}
