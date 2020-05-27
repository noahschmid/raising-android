package com.raising.app.util;

import android.content.Context;
import android.util.Log;

import com.raising.app.models.Account;
import com.raising.app.models.ContactData;
import com.raising.app.models.Investor;
import com.raising.app.models.Startup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This helper class handles the whole registration process and saves already entered information
 * to internal storage so it doesn't get lost when closing the app
 */

public class RegistrationHandler {
    private static final String registrationEndpoint = "account/register";
    private static final String freeEmailEndpoint = "account/valid";

    private static boolean inProgress = false;
    private static boolean isStartup = false;

    private static String accountType;
    private static Account account = new Account();
    private static Investor investor = new Investor();
    private static Startup startup = new Startup();
    private static ContactData contactData = new ContactData();

    public static boolean isInProgress() { return inProgress; }

    /**
     * Set the type of the account (startup or investor)
     * @param type string indicating type of account ('startup' or 'investor')
     * @throws IOException gets thrown if writing to internal storage failed
     */
    public static void setAccountType(String type) throws IOException {
        accountType = type;
        if(type.equals("startup")) {
            startup.setFirstName(account.getFirstName());
            startup.setLastName(account.getLastName());
            startup.setName(account.getName());
            startup.setEmail(account.getEmail());
            startup.setPassword(account.getPassword());
            contactData.setEmail(account.getEmail());
            isStartup = true;
            InternalStorageHandler.saveObject(startup, "rgstr_contact");
        }
        else{
            investor.setFirstName(account.getFirstName());
            investor.setLastName(account.getLastName());
            investor.setName(account.getName());
            investor.setEmail(account.getEmail());
            investor.setPassword(account.getPassword());
            contactData.setEmail(account.getEmail());
            isStartup = false;
            InternalStorageHandler.saveObject(investor, "rgstr_investor");
        }
        saveAccountType();
        InternalStorageHandler.saveObject(account, "rgstr_contact");
        InternalStorageHandler.saveObject(contactData, "rgstr_contact");
    }
    public static String getAccountType() { return accountType; }
    public static boolean isStartup() { return isStartup; }

    /**
     * Save login information to internal storage until registration gets submitted
     * @param firstName first name of user
     * @param lastName last name of user
     * @param email email of user
     * @param password password of user
     * @throws IOException gets thrown if writing to internal storage failed
     */
    public static void saveLoginInformation(String firstName, String lastName,
                                            String email, String password)  throws IOException {
        account.setEmail(email);
        account.setName(firstName + " " + lastName);
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setPassword(password);
        contactData.setEmail(email);

        InternalStorageHandler.saveObject(account, "rgstr_account");
        InternalStorageHandler.saveObject(contactData, "rgstr_contact");
    }

    public static Account getAccount() { return account; }
    public static Investor getInvestor() { return investor; }
    public static Startup getStartup() { return startup; }
    public static ContactData getContactData() { return contactData; }


    /**
     * Cancel current registration process
     */
    private static void cancel() {
        InternalStorageHandler.deleteFile("rgstr_contact");
        InternalStorageHandler.deleteFile("rgstr_account");
        InternalStorageHandler.deleteFile("rgstr");
        InternalStorageHandler.deleteFile("rgstr_startup");
        InternalStorageHandler.deleteFile("rgstr_investor");

        investor = new Investor();
        startup = new Startup();
    }

    /**
     * Check whether currently a registration is in progress
     * @return true if registration is in progress else false
     */
    public static boolean init() {
        if(!InternalStorageHandler.exists("rgstr"))
            return false;

        try {
            inProgress = true;

            FileInputStream fis = InternalStorageHandler.getContext().openFileInput("rgstr");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            accountType = bufferedReader.readLine();
            isr.close();
            fis.close();

            account = loadAccount();
            contactData = loadContactData();
            if (accountType.equals("investor")) {
                investor = loadInvestor();
            } else if (accountType.equals("startup")) {
                startup = loadStartup();
            }
        } catch(Exception e) {
            Log.e("RegistrationHandler", "" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Start a new registration process
     */
    public static void begin() throws IOException {
        accountType = "undefined";
        inProgress = true;
        account = new Account();
        contactData = new ContactData();

        String registrationInfo = accountType;
        InternalStorageHandler.saveString(registrationInfo, "rgstr");
    }

    /**
     * Save account type
     * @throws IOException gets thrown if writing to internal storage failed
     */
    private static void saveAccountType() throws IOException {
        String registrationInfo = accountType;
        InternalStorageHandler.saveString(registrationInfo, "rgstr");
    }

    /**
     * Load saved account details
     * @return instance of Account
     */
    public static Account loadAccount() {
        if(!InternalStorageHandler.exists("rgstr_account")) {
            return new Account();
        }
        try {
            return (Account) InternalStorageHandler.loadObject("rgstr_account");
        } catch (Exception e) {
            return new Account();
        }
    }

    /**
     * Load saved investor specific details
     * @return instance of Investor
     */
    public static Investor loadInvestor() {
        if(!InternalStorageHandler.exists("rgstr_investor")) {
            return new Investor();
        }
        try {
            return (Investor) InternalStorageHandler.loadObject("rgstr_investor");
        } catch (Exception e) {
            Log.e("RegistrationHandler", "Error loading investor: " + e.getMessage());
            return new Investor();
        }
    }

    /**
     * Load saved investor specific details
     * @return instance of Startup
     */
    public static Startup loadStartup() {
        if(!InternalStorageHandler.exists("rgstr_startup")) {
            return new Startup();
        }
        try {
            return (Startup) InternalStorageHandler.loadObject("rgstr_startup");
        } catch (Exception e) {
            Log.e("RegistrationHandler", "Error loading startup: " + e.getMessage());
            return new Startup();
        }
    }

    /**
     * Load saved private profile
     * @return instance of ContactData class containing the entered contact data
     */
    public static ContactData loadContactData() {
        if(!InternalStorageHandler.exists("rgstr_contact")) {
            return new ContactData();
        }
        try {
            return (ContactData) InternalStorageHandler.loadObject("rgstr_contact");
        } catch (Exception e) {
            return new ContactData();
        }
    }

    /**
     * Save private profile in a file named with id of account and cancel delete registration info
     * @param id the id of the newly created account
     * @param token the login token
     * @throws IOException gets thrown if writing to internal storage failed
     */
    public static void finish(long id, String token, boolean isStartup) throws Exception {
        AuthenticationHandler.login(account.getEmail(), token, id, isStartup);
        AccountService.saveContactData(contactData);
        cancel();
    }

    /**
     * Save investor to internal storage
     * @param investor instance of Investor class
     * @throws IOException gets thrown if writing to internal storage failed
     */
    public static void saveInvestor(Investor investor) throws IOException {
        RegistrationHandler.investor = investor;
        InternalStorageHandler.saveObject(RegistrationHandler.investor,
                "rgstr_investor");
    }

    /**
     * Save startup to internal storage
     * @param startup instance of Startup class
     * @throws IOException gets thrown if writing to internal storage failed
     */
    public static void saveStartup(Startup startup) throws IOException {
        RegistrationHandler.startup = startup;
        InternalStorageHandler.saveObject(RegistrationHandler.startup,
                "rgstr_startup");
    }

    /**
     * Save contact data to internal storage
     * @param contactData instance of ContactData class
     * @throws IOException gets thrown if writing to internal storage failed
     */
    public static void saveContactData(ContactData contactData) throws IOException {
        RegistrationHandler.contactData = contactData;
        InternalStorageHandler.saveObject(RegistrationHandler.contactData,
                "rgstr_contact");
    }
}
