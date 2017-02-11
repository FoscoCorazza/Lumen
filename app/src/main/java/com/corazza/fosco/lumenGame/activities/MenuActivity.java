package com.corazza.fosco.lumenGame.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.SoundsHelper;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.MenuSchemeLayout;

public class MenuActivity extends  SchemeLayoutActivity<MenuSchemeLayout> {

    @Override
    protected MenuSchemeLayout initSchemeLayout(Bundle savedInstanceState) {
        return Consts.getMenuSchemeLayout("000", new MenuSchemeLayout(this));
    }

    @Override
    protected void initBeforeLayout() {
        Consts.loadProgresses(this);
        SoundsHelper.getInstance().play_title_theme(this);
    }

}
