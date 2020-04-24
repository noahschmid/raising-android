package com.raising.app.models;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
public class ContactData implements Serializable {
    private String email;
    private String phone;
    private long accountId = -1;
    private long businessPlanId = -1;

    public ContactData() {}
}