package com.corazza.fosco.lumenGame.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.corazza.fosco.lumenGame.MainThread;
import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.helpers.SoundsHelper;
import com.corazza.fosco.lumenGame.savemanager.SaveFileManager;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.TitleSchemeLayout;

public abstract class SchemeLayoutActivity<T extends SchemeLayout> extends Activity {

    T mSchemeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initBeforeLayout();
        mSchemeLayout = initSchemeLayout(savedInstanceState);

        FrameLayout game = new FrameLayout(this);
        initAfterLayout(game, mSchemeLayout);

        setContentView(game);
        setFullscreen(game);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    protected void initAfterLayout(FrameLayout frameLayout, T schemeLayout) {
        frameLayout.addView(schemeLayout);
    }

    private void setFullscreen(View mContentView) {

        int uiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        mContentView.setSystemUiVisibility(uiVisibility);
    }

    protected abstract T initSchemeLayout(Bundle savedInstanceState);
    protected abstract void initBeforeLayout();

    @Override
    protected void onResume() {
        super.onResume();
        SoundsHelper.getInstance().resumeTheme();
        MainThread.setRunning(true);
        mSchemeLayout.setTimeElapsed(0);
    }

    @Override
    protected void onPause() {
        if(!mSchemeLayout.isLeavingViaIntent()) SoundsHelper.getInstance().pauseTheme();
        MainThread.setRunning(false);
        super.onPause();
    }

    protected String getSchemeToLoad(Bundle savedInstanceState) {
        String schemeToLoad;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                schemeToLoad = "001";
            } else {
                schemeToLoad = extras.getString(getString(R.string.schemeToLoad));
            }
        } else {
            schemeToLoad = (String) savedInstanceState.getSerializable(getString(R.string.schemeToLoad));
        }
        return schemeToLoad;
    }

}
