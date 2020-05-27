package com.raising.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.SyncStateContract;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.raising.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static com.android.volley.VolleyLog.TAG;

/**
 * This class handles all sending to / receiving from backend server
 */

public class ApiRequestHandler {
    // -- CHOOSE SERVER --
    private static final boolean CONNECT_TO_DEV_SERVER = false;

    private static ApiRequestHandler instance;
    private RequestQueue requestQueue;
    private static Context context;
    private static String domain = "https://33383.hostserv.eu:";

    /**
     * Get domain of backend server
     *
     * @return string holding the domain address
     */
    public static String getDomain() {
        if (CONNECT_TO_DEV_SERVER)
            return domain + "8081/";
        return domain + "8080/";
    }

    /**
     * Create new request queue
     * @param context the application context
     */
    private ApiRequestHandler(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    /**
     * Get instance of ApiRequestHandler (singleton)
     * @param context
     * @return
     */
    public static synchronized ApiRequestHandler getInstance(Context context) {
        if (instance == null)
            instance = new ApiRequestHandler(context);
        return instance;
    }

    /**
     * Get request queue
     * @return request queue
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    /**
     * Perform simple post request
     *
     * @param endpoint the backend enpoint
     * @param callback callback function that gets called when request was successful
     * @param errorCallback error function that gets called when an error happened
     * @param params JsonObject containing parameters for backend request
     */
    public static void performPostRequest(String endpoint, Function<JSONObject, Void> callback,
                                          Function<VolleyError, Void> errorCallback,
                                          JSONObject params) {
        try {
            GenericRequest request = new GenericRequest(
                    getDomain() + endpoint,
                    params,
                    response -> {
                        Log.d(TAG, "onResponse for " + endpoint);
                        callback.apply(response);
                    }, error -> {
                        if(error.networkResponse != null) {
                            Log.e(TAG, "onErrorResponse[" + error.networkResponse.statusCode +
                                    "] for " + getDomain() + endpoint);
                        } else {
                            Log.e(TAG, "onErrorResponse for " + getDomain() + endpoint);
                        }
                        errorCallback.apply(error);
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    if (AuthenticationHandler.isLoggedIn()) {
                        headers.put("Authorization", "Bearer " + AuthenticationHandler.getToken());
                    }
                    return headers;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1.0f));

            ApiRequestHandler.getInstance(InternalStorageHandler.getContext())
                    .addToRequestQueue(request);
        } catch (Exception e) {
            Log.e("ApiRequestHandler", "Error while sending POST request to " +
                    endpoint + ": " + e.getMessage());
        }
    }

    /**
     * Perform post request with array as params
     *
     * @param endpoint the backend enpoint
     * @param callback callback function that gets called when request was successful
     * @param errorCallback error function that gets called when an error happened
     * @param params JsonObject containing parameters for backend request
     */
    public static void performPostRequest(String endpoint, Function<JSONArray, Void> callback,
                                          Function<VolleyError, Void> errorCallback,
                                          JSONArray params) {
        try {
            GenericArrayRequest request = new GenericArrayRequest(
                    Request.Method.POST,
                    getDomain() + endpoint,
                    params,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, "onResponse for " + endpoint);
                            callback.apply(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse != null) {
                        Log.e(TAG, "onErrorResponse[" + error.networkResponse.statusCode +
                                "] for " + endpoint);
                    } else {
                        Log.e(TAG, "onErrorResponse for " + endpoint);
                    }
                    errorCallback.apply(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    if (AuthenticationHandler.isLoggedIn()) {
                        headers.put("Authorization", "Bearer " + AuthenticationHandler.getToken());
                    }
                    return headers;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1.0f));

            ApiRequestHandler.getInstance(InternalStorageHandler.getContext())
                    .addToRequestQueue(request);
        } catch (Exception e) {
            Log.e("ApiRequestHandler", "Error while sending POST request to " +
                    endpoint + ": " + e.getMessage());
        }
    }

    /**
     * Perform simple patch request
     *
     * @param endpoint the backend enpoint
     * @param callback callback function that gets called when request was successful
     * @param errorCallback error function that gets called when an error happened
     * @param params JsonObject containing parameters for backend request
     */
    public static void performPatchRequest(String endpoint, Function<JSONObject, Void> callback,
                                           Function<VolleyError, Void> errorCallback,
                                           JSONObject params) {
        try {
            GenericRequest request = new GenericRequest(Request.Method.PATCH,
                    getDomain() + endpoint,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callback.apply(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorCallback.apply(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    if (AuthenticationHandler.isLoggedIn()) {
                        headers.put("Authorization", "Bearer " + AuthenticationHandler.getToken());
                        Log.d(TAG, "getHeaders: Bearer " + AuthenticationHandler.getToken());
                    }
                    return headers;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1.0f));

            ApiRequestHandler.getInstance(InternalStorageHandler.getContext())
                    .addToRequestQueue(request);
        } catch (Exception e) {
            Log.e("ApiRequestHandler", "Error while sending PATCH request to " +
                    endpoint + ": " + e.getMessage());
        }
    }

    /**
     * Perform a simple get request
     *
     * @param endpoint      the backend endpoint
     * @param callback      the function to call when request was successful
     * @param errorCallback the function to call when there was an error
     */
    public static void performGetRequest(String endpoint, Function<JSONObject, Void> callback,
                                         Function<VolleyError, Void> errorCallback) {
        GenericRequest jsonObjectRequest = new GenericRequest
                (Request.Method.GET, getDomain() + endpoint, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.apply(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorCallback.apply(error);
                        Log.d(TAG, "onErrorResponse: GET");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (AuthenticationHandler.isLoggedIn()) {
                    headers.put("Authorization", "Bearer " + AuthenticationHandler.getToken());
                    Log.d(TAG, "getHeaders: Token" + AuthenticationHandler.getToken());
                }
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                0, 1.0f));

        getInstance(InternalStorageHandler.getContext())
                .addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Perform a simple delete request
     *
     * @param endpoint      the backend endpoint
     * @param callback      the function to call when request was successful
     * @param errorCallback the function to call when there was an error
     */
    public static void performDeleteRequest(String endpoint, Function<JSONObject, Void> callback,
                                            Function<VolleyError, Void> errorCallback) {
        GenericRequest jsonObjectRequest = new GenericRequest
                (Request.Method.DELETE, getDomain() + endpoint, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.apply(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorCallback.apply(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (AuthenticationHandler.isLoggedIn()) {
                    headers.put("Authorization", "Bearer " + AuthenticationHandler.getToken());
                }
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                0, 1.0f));

        getInstance(InternalStorageHandler.getContext())
                .addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Perform a simple get request with jsonarray as response
     *
     * @param endpoint      the backend endpoint
     * @param callback      the function to call when request was successful
     * @param errorCallback the function to call when there was an error
     */
    public static void performArrayGetRequest(String endpoint, Function<JSONArray, Void> callback,
                                              Function<VolleyError, Void> errorCallback) {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, getDomain() + endpoint, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        callback.apply(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorCallback.apply(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (AuthenticationHandler.isLoggedIn()) {
                    headers.put("Authorization", "Bearer " + AuthenticationHandler.getToken());
                }
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                0, 1.0f));
        getInstance(InternalStorageHandler.getContext())
                .addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Add new backend request to request queue
     * @param req the request to add
     * @param <T> type of request
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Parse error returned by volley to a string
     *
     * @param error error returned from a backend request
     */
    public static String parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            return data.getString("message");
        } catch (JSONException | UnsupportedEncodingException | NullPointerException e) {
            Log.e("ApiRequestHandler", "Error while parsing volley error: " +
                    e.getMessage());
            return "failed to parse error";
        }
    }

    /**
     * Default error handler
     */
    public static Function<VolleyError, Void> errorHandler = error -> {
        Log.e("ApiRequestHandler", "Error while performing request: " +
                parseVolleyError(error));
        return null;
    };
}

