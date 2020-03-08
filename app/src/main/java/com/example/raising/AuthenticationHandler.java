package com.example.raising;

public class AuthenticationHandler {
    private static String token;

    public static void saveToken(String token) {
        AuthenticationHandler.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static void deleteToken() {
        AuthenticationHandler.token = null;
    }

    public boolean isLoggedIn() {
        return (token != null);
    }
}
