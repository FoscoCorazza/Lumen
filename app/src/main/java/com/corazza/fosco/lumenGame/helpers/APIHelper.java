package com.corazza.fosco.lumenGame.helpers;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.corazza.fosco.lumenGame.helpers.restTasks.SendMailTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by Simone Chelo on 29/10/2016.
 */

public class APIHelper {

    public static void SendDesign(String guy, String design){
        SendMailTask task = new SendMailTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, guy, design);
    }

    public static String GetRestString(String name, String ... args) {
        String r = "http://dislike.netsons.org/rest/lumen/" + name + ".php?";
        try {
            return r + addParams(args);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private static String addParams(String ... args) throws UnsupportedEncodingException {
        String s = "";
        int i = 0;
        while(i < args.length){
            String parameter = args[i];
            if(i+1 < args.length){
                String value = args[i+1];
                value = URLEncoder.encode(value, "UTF-8");
                s += parameter + "=" + value + "&";
            }
            i+=2;
        }
        return s;
    }

    public static String GetSignedRestString(String name, String ... args){
        String r = GetRestString(name, "usr", "qkobrgfw_fosco", "pwd", "Subs0n1ca!");
        try {
            return r + addParams(args);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
