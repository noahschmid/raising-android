package com.raising.app;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.raising.app.models.Account;
import com.raising.app.models.ContactData;
import com.raising.app.util.InternalStorageHandler;
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
        InternalStorageHandler.setContext(context);
        RegistrationHandler.init();
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
        ContactData profile = new ContactData();
        profile.setPhone("0123456789");
        profile.setEmail("email");
        profile.setBusinessPlanId(15);
        profile.setAccountId(22);

        try {
            RegistrationHandler.begin();
            RegistrationHandler.saveContactData(profile);
        } catch (IOException e) {
            fail();
        }
        ContactData loaded = RegistrationHandler.loadContactData();

        assertEquals(profile.getAccountId(), loaded.getAccountId());
        assertEquals(profile.getEmail(), loaded.getEmail());
        assertEquals(profile.getPhone(), loaded.getPhone());
        assertEquals(profile.getBusinessPlanId(), loaded.getBusinessPlanId());
    }
}
