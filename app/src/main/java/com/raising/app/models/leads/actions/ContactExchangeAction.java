package com.raising.app.models.leads.actions;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.raising.app.R;
import com.raising.app.fragments.leads.LeadsContactExchangeFragment;
import com.raising.app.models.leads.Lead;
import com.raising.app.util.InternalStorageHandler;

public class ContactExchangeAction implements HandshakeAction {
    public void execute(Lead lead) {
        try {
            Fragment fragment = new LeadsContactExchangeFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("lead", lead);
            fragment.setArguments(bundle);

            ((AppCompatActivity)InternalStorageHandler.getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (NullPointerException e) {
            Log.e("ContactExchangeAction", "Error while changing Fragment: " + e.getMessage());
        }
    }
}
