package com.raising.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.raising.app.models.Account;
import com.raising.app.models.ViewState;
import com.raising.app.util.InternalStorageHandler;
import com.raising.app.util.RegistrationHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test saving and reading from internal storage
 */

@RunWith(AndroidJUnit4.class)
public class InternalStorageHandlerTest {
    private Context context;
    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        InternalStorageHandler.setContext(context);
    }

    @Test
    public void testSaveAndLoadString() {
        String original = "Hello World!";
        String loaded = null;

        try {
            InternalStorageHandler.saveString(original, "string");
            loaded = InternalStorageHandler.loadStrings("string").get(0);
        } catch (IOException e) {
            fail();
        }
        assertEquals(original, loaded);
    }

    @Test
    public void testSaveAndLoadObject() {
        ViewState original = ViewState.RESULT;
        ViewState loaded = null;

        try {
            InternalStorageHandler.saveObject(original, "object");
            loaded = (ViewState)InternalStorageHandler.loadObject("object");
        } catch (Exception e) {
            fail();
        }
        assertEquals(original, loaded);
    }

    @Test
    public void testSaveAndLoadBitmap() {
        Bitmap original = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_placeholder_24dp);
        Bitmap loaded = null;

        try {
            InternalStorageHandler.saveBitmap(original, "bitmap");
            loaded = InternalStorageHandler.loadBitmap("bitmap");
        } catch (Exception e) {
            fail();
        }
        assertEquals(original, loaded);
    }

    @Test
    public void testDeleteFile() {
        ViewState dummy = ViewState.RESULT;

        try {
            InternalStorageHandler.saveObject(dummy, "object");
            assertEquals(InternalStorageHandler.exists("object"), true);
            InternalStorageHandler.deleteFile("object");
            assertEquals(InternalStorageHandler.exists("object"), false);
        } catch (Exception e) {
            fail();
        }

    }
}
