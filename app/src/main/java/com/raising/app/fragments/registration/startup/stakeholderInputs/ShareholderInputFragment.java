package com.raising.app.fragments.registration.startup.stakeholderInputs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.registration.startup.stakeholderInputs.viewModels.ShareholderViewModel;
import com.raising.app.models.CorporateBody;
import com.raising.app.models.stakeholder.Shareholder;
import com.raising.app.util.ApiRequestHandler;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.NoFilterArrayAdapter;
import com.raising.app.util.customPicker.CustomPicker;
import com.raising.app.util.matchingCriteriaComponent.MatchingCriteriaAdapter;
import com.raising.app.util.matchingCriteriaComponent.MatchingCriteriaComponent;

import org.json.JSONObject;

import java.util.ArrayList;

public class ShareholderInputFragment extends RaisingFragment {
    private final String TAG = "ShareholderInputFragment";
    private boolean privateShareholder;

    private ShareholderViewModel shareholderViewModel;
    private FrameLayout privateFrameLayout, corporateFrameLayout;
    private RadioButton selectPrivateShareholder, selectCorporateShareholder;
    private EditText privateFirstNameInput, privateLastNameInput,
            corporateNameInput, corporateWebsiteInput, privateCountryInput,
            corporateCountryInput, privateEquityInput, corporateEquityInput;
    private AutoCompleteTextView corporateBodyInput;
    private RecyclerView privateShareholderTypeRecycler;
    private Button btnCancelShareholder, btnAddShareholder;

    private MatchingCriteriaComponent privateInvestorTypeLayout;

    private Shareholder shareholder;
    private int countryId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stakeholder_shareholder,
                container, false);

        hideBottomNavigation(true);
        customizeAppBar(getString(R.string.toolbar_title_shareholder), true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shareholderViewModel = new ViewModelProvider(requireActivity()).get(ShareholderViewModel.class);

        privateFrameLayout = view.findViewById(R.id.stakeholder_private_shareholder);
        privateFrameLayout.setVisibility(View.GONE);
        corporateFrameLayout = view.findViewById(R.id.stakeholder_corporate_shareholder);
        corporateFrameLayout.setVisibility(View.GONE);

        // setup radio group determining which layout should be visible
        selectPrivateShareholder = view.findViewById(R.id.stakeholder_select_private_shareholder);
        selectPrivateShareholder.setOnClickListener(v -> {
            privateShareholder = true;
            shareholder.setPrivateShareholder(true);
            corporateFrameLayout.setVisibility(View.GONE);
            privateFrameLayout.setVisibility(View.VISIBLE);
        });
        selectCorporateShareholder = view.findViewById(R.id.stakeholder_select_corporate_shareholder);
        selectCorporateShareholder.setOnClickListener(v -> {
            shareholder.setPrivateShareholder(false);
            privateShareholder = false;
            privateFrameLayout.setVisibility(View.GONE);
            corporateFrameLayout.setVisibility(View.VISIBLE);
        });

        // find views of private shareholder layout
        privateFirstNameInput = view.findViewById(R.id.input_shareholder_first_name);
        privateLastNameInput = view.findViewById(R.id.input_shareholder_last_name);
        privateCountryInput = view.findViewById(R.id.input_shareholder_country);
        privateCountryInput.setShowSoftInputOnFocus(false);
        privateEquityInput = view.findViewById(R.id.input_shareholder_equity_share);
        privateShareholderTypeRecycler = view.findViewById(R.id.private_shareholder_type);

        // find views of corporate shareholder layout
        corporateNameInput = view.findViewById(R.id.input_shareholder_name);
        corporateBodyInput = view.findViewById(R.id.input_shareholder_corporate_body);
        corporateWebsiteInput = view.findViewById(R.id.input_shareholder_website);
        corporateEquityInput = view.findViewById(R.id.input_shareholder_corporate_equity_share);
        corporateCountryInput = view.findViewById(R.id.input_shareholder_corporate_country);

        // find views for buttons
        btnAddShareholder = view.findViewById(R.id.button_add_shareholder);
        btnCancelShareholder = view.findViewById(R.id.button_cancel_shareholder);

    }

    public void passShareholder(Shareholder shareholder) {
        this.shareholder = shareholder;
    }

    @Override
    public void onResourcesLoaded() {
        Log.d(TAG, "onResourcesLoaded: ");
        // initialize exposed dropdown for corporate body
        ArrayList<String> bodies = new ArrayList<>();
        resources.getCorporateBodies().forEach(body -> bodies.add(body.getName()));

        NoFilterArrayAdapter<String> adapterCorporateBody = new NoFilterArrayAdapter<>(getContext(),
                R.layout.item_dropdown_menu, bodies);
        corporateBodyInput.setAdapter(adapterCorporateBody);

        // setup private shareholder investor type recycler view
        MatchingCriteriaAdapter.OnItemClickListener clickListener = position -> {
            // do nothing
        };

        privateInvestorTypeLayout = new MatchingCriteriaComponent(getView().findViewById(R.id.private_shareholder_type),
                resources.getInvestorTypes(), true, clickListener, false);

        // show country pickers
        CustomPicker.Builder builder =
                new CustomPicker.Builder()
                        .with(getContext())
                        .canSearch(true)
                        .listener(country -> {
                            privateCountryInput.setText(country.getName());
                            corporateCountryInput.setText(country.getName());
                            countryId = (int) country.getId();
                        })
                        .setItems(resources.getCountries());
        CustomPicker countryPicker = builder.build();

        corporateCountryInput.setOnClickListener(v -> {
            countryPicker.dismiss();
            countryPicker.showDialog(getActivity());
        });

        privateCountryInput.setOnClickListener(v -> {
            countryPicker.dismiss();
            countryPicker.showDialog(getActivity());
        });

        // set click listeners for buttons
        btnAddShareholder.setOnClickListener(v -> getNewShareholderData());
        btnCancelShareholder.setOnClickListener(v -> leaveShareholderFragment());

        loadData();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        hideBottomNavigation(false);
        super.onDestroyView();
    }

    private void loadData() {
        Log.d(TAG, "loadData: ");
        if (shareholder != null) {
            btnAddShareholder.setText(getString(R.string.submit_text));
            customizeAppBar(getString(R.string.toolbar_title_edit_shareholder), true);
            if (shareholder.getId() != -1) {
                hideBottomNavigation(false);
            }
            if (shareholder.isPrivateShareholder()) {
                privateShareholder = true;

                selectPrivateShareholder.setChecked(true);
                selectCorporateShareholder.setChecked(false);
                corporateFrameLayout.setVisibility(View.GONE);
                privateFrameLayout.setVisibility(View.VISIBLE);

                privateFirstNameInput.setText(shareholder.getFirstName());
                privateLastNameInput.setText(shareholder.getLastName());
                privateCountryInput.setText(resources.getCountry((int) shareholder
                        .getCountryId()).getName());
                privateEquityInput.setText(shareholder.getEquityShare() + "");
                privateInvestorTypeLayout.setChecked(shareholder.getInvestorTypeId());

            } else {
                privateShareholder = false;

                selectPrivateShareholder.setChecked(false);
                selectCorporateShareholder.setChecked(true);

                privateFrameLayout.setVisibility(View.GONE);
                corporateFrameLayout.setVisibility(View.VISIBLE);

                if (shareholder.getCorporateBodyId() != -1)
                    corporateNameInput.setText(resources.getCorporateBody(
                            shareholder.getCorporateBodyId()).getName());
                corporateBodyInput.setText(resources
                        .getCorporateBody(shareholder.getCorporateBodyId()).getName());
                corporateWebsiteInput.setText(shareholder.getWebsite());
                corporateEquityInput.setText(shareholder.getEquityShare() + "");
                corporateCountryInput.setText(resources.getCountry((int) shareholder
                        .getCountryId()).getName());
            }
        } else {
            shareholder = new Shareholder();
        }
    }

    private void getNewShareholderData() {
        Log.d(TAG, "getNewShareholderData: ");
        if (privateShareholder) {
            String firstName = privateFirstNameInput.getText().toString();
            String lastName = privateLastNameInput.getText().toString();
            String privateEquityShare = privateEquityInput.getText().toString();
            long typeId = privateInvestorTypeLayout.getSingleSelected();

            if (firstName.length() == 0 || lastName.length() == 0
                    || privateEquityShare.length() == 0 || typeId == -1) {
                showSimpleDialog(getString(R.string.register_dialog_title),
                        getString(R.string.register_dialog_text_empty_credentials));
                return;
            }

            // check for country
            if(countryId == -1 && shareholder.getCountryId() == -1) {
                showSimpleDialog(getString(R.string.register_dialog_title),
                        getString(R.string.register_dialog_text_empty_credentials));
                return;
            }

            if (Float.parseFloat(privateEquityShare) > 100) {
                showSimpleDialog(getString(R.string.register_dialog_title), getString(R.string.register_stakeholder_high_equity));
                return;
            }

            shareholder.setPrivateShareholder(true);
            shareholder.setCountryId(countryId);
            shareholder.setLastName(lastName);
            shareholder.setFirstName(firstName);
            shareholder.setEquityShare(Float.parseFloat(privateEquityShare));
            shareholder.setInvestorTypeId(typeId);
        } else {
            String name = corporateNameInput.getText().toString();
            String corporateBody = corporateBodyInput.getText().toString();
            String website = corporateWebsiteInput.getText().toString();
            String corporateEquityShare = corporateEquityInput.getText().toString();

            int corporateBodyId = -1;
            for (CorporateBody body : resources.getCorporateBodies()) {
                if (body.getName().equals(corporateBody)) {
                    corporateBodyId = (int) body.getId();
                }
            }

            if (name.length() == 0 || corporateBody.length() == 0 ||
                    corporateBodyId == -1 || corporateEquityShare.length() == 0) {
                showSimpleDialog(getString(R.string.register_dialog_title),
                        getString(R.string.register_dialog_text_empty_credentials));
                return;
            }

            // check for country
            if(countryId == -1 && shareholder.getCountryId() == -1) {
                showSimpleDialog(getString(R.string.register_dialog_title),
                        getString(R.string.register_dialog_text_empty_credentials));
                return;
            }

            if (Float.parseFloat(corporateEquityShare) > 100) {
                showSimpleDialog(getString(R.string.register_dialog_title), getString(R.string.register_stakeholder_high_equity));
                return;
            }

            shareholder.setCorporateBodyId(corporateBodyId);
            shareholder.setEquityShare(Float.parseFloat(corporateEquityShare));
            shareholder.setCorpName(name);
            shareholder.setWebsite(website);
            shareholder.setCountryId(countryId);
            shareholder.setInvestorTypeId(-1);
            shareholder.setPrivateShareholder(false);
        }

        updateExistingShareholder();
    }

    private void updateExistingShareholder() {
        Log.d(TAG, "updateExistingShareholder: ");
        if (shareholder.getId() != -1) {
            try {
                Gson gson = new Gson();
                JSONObject params = new JSONObject(gson.toJson(shareholder));
                String endpoint = shareholder.isPrivateShareholder() ?
                        "startup/privateshareholder/" : "startup/corporateshareholder/";
                ApiRequestHandler.performPatchRequest(endpoint +
                                shareholder.getId(), result -> {
                            shareholderViewModel.select(shareholder);
                            leaveShareholderFragment();
                            return null;
                        },
                        ApiRequestHandler.errorHandler,
                        params);
            } catch (Exception e) {
                Log.e("shareholderInput",
                        "Error while updating board member: " +
                                e.getMessage());
            }
        } else {
            if (AuthenticationHandler.isLoggedIn()) {
                Gson gson = new Gson();
                String endpoint = shareholder.isPrivateShareholder() ?
                        "startup/privateshareholder" : "startup/corporateshareholder";
                try {
                    JSONObject params = new JSONObject(gson.toJson(shareholder));
                    params.put("startupId", AuthenticationHandler.getId());
                    Log.d("shareholderInput", "params: " + params.toString());
                    ApiRequestHandler.performPostRequest(endpoint,
                            result -> {
                                shareholderViewModel.select(shareholder);
                                leaveShareholderFragment();
                                return null;
                            },
                            ApiRequestHandler.errorHandler,
                            params);
                } catch (Exception e) {
                    showGenericError();
                    Log.e("shareholderInput",
                            "Could not add shareholder: " + e.getMessage());
                }
            } else {
                shareholderViewModel.select(shareholder);
                leaveShareholderFragment();
            }
        }
    }

    private void leaveShareholderFragment() {
        Log.d(TAG, "leaveShareholderFragment: ");
        popFragment(this);
    }
}
