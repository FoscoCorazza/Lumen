package com.corazza.fosco.lumenGame.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.adapters.SchemeStickyListAdapter;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.schemes.SchemeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class DebugMenuActivity extends Activity {

    StickyListHeadersListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        bind();
        populate();
    }

    private void bind() {
        listView = (StickyListHeadersListView) findViewById(R.id.schemeInfoStickyListView);
    }

    long ms = System.currentTimeMillis();
    private void logMs(String n) {
        Log.e("Time " + n +": ", String.valueOf(System.currentTimeMillis() - ms));
        ms = System.currentTimeMillis();
    }
    private void populate() {


        logMs("0");
        Consts.loadProgresses(this);

        logMs("1");

        List<SchemeInfo> schemes = new ArrayList<>(Consts.schemeList.values());

        logMs("2");
        Collections.sort(schemes, new Comparator<SchemeInfo>() {
            @Override
            public int compare(SchemeInfo lhs, SchemeInfo rhs) {
                int sctrCompare = lhs.getSector() != null ? lhs.getSector().compareTo(rhs.getSector()) : (rhs.getSector() != null ? -1 : 0);
                int codeCompare = lhs.getCode() != null ? lhs.getCode().compareTo(rhs.getCode()) : (rhs.getCode() != null ? -1 : 0);

                return sctrCompare == 0 ? codeCompare : sctrCompare;
            }

            @Override
            public boolean equals(Object object) {
                return false;
            }
        });

        logMs("3");

        SchemeStickyListAdapter adapter = new SchemeStickyListAdapter(this, schemes);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SchemeInfo item = (SchemeInfo) parent.getItemAtPosition(position);
                MainActivity.play(DebugMenuActivity.this, item);
            }
        });

        logMs("4");

        scrollToLastLevelPlayed(adapter);

        logMs("5");
    }



    private void scrollToLastLevelPlayed(SchemeStickyListAdapter adapter) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String schemeToLoad = extras.getString(getString(R.string.schemeToLoad));
            final int pos = adapter.getPositionFromCode(schemeToLoad);
            listView.post(new Runnable() {
                @Override
                public void run() {
                    listView.smoothScrollToPosition(pos);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
