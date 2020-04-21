package com.raising.app.models;

import lombok.Data;

@Data
public class PersonalSettings {
    private String language;
    private int numberOfMatches;

    private boolean generalNotifications = true;
    private boolean matchlistNotifications = false;
    private boolean contactNotifications = false;
    private boolean connectionNotifications = false;
}
