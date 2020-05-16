package com.raising.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.fragments.leads.LeadsContainerFragment;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.fragments.leads.LeadsInteractionFragment;
import com.raising.app.fragments.leads.LeadsOpenRequestsFragment;
import com.raising.app.fragments.onboarding.OnboardingPre1Fragment;
import com.raising.app.fragments.profile.ContactDataInput;
import com.raising.app.fragments.registration.RegisterLoginInformationFragment;
import com.raising.app.fragments.settings.SettingsFragment;
import com.raising.app.fragments.profile.MyProfileFragment;
import com.raising.app.models.NotificationType;
import com.raising.app.util.AccountService;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.viewModels.AccountViewModel;
import com.raising.app.viewModels.LeadsViewModel;
import com.raising.app.viewModels.MatchesViewModel;
import com.raising.app.viewModels.ResourcesViewModel;
import com.raising.app.viewModels.SettingsViewModel;
import com.raising.app.viewModels.SubscriptionViewModel;
import com.raising.app.viewModels.TabViewModel;
import com.raising.app.viewModels.ViewStateViewModel;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    AccountViewModel accountViewModel;
    ResourcesViewModel resourcesViewModel;
    MatchesViewModel matchesViewModel;
    LeadsViewModel leadsViewModel;
    SettingsViewModel settingsViewModel;
    ViewStateViewModel viewStateViewModel;
    TabViewModel tabViewModel;
    SubscriptionViewModel subscriptionViewModel;

    MaterialToolbar toolbar;

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: activity created");
        setContentView(R.layout.activity_main);

        InternalStorageHandler.setContext(getApplicationContext());
        InternalStorageHandler.setActivity(this);
        AuthenticationHandler.init();
        RegistrationHandler.init();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        toolbar = findViewById(R.id.raising_app_bar);
        // setSupportActionBar(toolbar);

        viewStateViewModel = new ViewModelProvider(this).get(ViewStateViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        resourcesViewModel = new ViewModelProvider(this).get(ResourcesViewModel.class);
        matchesViewModel = new ViewModelProvider(this).get(MatchesViewModel.class);
        leadsViewModel = new ViewModelProvider(this).get(LeadsViewModel.class);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        tabViewModel = new ViewModelProvider(this).get(TabViewModel.class);
        subscriptionViewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

        viewStateViewModel.addViewModel(accountViewModel.getViewState(), this);
        viewStateViewModel.addViewModel(resourcesViewModel.getViewState(), this);
        viewStateViewModel.addViewModel(matchesViewModel.getViewState(), this);
        viewStateViewModel.addViewModel(leadsViewModel.getViewState(), this);
        viewStateViewModel.addViewModel(settingsViewModel.getViewState(), this);
        viewStateViewModel.addViewModel(subscriptionViewModel.getViewState(), this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // check internal storage if user has completed his onboarding
        boolean disablePreOnboarding = false;
        try {
            if (InternalStorageHandler.exists("onboarding")) {
                disablePreOnboarding = (boolean) InternalStorageHandler.loadObject("onboarding");
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error loading onboarding ");
        }

        if (isFirstAppLaunch() && !disablePreOnboarding || !(InternalStorageHandler.exists("onboarding"))) {
            // if user has not completed his onboarding and this is a newly installed app
            fragmentTransaction.replace(R.id.fragment_container, new OnboardingPre1Fragment());
        } else if (!AuthenticationHandler.isLoggedIn()) {
            resourcesViewModel.loadResources();
            hideBottomNavigation(true);
            hideToolbar(true);
            fragmentTransaction.replace(R.id.fragment_container, new RegisterLoginInformationFragment());
        } else {
            resourcesViewModel.loadResources();
            leadsViewModel.loadLeads();
            matchesViewModel.loadMatches();
            if (!AccountService.loadContactData(AuthenticationHandler.getId())) {
                hideBottomNavigation(true);
                hideToolbar(true);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isStartup", AuthenticationHandler.isStartup());
                bundle.putString("email", AuthenticationHandler.getEmail());
                bundle.putString("token", AuthenticationHandler.getToken());
                bundle.putLong("id", AuthenticationHandler.getId());
                Fragment fragment = new ContactDataInput();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, fragment);
            } else {
                Log.d(TAG, "onCreate: User[" + AuthenticationHandler.getId()
                        + "] with email " + AccountService.getContactData().getEmail()
                        + " logged in");
                settingsViewModel.loadSettings();
                accountViewModel.loadAccount();
                subscriptionViewModel.loadSubscription();
                hideBottomNavigation(false);
                hideToolbar(false);

                /* handle notification */
                Intent intent = getIntent();
                if(intent.getExtras() != null && intent.getExtras().containsKey("interaction")) {
                    NotificationType type = NotificationType.valueOf(intent.getExtras().getString("interaction"));
                    switch(type) {
                        case LEAD:
                        case CONNECTION:
                            long leadId = Long.parseLong(intent.getExtras().getString("relationshipId"));
                            Fragment fragment = new LeadsInteractionFragment();
                            Bundle bundle = new Bundle();
                            bundle.putLong("leadId", leadId);
                            fragment.setArguments(bundle);
                            tabViewModel.setCurrentLeadsFragment(fragment);
                            selectBottomNavigation(R.id.nav_leads);
                            break;
                        case MATCHLIST:
                            tabViewModel.setCurrentLeadsFragment(new LeadsOpenRequestsFragment());
                            selectBottomNavigation(R.id.nav_leads);
                            break;
                        case REQUEST:
                            tabViewModel.resetCurrentLeadsFragment();
                            tabViewModel.setCurrentLeadsTab(0);
                            selectBottomNavigation(R.id.nav_leads);
                            break;
                    }
                } else {
                    fragmentTransaction.add(R.id.fragment_container, new MatchesFragment());
                }
            }
        }
        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selected = null;
                if (!AuthenticationHandler.isLoggedIn()) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new RegisterLoginInformationFragment())
                            .commit();
                    return true;
                } else {
                    switch (item.getItemId()) {
                        case R.id.nav_matches:
                            selected = new MatchesFragment();
                            break;
                        case R.id.nav_leads:
                            selected = new LeadsContainerFragment();
                            break;
                        case R.id.nav_profile:
                            selected = new MyProfileFragment();
                            break;
                        case R.id.nav_settings:
                            selected = new SettingsFragment();
                            break;
                        default:
                            return false;
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, selected)
                            .commit();
                    return true;
                }
            };

    /**
     * Toggles the bottom navigation
     * @param isHidden if true, the bottom navigation is hidden
     *                 if false, the bottom navigation is visible
     */
    public void hideBottomNavigation(boolean isHidden) {
        findViewById(R.id.bottom_navigation).setVisibility(isHidden ? View.GONE : View.VISIBLE);
    }

    /**
     * Toggle the visibility of the toolbar
     * @param isHidden if true, toolbar is hidden
     *                 if false, toolbar is visible
     */
    public void hideToolbar(boolean isHidden) {
        findViewById(R.id.raising_app_bar).setVisibility(isHidden ? View.GONE : View.VISIBLE);
    }

    /**
     * Reset the selected item of the bottom navigation to the default value
     *
     * @param selectedId The id of the navigation element, that should be selected
     */
    public void selectBottomNavigation(int selectedId) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(selectedId);
    }

    /**
     * Allow to set a title and icon to our top app bar
     *
     * @param title          The title of the current app bar
     * @param showBackButton true, if the app bar should contain a "back" arrow
     *                       false, if app bar should not have this arrow
     */
    public void customizeActionBar(String title, boolean showBackButton) {
        toolbar.setTitle(title);
        if (showBackButton) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_32dp);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        } else {
            //TODO: add icon as icon, if back button is not wanted
            toolbar.setNavigationIcon(null);
        }
    }

    /**
     * Allow to set a custom menu containing a logout button into the toolbar
     *
     * @param setMenu true, if menu should be visible
     *                false, if menu should not be visible
     */
    public void setActionBarMenu(boolean setMenu) {
        if (setMenu) {
            toolbar.inflateMenu(R.menu.top_bar_menu);
            toolbar.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.top_bar_logout:
                        return true;
                    default:
                        return false;
                }
            });
        } else {
            toolbar.getMenu().clear();
        }
    }

    public void disablePreOnboarding() {
        try {
            InternalStorageHandler.saveObject(true, "onboarding");
        } catch (IOException e) {
            Log.e(TAG, "disablePreOnboarding: Error saving onboarding");
        }
    }

    public void disablePostOnboarding() {
        try {
            InternalStorageHandler.saveObject(true, "postOnboarding");
        } catch (IOException e) {
            Log.e(TAG, "disablePostOnboarding: Error saving post onboarding");
        }
    }

    public boolean isFirstAppLaunch() {
        long firstInstallTime = 0, lastUpdateTime = 1;
        try {
            firstInstallTime = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).firstInstallTime;
            lastUpdateTime = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).lastUpdateTime;
            Log.d(TAG, "onCreate: FirstInstall: " + firstInstallTime + "LastUpdate: " + lastUpdateTime);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "onCreate: PackageManagerError");
        }

        return (firstInstallTime == lastUpdateTime);
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(f instanceof RaisingFragment) {
            // do something with f
            ((RaisingFragment) f).resetTab();

        }

        super.onBackPressed();
    }
}
