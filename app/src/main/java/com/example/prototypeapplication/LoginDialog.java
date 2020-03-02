package com.example.prototypeapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class LoginDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // LayoutInflater inflater = requireActivity().getLayoutInflater();
        // builder.setView(inflater.inflate(R.layout.dialog_login, null))
        builder.setTitle(R.string.login_error_dialog_title)
                // default message, to be defined with specific message
                .setMessage(R.string.login_error_dialog_text)
                .setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: implement action after user pressed 'OK'
                    }
                });
        return builder.create();
    }
}
