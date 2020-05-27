package com.raising.app.util;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.raising.app.models.ContactData;
import com.raising.app.models.ViewState;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.Resources;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Handles the contact data of accounts (including logged in account)
 */

public class ContactDataHandler {
    private static final String TAG = "ContactDataHandler";
    private static ArrayList<ContactData> contactData = new ArrayList<>();

    /**
     * Load stored data from internal storage
     */
    public static void init() {
        contactData = loadContactData();
        if(contactData == null)
            contactData = new ArrayList<>();

        Log.d(TAG, "init: contact data size: " + contactData.size());
        contactData.forEach(data -> {
            Log.d(TAG, "- id: " + data.getAccountId() + " email: " + data.getEmail() +  " phone: "
            + data.getPhone());
        });
    }

    /**
     * Get contact data of lead
     * @param leadAccountId id of lead account
     * @return contact data object
     */
    public static ContactData getContactData(long leadAccountId) {
        for(ContactData data : contactData) {
            if(data.getAccountId() == leadAccountId) {
                return data;
            }
        }
        return null;
    }

    /**
     * Process new contact data information. If already an entry exists for given leadAccountId,
     * update this entry. Otherwise add a new ContactData instance
     * @param data a ContactData instance containing contact information about a certain lead
     */
    public static void processNewData(ContactData data) {
        if(data.getAccountId() == -1)
            return;

        boolean found = false;
        for(int i = 0; i < contactData.size(); ++i) {
            if(contactData.get(i).getAccountId() == data.getAccountId()) {
                ContactData updated = contactData.get(i);
                if(data.getEmail() != null) {
                    updated.setEmail(data.getEmail());
                }
                if(data.getPhone() != null) {
                    updated.setPhone(data.getPhone());
                }
                if(data.getBusinessPlanId() != -1) {
                    updated.setBusinessPlanId(data.getBusinessPlanId());
                }

                contactData.set(i, updated);
                found = true;
                break;
            }
        }

        if(!found){
            contactData.add(data);
        }

        saveContactData();
    }

    /**
     * Load contact data from internal storage
     * @return list of ContactData instances
     */
    private static ArrayList<ContactData> loadContactData() {
        try {
            if(InternalStorageHandler.exists("contactData_" + AuthenticationHandler.getId())) {
                return (ArrayList<ContactData>)InternalStorageHandler
                        .loadObject("contactData_" + AuthenticationHandler.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while loading contact data: " + e.getMessage());
        }

        return null;
    }

    /**
     * Save current contact data to internal storage
     */
    private static void saveContactData() {
        try {
            InternalStorageHandler.saveObject(contactData,
                    "contactData_" + AuthenticationHandler.getId());
            Log.d(TAG, "saveContactData: saving contact data successful");
        } catch(Exception e) {
            Log.e(TAG, "error saving contact data: " + e.getMessage());
        }
    }
}
