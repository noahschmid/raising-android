package com.raising.app.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Account implements Serializable {
    protected long id = -1L;
    protected String companyName;
    protected String name;
    private String firstName;
    private String lastName;
    private String password;
    private String roles = "ROLE_USER";
    private String email;
    private String pitch;
    private String description;
    private int ticketMinId = -1;
    private int ticketMaxId = -1;
    private Image profilePicture;
    private long profilePictureId = -1;
    private List<Long> galleryIds = new ArrayList<>();
    private List<Image> gallery = new ArrayList<>();
    private long countryId = -1l;
    private String phone;
    private Timestamp lastChanged;

    private List<Long> countries = new ArrayList<>();
    private List<Long> continents = new ArrayList<>();
    private List<Long> support = new ArrayList<>();
    private List<Long> industries = new ArrayList<>();

    public void clearSupport() { support.clear(); }
    public void clearIndustries() { industries.clear(); }
    public void clearCountries() { countries.clear(); }
    public void clearContinents() { continents.clear(); }

    public void addCountry(Long country) {
        countries.add(country);
    }
    public void addContinent(Long continent) {
        continents.add(continent);
    }
    public void addIndustry(Long industry) {
        industries.add(industry);
    }
    public void addSupport(Long spprt) {
        support.add(spprt);
    }
    public void addToGallery(Image image) { gallery.add(image); }

    public Bitmap getImage() {
        return null;
    }
}
