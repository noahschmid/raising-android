package com.raising.app.models;

import java.util.Arrays;
import java.util.List;

public enum NotificationType {
    MATCHLIST,
    REQUEST,
    LEAD,
    CONNECTION;

    public static List<NotificationType> getAll(){
        return Arrays.asList(MATCHLIST,REQUEST,LEAD,CONNECTION);
    }
}
