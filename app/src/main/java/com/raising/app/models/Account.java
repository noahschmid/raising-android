package com.raising.app.models;

import lombok.Data;

@Data
public class Account {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int investmentMin;
    private int investmentMax;
    private long investorTypeId;
}
