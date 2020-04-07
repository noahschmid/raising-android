package com.raising.app.util;

import com.raising.app.R;

public class NotificationHandler {
    /**
     * Display a generic "oops something went wrong" message
     */
    public static void displayGenericError() {
        SimpleMessageDialog dialog =
                new SimpleMessageDialog().newInstance(ResourcesManager.getContext()
                                .getString(R.string.generic_error_title),
                        ResourcesManager.getContext()
                                .getString(R.string.generic_error_text));
        dialog.show(ResourcesManager.getFragmentManager(), "generic_error");
    }
}
