package com.corazza.fosco.lumenGame.gameObjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;

import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

import java.util.ArrayList;
import java.util.Random;

import static com.corazza.fosco.lumenGame.helpers.Consts.*;
import static com.corazza.fosco.lumenGame.helpers.Utils.scaledFrom480;

/**
 * Created by Simone on 05/06/2016.
 */
public class Background extends SchemeLayoutDrawable {

    private static final String BACKGROUND = "MAINBACKGROUNDCOLOR";
    private ArrayList<Layer> layers = new ArrayList<>();
    private int lastColorIndex = 0;
    private int lastLayerId = 0;
    private double percentageModifier = 0;



    private int[] colors() {
        return new int[]{
                Palette.get().getBack(Palette.Gradiation.LUMOUS),
                Palette.get().getBack(Palette.Gradiation.BRIGHT),
                Palette.get().getBack(Palette.Gradiation.NORMAL),
                Palette.get().getBack(Palette.Gradiation.GLOOMY)
        };
    }

    private int colors(int i) {
        return colors()[i];
    }

    private void generateNewLayer(boolean startOutscreen){
        boolean rb = new Random().nextBoolean();
        // Colore
        if(lastColorIndex == 0){
            lastColorIndex++;
        } else if(lastColorIndex == colors().length - 1){
            lastColorIndex--;
        } else {
            lastColorIndex = lastColorIndex + (rb ? -1 : 1);
        }

        // Offset
        int standardOffsetX = startOutscreen ? 2*W : 0;
        int offsetX = 0;
        if(layers.size() > 0){
            offsetX = layers.get(layers.size()-1).getX() + (rb ? 100 : 200);
        }

        int layerId = ++lastLayerId;
        layers.add(new Layer(layerId, lastColorIndex, rb, Math.max(standardOffsetX, offsetX)));
        setTimeElapsed(layerId, 0);
    }

    public Background() {
        super(new PixelDot(0, 0));
        for(int i=0; i< 7; i++) generateNewLayer(false);
    }

    @Override
    protected void initPaints() {
        Paints.put(BACKGROUND, Palette.get().getBack(Palette.Gradiation.DARKKK));
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawRect(0,0,canvas.getWidth(), canvas.getHeight(), Paints.get(BACKGROUND, alpha()));

        /*Path path = new Path();
        path.addCircle(20, 30, 100, Path.Direction.CW);
        canvas.clipPath(path, Region.Op.DIFFERENCE);*/

        for(int i = layers.size()-1; i >= 0; i--){
            layers.get(i).render(canvas);
        }


        //canvas.clipPath(path, Region.Op.UNION);
    }

    @Override
    public void update() {
        super.update();
        Layer out = null;
        for(Layer layer : layers) {
            layer.update();
            if(layer.outOfScreen()) out = layer;
        }

        if(out != null){
            layers.remove(out);
            generateNewLayer(true);
        }
    }

    public Paint getBackPaint() {
        return Paints.get(BACKGROUND, 255);
    }

    public void setPercentageModifier(double percentageModifier) {
        this.percentageModifier = percentageModifier;
    }

    private class Layer{
        private boolean active = false;
        private Paint paint = new Paint();
        private PixelDot offset = new PixelDot(0,0);
        private float degrees;
        private float speed = W * 6/100000f;
        private final float w = 1f*W;
        private final float h = 3f*H;
        int baseOffsetX;
        int layerId;

        Layer(int layerId, int colorIndex, boolean typeA, int baseOffsetX){
            paint = new Paint();
            paint.setColor(colors(colorIndex));
            degrees = typeA ? -145 : 145;
            speed = scaledFrom480((new Random().nextInt(5) + 2)) / 100f;

            this.layerId = layerId;
            this.baseOffsetX = baseOffsetX;
        }

        public void render(Canvas canvas) {
            if(active) {
                float left = offset.pixelX() + position.pixelX();
                float top = (H-h)/2 + position.pixelY();
                float right = left + w;
                float bottom = top + h;
                PixelDot center = new PixelDot((left + right) / 2, (top + bottom) / 2);

                canvas.save();
                canvas.rotate(degrees, center.pixelX(), center.pixelY());
                canvas.drawRect(left, top, right, bottom, paint);
                canvas.restore();
            }
        }

        public void update() {
            int endPoint = -W*2;
            float wayLength = baseOffsetX - endPoint;
            offset = new PixelDot((int) valueOfNow(layerId, baseOffsetX, endPoint, 0, (long) (wayLength / speed), AnimType.DEFAULT), -300);
            active = true;
        }

        boolean outOfScreen(){
            return offset.pixelX() < -(W*2-50);
        }

        public int getX() {
            return active ? (int) offset.pixelX() : baseOffsetX;
        }
    }


}
