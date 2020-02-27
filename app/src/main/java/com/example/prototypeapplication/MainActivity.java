package com.example.prototypeapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, new PrototypeLoginFragment());
        fragmentTransaction.addToBackStack("PrototypeLoginFragment");
        fragmentTransaction.commit();
    }

    public void switchFragment(View view) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, new PrototypeLoginFragment());
        fragmentTransaction.addToBackStack("PrototypeLoginFragment");
        fragmentTransaction.commit();
    }
}
