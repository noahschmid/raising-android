package com.raising.app.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.raising.app.R;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.onboarding.OnboardingPost1Fragment;
import com.raising.app.models.ContactData;
import com.raising.app.util.AccountService;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.SubscriptionHandler;

public class ContactDataInput extends RaisingFragment {
    private EditText phoneNumberInput;
    private ContactData contactDetails;
    private Button saveButton;
    private boolean isStartup;
    private String token;
    private long accountId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_contact_details), false);

        return inflater.inflate(R.layout.fragment_contact_details_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views
        phoneNumberInput = view.findViewById(R.id.contact_input_phone);
        saveButton = view.findViewById(R.id.contact_input_save_button);
        saveButton.setOnClickListener(v -> processInputs());

        // prepare contactDetails object
        contactDetails = new ContactData();
        contactDetails.setEmail(this.getArguments().getString("email"));
        isStartup = this.getArguments().getBoolean("isStartup");
        token = this.getArguments().getString("token");
        accountId = this.getArguments().getLong("id");
        contactDetails.setAccountId(accountId);

        Log.d("ContactDataInput", "onViewCreated: " + contactDetails.getEmail());

        viewStateViewModel.stopLoading();
    }

    /**
     * Check if all necessary details are provided and if so, save contact details and
     * proceed to matching fragment
     */
    private void processInputs() {
        if(phoneNumberInput.getText().length() == 0) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }
        contactDetails.setPhone(phoneNumberInput.getText().toString());

        try {
            InternalStorageHandler.saveObject(contactDetails, "contact_" +
                    accountId);
            Log.d("ContactDataInput", "processInputs: " + contactDetails.getEmail());
            AuthenticationHandler.login(contactDetails.getEmail(),
                    token, accountId, isStartup);
            accountViewModel.loadAccount();
            settingsViewModel.loadSettings();
            SubscriptionHandler.loadSubscription();
            AccountService.loadContactData(AuthenticationHandler.getId());
            hideBottomNavigation(false);

            if(isFirstAppLaunch() && !isDisablePostOnboarding()) {
                clearBackstackAndReplace(new OnboardingPost1Fragment());
            } else {
                clearBackstackAndReplace(new MatchesFragment());
            }

        } catch (Exception e) {
            Log.e("ContactDetailsInput", "Error while saving contact details: " +
                    e.getMessage());
            showGenericError();
        }
    }
}
