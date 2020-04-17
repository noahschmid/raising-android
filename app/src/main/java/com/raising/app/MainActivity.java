package com.raising.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.raising.app.fragments.handshake.HandshakesFragment;
import com.raising.app.fragments.LoginFragment;
import com.raising.app.fragments.MatchesFragment;
import com.raising.app.fragments.SettingsFragment;
import com.raising.app.fragments.profile.ContactDetailsInput;
import com.raising.app.fragments.profile.MyProfileFragment;
import com.raising.app.util.AccountService;
import com.raising.app.util.AuthenticationHandler;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.RegistrationHandler;
import com.raising.app.viewModels.AccountViewModel;
import com.raising.app.viewModels.MatchesViewModel;
import com.raising.app.viewModels.ResourcesViewModel;

public class MainActivity extends AppCompatActivity {
    AccountViewModel accountViewModel;
    ResourcesViewModel resourcesViewModel;
    MatchesViewModel matchesViewModel;

    MaterialToolbar toolbar;

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InternalStorageHandler.setContext(getApplicationContext());
        AuthenticationHandler.init();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        toolbar = findViewById(R.id.raising_app_bar);
        // setSupportActionBar(toolbar);

        RegistrationHandler.setContext(getApplicationContext());

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        resourcesViewModel = new ViewModelProvider(this).get(ResourcesViewModel.class);
        matchesViewModel = new ViewModelProvider(this).get(MatchesViewModel.class);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if(!AuthenticationHandler.isLoggedIn()) {
                hideBottomNavigation(true);
                fragmentTransaction.replace(R.id.fragment_container, new LoginFragment());
            } else {
                if(!AccountService.loadContactDetails()) {
                    hideBottomNavigation(true);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isStartup", AuthenticationHandler.isStartup());
                    bundle.putString("email", AuthenticationHandler.getEmail());
                    bundle.putString("token", AuthenticationHandler.getToken());
                    bundle.putLong("id", AuthenticationHandler.getId());
                    Fragment fragment = new ContactDetailsInput();
                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                } else {
                    accountViewModel.loadAccount();
                    hideBottomNavigation(false);
                    fragmentTransaction.add(R.id.fragment_container, new MatchesFragment());
                }
            }
            fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selected = null;
                    if(!AuthenticationHandler.isLoggedIn()) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, new LoginFragment())
                                .commit();
                        return true;
                    } else {
                        switch (item.getItemId()) {
                            case R.id.nav_matches:
                                selected = new MatchesFragment();
                                break;
                            case R.id.nav_handshakes:
                                selected = new HandshakesFragment();
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
     * Allow to set a title and icon to our top app bar
     * @param title The title of the current app bar
     * @param showBackButton true, if the app bar should contain a "back" arrow
     *                       false, if app bar should not have this arrow
     */
    public void customizeActionBar(String title, boolean showBackButton ) {
        toolbar.setTitle(title);
        if (showBackButton) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_32dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            //TODO: add icon as icon, if back button is not wanted
            toolbar.setNavigationIcon(null);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FragmentManager manager = getSupportFragmentManager();

        int currentEntryCount = manager.getBackStackEntryCount();
        Log.d(TAG, "onBackPressed: EntryCount: " + currentEntryCount);
        if(currentEntryCount == 1) {

            return;
        }
        Fragment currentFragment = manager.findFragmentById(currentEntryCount - 1);
        Log.d(TAG, "onBackPressed: " + currentFragment);
        if(currentFragment != null) {
            // manager.beginTransaction().remove(currentFragment);
            manager.popBackStackImmediate();
        }
    }
}
