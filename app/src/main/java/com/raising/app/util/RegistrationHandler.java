package com.raising.app.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raising.app.models.Account;
import com.raising.app.models.Continent;
import com.raising.app.models.Country;
import com.raising.app.models.Image;
import com.raising.app.models.Industry;
import com.raising.app.models.InvestmentPhase;
import com.raising.app.models.Investor;
import com.raising.app.models.InvestorType;
import com.raising.app.models.Label;
import com.raising.app.models.PrivateProfile;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
    private static PrivateProfile privateProfile = new PrivateProfile();

    public static void setContext(Context ctx) {
        context = ctx;
    }

    public static boolean shouldCancel() {
        return cancelAllowed;
    }

    public static void setCancelAllowed(boolean allowed) { cancelAllowed = allowed; }

    public static void setAccountType(String type) throws IOException {
        accountType = type;
        if(isStartup()) {
            startup.setFirstName(account.getFirstName());
            startup.setLastName(account.getLastName());
            startup.setName(account.getName());
            startup.setEmail(account.getEmail());
            startup.setPassword(account.getPassword());
            privateProfile.setStartup(true);
            InternalStorageHandler.saveObject(privateProfile, "rgstr_startup");
        }
        else{
            investor.setFirstName(account.getFirstName());
            investor.setLastName(account.getLastName());
            investor.setName(account.getName());
            investor.setEmail(account.getEmail());
            investor.setPassword(account.getPassword());
            privateProfile.setStartup(false);
            InternalStorageHandler.saveObject(privateProfile, "rgstr_investor");
        }
        saveRegistrationState();
        InternalStorageHandler.saveObject(privateProfile, "rgstr_profile");
    }
    public static String getAccountType() { return accountType; }
    public static boolean isStartup() { return privateProfile.isStartup(); }

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
        privateProfile.setEmail(email);

        InternalStorageHandler.saveObject(account, "rgstr_account");
        InternalStorageHandler.saveObject(privateProfile, "rgstr_profile");
    }

    /**
     * Set images
     * @param profilePicture
     * @param gallery
     */
    public static void setImages(Image profilePicture, List<Image> gallery) throws IOException {
        if(isStartup()) {
            startup.setProfilePicture(profilePicture);
            if(!gallery.isEmpty()) {
                startup.setGallery(gallery);
            }
            InternalStorageHandler.saveObject(startup, "rgstr_startup");
        } else {
            investor.setProfilePicture(profilePicture);
            if(!gallery.isEmpty()) {
                investor.setGallery(gallery);
            }
            InternalStorageHandler.saveObject(investor, "rgstr_investor");
        }
    }

    /**
     * Save the private profile information which doesn't get stored in the database
     * @param company
     * @param phone
     * @param website
     * @param countryId
     */
    public static void saveProfileInformation(String company, String phone, String website,
                                              int countryId) throws IOException {
        investor.setCompany(company);
        privateProfile.setPhone(phone);
        privateProfile.setWebsite(website);
        privateProfile.setCountryId(countryId);

        InternalStorageHandler.saveObject(privateProfile, "rgstr_profile");
        InternalStorageHandler.saveObject(investor, "rgstr_investor");
    }

    public static Account getAccount() { return account; }
    public static Investor getInvestor() { return investor; }
    public static Startup getStartup() { return startup; }
    public static PrivateProfile getPrivateProfile() { return privateProfile; }

    /**
     * Cancel current registration process
     */
    public static void cancel() {
        context.deleteFile("rgstr_profile");
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
            privateProfile = loadPrivateProfile();
            investor = loadInvestor();
            startup = loadStartup();
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
        accountType = "undefined";
        inProgress = true;
        account = new Account();
        privateProfile = new PrivateProfile();

        String registrationInfo = accountType;
        saveString(registrationInfo, "rgstr");
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

    /**
     * Save current page and account type
     * @throws IOException
     */
    private static void saveRegistrationState() throws IOException {
        String registrationInfo = accountType;
        saveString(registrationInfo, "rgstr");
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
            Log.d("debugMessage", "error loading investor");
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
            Log.d("debugMessage", "error loading startup");
            return new Startup();
        }
    }

    /**
     * Load saved private profile
     * @return
     */
    public static PrivateProfile loadPrivateProfile() {
        try {
            return (PrivateProfile) InternalStorageHandler.loadObject("rgstr_profile");
        } catch (Exception e) {
            return new PrivateProfile();
        }
    }

    /**
     * Save matching information for investor
     * @param ticketSizeMinId
     * @param ticketSizeMaxId
     * @param investorType
     * @param investmentPhases
     * @param industries
     * @param support
     * @param countries
     * @throws IOException
     */
    public static void saveInvestorMatchingFragment(int ticketSizeMinId, int ticketSizeMaxId,
                                                    long investorType, ArrayList<Long> investmentPhases,
                                                    ArrayList<Long> industries, ArrayList<Long> support,
                                                    ArrayList<Country> countries,
                                                    ArrayList<Continent> continents) throws IOException {
        investor.setTicketMaxId(ticketSizeMaxId);
        investor.setTicketMinId(ticketSizeMinId);
        investor.setInvestorTypeId(investorType);

        investor.clearIndustries();
        investor.clearCountries();
        investor.clearInvestmentPhases();
        investor.clearSupport();

        for(Long id : industries) {
            investor.addIndustry(new Industry(id));
        }

        for(Long id : support) {
            investor.addSupport(new Support(id));
        }

        investor.setCountries(countries);
        investor.setContinents(continents);

        for(Long id : investmentPhases) {
            investor.addInvestmentPhase(new InvestmentPhase(id));
        }

        InternalStorageHandler.saveObject(investor, "rgstr_investor");
    }

    /**
     * Save pitch
     * @param description
     * @param pitch
     * @throws IOException
     */
    public static void savePitch(String description, String pitch) throws IOException {
        investor.setPitch(pitch);
        investor.setDescription(description);

        InternalStorageHandler.saveObject(investor, "rgstr_investor");
    }

    /**
     * Save company information to internal storage
     * @param breakevenYear
     * @param fte
     * @param companyName
     * @param companyUid
     * @param revenueMinId
     * @param revenueMaxId
     * @param foundingYear
     */
    public static void saveCompanyInformation(int breakevenYear, int fte, String companyName,
                                              String companyUid, int revenueMinId, int revenueMaxId,
                                              int foundingYear,
                                              ArrayList<Country> countries,
                                              ArrayList<Continent> continents,
                                              Country country, String phone, String website) throws IOException{
        startup.setBreakEvenYear(breakevenYear);
        startup.setNumberOfFte(fte);
        startup.setCompany(companyName);
        startup.setUId(companyUid);
        startup.setRevenueMinId(revenueMinId);
        startup.setRevenueMaxId(revenueMaxId);
        startup.setFoundingYear(foundingYear);
        startup.setCountries(countries);
        startup.setContinents(continents);
        startup.setCountryId((int)country.getId());
        startup.setWebsite(website);
        privateProfile.setPhone(phone);

        InternalStorageHandler.saveObject(startup, "rgstr_startup");
        InternalStorageHandler.saveObject(privateProfile, "rgstr_profile");
    }

    /**
     * Save startup matching information
     * @param ticketSizeMinId
     * @param ticketSizeMaxId
     * @param investorTypes
     * @param investmentPhases
     * @param industries
     * @param support
     */
    public static void saveStartupMatchingFragment(int ticketSizeMinId, int ticketSizeMaxId,
                                                   ArrayList<Long> investorTypes,
                                                   ArrayList<Long> investmentPhases,
                                                   ArrayList<Long> industries, ArrayList<Long> support
                                                   ) throws IOException {
        startup.setTicketMinId(ticketSizeMinId);
        startup.setTicketMaxId(ticketSizeMaxId);
        startup.clearSupport();
        startup.clearInvestorTypes();
        startup.clearIndustries();

        for(Long id : industries) {
            startup.addIndustry(new Industry(id));
        }

        for(Long id : support) {
            startup.addSupport(new Support(id));
        }

        for(Long id : investorTypes) {
            startup.addInvestorType(new InvestorType(id));
        }

        startup.setInvestmentPhaseId(investmentPhases.get(0));
        InternalStorageHandler.saveObject(startup, "rgstr_startup");
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
            startup.addLabel(new Label(id));
        }

        InternalStorageHandler.saveObject(startup, "rgstr_startup");
    }

    /**
     *
     * @param type
     * @param valuation
     * @param closingTime
     * @param scope
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void saveFinancialRequirements(long type, float valuation,
                                                 Calendar closingTime, float scope,
                                                 int raised) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd" );
        startup.setClosingTime(format.format(closingTime.getTime()));
        startup.setPreMoneyValuation((int)valuation);
        startup.setFinanceTypeId(type);
        startup.setScope((int)scope);
        startup.setRaised(raised);

        InternalStorageHandler.saveObject(startup, "rgstr_startup");
    }

    /**
     * Save stakeholder information for startup
     * @param shareholderList
     * @param boardMemberList
     * @param founderList
     * @throws IOException
     */
    public static void saveStakeholder(ArrayList<StakeholderItem> shareholderList,
                                       ArrayList<StakeholderItem> boardMemberList,
                                       ArrayList<StakeholderItem> founderList) throws IOException {
        boardMemberList.forEach(boardMember -> startup.addBoardMember((BoardMember)boardMember));
        founderList.forEach(founder -> startup.addFounder((Founder) founder));

        for(StakeholderItem shareholder : shareholderList) {
            if(((Shareholder)shareholder).isPrivateShareholder()) {
                startup.addPrivateShareholder((Shareholder)shareholder);
            } else {
                startup.addCorporateShareholder((Shareholder)shareholder);
            }
        }

        InternalStorageHandler.saveObject(startup, "rgstr_startup");
    }

}
