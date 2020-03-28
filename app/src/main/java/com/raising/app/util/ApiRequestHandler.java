package com.raising.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.telephony.AccessNetworkConstants;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.raising.app.GenericRequest;
import com.raising.app.MatchesFragment;
import com.raising.app.R;
import com.raising.app.authentication.fragments.LoginFragment;
import com.raising.app.authentication.fragments.registration.RegisterSelectTypeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class ApiRequestHandler {
    private static final boolean CONNECT_TO_DEV_SERVER = true;
    private static ApiRequestHandler instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context context;
    private static String domain = "https://33383.hostserv.eu:";

    public static String getDomain() {
        if(CONNECT_TO_DEV_SERVER)
            return domain + "8081/";
        return domain + "8080/";
    }

    private ApiRequestHandler(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache
                            = new LruCache<String, Bitmap>(20);
                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
        });
    }

    public static synchronized ApiRequestHandler getInstance(Context context) {
        if(instance == null)
            instance = new ApiRequestHandler(context);
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    /**
     * Perform simple post request
     * @param endpoint
     * @param callback
     * @param errorCallback
     * @param params
     * @param ctx
     */
    public static void performPostRequest(String endpoint, Function<JSONObject, Void> callback,
                                          Function<VolleyError, Void> errorCallback,
                                          JSONObject params, Context ctx) {
        try {
            GenericRequest request = new GenericRequest(
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
            });
            ApiRequestHandler.getInstance(ctx).addToRequestQueue(request);
        } catch (Exception e) {
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Perform a simple get request
     * @param endpoint the backend endpoint
     * @param callback the function to call when request was successful
     * @param errorCallback the function to call when there was an error
     */
    public static void performGetRequest(String endpoint, Function<JSONObject, Void> callback,
                           Function<VolleyError, Void> errorCallback, Context ctx) {
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
                    }
                });

        getInstance(ctx).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Perform a simple get request with jsonarray as response
     * @param endpoint the backend endpoint
     * @param callback the function to call when request was successful
     * @param errorCallback the function to call when there was an error
     */
    public static void performArrayGetRequest(String endpoint, Function<JSONArray, Void> callback,
                                         Function<VolleyError, Void> errorCallback, Context ctx) {
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
                });

        getInstance(ctx).addToRequestQueue(jsonObjectRequest);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Parse error returned by volley to a string
     * @param error
     */
    public static String parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            JSONArray errors = data.getJSONArray("errors");
            JSONObject jsonMessage = errors.getJSONObject(0);
            String message = jsonMessage.getString("message");
            return message;
        } catch (JSONException | UnsupportedEncodingException e) {
            Log.d("debugMessage", e.getMessage() + "");
            return "failed to parse error";
        }
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}

