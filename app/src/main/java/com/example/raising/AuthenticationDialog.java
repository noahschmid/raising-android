package com.example.raising;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class AuthenticationDialog extends DialogFragment {

    /**
     * Create a new instance of a AuthenticationDialog
     * @param dialogTitle The title of the dialog
     * @param dialogMessage The message, that should be displayed inside the dialog
     * @return Instance of AuthenticationDialog
     *
     * @author Lorenz Caliezi 03.03.2020
     * @version 1.1
     */
    public AuthenticationDialog newInstance(String dialogTitle, String dialogMessage) {
        AuthenticationDialog authenticationDialog = new AuthenticationDialog();

        Bundle bundle = new Bundle();
        bundle.putString("title", dialogTitle);
        bundle.putString("message", dialogMessage);
        authenticationDialog.setArguments(bundle);

        return authenticationDialog;
    }

    /**
     * Build the AuthenticationDialog
     * @param savedInstanceState Certain parameters for the new dialog
     * @return Instance of the fully built AuthenticationDialog
     *
     * @author Lorenz Caliezi 02.03.2020
     * @version 1.0
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        /*
        For a customized layout add the following lines:
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_login, null));
         */

        builder.setTitle(getArguments().getString("title"))
                .setMessage(getArguments().getString("message"))
                .setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: implement action after user pressed 'OK'
                    }
                });
        return builder.create();
    }
}
