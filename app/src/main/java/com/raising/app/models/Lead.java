package com.raising.app.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class Lead implements Serializable {
    private long id;
    private boolean isStartup;
    private String companyName;
    private String firstName;
    private String lastName;
    private int matchingPercent;
    private Image profileImage;
    private long accountId;
    private long investmentPhaseId;
    private long investorTypeId;
    private long statusId;
    private Date date;

    private LeadState state;
    private HandshakeState handshakeState;

    public Lead (long id, boolean isStartup, String companyName, String firstName, String lastName,
                 int matchingPercent, Image profileImage, long accountId, long investmentPhaseId,
                 long investorTypeId, long statusId, LeadState state, Date date, HandshakeState handshakeState) {
        this.id = id;
        this.isStartup = isStartup;
        this.companyName = companyName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.matchingPercent = matchingPercent;
        this.profileImage = profileImage;
        this.accountId = accountId;
        this.investmentPhaseId = investmentPhaseId;
        this.investorTypeId = investorTypeId;
        this.statusId = statusId;
        this.state = state;
        this.date = date;
        this.handshakeState = handshakeState;
    }

    public Bitmap getBitmap() {
        return profileImage.getBitmap();
    }
}
