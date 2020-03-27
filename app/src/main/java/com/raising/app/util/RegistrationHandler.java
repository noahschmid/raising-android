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
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        Gson gson  = new Gson();
        String tmp = gson.toJson(account);
        if(isStartup()) {
            startup = gson.fromJson(tmp, new TypeToken<Startup>(){}.getType());
        }
        else{
            investor = gson.fromJson(tmp, new TypeToken<Investor>(){}.getType());
        }
        saveRegistrationState();
    }
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
        account.setName(firstName + " " + lastName);
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setPassword(password);

        saveObject(account, "rgstr_account");
    }

    /**
     * Save the private profile information which doesn't get stored in the database
     * @param company
     * @param website
     * @param country
     */
    public static void saveProfileInformation(String company, String phone, String website,
                                              String country) throws IOException {
        privateProfile.setCompany(company);
        privateProfile.setPhone(phone);
        privateProfile.setWebsite(website);
        privateProfile.setCountry(country);

        saveObject(privateProfile, "rgstr_profile");
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

    /**
     * Save current page and account type
     * @throws IOException
     */
    private static void saveRegistrationState() throws IOException {
        String registrationInfo = accountType;
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

    /**
     * Save matching information for investor
     * @param ticketSizeMin
     * @param ticketSizeMax
     * @param investorType
     * @param investmentPhases
     * @param industries
     * @param support
     * @param countries
     * @throws IOException
     */
    public static void saveInvestorMatchingFragment(float ticketSizeMin, float ticketSizeMax,
                                                    long investorType, ArrayList<Long> investmentPhases,
                                                    ArrayList<Long> industries, ArrayList<Long> support,
                                                    ArrayList<Country> countries,
                                                    ArrayList<Continent> continents) throws IOException {
        investor.setInvestmentMax((int)ticketSizeMax);
        investor.setInvestmentMin((int)ticketSizeMin);
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
                                              String companyUid, int revenue,
                                              ArrayList<Long> markets, int foundingYear) throws IOException{
        startup.setBreakEvenYear(breakevenYear);
        startup.setNumberOfFte(fte);
        startup.setName(companyName);
        startup.setRevenueMin(revenue);
        startup.setRevenueMax(revenue);
        startup.setFoundingYear(foundingYear);

        startup.clearCountries();

        for(Long id : markets) {
            startup.addCountry(new Country(id));
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
        startup.setInvestmentMin((int)ticketSizeMin);
        startup.setInvestmentMax((int)ticketSizeMax);

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
            startup.addLabel(new Label(id));
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void saveFinancialRequirements(long type, float valuation,
                                                 Calendar closingTime, float scope) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd" );
        startup.setClosingTime(format.format(closingTime.getTime()));
        startup.setPreMoneyValuation((int)valuation);
        startup.setFinanceTypeId(type);
        startup.setScope((int)scope);

        saveObject(startup, "rgstr_startup");
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

        saveObject(startup, "rgstr_startup");
    }
}
