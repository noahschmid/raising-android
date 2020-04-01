package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class PrivateProfile implements Serializable {
    private boolean isStartup;
    private int countryId = -1;
    private String phone;
    private String email;
    private String website;
}