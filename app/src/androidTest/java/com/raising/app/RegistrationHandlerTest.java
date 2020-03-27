package com.raising.app;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.raising.app.models.Account;
import com.raising.app.models.PrivateProfile;
import com.raising.app.util.RegistrationHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class RegistrationHandlerTest {
    private Context context;
    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RegistrationHandler.setContext(context);
    }

    @Test
    public void testSaveAndLoadAccount() {
        Account account = new Account();
        account.setFirstName("Jon");
        account.setLastName("Doe");
        account.setEmail("email@email.ch");
        account.setPassword("testpassword");
        try {
            RegistrationHandler.begin();
            RegistrationHandler.saveLoginInformation("Jon", "Doe", "email@email.ch", "testpassword");
        } catch (IOException e) {
            fail();
        }
        Account loaded = RegistrationHandler.loadAccount();

        assertEquals(account.getFirstName(), loaded.getFirstName());
        assertEquals(account.getLastName(), loaded.getLastName());
        assertEquals(account.getEmail(), loaded.getEmail());
        assertEquals(account.getPassword(), loaded.getPassword());
    }

    @Test
    public void testSaveAndLoadProfile() {
        PrivateProfile profile = new PrivateProfile();
        profile.setPhone("0123456789");
        profile.setCompany("company");
        profile.setWebsite("website");
        profile.setZipCode("zipCode");

        try {
            RegistrationHandler.begin();
            RegistrationHandler.saveProfileInformation("company", "0123456789", "website", "country");
        } catch (IOException e) {
            fail();
        }
        PrivateProfile loaded = RegistrationHandler.loadPrivateProfile();

        assertEquals(profile.getCompany(), loaded.getCompany());
        assertEquals(profile.getCountry(), loaded.getCountry());
        assertEquals(profile.getPhone(), loaded.getPhone());
        assertEquals(profile.getWebsite(), loaded.getWebsite());
    }
}
