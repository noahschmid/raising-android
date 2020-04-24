package com.raising.app.models;

import java.util.ArrayList;

import lombok.Data;

@Data
public class PersonalSettings {
    private String language;
    private int numberOfMatches;

    ArrayList<NotificationSettings> notificationSettings;
}
