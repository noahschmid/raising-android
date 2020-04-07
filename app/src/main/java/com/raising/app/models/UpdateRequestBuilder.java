package com.raising.app.models;

import android.util.Log;

import com.raising.app.fragments.RaisingFragment;
import com.raising.app.util.NotificationHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UpdateRequestBuilder {
    JSONObject request;
    private int unitializedIntValue = -1;

    public void UpdateRequestBuilder() {
        request = new JSONObject();
    }


    /**
     * Adds new field to update query. Field gets only updated, if field is not equal null
     * @param field the field which needs to be updated
     * @param fieldName the name of the field inside the database
     */
    public void addField(Object field, String fieldName) {
        try {
            request.put(fieldName, field);
        } catch (JSONException e) {
            NotificationHandler.displayGenericError();
            Log.e("UpdateRequestBuidler", "Error while adding field '" +
                    fieldName + "': " + e.getMessage() );
        }
    }


}
