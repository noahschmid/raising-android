package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class ContactData implements Serializable {
    private String email;
    private String phone;
    private long leadAccountId;
    private long businessPlanId = -1;
}