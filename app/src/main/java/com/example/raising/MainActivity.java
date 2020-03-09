package com.example.raising;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.raising.authentication.fragments.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(!AuthenticationHandler.isLoggedIn(getApplicationContext())) {
            fragmentTransaction.add(R.id.fragment_container, new LoginFragment());
            fragmentTransaction.addToBackStack("LoginFragment");
        } else {
            fragmentTransaction.add(R.id.fragment_container, new MatchesFragment());
            fragmentTransaction.addToBackStack("MatchesFragment");
        }
        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selected = null;

                    switch(item.getItemId()) {
                        case R.id.nav_home:
                            selected = new MatchesFragment();
                            break;
                        case R.id.nav_profile:
                            selected = new ProfileFragment();
                            break;
                        case R.id.nav_settings:
                            selected = new SettingsFragment();
                            break;
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
        findViewById(R.id.bottom_navigation).setVisibility(isHidden ? View.INVISIBLE : View.VISIBLE);
    }
}
