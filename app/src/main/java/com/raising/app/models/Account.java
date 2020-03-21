package com.raising.app.models;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class Account implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String description;
    private String pitch;
}
