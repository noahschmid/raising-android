package com.raising.app;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.raising.app.authentication.SimpleMessageDialog;

public class RaisingFragment extends Fragment {

    /**
     * Change from the current fragment to the next
     * @param fragment The fragment, that should be displayed next
     * @param fragmentName The name of the next fragment.
     *                     Allows us to put the fragment on the BackStack
     *
     * @author Lorenz Caliezi 09.03.2020
     */
    public void changeFragment(Fragment fragment, String fragmentName) {
        try {
            getActivitiesFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(fragmentName)
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
}
