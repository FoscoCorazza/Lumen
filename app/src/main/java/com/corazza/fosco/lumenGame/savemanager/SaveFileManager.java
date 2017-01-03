package com.corazza.fosco.lumenGame.savemanager;

import android.content.Context;

import com.corazza.fosco.lumenGame.helpers.Consts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Simone on 01/06/2016.
 */
public class SaveFileManager {


    private static final String filename = "save.fos";

    public static void createFile(Context context){
        new File(context.getFilesDir(), filename);
    }

    public static void writeScheme(Context context, SchemeResult result){
        File file = new File(context.getFilesDir(), filename);

        try {
            FileWriter writer = new FileWriter(file, true);
            writer.append("\n").append(result.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Consts.updateSchemeList(result);
    }

    public static void clear(Context context){
        File file = new File(context.getFilesDir(), filename);

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.write("");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, SchemeResult> readFile(Context context){
        File file = new File(context.getFilesDir(), filename);
        HashMap<String, SchemeResult> collection = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            SchemeResult result;

            while ((line = br.readLine()) != null) {
                result = SchemeResult.fromString(line);
                if(result != null) collection.put(result.getCode(), result);
            }
            br.close();
        }
        catch (IOException ignored) {}

        return collection;
    }

}
