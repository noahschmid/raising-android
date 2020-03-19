package com.raising.app.util;

import com.raising.app.models.Account;

public class RegistrationHandler {
    private static final String registrationEndpoint = "https://33383.hostserv.eu:8081/account/register";
    private static final String freeEmailEndpoint = "https://33383.hostserv.eu:8081/account/valid";

    private static String accountType;
    private static Account account;

    public static void setAccountType(String type) { accountType = type; }
    public static String getAccountType() { return accountType; }
    public static boolean isStartup() { return accountType.equalsIgnoreCase("startup"); }

    public static void submit() {

    }
}
