package com.example.raising;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class LoginDialog extends DialogFragment {

    /**
     * Create a new instance of a LoginDialog
     * @param message The message, that should be displayed inside the dialog
     * @param title The title of the dialog
     * @return Instance of LoginDialog
     */
    public LoginDialog newInstance(String title, String message) {
        LoginDialog loginDialog = new LoginDialog();

        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putString("title", title);
        loginDialog.setArguments(bundle);

        return loginDialog;
    }

    /**
     * Build the LoginDialog
     * @param savedInstanceState
     * @return Instance of the fully built LoginDialog
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
