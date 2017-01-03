package com.corazza.fosco.lumenGame.helpers;

import android.content.Context;

public class MessagesHelper {

    private static boolean messageOnGoing = false;
    public static void notifyLumenWillSplit(Context context){}
    public static void notifyLumenJustCantStart(Context context){}
    public static void notifyTooManyLumens(Context context){
        //if(!messageOnGoing){
        //    messageOnGoing = true;
            SoundsHelper.getInstance().play_too_many_lumens(context);
            /*Dialog dialog = new Dialog(context);
            dialog.setTitle("No no no, I think something went wrong");
            dialog.show();*/
        //}
    }
}
