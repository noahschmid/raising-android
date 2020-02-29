package com.example.prototypeapplication;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


public class PrototypeLoginFragment extends Fragment implements View.OnClickListener {
    private EditText username_input;
    private EditText password_input;

    private PrototypeLoginViewModel mViewModel;

    public static PrototypeLoginFragment newInstance() {
        return new PrototypeLoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prototype_login_fragment, container, false);

        username_input = view.findViewById(R.id.editText_login_username);
        password_input = view.findViewById(R.id.editText_login_password);

        Button btn_login = view.findViewById(R.id.button_login);
        btn_login.setOnClickListener(this);
        Button btn_register = view.findViewById(R.id.button_register);
        btn_register.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                login();
                break;
            case R.id.button_register:
                goToRegisterFragment();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PrototypeLoginViewModel.class);
        // TODO: Use the ViewModel
    }


    private void login() {

        String username = username_input.getText().toString();
        String password = password_input.getText().toString();

        try {
            String endpoint = "http://33383.hostserv.eu:8080/account/login";
            JSONObject params = new JSONObject();
            params.put("username", username);
            params.put("password", password);
            JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST,
                    endpoint,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                username_input.setText(response.getString("message"));
                            } catch (JSONException e){
                                Log.d("debugMessage", e.getMessage());
                                return;
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse.statusCode == 403) {
                        String body = new String(error.networkResponse.data);
                        try {
                            JSONObject response = new JSONObject(body);
                            username_input.setText(response.getString("message"));
                        } catch (JSONException e) {
                            Log.d("debugMessage", e.toString());
                        }
                    }
                    Log.d("debugMessage", error.toString());
                }
            });
            ApiRequestHandler.getInstance(getContext()).addToRequestQueue(loginRequest);
        } catch(Exception e) {
            Log.d("debugMessage", e.getMessage());
            return;
        }
    }

    private void goToRegisterFragment() {

        try {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new PrototypeRegisterFragment())
                    .addToBackStack("PrototypeRegisterFragment")
                    .commit();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
