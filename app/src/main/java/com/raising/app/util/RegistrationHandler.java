package com.raising.app.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.raising.app.models.Account;
import com.raising.app.models.Continent;
import com.raising.app.models.Country;
import com.raising.app.models.Image;
import com.raising.app.models.Industry;
import com.raising.app.models.InvestmentPhase;
import com.raising.app.models.Investor;
import com.raising.app.models.InvestorType;
import com.raising.app.models.Label;
import com.raising.app.models.ContactDetails;
import com.raising.app.models.Model;
import com.raising.app.models.Startup;
import com.raising.app.models.Support;
import com.raising.app.models.stakeholder.BoardMember;
import com.raising.app.models.stakeholder.Founder;
import com.raising.app.models.stakeholder.Shareholder;
import com.raising.app.models.stakeholder.StakeholderItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistrationHandler {
    private static final String registrationEndpoint = "account/register";
    private static final String freeEmailEndpoint = "account/valid";

    private static boolean inProgress = false;
    private static Context context;
    private static boolean cancelAllowed = false;

    private static String accountType;
    private static Account account = new Account();
    private static Investor investor = new Investor();
    private static Startup startup = new Startup();
    private static ContactDetails contactDetails = new ContactDetails();

    public static void setContext(Context ctx) {
        context = ctx;
    }

    public static boolean shouldCancel() {
        return cancelAllowed;
    }

    public static void setCancelAllowed(boolean allowed) { cancelAllowed = allowed; }

    /**
     * Set the type of the account (startup or investor)
     * @param type
     * @throws IOException
     */
    public static void setAccountType(String type) throws IOException {
        accountType = type;
        if(type.equals("startup")) {
            startup.setFirstName(account.getFirstName());
            startup.setLastName(account.getLastName());
            startup.setName(account.getName());
            startup.setEmail(account.getEmail());
            startup.setPassword(account.getPassword());
            contactDetails.setStartup(true);
            contactDetails.setEmail(account.getEmail());
            InternalStorageHandler.saveObject(contactDetails, "rgstr_startup");
        }
        else{
            investor.setFirstName(account.getFirstName());
            investor.setLastName(account.getLastName());
            investor.setName(account.getName());
            investor.setEmail(account.getEmail());
            investor.setPassword(account.getPassword());
            contactDetails.setStartup(false);
            contactDetails.setEmail(account.getEmail());
            InternalStorageHandler.saveObject(contactDetails, "rgstr_investor");
        }
        saveRegistrationState();
        InternalStorageHandler.saveObject(contactDetails, "rgstr_contact");
    }
    public static String getAccountType() { return accountType; }
    public static boolean isStartup() { return contactDetails.isStartup(); }

    /**
     * Save login information to internal storage until registration gets submitted
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     * @throws IOException
     */
    public static void saveLoginInformation(String firstName, String lastName,
                                            String email, String password)  throws IOException {
        account.setEmail(email);
        account.setName(firstName + " " + lastName);
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setPassword(password);
        contactDetails.setEmail(email);

        InternalStorageHandler.saveObject(account, "rgstr_account");
        InternalStorageHandler.saveObject(contactDetails, "rgstr_contact");
    }

    public static Account getAccount() { return account; }
    public static Investor getInvestor() { return investor; }
    public static Startup getStartup() { return startup; }
    public static ContactDetails getContactDetails() { return contactDetails; }

    /**
     * Cancel current registration process
     */
    public static void cancel() {
        context.deleteFile("rgstr_contact");
        context.deleteFile("rgstr_account");
        context.deleteFile("rgstr");
        context.deleteFile("rgstr_startup");
        context.deleteFile("rgstr_investor");

    }

    /**
     * Check whether currently a registration is in progress
     * @return true if registration is in progress else false
     */
    public static boolean isInProgress(Context ctx) {
        context = ctx;
        File file = context.getFileStreamPath("rgstr");
        if(!file.exists())
            return false;

        try {
            FileInputStream fis = context.openFileInput("rgstr");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            accountType = bufferedReader.readLine();
            isr.close();
            fis.close();

            account = loadAccount();
            contactDetails = loadContactDetails();
            investor = loadInvestor();
            startup = loadStartup();
        } catch(Exception e) {
            Log.e("RegistrationHandler", e.getMessage());
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
        contactDetails = new ContactDetails();

        String registrationInfo = accountType;
        InternalStorageHandler.saveString(registrationInfo, "rgstr");
    }

    /**
     * Save current page and account type
     * @throws IOException
     */
    private static void saveRegistrationState() throws IOException {
        String registrationInfo = accountType;
        InternalStorageHandler.saveString(registrationInfo, "rgstr");
    }

    /**
     * Load saved account details
     * @return
     */
    public static Account loadAccount() {
        try {
            return (Account) InternalStorageHandler.loadObject("rgstr_account");
        } catch (Exception e) {
            return new Account();
        }
    }

    /**
     * Load saved investor specific details
     * @return
     */
    public static Investor loadInvestor() {
        try {
            return (Investor) InternalStorageHandler.loadObject("rgstr_investor");
        } catch (Exception e) {
            Log.e("RegistrationHandler", "Error loading investor: " + e.getMessage());
            return new Investor();
        }
    }

    /**
     * Load saved investor specific details
     * @return
     */
    public static Startup loadStartup() {
        try {
            return (Startup) InternalStorageHandler.loadObject("rgstr_startup");
        } catch (Exception e) {
            Log.e("RegistrationHandler", "Error loading startup: " + e.getMessage());
            return new Startup();
        }
    }

    /**
     * Load saved private profile
     * @return
     */
    public static ContactDetails loadContactDetails() {
        try {
            return (ContactDetails) InternalStorageHandler.loadObject("rgstr_contact");
        } catch (Exception e) {
            return new ContactDetails();
        }
    }

    /**
     * Save private profile in a file named with id of account and cancel delete registration info
     * @param id the id of the newly created account
     * @param token the login token
     * @throws IOException
     */
    public static void finish(long id, String token, boolean isStartup) throws Exception {
        AuthenticationHandler.login(account.getEmail(), token, id, isStartup);
        AccountService.saveContactDetails(contactDetails);
        cancel();
    }

    public static void saveInvestor(Investor investor) throws IOException {
        RegistrationHandler.investor = investor;
        InternalStorageHandler.saveObject(RegistrationHandler.investor,
                "rgstr_investor");
    }

    public static void saveStartup(Startup startup) throws IOException {
        RegistrationHandler.startup = startup;
        InternalStorageHandler.saveObject(RegistrationHandler.startup,
                "rgstr_startup");
    }

    public static void saveContactDetails(ContactDetails contactDetails) throws IOException {
        RegistrationHandler.contactDetails = contactDetails;
        InternalStorageHandler.saveObject(RegistrationHandler.contactDetails,
                "rgstr_contact");
    }
}
