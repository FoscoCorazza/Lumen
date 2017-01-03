package com.corazza.fosco.lumenGame.activities;

import android.os.Bundle;

import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.MenuSchemeLayout;

public class MenuActivity extends  SchemeLayoutActivity<MenuSchemeLayout> {

    @Override
    protected MenuSchemeLayout initSchemeLayout(Bundle savedInstanceState) {
        return Consts.getMenuSchemeLayout("000", new MenuSchemeLayout(this));
    }

    @Override
    protected void initBeforeLayout() {
        Consts.loadProgresses(this);
    }


}
