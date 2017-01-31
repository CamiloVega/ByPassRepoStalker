package com.bypassmobile.octo.image;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * A transformation that can be leveraged by Picasso to display
 * a rectangular image as a circle.  When applying borders, it is important that
 * incoming images are of a uniform size / resolution, otherwise the border may
 * become inconsistent.
 * <p/>
 * Adapted from:
 * https://gist.github.com/berkkaraoglu/ab4caa8b1fe48231dec2
 */
public class CircleTransformation implements Transformation {

    private static final String KEY = "circle";
    private static final int TWO = 2;
    private static final float FLOAT_TWO = 2f;
    private float borderThicknessPix = 0;
    private Paint strokePaint = null;

    /**
     * Initialize a transform that has no stroke.
     */
    public CircleTransformation() {
    }

    /**
     * Initialize a tranform with stroke.
     * @param borderColor
     */
    public CircleTransformation(int borderColor) {
        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(0);
        strokePaint.setColor(borderColor);
        strokePaint.setAntiAlias(true);
    }

    @Override
    public Bitmap transform(Bitmap source) {
        final int srcWidth = source.getWidth();
        final int srcHeight = source.getHeight();
        try {
            int size = Math.min(source.getWidth(), source.getHeight());

            Bitmap squaredBitmap;

            // if the image is not already square, make it so:
            if (source.getHeight() != source.getWidth()) {
                int x = (srcWidth - size) / TWO;
                int y = (srcHeight - size) / TWO;

                squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                source.recycle();
            } else {
                // img is already square so use the source:
                squaredBitmap = source;
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, getConfig(squaredBitmap));

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP,
                    BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / FLOAT_TWO;

            // draw the rounded image:
            canvas.drawCircle(r, r, r, paint);

            if (strokePaint != null) {
                // draw stroke border:
                canvas.drawCircle(r, r, r - (borderThicknessPix / FLOAT_TWO), strokePaint);
            }

            squaredBitmap.recycle();
            return bitmap;
        } catch (Exception e) {
            throw new RuntimeException("Circle transform failed. src height=" +
                    srcHeight + ", width=" + srcWidth, e);
        }
    }

    private Bitmap.Config getConfig(Bitmap bitmap) {
        // Per https://gist.github.com/julianshen/5829333
        Bitmap.Config config = bitmap.getConfig();
        return config != null ? config : Bitmap.Config.ARGB_8888;
    }

    @Override
    public String key() {
        return KEY;
    }
}
