package com.raising.app.models;

import lombok.Data;

@Data
public class ContactData {
    private String email;
    private String phone;
    private long leadAccountId;
    private long businessPlanId = -1;
}