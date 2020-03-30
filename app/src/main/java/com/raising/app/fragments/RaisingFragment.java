package com.raising.app.fragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.MainActivity;
import com.raising.app.R;
import com.raising.app.models.Model;
import com.raising.app.util.SimpleMessageDialog;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class RaisingFragment extends Fragment {

    /**
     * Change from the current fragment to the next
     * @param fragment The fragment, that should be displayed next
     *
     * @author Lorenz Caliezi 09.03.2020
     */
    public void changeFragment(Fragment fragment) {
        try {
            getActivitiesFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (NullPointerException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Change from the current fragment to the next
     * @param fragment The fragment, that should be displayed next
     * @param name The transaction name
     * @author Lorenz Caliezi 09.03.2020
     */
    public void changeFragment(Fragment fragment, String name) {
        try {
            getActivitiesFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(name)
                    .commit();
        } catch (NullPointerException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Clear all fragments on the backstack and replace fragment container with new fragment
     * @param fragment the fragment to display next
     */
    public void clearBackstackAndReplace(RaisingFragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


    /**
     * Call {@link com.raising.app.MainActivity#hideBottomNavigation(boolean)}
     * @param isHidden if true, the bottomNavigation should be invisible,
     *                 if false, the bottomNavigation should be visible
     *
     * @author Lorenz Caliezi 06.03.2020
     */
    public void hideBottomNavigation(boolean isHidden) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null)
            activity.hideBottomNavigation(isHidden);
    }

    /**
     * This methods retrieves an instance the SupportFragmentManager of the underlying activity
     * @return Instance of SupportFragmentManager of used Activity
     *
     * @author Lorenz Caliezi 09.03.2020
     */
    public FragmentManager getActivitiesFragmentManager() {
        try {
            return getParentFragmentManager();
        } catch (NullPointerException e) {
            Log.d("debugMessage", e.toString());
        }
        return null;
    }

    /**
     * Opens a simple dialog, which can only be accepted
     * @param dialogTitle The title of the simple message dialog
     * @param dialogMessage The message, that is to be displayed
     *
     * @author Lorenz Caliezi 09.03.2020
     */
    public void showSimpleDialog(String dialogTitle, String dialogMessage) {
        SimpleMessageDialog dialog =
                new SimpleMessageDialog().newInstance(dialogTitle, dialogMessage);
        dialog.show(getActivitiesFragmentManager(), "loginDialog");
    }

    /**
     * Prepares the TextInputLayouts where a word limiter is needed
     * @param textLayout The layout that has the limiter
     * @param textInput The input of the layout with the limiter
     * @param WORD_MAXIMUM The limit of words, that the layout allows
     *
     * @author Lorenz Caliezi 18.03.2020
     */
    public void prepareRestrictedTextLayout(final TextInputLayout textLayout, final EditText textInput, final int WORD_MAXIMUM ) {
        textLayout.setHelperText(0 + "/" + WORD_MAXIMUM);
        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = textInput.getText().toString();
                text.replace("\n", " ");
                String [] textArray = text.split(" ");
                textLayout.setHelperText(textArray.length + "/" + WORD_MAXIMUM);

                if(textArray.length > WORD_MAXIMUM) {
                    textLayout.setError(getString(R.string.register_error_word_limit_overflow));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Leaves the currentFragment and removes currentFragment from the backstack
     * Currently only works, if currentFragment is on top of the stack
     * @param currentFragment The fragment that is to be removed
     *
     * @author Lorenz Caliezi 23.03.2020
     */
    public void popCurrentFragment(Fragment currentFragment) {
        FragmentManager fragmentManager = getActivitiesFragmentManager();
        fragmentManager.beginTransaction().remove(currentFragment);
        fragmentManager.popBackStackImmediate();
    }

    /**
     * Create checkbox group out of array list
     * @param list the items to add
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
     * @param list the items to add
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
     * @param layout
     * @param id
     */
    protected void tickCheckbox(LinearLayout layout, long id) {
        for (int i = 0; i < layout.getChildCount(); ++i) {
            CheckBox cb = (CheckBox) layout.getChildAt(i);
            if(Long.parseLong((String) cb.getContentDescription()) == id)
                cb.setChecked(true);
        }
    }

    /**
     * Tick radio button with given id
     * @param group
     * @param id
     */
    protected void tickRadioButton(RadioGroup group, long id) {
        for (int i = 0; i < group.getChildCount(); ++i) {
            RadioButton rb = (RadioButton) group.getChildAt(i);
            if(Long.parseLong((String) rb.getContentDescription()) == id)
                rb.setChecked(true);
        }
    }


    protected ArrayList<Long> getSelectedCheckboxIds(LinearLayout layout) {
        ArrayList<Long> results = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); ++i) {
            View v = layout.getChildAt(i);
            if(((CheckBox)v).isChecked() && ((String)((CheckBox)v).getContentDescription()).length() > 0) {
                results.add(Long.parseLong((String)((CheckBox)v).getContentDescription()));
            }
        }

        return results;
    }

    /**
     * Get selected radio button item and return id of the element
     * @param group the radio group
     * @return id of the selected element
     */
    protected Long getSelectedRadioId(RadioGroup group) {
        ArrayList<Long> results = new ArrayList<>();
        for (int i = 0; i < group.getChildCount(); ++i) {
            View v = group.getChildAt(i);
            if(((RadioButton)v).isChecked() &&
                    ((String)((RadioButton)v).getContentDescription()).length() > 0) {
                return Long.parseLong((String)((RadioButton)v).getContentDescription());
            }
        }

        return -1l;
    }

    /**
     * Show year picker
     * @param title title of the picker
     * @param inputField the field to print the output to
     */
    protected void showYearPicker(String title, EditText inputField) {
        Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) { // on date set }
                    }}, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

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
                    }})
                .build()
                .show();

        inputField.setText(String.valueOf(today.get(Calendar.YEAR)));
    }
}
