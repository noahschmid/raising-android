package com.raising.app.models;

import android.graphics.Bitmap;

import java.util.ArrayList;

import lombok.Data;

@Data
public class HandshakeContact {
    private long id;
    private String name;
    private String emailAddress;
    private int phoneNumber;
    private Image image;

    private InteractionState handshakeState;

    private ArrayList<Interaction> interactions;

    private Interaction coffee;
    private Interaction businessplan;
    private Interaction phone;
    private Interaction email;
    private Interaction video;
}

