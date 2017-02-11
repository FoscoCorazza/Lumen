package com.corazza.fosco.lumenGame.gameObjects;

import android.content.Context;
import android.graphics.Canvas;

import com.corazza.fosco.lumenGame.geometry.Line;
import com.corazza.fosco.lumenGame.geometry.Radical;
import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;
import com.corazza.fosco.lumenGame.schemes.schemeLayout.SchemeLayout;
import com.corazza.fosco.lumenGame.geometry.Segment;
import com.corazza.fosco.lumenGame.helpers.Consts;

public class Lumen extends SchemeLayoutDrawable {

    private static final int HEARTBEAT = 1;
    private static final String DETAIL = "LMNDET";
    private static final String BACKGROUND = "LMNBACK";
    private static final String SECONDARY= "LMNSECONDARY";

    public Segment.Direction direction;
    public boolean active = true;
    private Radical travelLength = new Radical();

    //private Bitmap image;
    private SchemeLayout layout;
    private double speed = Consts.lumenSpeed; // Pixel al millisecondo
    private Context context;

    // Constructors
    public Lumen(Context ctx, SchemeLayout layout, Dot position) {
        super(position);
        init(ctx, layout);
    }

    private void init(Context ctx, SchemeLayout layout){
        this.size = Consts.lumenSize;
        this.active = true;
        this.context = ctx;
        this.layout = layout;
        setTimeElapsed(HEARTBEAT, 0);
    }

    public Lumen duplicate(){
        Lumen duplo = new Lumen(context ,layout, position);
        duplo.travelLength = new Radical(travelLength);
        return duplo;
    }

    // Position
    public void setPosition(Dot position) { this.position = position;}

    // Getter e Setter
    public boolean isActive() {
        return active;
    }
    public double getSpeed()  { return speed; }

    //Animazioni
    public void fadeOff(boolean renew){
        active = false;
        isMoving = false;
        opacity = 0;
        if (renew) layout.setMainLumen(layout.spnt);
    }

    @Override
    protected void initPaints() {
        Paints.put(DETAIL, Palette.get().getAnti(Palette.Gradiation.LUMOUS));
        Paints.put(BACKGROUND, Palette.get().getMain(Palette.Gradiation.NORMAL));
        Paints.put(SECONDARY, Palette.get().getMain(Palette.Gradiation.GLOOMY));
    }

    public void render(Canvas canvas) {
        if(isActive() || opacity == 0) {
            canvas.drawCircle(pixelX(), pixelY(), multiplier * size, Paints.get(DETAIL, alpha()));
            canvas.drawCircle(pixelX(), pixelY(), multiplier * (size - 2), Paints.get(SECONDARY, alpha()));
            canvas.drawCircle(pixelX(), pixelY(), multiplier * (size - 4), Paints.get(BACKGROUND, alpha()));
            canvas.drawCircle(pixelX(), pixelY(), multiplier * 2, Paints.get(DETAIL, alpha()));
        }
    }

    //Animazione:

    //Variabili di controllo:

    public boolean isMoving = false;
    public Line laying;
    private PixelDot startPosition;
    private PixelDot finalPosition;
    private long time;

    public void startAnimation(Line layingOn, long duration) {
        direction = layingOn.suggestDirection(this);
        startPosition = position.pixelDot();
        finalPosition = layingOn.getLast(direction).pixelDot();
        isMoving = true;
        time = duration;
        laying = layingOn;
        addToTravel(laying);
        setTimeElapsed(0);
    }

    private float multiplier = 1;
    public void update() {
        layout.onUpdate(this);
        updateOpacity();
        updateHeartbeat();
        if (isMoving) {
            if(getTimeElapsed() < time) {
                int newX = (int) valueOfNow(startPosition.pixelX(), finalPosition.pixelX(), 0, time, AnimType.DEFAULT);
                int newY = (int) valueOfNow(startPosition.pixelY(), finalPosition.pixelY(), 0, time, AnimType.DEFAULT);
                setPosition(new PixelDot(newX, newY));
            }
            else {
                setPosition(finalPosition);
                layout.animate(this, laying, getTimeElapsed()-time);
            }
        }
    }


    //HEARTBEAT
    int W = 5000, i = 100, w = 50, A = 2;

    int step0 = 0;
    int step1 = step0 + W;
    int step2 = step1 + i/2;
    int step3 = step2 + i/2;
    int step4 = step3 + w;
    int step5 = step4 + i/2;
    int stepX = step5 + i/2;

    private void updateHeartbeat() {
        if (isMoving) {
            multiplier = 1;
            return;
        }
        int x = (int) getTimeElapsed(HEARTBEAT);

        if(x >= stepX) {
            setTimeElapsed(HEARTBEAT, 0);
            multiplier = 1;
        }
        else if ( step1 < x && x <= step2 )     {
            multiplier = valueAtTimeX(x, 1, A, step1, step2, AnimType.DEFAULT);
        }
        else if ( step2 < x && x <= step3 ) {
            multiplier = valueAtTimeX(x, A, 1, step2, step3, AnimType.DEFAULT);
        }
        else if ( step4 < x && x <= step5 )     {
            multiplier = valueAtTimeX(x, 1, A, step4, step5, AnimType.DEFAULT);
        }
        else if ( step5 < x && x <= stepX)      {
            multiplier = valueAtTimeX(x, A, 1, step5, stepX, AnimType.DEFAULT);
        } else {
            multiplier = 1;
        }

    }

    public Dot endPoint() {
        Segment s = ((Segment) laying);
        boolean gt = direction == Line.Direction.GAMMATHETA;
        return s != null ? (gt ? s.theta : s.gamma) : null;
    }

    public Radical getTravelLength() {
        return travelLength;
    }

    public void addToTravel(Line line){
        travelLength.add(line.length());
    }

    public void setTravelLength(Radical travelLength) {
        this.travelLength = travelLength;
    }

    public boolean onTheWayOn(Dot dot) {
        return dot.equals(endPoint());
    }

    public boolean traveledAsMuchAs(Lumen lumen) {
        return getTravelLength().equals(lumen.getTravelLength());
    }

    public interface SchemeLayoutListener {
         void onUpdate(SchemeLayoutDrawable sender);
    }


}