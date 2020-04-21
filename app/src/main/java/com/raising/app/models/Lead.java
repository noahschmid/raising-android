package com.raising.app.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import lombok.Data;

@Data
public class Lead implements Serializable {
    private long id;
    private boolean isStartup;
    private String title;
    private String companyName;
    private String firstName;
    private String lastName;
    private String attribute;
    private int matchingPercent;
    private long profilePictureId;
    private long accountId;
    private long investmentPhaseId;
    private long investorTypeId;
    private Date timestamp;
    private long contactDataId;

    private LeadState state;
    private InteractionState handshakeState;

    private ArrayList<Interaction> interactions;

    private Interaction coffee;
    private Interaction businessplan;
    private Interaction phone;
    private Interaction email;
    private Interaction video;

    public String getHandshakePercentString() {
        return matchingPercent + " %";
    }
}
