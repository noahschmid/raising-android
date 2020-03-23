package com.raising.app.util;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.raising.app.authentication.fragments.registration.RegisterLoginInformationFragment;
import com.raising.app.models.Account;
import com.raising.app.models.Investor;
import com.raising.app.models.PrivateProfile;
import com.raising.app.models.Startup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;

public class RegistrationHandler {
    private static final String registrationEndpoint = "account/register";
    private static final String freeEmailEndpoint = "account/valid";

    private static boolean inProgress = false;
    private static int currentPage = 0;
    private static Context context;
    private static boolean cancelAllowed = false;

    private static String accountType;
    private static Account account = new Account();
    private static Investor investor = new Investor();
    private static Startup startup = new Startup();
    private static PrivateProfile privateProfile = new PrivateProfile();

    /**
     * Proceed to next page. Gets called whenever user gets to next page in the registration process
     */
    public static void proceed() throws IOException {
        currentPage++;
        inProgress = true;
        saveRegistrationState();
    }

    public static void setContext(Context ctx) {
        context = ctx;
    }

    /**
     * Indicates whether current registration page has already been visited
     * @return true if page has been visited, false otherwise
     */
    public static boolean hasBeenVisited() {
        cancelAllowed = true;
        return (currentPage > 0) && !inProgress;
    }

    public static boolean shouldCancel() {
        return cancelAllowed;
    }

    /**
     * Skip current page. Gets called  when app is reloaded to skip to the page that was last modified
     */
    public static void skip() { currentPage--; }

    public static void setAccountType(String type) { accountType = type; }
    public static String getAccountType() { return accountType; }
    public static boolean isStartup() { return accountType.equalsIgnoreCase("startup"); }

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
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setPassword(password);

        saveObject(account, "rgstr_account");
    }

    /**
     * Save the private profile information which doesn't get stored in the database
     * @param company
     * @param city
     * @param street
     * @param zipCode
     * @param website
     * @param country
     */
    public static void saveProfileInformation(String company, String city,
                                              String street, String zipCode, String website,
                                              String country) throws IOException {
        privateProfile.setCompany(company);
        privateProfile.setCity(city);
        privateProfile.setStreet(street);
        privateProfile.setWebsite(website);
        privateProfile.setZipCode(zipCode);
        privateProfile.setCountry(country);

        saveObject(privateProfile, "rgstr_profile");
    }

    /**
     * Submit registration to server
     */
    public static void submit() {
        inProgress = false;

        

        context.deleteFile("rgstr_profile");
        context.deleteFile("rgstr_account");
        context.deleteFile("rgstr_startup");
        context.deleteFile("rgstr_investor");
        context.deleteFile("rgstr");
        currentPage = 0;
    }

    public static Account getAccount() { return account; }
    public static Investor getInvestor() { return investor; }
    public static Startup getStartup() { return startup; }
    public static PrivateProfile getPrivateProfile() { return privateProfile; }

    /**
     * Cancel current registration process
     */
    public static void cancel() {
        currentPage = 0;
        context.deleteFile("rgstr_profile");
        context.deleteFile("rgstr_account");
        context.deleteFile("rgstr");
        context.deleteFile("rgstr_startup");
        context.deleteFile("rgstr_investor");
        Log.d("debugMessage", "canceled registration");
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

            currentPage = Integer.parseInt(bufferedReader.readLine());
            Log.d("debugMessage", "page: " + currentPage);
            accountType = bufferedReader.readLine();
            isr.close();
            fis.close();

            account = loadAccount();
            privateProfile = loadPrivateProfile();
            investor = loadInvestor();
        } catch(Exception e) {
            Log.d("debugMessage", e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Start a new registration process
     */
    public static void begin() throws IOException {
        currentPage = 0;
        accountType = "undefined";
        inProgress = true;
        account = new Account();
        privateProfile = new PrivateProfile();

        String registrationInfo = currentPage + "\n" + accountType;
        saveString(registrationInfo, "rgstr");
    }

    /**
     * Save object to internal storage
     * @param object
     * @param filename
     * @throws IOException
     */
    private static void saveObject(Object object, String filename) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    /**
     * Save string to internal storage
     * @param string
     * @param filename
     * @throws IOException
     */
    private static void saveString(String string, String filename) throws IOException {
        FileOutputStream outputStream;
        outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        outputStream.write(string.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private static void saveRegistrationState() throws IOException {
        String registrationInfo = currentPage + "\n" + accountType;
        saveString(registrationInfo, "rgstr");
    }

    /**
     * Load object from internal storage
     * @param filename the file to load from
     * @return Object loaded from internal storage
     * @throws Exception
     */
    private static Object loadObject(String filename) throws Exception {
        FileInputStream fis = context.openFileInput(filename);
        ObjectInputStream ois = new ObjectInputStream (fis);
        Object obj = ois.readObject();
        ois.close();
        fis.close();
        return obj;
    }

    /**
     * Read file line by line and return array list of strings
     * @param filename
     * @return
     * @throws IOException
     */
    private static ArrayList<String> loadStrings(String filename) throws IOException {
        FileInputStream fis = context.openFileInput(filename);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        ArrayList<String> result = new ArrayList<>();
        String line;
        while((line = bufferedReader.readLine()) != null){
            result.add(line);
        }
        isr.close();
        fis.close();
        return result;
    }

    /**
     * Load saved account details
     * @return
     */
    public static Account loadAccount() {
        try {
            return (Account) loadObject("rgstr_account");
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
            return (Investor) loadObject("rgstr_investor");
        } catch (Exception e) {
            return new Investor();
        }
    }

    /**
     * Load saved investor specific details
     * @return
     */
    public static Startup loadStartup() {
        try {
            return (Startup) loadObject("rgstr_startup");
        } catch (Exception e) {
            return new Startup();
        }
    }

    /**
     * Load saved private profile
     * @return
     */
    public static PrivateProfile loadPrivateProfile() {
        try {
            return (PrivateProfile) loadObject("rgstr_profile");
        } catch (Exception e) {
            return new PrivateProfile();
        }
    }

    public static void saveInvestorMatchingFragment(float ticketSizeMin, float ticketSizeMax,
                                                    int investorType, ArrayList<Long> investmentPhases,
                                                    ArrayList<Long> industries, ArrayList<Long> support,
                                                    ArrayList<Long> countries) throws IOException {
        investor.setTicketSizeMax(ticketSizeMax);
        investor.setTicketSizeMin(ticketSizeMin);
        investor.setInvestorTypeId(investorType);

        investor.clearIndustries();
        investor.clearCountries();
        investor.clearInvestmentPhases();
        investor.clearSupport();

        for(Long id : industries) {
            investor.addIndustry(id);
        }

        for(Long id : support) {
            investor.addSupport(id);
        }

        for(Long id : countries) {
            investor.addCountry(id);
        }

        for(Long id : investmentPhases) {
            investor.addInvestmentPhase(id);
        }

        saveObject(investor, "rgstr_investor");
    }

    /**
     * Save pitch
     * @param description
     * @param pitch
     * @throws IOException
     */
    public static void savePitch(String description, String pitch) throws IOException {
        account.setPitch(pitch);
        account.setDescription(description);

        saveObject(account, "rgstr_account");
    }

    /**
     * Save company information to internal storage
     * @param breakevenYear
     * @param fte
     * @param companyName
     * @param companyUid
     * @param revenue
     * @param markets
     * @param foundingYear
     */
    public static void saveCompanyInformation(int breakevenYear, int fte, String companyName,
                                              String companyUid, String revenue,
                                              ArrayList<Long> markets, int foundingYear) throws IOException{
        startup.setBreakevenYear(breakevenYear);
        startup.setNumberOfFte(fte);
        startup.setName(companyName);
        startup.setRevenue(revenue);
        startup.setFoundingYear(foundingYear);

        startup.clearCurrentMarkets();

        for(Long id : markets) {
            startup.addMarket(id);
        }

        saveObject(startup, "rgstr_startup");
    }

    /**
     * Save startup matching information
     * @param ticketSizeMin
     * @param ticketSizeMax
     * @param investorTypes
     * @param investmentPhases
     * @param industries
     * @param support
     */
    public static void saveStartupMatchingFragment(float ticketSizeMin, float ticketSizeMax,
                                                   ArrayList<Long> investorTypes,
                                                   ArrayList<Long> investmentPhases,
                                                   ArrayList<Long> industries, ArrayList<Long> support
                                                   ) throws IOException {
        startup.setTicketSizeMin(ticketSizeMin);
        startup.setTicketSizeMax(ticketSizeMax);

        startup.clearSupport();
        startup.clearInvestorTypes();
        startup.clearIndustries();
        startup.clearInvestmentPhases();

        for(Long id : industries) {
            startup.addIndustry(id);
        }

        for(Long id : investorTypes) {
            startup.addInvestorType(id);
        }

        for(Long id : investmentPhases) {
            startup.addInvestmentPhase(id);
        }

        for(Long id : support) {
            startup.addSupport(id);
        }

        saveObject(startup, "rgstr_startup");
    }

    /**
     * Save pitch information to startup
     * @param pitch
     * @param description
     * @param labels
     */
    public static void saveStartupPitch(String pitch, String description,
                                        ArrayList<Long> labels) throws IOException{
        startup.setDescription(description);
        startup.setPitch(pitch);

        startup.clearLabels();

        for(Long id : labels) {
            startup.addLabel(id);
        }

        saveObject(startup, "rgstr_startup");
    }

    /**
     *
     * @param type
     * @param valuation
     * @param closingTime
     * @param scope
     */
    public static void saveFinancialRequirements(long type, float valuation,
                                                 Date closingTime, float scope) throws IOException {
        startup.setClosingTime(closingTime);
        startup.setValuation(valuation);
        startup.setFinancialType(type);
        startup.setScope(scope);

        saveObject(startup, "rgstr_startup");
    }
}
