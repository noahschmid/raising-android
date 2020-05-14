package com.raising.app.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.MainActivity;
import com.raising.app.R;
import com.raising.app.models.Account;
import com.raising.app.models.Model;
import com.raising.app.models.ViewState;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.Resources;
import com.raising.app.util.SimpleMessageDialog;
import com.raising.app.util.TabOrigin;
import com.raising.app.util.ToastHandler;
import com.raising.app.viewModels.AccountViewModel;
import com.raising.app.viewModels.ResourcesViewModel;
import com.raising.app.viewModels.SettingsViewModel;
import com.raising.app.viewModels.TabViewModel;
import com.raising.app.viewModels.ViewStateViewModel;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class RaisingFragment extends Fragment {
    final private String TAG = "RaisingFragment";

    protected View loadingPanel;
    protected FrameLayout overlayLayout;
    protected AccountViewModel accountViewModel;
    protected ResourcesViewModel resourcesViewModel;
    protected SettingsViewModel settingsViewModel;
    protected ViewStateViewModel viewStateViewModel;
    protected TabViewModel tabViewModel;

    protected Resources resources;
    protected Account currentAccount;

    private TabOrigin origin = TabOrigin.NONE;
    private TabOrigin base = TabOrigin.NONE;

    protected void onAccountUpdated() {
    }

    protected void onResourcesLoaded() {
    }

    protected void setBase(TabOrigin tab) {
        this.base = tab;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingPanel = getLayoutInflater().inflate(R.layout.view_loading_panel, null);
        overlayLayout = view.findViewById(R.id.overlay_layout);

        if(getArguments() != null) {
            if (getArguments().getString("origin") != null) {
                origin = TabOrigin.valueOf(getArguments().getString("origin"));
            }
        }

        if (overlayLayout != null) {
            overlayLayout.setFocusable(false);
            overlayLayout.setClickable(false);
            overlayLayout.addView(loadingPanel);
            loadingPanel.setVisibility(View.GONE);
        }

        viewStateViewModel = ViewModelProviders.of(getActivity()).get(ViewStateViewModel.class);
        viewStateViewModel.getViewState().observe(getViewLifecycleOwner(), this::processViewState);

        if(accountViewModel == null) {
            accountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);
        }
        accountViewModel.getAccount().observe(getViewLifecycleOwner(), account -> currentAccount = account);
        accountViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
            if (state.equals(ViewState.UPDATED)) {
                currentAccount = accountViewModel.getAccount().getValue();
                dismissLoadingPanel();
                onAccountUpdated();
            }
        });
        currentAccount = accountViewModel.getAccount().getValue();

        settingsViewModel = ViewModelProviders.of(getActivity()).get(SettingsViewModel.class);
        tabViewModel = ViewModelProviders.of(getActivity()).get(TabViewModel.class);
        resourcesViewModel = ViewModelProviders.of(getActivity()).get(ResourcesViewModel.class);
        resourcesViewModel.getResources().observe(getViewLifecycleOwner(), resources -> this.resources = resources);
        resourcesViewModel.getViewState().observe(getViewLifecycleOwner(), state -> {
            if(state == ViewState.RESULT || state == ViewState.CACHED)
                onResourcesLoaded();
            if(state == ViewState.ERROR)
                resourcesViewModel.loadResources();
        });
        resources = resourcesViewModel.getResources().getValue();


        processViewState(Objects.requireNonNull(viewStateViewModel.getViewState().getValue()));
    }

    /**
     * Process the global view state
     * @param viewState state of currently displayed view
     */
    private void processViewState(ViewState viewState) {
        switch (viewState) {
            case LOADING:
                Log.d(TAG, "processViewState: LOADING");
                showLoadingPanel();
                break;
            case RESULT:
                Log.d(TAG, "processViewState: RESULT");
                dismissLoadingPanel();
                break;

            case ERROR:
                Log.d(TAG, "processViewState: ERROR");
                dismissLoadingPanel();
                ToastHandler toastHandler = new ToastHandler(getContext());
                toastHandler.showToast(getString(R.string.billing_connection_failed_title), Toast.LENGTH_LONG);
                viewStateViewModel.setViewState(ViewState.EMPTY);
                break;

            case EXPIRED:
                Log.d(TAG, "processViewState: EXPIRED");
                viewStateViewModel.setViewState(ViewState.EMPTY);
                dismissLoadingPanel();

                if(!AuthenticationHandler.isLoggedIn())
                    return;

                showSimpleDialog(getString(R.string.session_expired_title), getString(R.string.session_expired_text));
                AuthenticationHandler.logout();
                clearBackstackAndReplace(new LoginFragment());
                break;
        }
    }

    /**
     * Get path from uri
     *
     * @param uri
     * @return
     */
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        getActivity().startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Load profile image into image view
     *
     * @param id        id of the profile image
     * @param imageView where to load the image into
     */
    protected void loadProfileImage(long id, ImageView imageView) {
        if (id <= 0) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_placeholder_24dp));
        } else {
            Glide
                    .with(InternalStorageHandler.getContext())
                    .load(ApiRequestHandler.getDomain() + "media/profilepicture/" + id)
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.ic_placeholder_24dp)
                    .into(imageView);
        }
    }

    /**
     * Change from the current fragment to the next
     *
     * @param fragment The fragment, that should be displayed next
     * @author Lorenz Caliezi 09.03.2020
     */
    public void changeFragment(Fragment fragment) {
        try {
            if(base != TabOrigin.NONE) {
                Bundle bundle = fragment.getArguments();
                if(bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putString("origin", base.toString());
                fragment.setArguments(bundle);

                switch (base) {
                    case MATCHES:
                        tabViewModel.setCurrentMatchesFragment(fragment);
                        break;
                    case LEADS:
                        tabViewModel.setCurrentLeadsFragment(fragment);
                        break;
                    case SETTINGS:
                        tabViewModel.setCurrentSettingsFragment(fragment);
                        break;
                    case PROFILE:
                        tabViewModel.setCurrentProfileFragment(fragment);
                        break;
                }
            }
            getActivitiesFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (NullPointerException e) {
            Log.e(TAG, "Error while changing Fragment: " + e.getMessage());
        }
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
            if(base != TabOrigin.NONE) {
                Bundle bundle = fragment.getArguments();
                if(bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putString("origin", base.toString());
                fragment.setArguments(bundle);

                switch (base) {
                    case MATCHES:
                        tabViewModel.setCurrentMatchesFragment(fragment);
                        break;
                    case LEADS:
                        tabViewModel.setCurrentLeadsFragment(fragment);
                        break;
                    case SETTINGS:
                        tabViewModel.setCurrentSettingsFragment(fragment);
                        break;
                    case PROFILE:
                        tabViewModel.setCurrentProfileFragment(fragment);
                        break;
                }
            }

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

    protected void changeFragmentWithAnimation(Fragment fragment) {
        try {
            getActivitiesFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.animation_slide_in_right, R.anim.animation_slide_out_left, R.anim.animation_slide_in_left, R.anim.animation_slide_out_right)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (NullPointerException e) {
            Log.d(TAG, "changeFragmentWithAnimation: Error while changing fragment" + e.getMessage());
        }
    }

    protected void changeFragmentWithAnimation(Fragment fragment, String name) {
        try {
            getActivitiesFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.animation_slide_in_right, R.anim.animation_slide_out_left, R.anim.animation_slide_in_left, R.anim.animation_slide_out_right)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(name)
                    .commit();
        } catch (NullPointerException e) {
            Log.d(TAG, "changeFragmentWithAnimation: Error while changing fragment" + e.getMessage());
        }
    }

    /**
     * Clear all fragments on the backstack and replace fragment container with new fragment
     *
     * @param fragment the fragment to display next
     */
    protected void clearBackstackAndReplace(Fragment fragment) {
        clearBackstack();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    /**
     * Clear all fragments on the backstack
     *
     */
    protected void clearBackstack() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
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
     * Call {@link com.raising.app.MainActivity#hideToolbar(boolean)}
     *
     * @param isHidden if true, the toolbar should be hidden
     *                 if false, the toolbar should be visible
     */
    protected void hideToolbar(boolean isHidden) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.hideToolbar(isHidden);
        }
    }

    /**
     * Call {@link MainActivity#selectBottomNavigation(int)}
     */
    protected void selectBottomNavigation(int selectedId) {
        MainActivity activity = (MainActivity) getActivity();
        if(activity != null) {
            activity.selectBottomNavigation(selectedId);
        }
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

    protected void showInformationToast(String message) {
        ToastHandler toastHandler = new ToastHandler(getContext());
        toastHandler.showToast(message, Toast.LENGTH_LONG);
    }

    /**
     * Display a generic "oops something went wrong" message
     */
    protected void showGenericError() {
        showSimpleDialog(getString(R.string.generic_error_title),
                getString(R.string.generic_error_text));
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
     * Create and show an alert dialog, which allows the user to either decline or accept a message
     *
     * @param title          The title of the dialog
     * @param message        The message of the dialog
     * @param positiveButton The string of the positive button
     * @param negativeButton The string of the negative button
     * @return true, if user has accepted the dialog, false, if user has declined the dialog
     */
    public boolean showActionDialog(String title, String message, String positiveButton, String negativeButton) {
        final boolean[] confirmDialog = {false};
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, (dialog, which) -> {
                    Log.d(TAG, "AlertDialog onClick");
                    confirmDialog[0] = true;
                })
                .setNegativeButton(negativeButton, (dialog, which) -> {
                    confirmDialog[0] = false;
                })
                .show()
                .setOnDismissListener(dialog -> {
                    throw new RuntimeException();
                });
        try {
            Looper.loop();
        } catch (RuntimeException e) {
        }

        return confirmDialog[0];
    }

    /**
     * Extend a simple request with "Yes" and "Cancel" to then create an action dialog.
     * Call {@link com.raising.app.fragments.RaisingFragment#showActionDialog(String, String, String, String)}
     *
     * @param title   The title of the action dialog
     * @param message The message of the action dialog
     * @return The return value of {@link com.raising.app.fragments.RaisingFragment#showActionDialog(String, String, String, String)}
     */
    public boolean showActionDialog(String title, String message) {
        return showActionDialog(title, message, getString(R.string.yes_text), getString(R.string.cancel_text));
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
     * Leaves the fragment and removes fragment from the backstack
     * Currently only works, if fragment is on top of the stack
     *
     * @param fragment The fragment that is to be removed
     * @author Lorenz Caliezi 23.03.2020
     */
    protected void popFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivitiesFragmentManager();
        fragmentManager.beginTransaction().remove(fragment);
        fragmentManager.popBackStackImmediate();
        accountViewModel.updateCompleted();
    }

    /**
     * Create checkbox group out of array list
     *
     * @param list   the items to add
     * @param layout where to add to
     */
    protected void setupCheckboxes(ArrayList<? extends Model> list, FlexboxLayout layout) {
        list.forEach(item -> {
            LinearLayout wrapper = new LinearLayout(getContext());
            wrapper.setOrientation(LinearLayout.VERTICAL);
            wrapper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            CheckBox cb = new CheckBox(getContext());
            cb.setContentDescription(String.valueOf(item.getId()));
            StateListDrawable cbImage = new StateListDrawable();
            Bitmap iconBitmap = Bitmap.createScaledBitmap(item.getImage(), 100, 100, true);
            RoundedBitmapDrawable icon = RoundedBitmapDrawableFactory.create(getResources(), iconBitmap);
            RoundedBitmapDrawable iconChecked = RoundedBitmapDrawableFactory.create(getResources(), iconBitmap);
            icon.setCornerRadius(50);
            icon.setAntiAlias(true);
            iconChecked.setCornerRadius(50);
            iconChecked.setTint(getResources().getColor(R.color.raisingPrimary));
            cbImage.addState(new int[]{android.R.attr.state_checked}, iconChecked);
            cbImage.addState(new int[]{-android.R.attr.state_checked}, icon);
            cb.setButtonDrawable(cbImage);
            wrapper.addView(cb);
            TextView label = new TextView(getContext());
            label.setText(item.getName());
            label.setSingleLine(false);
            label.setMaxWidth(40);
            wrapper.addView(label);
            layout.addView(wrapper);
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
    protected void tickCheckbox(FlexboxLayout layout, long id) {
        for (int i = 0; i < layout.getChildCount(); ++i) {
            if(layout.getChildAt(i) instanceof LinearLayout) {
                if(((LinearLayout)layout.getChildAt(i)).getChildAt(0) instanceof  CheckBox) {
                    CheckBox cb = (CheckBox) ((LinearLayout) layout.getChildAt(i)).getChildAt(0);
                    if (Long.parseLong((String) cb.getContentDescription()) == id) {
                        cb.setChecked(true);
                    }
                }
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
    protected ArrayList<Long> getSelectedCheckboxIds(FlexboxLayout layout) {
        ArrayList<Long> results = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); ++i) {
            if(layout.getChildAt(i) instanceof LinearLayout) {
                if (((LinearLayout) layout.getChildAt(i)).getChildAt(0) instanceof CheckBox) {
                    CheckBox v = (CheckBox)((LinearLayout) layout.getChildAt(i)).getChildAt(0);
                    if (v.isChecked() && ((String)v.getContentDescription()).length() > 0) {
                        results.add(Long.parseLong((String) ((CheckBox) v).getContentDescription()));
                    }
                }
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
        dismissLoadingPanel();
        super.onDestroyView();
    }

    public void resetTab() {
        switch (origin) {
            case MATCHES:
                tabViewModel.resetCurrentMatchesFragment();
                break;
            case LEADS:
                tabViewModel.resetCurrentLeadsFragment();
                break;
            case SETTINGS:
                tabViewModel.resetCurrentSettingsFragment();
                break;
            case PROFILE:
                tabViewModel.resetCurrentProfileFragment();
                break;
        }
    }

    /**
     * Show year picker
     *
     * @param title         title of the picker
     * @param inputField    the field to print the output to
     * @param activatedYear the activated year of the picker
     */
    protected void showYearPicker(String title, EditText inputField, int activatedYear) {
        Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                (selectedMonth, selectedYear) -> { // on date set }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(Calendar.JULY).setMinYear(1900)
                .setActivatedYear(activatedYear);
        // check for founding year picker, founding year cannot be in the future
        if (title.contains("founding")) {
            builder.setMaxYear(today.get(Calendar.YEAR));
        } else {
            builder.setMaxYear(today.get(Calendar.YEAR) + 200);
        }
        builder.setMinMonth(Calendar.FEBRUARY)
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
        inputField.setText(String.valueOf(activatedYear));
    }

    /**
     * Delegate year picker based on title, inputField and current date
     *
     * @param title      title of the picker
     * @param inputField the field to print the output to
     *                   <p>
     *                   Call {@link com.raising.app.fragments.RaisingFragment#showYearPicker(String, EditText, int)}
     */
    protected void showYearPicker(String title, EditText inputField) {
        Calendar today = Calendar.getInstance();
        showYearPicker(title, inputField, today.get(Calendar.YEAR));
    }

    protected void showLoadingPanel() {
        if (overlayLayout == null) {
            Log.e("RaisingFragment", "No overlay layout found!");
            return;
        }

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loadingPanel.setVisibility(View.VISIBLE);
    }

    protected void dismissLoadingPanel() {
        loadingPanel.setVisibility(View.GONE);
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    protected void disablePreOnboarding() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.disablePreOnboarding();
        }
    }

    protected boolean isDisablePostOnboarding() {
        boolean disablePostOnboarding = false;
        try {
            if (!(InternalStorageHandler.exists("postOnboarding"))) {
                return false;
            } else {
                disablePostOnboarding = (boolean) InternalStorageHandler.loadObject("postOnboarding");
            }
        } catch (Exception e) {
            Log.e(TAG, "isDisablePostOnboarding: Error loading post onboarding");
        }
        return disablePostOnboarding;
    }

    protected void disablePostOnboarding() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.disablePostOnboarding();
        }
    }

    protected boolean isFirstAppLaunch() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            return activity.isFirstAppLaunch();
        }
        return false;
    }
}
