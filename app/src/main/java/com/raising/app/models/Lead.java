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
    private long profilePictureId;
    private long accountId;
    private long investmentPhaseId;
    private long investorTypeId;
    private long statusId;
    private Date date;

    private LeadState state;
    private InteractionState interactionState;
}
