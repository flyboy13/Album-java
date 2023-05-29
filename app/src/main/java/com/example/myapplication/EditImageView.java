package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ColorSpace;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EditImageView extends View {

    private Paint paint;
    private Path path;
    private Bitmap editBitmap,newBitmap,restemp;
    private Canvas customCanvas;
    private Matrix matrix;
    private ColorMatrix colorMatrix;
    private float dx, dy;
    private int angleRotate;
    private List<Bitmap> listBitmap;
    private List<Path> listPath;
    private boolean isRotate, isBrush,isResize;
    private float scale_value;
    private int wB,hB;

    private float[] originalCMatrix = new float[] {
            1, 0, 0, 0, 0,
            0, 1, 0, 0, 0,
            0, 0, 1, 0, 0,
            0, 0, 0, 1, 0 };
    private List<float[]> colorMatrixList;
    private int brushSize, eraserSize;
    private String brushColor;

    public EditImageView(Context context) {
        super(context);
        init();
    }

    public EditImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public EditImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        brushSize = 10;
        brushColor = "#000000";
        eraserSize = 10;
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.parseColor(brushColor));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(brushSize);

        matrix = new Matrix();
        listBitmap = new ArrayList<Bitmap>();
        listPath = new ArrayList<Path>();
        isRotate = false;
        isBrush = false;
        isResize=false;

        colorMatrix = new ColorMatrix(originalCMatrix);
        colorMatrixList = new ArrayList<float[]>();
        colorMatrixList.add(originalCMatrix);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isRotate) {
            Paint mPaint = new Paint();
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(mPaint);
            isRotate = false;
        }

        if (editBitmap != null) {
            float px = getWidth()/2;
            float py = getHeight()/2;
            matrix.setTranslate(px - (editBitmap.getWidth()/2), py - editBitmap.getHeight()/2);
            matrix.postRotate(angleRotate);
            //matrix.setTranslate(px, py);
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(editBitmap,matrix, paint);
        }
    }

    public void setBitmapResource(Bitmap resource, int widthView, int heightView) {
        int widthBitmap = resource.getWidth();
        wB=widthBitmap;
        int heightBitmap = resource.getHeight();
        hB=heightBitmap;
        float scaleWidth = (float) (widthView * 1.0 / resource.getWidth());
        float scaleHeight = (float) (heightView * 1.0 / resource.getHeight());
        float scale = Math.min(scaleWidth, scaleHeight);
        int width = (int) (widthBitmap * scale);
        int height = (int) (heightBitmap * scale);
        scale_value=scale;
        this.editBitmap = Bitmap.createScaledBitmap(resource, width, height, true);
        customCanvas = new Canvas(editBitmap);
        invalidate();
    }
    public int convertDptoPixels(int dp)
    {
        return (int)(dp* Resources.getSystem().getDisplayMetrics().density);
    }
    public void scaleBitmapResource(int width, int height) {
        if (!isResize) {
            restemp = editBitmap.copy(editBitmap.getConfig(), true);
            isResize=true;
        }
        newBitmap = editBitmap.copy(editBitmap.getConfig(), true);
        editBitmap = Bitmap.createScaledBitmap(newBitmap, convertDptoPixels(width), convertDptoPixels(height), true);
        customCanvas = new Canvas(newBitmap);
        newBitmap.recycle();
        invalidate();
    }

    public void rescale()
    {
        editBitmap=restemp;
    }
    public boolean ISRES(){
        return isResize;
    }

    public Bitmap getBitmapResource() {
        return editBitmap;
    }

    public void setAngleRotate(int rotate) {
        angleRotate = rotate;
        isRotate = true;
        invalidate();
    }

    public void reset() {
        angleRotate = 0;
        isBrush = false;
        colorMatrix.set(originalCMatrix);
        colorMatrixList.clear();
        colorMatrixList.add(originalCMatrix);
        enableBrush();
        invalidate();
    }

    public void clearRotate() {
        angleRotate = 0;
        invalidate();
    }

    public void flip_hor()
    {
        matrix.postScale(1, -1, editBitmap.getWidth()/2, editBitmap.getWidth()/2);
        Bitmap bOutput = Bitmap.createBitmap(editBitmap, 0, 0, editBitmap.getWidth(), editBitmap.getHeight(), matrix, true);
        editBitmap=bOutput;
        invalidate();
    }
    public void flip_ver() {
        matrix.postScale(-1, 1, editBitmap.getWidth()/2, editBitmap.getWidth()/2);
        Bitmap bOutput = Bitmap.createBitmap(editBitmap, 0, 0, editBitmap.getWidth(), editBitmap.getHeight(), matrix, true);
        editBitmap=bOutput;
        invalidate();
    }


    Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public Bitmap getEditBitmap() {
        invalidate();
        this.editBitmap = Bitmap.createBitmap(getBitmapFromView(this));
        return editBitmap;
    }

    public void saveImage() {
        addLastBitmap(getEditBitmap());
        isBrush = false;
        colorMatrixList.clear();
        colorMatrixList.add(colorMatrix.getArray());
        enableBrush();
        invalidate();
    }

    public void fastblur( int radius)
    {
        restemp = editBitmap.copy(editBitmap.getConfig(), true);
        Bitmap bitmap = editBitmap.copy(editBitmap.getConfig(), true);

        if (radius < 1) {
            return;
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        editBitmap=bitmap;
        customCanvas = new Canvas(bitmap);
    }

    public void addLastBitmap(Bitmap bitmap) {
        listBitmap.add(bitmap);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isBrush) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStart(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    addLastBitmap(getEditBitmap());
                    break;
            }

            return true;
        }
        return false;
    }

    private void touchUp() {
        path.reset();
    }

    private void touchMove(float x, float y) {
        float spaceX = Math.abs(x - dx);
        float spaceY = Math.abs(y - dy);

        if (spaceX >= 5 || spaceY >= 5) {
            path.quadTo(x, y, (x+dx)/2, (y+dy)/2);
            dx = x;
            dy = y;

            customCanvas.drawPath(path, paint);
            customCanvas.setBitmap(editBitmap);
            listPath.add(path);
            invalidate();
        }
    }

    private void touchStart(float x, float y) {
        path.moveTo(x, y);
        dx = x;
        dy = y;
    }

    public void enableBrush() {
        paint.setXfermode(null);
        paint.setShader(null);
        paint.setMaskFilter(null);
    }

    public void setIsBrush(boolean brush) {
        isBrush = brush;
    }

    public void setColorMatrix(ColorMatrix cMatrix) {
        colorMatrixList.add(cMatrix.getArray());
        colorMatrix = cMatrix;
        invalidate();
    }

    public void clearFilter() {
        colorMatrix = new ColorMatrix(colorMatrixList.get(0));
        colorMatrixList.clear();
        colorMatrixList.add(colorMatrix.getArray());
        invalidate();
    }

    private int toPx(int size) {
        return (int) (size * getResources().getDisplayMetrics().density);
    }

    public void setBrushSize(int size) {
        brushSize = toPx(size);
        paint.setStrokeWidth(brushSize);
    }

    public void setBrushColor(String color) {
        brushColor = color;
        paint.setColor(Color.parseColor(brushColor));
    }
}

