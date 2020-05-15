package com.raising.app.models;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class Match implements Serializable {
    private long id;
    private boolean isStartup;
    private String companyName;
    private String firstName;
    private String lastName;
    private String description;
    private int matchingPercent;
    private long profilePictureId;
    private long accountId;
    private long investmentPhaseId;
    private long investorTypeId;
    private Timestamp accountLastChanged;
}
