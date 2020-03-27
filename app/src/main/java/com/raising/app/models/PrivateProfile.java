package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class PrivateProfile implements Serializable {
    private String company;
    private String website;
    private String country;
    private String street;
    private String zipCode;
    private String city;
    private String phone;
}
