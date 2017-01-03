package com.corazza.fosco.lumenGame.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.corazza.fosco.lumenGame.MainThread;
import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeCreatorLayout;

/**
 * Created by Simone Chelo on 10/10/2016.
 */

public class SchemeCreatorActivity extends SchemeLayoutActivity<SchemeCreatorLayout> {

    @Override
    protected SchemeCreatorLayout initSchemeLayout(Bundle savedInstanceState) {
        String schemeToLoad = getSchemeToLoad(savedInstanceState);
        return (SchemeCreatorLayout) Consts.getSchemeLayout(schemeToLoad, new SchemeCreatorLayout(this));
    }

    @Override
    protected void initBeforeLayout() {

    }

    public static void play(Activity ctx, String code) {
        if(code != null)
        {
            MainThread.setRunning(false);
            Intent i = new Intent(ctx, SchemeCreatorActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra(ctx.getString(R.string.schemeToLoad), code);
            ctx.startActivity(i);
            ctx.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            ctx.finish();
        }
    }
}
