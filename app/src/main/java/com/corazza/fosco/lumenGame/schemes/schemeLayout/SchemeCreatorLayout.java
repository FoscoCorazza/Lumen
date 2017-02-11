package com.corazza.fosco.lumenGame.schemes.schemeLayout;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;

import com.corazza.fosco.lumenGame.MainThread;
import com.corazza.fosco.lumenGame.gameObjects.Background;
import com.corazza.fosco.lumenGame.gameObjects.Bulb;
import com.corazza.fosco.lumenGame.gameObjects.SegmentCreatorListener;
import com.corazza.fosco.lumenGame.gameObjects.Star;
import com.corazza.fosco.lumenGame.gameObjects.huds.Button;
import com.corazza.fosco.lumenGame.gameObjects.huds.CreatorHud;
import com.corazza.fosco.lumenGame.gameObjects.ResultView;
import com.corazza.fosco.lumenGame.gameObjects.huds.ResultHud;
import com.corazza.fosco.lumenGame.gameObjects.obstacles.Obstacle;
import com.corazza.fosco.lumenGame.gameObjects.obstacles.Obstructor;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.GridDot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.APIHelper;
import com.corazza.fosco.lumenGame.helpers.Phase;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * Created by Simone Chelo on 10/10/2016.
 */
public class SchemeCreatorLayout extends SchemeLayout {

    private Button.Action createMode = null;

    public SchemeCreatorLayout(Context activity) {
        super(activity);
    }

    protected void init() {
        super.init();
        getHolder().addCallback(this);
        phase = Phase.USER_PLAYING;
        thread = new MainThread(getHolder(), this);
        bckg = new Background();
        hud1 = new CreatorHud(this);
        hud2 = new ResultHud(this);
        ends = new ResultView(getContext());

        collect(hud1, bckg, obst, strs, lums, hud2, ends);

        setOnTouchListener(new SegmentCreatorListener(this));
    }


    @Override
    public void onButtonClick(Button button) {
        super.onButtonClick(button);
        Button.Action action = button.getAction();
        if(Button.isCreatorAction(action)){
            CreatorHud hud = (CreatorHud) hud1;
            if(action == Button.Action.NEED){
                incrementNeedOfAll();
            } else if(action == Button.Action.GRID_W){
                grid.incrementWidth(2);
                reset();
            } else if(action == Button.Action.GRID_H){
                grid.incrementHeight(2);
                reset();
            } else if(action == Button.Action.HIDE){
                button.setActivated(!button.isActivated());
                hud.hideAll(action, button.isActivated());
            } else if (button.isActivated()) {
                button.setActivated(false);
                createMode = null;
                setOnTouchListener(getSegmentCreator());
            } else {
                hud.activateOnly(button.getAction());
                createMode = button.getAction();
                setOnTouchListener(getCreativeListener());
            }
        }
    }

    private Obstructor onBuildLine;
    private OnTouchListener getCreativeListener() {
        return new View.OnTouchListener() {
            boolean fingerDown = false;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                boolean hud1Touched = hud1.onTouch(event);
                boolean hud2Touched = hud2.onTouch(event);
                if (phase != Phase.USER_PLAYING || !touchEnabled || hud1Touched || hud2Touched) return true;
                view.getParent().requestDisallowInterceptTouchEvent(true);
                PixelDot rawPoint = new PixelDot(event.getRawX(),  event.getRawY());
                GridDot normPoint = grid.nearest(rawPoint).gridDot();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fingerDown = true;
                        if(createMode == Button.Action.OBST){
                            //Creo il punto Gamma del Segmento
                            onBuildLine = new Obstructor(normPoint, normPoint);
                            obst.add(onBuildLine);

                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if(createMode == Button.Action.OBST){
                            if(onBuildLine != null) {
                                onBuildLine.theta = normPoint.pixelDot();
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:

                        if(createMode == Button.Action.OBST){
                            if(onBuildLine != null) {
                                onBuildLine.theta = normPoint.pixelDot();
                                reset();
                            }
                        }

                        if(fingerDown){
                            fingerDown = false;
                            switch (createMode){
                                case LUMEN:
                                    moveElement(getMainLumen(), normPoint);
                                    spnt = normPoint;
                                    break;
                                case BULB:
                                    Bulb b = blbs.elementIn(normPoint);
                                    if(b == null){
                                        addBulb(normPoint);
                                    } else if (blbs.size() > 1){
                                        removeBulb(b);
                                    }
                                    break;
                                case DOTS:
                                    grid.toggleOnTap(rawPoint);
                                    reset();
                                    break;
                                case STRONG_ERASE:
                                    eraseElementAt(normPoint);
                                    break;
                                case STAR:
                                    Star s = strs.elementIn(normPoint);
                                    if(s == null){
                                        addStar(normPoint);
                                    } else {
                                        removeStar(s);
                                    }
                                    break;
                            }
                        }
                        break;

                }

                return true;
            }

            private void moveElement(SchemeLayoutDrawable element, Dot dot) {
                if (!element.isIn(dot)) {
                    if(grid.contains(dot)){
                        element.position = dot;
                    }
                }
            }
        };
    }

    private void eraseElementAt(GridDot dot) {
        if(strs != null){
            Star star = strs.elementIn(dot);
            if(star != null) {
                removeStar(star);
                return;
            }
        }

        if(obst != null){
            Obstacle o = obst.elementIn(dot);
            if(o != null) {
                removeObstacle(o);
            }
        }
    }


    @Override
    public void onNextButtonClick() {
        MainThread.setRunning(false);
        sendEmail();
        toTitle();
    }

    private void sendEmail(){

        String guy = "";

        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(getActivity()).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                guy = account.name;
                break;
            }
        }

        APIHelper.SendDesign(guy, XMLify());


    }

    private String XMLify() {

        // Header
        String r =  "<scheme ";
        r += "code=\"" + getCode() + "\" ";
        r += "name=\"" + getName() + "\" ";
        r += "sector=\"" + getSector() + "\">\n";

        // Content

        if(blbs != null){
            for(Bulb b : blbs){
                r += "<bulb need = \"" + b.getNeed() +"\">" + b.position.XMLString() + "</bulb>\n";
            }
        }
        r += "<lumen>" + spnt.XMLString() + "</lumen>\n";
        r += "<grid>\n" + grid.XMLString() + "</grid>\n";
        if(obst != null){
            for(Obstacle o : obst){
                r += "<obst>" + o.gamma.XMLString() + ";" + o.theta.XMLString() + "</obst>\n";
            }
        }
        if(strs != null){
            for(Star o : strs){
                r += "<star>" + o.gamma.XMLString() + "</star>\n";
            }
        }

        // Tail
        r += "</scheme>";

        return r;
    }

    @Override
    protected boolean isSpecialWinConditionFulfilled() {
        return strs.size() == starsPicked() && unwasted();
    }

}
