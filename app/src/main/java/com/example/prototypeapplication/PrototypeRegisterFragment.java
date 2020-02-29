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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class PrototypeRegisterFragment extends Fragment {

    private PrototypeRegisterViewModel mViewModel;

    public static PrototypeRegisterFragment newInstance() {
        return new PrototypeRegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.prototype_register_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PrototypeRegisterViewModel.class);
        // TODO: Use the ViewModel
    }

    private void register(String username, String password) {
        try {
            String endpoint = "http://33383.hostserv.eu:8080/account/register";
            JSONObject params = new JSONObject();
            params.put("username", username);
            params.put("password", password);
            JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST,
                    endpoint,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //TODO: display success
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse.statusCode == 403) {
                        String body = new String(error.networkResponse.data);
                        try {
                            JSONObject response = new JSONObject(body);
                            //TODO: display error
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
        }
    }

}
