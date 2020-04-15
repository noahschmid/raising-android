package com.raising.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.raising.app.fragments.HandshakesFragment;
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
import com.raising.app.viewModels.ResourcesViewModel;

public class MainActivity extends AppCompatActivity {
    AccountViewModel accountViewModel;
    ResourcesViewModel resourcesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InternalStorageHandler.setContext(getApplicationContext());
        AuthenticationHandler.init();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        RegistrationHandler.setContext(getApplicationContext());

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        resourcesViewModel = new ViewModelProvider(this).get(ResourcesViewModel.class);

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
}
