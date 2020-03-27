package com.raising.app;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.raising.app.authentication.SimpleMessageDialog;

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
     * Takes the values of an integer arrays of ticket size steps and converts them into more readable strings
     * @param ticketSizeSteps The integer array that is to be converted
     * @return Array of String representations of the given integer array
     */
    public String [] createStringRepresentationOfTicketSizeSteps(int [] ticketSizeSteps) {
        String [] ticketSizeStrings = new String[ticketSizeSteps.length];
        for(int i = 0; i < ticketSizeSteps.length; i++ ) {
            StringBuilder stringBuilder = new StringBuilder();
            if((ticketSizeSteps[i] % getResources().getInteger(R.integer.billion)) >= 1) {
                stringBuilder.append(
                        Math.floor(ticketSizeSteps[i] % getResources().getInteger(R.integer.billion)));
                stringBuilder.append(getString(R.string.billion));
                ticketSizeStrings[i] = stringBuilder.toString();
            } else if((ticketSizeSteps[i] % getResources().getInteger(R.integer.million)) >= 1) {
                stringBuilder.append(
                        Math.floor(ticketSizeSteps[i] % getResources().getInteger(R.integer.million)));
                stringBuilder.append(getString(R.string.million));
                ticketSizeStrings[i] = stringBuilder.toString();
            } else if((ticketSizeSteps[i] % getResources().getInteger(R.integer.thousand)) >= 1) {
                stringBuilder.append(
                        Math.floor(ticketSizeSteps[i] % getResources().getInteger(R.integer.thousand)));
                stringBuilder.append(getString(R.string.thousand));
                ticketSizeStrings[i] = stringBuilder.toString();
            } else {
                stringBuilder.append(Math.floor(ticketSizeSteps[i]));
                ticketSizeStrings[i] = stringBuilder.toString();
            }
        }
        return ticketSizeStrings;
    }
}
