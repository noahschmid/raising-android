package com.raising.app.fragments.registration.startup.stakeholderInputs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels.FounderViewModel;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.customPicker.PickerItem;
import com.raising.app.util.customPicker.listeners.OnCustomPickerListener;

import org.json.JSONObject;

public class FounderInputFragment extends RaisingFragment {
    private FounderViewModel founderViewModel;
    private EditText founderFirstNameInput, founderLastNameInput,
            founderEducationInput, founderCompanyPositionInput, founderCountryInput;
    private Button btnCancelFounder, btnAddFounder;

    private Founder founder;
    private CustomPicker countryPicker;
    private int countryId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stakeholder_founder,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_founder), true);
        return view;
    }

    /**
     * Allow to pass a founder to this fragment
     * @param founder The founder that should be passed to this fragment
     */
    public void passFounder(Founder founder) {
        this.founder = founder;
    }

    @Override
    public void onResourcesLoaded() {
        View view = getView();

        founderViewModel = new ViewModelProvider(requireActivity()).get(FounderViewModel.class);

        // find all views
        founderFirstNameInput = view.findViewById(R.id.input_founder_first_name);
        founderLastNameInput = view.findViewById(R.id.input_founder_last_name);
        founderEducationInput = view.findViewById(R.id.input_founder_education);
        founderCompanyPositionInput = view.findViewById(R.id.input_founder_poistion);
        founderCountryInput = view.findViewById(R.id.input_founder_country);

        btnCancelFounder = view.findViewById(R.id.button_cancel_founder);
        btnCancelFounder.setOnClickListener(v -> leaveFounderFragment());

        btnAddFounder = view.findViewById(R.id.button_add_founder);
        btnAddFounder.setOnClickListener(v -> processInputs());

        prepareCountryPicker();

        populateFragment();
    }

    @Override
    public void onDestroyView() {
        hideBottomNavigation(false);
        super.onDestroyView();
    }

    /**
     * Populate fragment with existing user data
     */
    private void populateFragment() {
        if(founder == null) {
            founder = new Founder();
        } else {
            customizeAppBar(getString(R.string.toolbar_title_edit_founder), true);
            if(founder.getId() != -1) {
                hideBottomNavigation(false);
            }
            btnAddFounder.setText(getString(R.string.submit_text));
            founderFirstNameInput.setText(founder.getFirstName());
            founderLastNameInput.setText(founder.getLastName());
            founderCompanyPositionInput.setText(founder.getPosition());
            founderEducationInput.setText(founder.getEducation());
            countryId = founder.getCountryId();
            if (countryId != -1) {
                founderCountryInput.setText(resources.getCountry(countryId).getName());
            }
        }
    }

    /**
     * Prepare the country picker with resources and existing user data
     */
    private void prepareCountryPicker() {
        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .listener(country -> {
                            founderCountryInput.setText(country.getName());
                            countryId = (int)country.getId();
                        })
                        .setItems(resources.getCountries());

        countryPicker = builder.build();

        founderCountryInput.setOnClickListener(v -> countryPicker.showDialog(getActivity()));
    }

    /**
     * Check the validity of user inputs, then handle the inputs
     */
    private void processInputs() {
        String firstName = founderFirstNameInput.getText().toString();
        String lastName = founderLastNameInput.getText().toString();
        String companyPosition = founderCompanyPositionInput.getText().toString();
        String education = founderEducationInput.getText().toString();

        if(firstName.length() == 0 || lastName.length() == 0
                || companyPosition.length() == 0 || education.length() == 0 || countryId == -1) {
            showSimpleDialog(getString(R.string.register_dialog_title),
                    getString(R.string.register_dialog_text_empty_credentials));
            return;
        }

        founder.setFirstName(firstName);
        founder.setLastName(lastName);
        founder.setPosition(companyPosition);
        founder.setEducation(education);
        founder.setCountryId(countryId);
        founder.setTitle(firstName + " " + lastName + ", " + companyPosition);

        if(founder.getId() != -1) {
            try {
                Gson gson = new Gson();
                JSONObject params = new JSONObject(gson.toJson(founder));
                ApiRequestHandler.performPatchRequest("startup/founder/" +
                                founder.getId(), result -> {
                            founderViewModel.select(founder);
                            leaveFounderFragment();
                            return null;
                        },
                        ApiRequestHandler.errorHandler,
                        params);
            } catch (Exception e) {
                Log.e("founderInput",
                        "Error while updating board member: " +
                                e.getMessage());
            }
        } else {
            if(AuthenticationHandler.isLoggedIn()) {
                Gson gson = new Gson();
                try {
                    JSONObject params = new JSONObject(gson.toJson(founder));
                    params.put("startupId", AuthenticationHandler.getId());
                    Log.d("founderInput", "params: " + params.toString());
                    ApiRequestHandler.performPostRequest("startup/founder",
                            result -> {
                                founderViewModel.select(founder);
                                leaveFounderFragment();
                                return null;
                            },
                            ApiRequestHandler.errorHandler,
                            params);
                } catch (Exception e) {
                    showGenericError();
                    Log.e("FounderInput",
                            "Could not add founder: " + e.getMessage());
                }
            } else {
                founderViewModel.select(founder);
                leaveFounderFragment();
            }
        }
    }

    /**
     * {@link RaisingFragment#popFragment(androidx.fragment.app.Fragment)}
     */
    private void leaveFounderFragment() {
        popFragment(this);
    }
}

