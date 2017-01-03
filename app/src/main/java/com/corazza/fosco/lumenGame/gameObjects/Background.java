package com.corazza.fosco.lumenGame.gameObjects;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.corazza.fosco.lumenGame.geometry.dots.Dot;
import com.corazza.fosco.lumenGame.geometry.dots.PixelDot;
import com.corazza.fosco.lumenGame.helpers.AnimType;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Paints;
import com.corazza.fosco.lumenGame.schemes.SchemeLayoutDrawable;

import java.util.ArrayList;
import java.util.Random;

import static com.corazza.fosco.lumenGame.helpers.Consts.*;
import static com.corazza.fosco.lumenGame.helpers.Utils.scaled;

/**
 * Created by Simone on 05/06/2016.
 */
public class Background extends SchemeLayoutDrawable {

    private static final String BACKGROUND = "MAINBACKGROUNDCOLOR";
    ArrayList<Layer> layers = new ArrayList<>();
    int lastColorIndex = 0;
    int lastLayerId = 0;

    private int[] dark_colors = {
            0xff212829,
            0xff283232,
            0xff323c3c,
            0xff3c4646
    };

    private int[] bright_colors = {
            0xffDED7D6,
            0xffD7CDCD,
            0xffCDC3C3,
            0xffC3B9B9
    };

    private int[] colors() {
        return dark_colors;
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
        Paints.put(BACKGROUND, Consts.Colors.MATERIAL_BLACK);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawRect(0,0,canvas.getWidth(), canvas.getHeight(), Paints.get(BACKGROUND, alpha()));

        /*Path path = new Path();
        path.addCircle(bulb.pixelX(),bulb.pixelY(), 100, Path.Direction.CW);
        canvas.clipPath(path, Region.Op.DIFFERENCE);*/

        for(int i = layers.size()-1; i >= 0; i--){
            layers.get(i).render(canvas);
        }


        /*canvas.clipPath(path, Region.Op.UNION);*/
    }

    @Override
    public void update() {
        updateOpacity();
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
            //paint.setShadowLayer(10.0f, 0.0f, 0.0f, 0xFF000000);
            degrees = typeA ? -145 : 145;
            speed = scaled((new Random().nextInt(5) + 2)) / 100f;

            this.layerId = layerId;
            this.baseOffsetX = baseOffsetX;
        }

        public void render(Canvas canvas) {
            if(active) {
                float left = offset.pixelX();
                float top = (H-h)/2;
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
