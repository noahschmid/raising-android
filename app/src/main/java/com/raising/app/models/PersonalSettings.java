package com.raising.app.models;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class PersonalSettings implements Serializable {
    private int numberOfMatches;

    ArrayList<NotificationSettings> notificationSettings;
}
