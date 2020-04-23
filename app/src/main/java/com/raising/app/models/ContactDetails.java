package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class ContactDetails implements Serializable {
    private String phone;
    private String email;
}
