package com.corazza.fosco.lumenGame.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.StringBuilderPrinter;

import com.corazza.fosco.lumenGame.MainThread;
import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.savemanager.SaveFileManager;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.TitleSchemeLayout;

public class TitleActivity extends SchemeLayoutActivity<TitleSchemeLayout> {

    @Override
    protected TitleSchemeLayout initSchemeLayout(Bundle savedInstanceState) {
        if(Consts.DEMO){
            String stl = getSchemeToLoad(savedInstanceState);
            if(stl != null && !"001".equals(stl)){
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.demo), Context.MODE_PRIVATE);
                long timeElapsed = sharedPref.getLong(getString(R.string.time_begin), -1);
                if(timeElapsed != -1) {
                    timeElapsed = (System.currentTimeMillis() - timeElapsed) / 1000;
                    long sec = timeElapsed % 60;
                    long min = timeElapsed / 60;
                    String secS = String.valueOf(sec);
                    String minS = String.valueOf(min);

                    String time = (min > 0 ? (minS + (min == 1 ? " minute and " : " minutes and ")) : "") +
                            secS + (sec == 1 ? " second" : " seconds");
                    AlertDialog alertDialog = new AlertDialog.Builder(TitleActivity.this).create();
                    alertDialog.setTitle("The demo is over!");
                    alertDialog.setMessage("It took " + time + " to complete. Thank you a lot for your feedback!");
                    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "You are welcome =)", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        }
        return (TitleSchemeLayout) Consts.getSchemeLayout("TITLE", new TitleSchemeLayout(this));
    }

    @Override
    protected void initBeforeLayout() {
        SaveFileManager.createFile(this);
        Consts.loadConsts(this);
        Consts.loadProgresses(this);
    }

}
