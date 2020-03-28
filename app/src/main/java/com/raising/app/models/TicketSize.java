package com.raising.app.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class TicketSize implements Serializable {
    private long id;
    private int ticketSize;
}
