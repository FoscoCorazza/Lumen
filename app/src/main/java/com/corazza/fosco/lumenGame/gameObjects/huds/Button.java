package com.corazza.fosco.lumenGame.gameObjects.huds;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.corazza.fosco.lumenGame.geometry.Radical;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.helpers.Utils;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;

public class Button extends SchemeLayoutDrawable {

    private static final String MAINPAINT = "BTTNMAIN";
    private static final String SECONDARY = "BTTNSECOND";
    private static final String IMGEPAINT = "BTTNIMAGE";

    private Bitmap image;

    private boolean enabled = true;
    private boolean pressed = false;
    private boolean activateOnTouch = false;
    private boolean activated = false;
    private boolean pivotInCenter = false;
    private boolean invisibleIfDisabled;
    private boolean invisible = false;

    private Action action;
    private Integer orbitant;
    private ButtonListener listener;

    public void setInvisibleIfDisabled(boolean invisibleIfDisabled) {
        this.invisibleIfDisabled = invisibleIfDisabled;
    }

    public boolean isInvisibleIfDisabled() {
        return invisibleIfDisabled;
    }

    public void setActivateOnTouch(boolean activateOnTouch) {
        this.activateOnTouch = activateOnTouch;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public static boolean isCreatorAction(Action action) {
        return  action == Action.NEED || action == Action.LUMEN ||
                action == Action.STAR || action == Action.BULB ||
                action == Action.OBST || action == Action.HIDE ||
                action == Action.DOTS || action == Action.INCLUDE ||
                action == Action.GRID_H || action == Action.GRID_W;
    }

    public Action getAction() {
        return action;
    }

    public boolean isActivated() {
        return activated;
    }

    void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public enum Action {
        START, RESET, BACK, STOP, UNDO, MODE, NEXT,
        REDO, NEED, LUMEN, STAR, BULB, OBST, HIDE,
        DOTS, GRID_H, GRID_W, ERASE, MENU, INCLUDE}

    public Button(int image, Dot position, SchemeLayout schemeLayout, Action action, boolean pivotInCenter) {
        super(position);
        this.image = BitmapFactory.decodeResource(schemeLayout.getContext().getResources(), image);
        this.listener = schemeLayout;
        this.action = action;
        this.pivotInCenter = pivotInCenter;
        this.invisibleIfDisabled = false;
        this.orbitant = null;
    }

    @Override
    protected void initPaints() {
        Paints.put(MAINPAINT, Palette.get().getBack(Palette.Gradiation.BRIGHT));
        Paints.put(SECONDARY, Palette.get().getBack(Palette.Gradiation.DARKKK));
        Paints.put(IMGEPAINT, Palette.get().getBack(Palette.Gradiation.DARKKK));
        ColorFilter filter = new PorterDuffColorFilter(Palette.get().getAnti(Palette.Gradiation.NORMAL), PorterDuff.Mode.SRC_IN);
        Paints.get(IMGEPAINT, 255).setColorFilter(filter);
    }

    public void render(Canvas canvas) {

        if(opacity != 0 && (!invisibleIfDisabled || enabled) && !invisible) {
            int ctx = (int) (position.pixelX() + (pivotInCenter ? 0 : image.getWidth() / 2));
            int cty = (int) (position.pixelY() + (pivotInCenter ? 0 : image.getHeight() / 2));
            int rad = image.getWidth() / 2;


            int ptx = ctx - rad;
            int pty = cty - rad;

            int alpha = enabled ? 255 : 24;
            alpha = extAlpha(alpha);
            boolean drawAsPressed = pressed ||  activated;

            canvas.drawCircle(ctx, cty, rad, Paints.get(drawAsPressed ? MAINPAINT : SECONDARY, alpha));
            canvas.drawCircle(ctx, cty - 2, rad, Paints.get(drawAsPressed ? SECONDARY : MAINPAINT, alpha));
            canvas.drawBitmap(image, ptx, pty, Paints.get(IMGEPAINT, alpha));
            if (isOrbitant()) {
                canvas.save();
                canvas.rotate(orbitant, ctx, cty);
                canvas.drawCircle(ctx, pty + 6, 3, Paints.get(SECONDARY, alpha));
                canvas.restore();
            }
        }


    }

    public void update() {
        super.update();
        if(isOrbitant()) {
            orbitant = (int) valueOfNow(0,359,0,3000, AnimType.INFINITE);
        }
    }

    public boolean onTouch(MotionEvent event) {
        boolean onButton = onButton(new PixelDot(event.getX(), event.getY()));
        int action = event.getAction();
        if(action == MotionEvent.ACTION_MOVE && !onButton) {
            pressed = false;
        }else if(enabled && onButton && !invisible){
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    pressed = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (pressed) {
                        pressed = false;
                        if(activateOnTouch) activated = !activated;
                        listener.onButtonClick(this);
                    }
                    break;
            }
            return true;
        }
        return false;
    }

    private boolean onButton(Dot dot){
        int rad = image.getWidth()/2 + 25;
        int ca1 = (int) (dot.pixelX() - position.pixelX());
        int ca2 = (int) (dot.pixelY() - position.pixelY());
        int dis = (int) Utils.hypotenuse(ca1, ca2);
        return dis < rad;
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

    void setOrbitant(boolean value){
        if(value) setTimeElapsed(0);
        orbitant = value ? 0 : null;
    }

    private int getOrbitant(){
       return orbitant;
    }

    private boolean isOrbitant(){
        return orbitant != null;
    }

    public interface ButtonListener {
        void onButtonClick(Button button);
    }
}
