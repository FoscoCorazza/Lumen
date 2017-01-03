package com.corazza.fosco.lumenGame.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import com.corazza.fosco.lumenGame.MainThread;
import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.schemes.SchemeInfo;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.SoundsHelper;

public class MainActivity extends SchemeLayoutActivity<SchemeLayout> {

    SchemeLayout mSchemeLayout;

    @Override
    protected SchemeLayout initSchemeLayout(Bundle savedInstanceState) {
        String schemeToLoad = getSchemeToLoad(savedInstanceState);
        if(Consts.DEMO){
            if("000".equals(schemeToLoad)){
                SharedPreferences p = getSharedPreferences(getString(R.string.demo), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = p.edit();
                editor.putLong(getString(R.string.time_begin), System.currentTimeMillis());
                editor.apply();
            }
        }
        mSchemeLayout =  Consts.getSchemeLayout(schemeToLoad, new SchemeLayout(this));
        return Consts.getSchemeLayout(schemeToLoad, new SchemeLayout(this));
    }

    @Override
    protected void initBeforeLayout() {
        Consts.loadConsts(this);
        if(!SoundsHelper.getInstance().isPlaying()) {
            //SoundsHelper.getInstance().play_theme(this);
        }
    }

    @Override
    public void onBackPressed() {
        mSchemeLayout.toMenu();
    }

    public static void play(Activity ctx, SchemeInfo schemeInfo) {
        if(schemeInfo != null) play(ctx, schemeInfo.getCode());
    }

    public static void play(Activity ctx, String code) {
        if(code != null)
        {
            MainThread.setRunning(false);
            Intent i = new Intent(ctx, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra(ctx.getString(R.string.schemeToLoad), code);
            ctx.startActivity(i);
            ctx.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            ctx.finish();
        }
    }
}
