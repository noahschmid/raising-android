package com.raising.app.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.raising.app.R;
import com.raising.app.models.Image;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class InternalStorageHandler {
    private static Context context;
    private static AppCompatActivity activity;

    public static void setContext(Context context) { InternalStorageHandler.context = context; }
    public static void setActivity(AppCompatActivity activity) { InternalStorageHandler.activity = activity; }

    public static Context getContext() { return context; }
    public static AppCompatActivity getActivity() { return activity; }

    /**
     * Load object from internal storage
     * @param filename the file to load from
     * @return Object loaded from internal storage
     * @throws Exception
     */
    public static Object loadObject(String filename) throws Exception {
        FileInputStream fis = context.openFileInput(filename);
        ObjectInputStream ois = new ObjectInputStream (fis);
        Object obj = ois.readObject();
        ois.close();
        fis.close();
        return obj;
    }

    /**
     * Save object to internal storage
     * @param object
     * @param filename
     * @throws IOException
     */
    public static void saveObject(Object object, String filename) throws IOException {
        FileOutputStream outputStream = context
                .openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    /**
     * Check whether file with given filename exists in internal storage
     * @param filename the name of the file
     * @return true if file exists, false otherwise
     */
    public static boolean exists(String filename) {
        File file = context.getFileStreamPath(filename);
        return file.exists();
    }

    /**
     * Save string to internal storage
     * @param string
     * @param filename
     * @throws IOException
     */
    public static void saveString(String string, String filename) throws IOException {
        FileOutputStream outputStream;
        outputStream = context
                .openFileOutput(filename, Context.MODE_PRIVATE);
        outputStream.write(string.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    /**
     * Read file line by line and return array list of strings
     * @param filename
     * @return
     * @throws IOException
     */
    public static ArrayList<String> loadStrings(String filename) throws IOException {
        FileInputStream fis = context
                .openFileInput(filename);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        ArrayList<String> result = new ArrayList<>();
        String line;
        while((line = bufferedReader.readLine()) != null){
            result.add(line);
        }
        isr.close();
        fis.close();
        return result;
    }

    public static void deleteFile(String filename) {
        if(!exists(filename))
            return;

        context.deleteFile(filename);
    }

    /**
     * Save a bitmap to internal storage
     * @param bitmap bitmap that should be saved
     * @param filename name of the file to be saved
     * @return path to the saved file
     */
    public static String saveBitmap(Bitmap bitmap, String filename){
        ContextWrapper cw = new ContextWrapper(InternalStorageHandler.getContext());
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File mypath = new File(directory,filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    /**
     * Load a bitmap from the internal storage
     * @param filename name to the file
     * @return loaded bitmap or null
     */
    public static Bitmap loadBitmap(String filename) {
        try {
            File f=new File("images", filename);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}
