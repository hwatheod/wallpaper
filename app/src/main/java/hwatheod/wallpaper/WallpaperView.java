package hwatheod.wallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class WallpaperView extends View {

    private static final String TAG = "WallpaperView";

    private Paint drawPaint, fundamentalRegionPaint;
    private Canvas drawCanvas;
    private Path currentPath;
    private Bitmap canvasBitmap;

    private int symmetryGroupId;
    private SymmetryGroup gp;

    public WallpaperView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        currentPath = new Path();
        drawPaint = new Paint();
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setColor(Color.BLUE);

        fundamentalRegionPaint = new Paint();
        fundamentalRegionPaint.setColor(Color.BLACK);
        fundamentalRegionPaint.setStyle(Paint.Style.STROKE);

        symmetryGroupId = R.id.group_o;
    }

    protected Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }

    public int getSymmetryGroupId() {
        return symmetryGroupId;
    }

    public void setSymmetryGroupId(int newSymmetryGroupId) {
        canvasBitmap.eraseColor(Color.WHITE);
        currentPath.reset();
        symmetryGroupId = newSymmetryGroupId;
        gp = new SymmetryGroup(newSymmetryGroupId, getWidth(), getHeight());
        invalidate();
    }

    public int getColor() {
        return drawPaint.getColor();
    }

    public void setColor(int color) {
        drawPaint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

        gp = new SymmetryGroup(symmetryGroupId, w, h);
    }

    // Save/restore instance code from:
    // http://stackoverflow.com/questions/3542333/how-to-prevent-custom-views-from-losing-state-across-screen-orientation-changes/3542895#3542895

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        WallpaperViewSavedState ss = new WallpaperViewSavedState(superState);
        ss.symmetryGroupId = this.symmetryGroupId;
        ss.color = this.drawPaint.getColor();

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof WallpaperViewSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        WallpaperViewSavedState ss = (WallpaperViewSavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.symmetryGroupId = ss.symmetryGroupId;
        setColor(ss.color);
    }

    static class WallpaperViewSavedState extends BaseSavedState {
        int symmetryGroupId;
        int color;

        WallpaperViewSavedState(Parcelable superState) {
            super(superState);
        }

        private WallpaperViewSavedState(Parcel in) {
            super(in);
            this.symmetryGroupId = in.readInt();
            this.color = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.symmetryGroupId);
            out.writeInt(this.color);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<WallpaperViewSavedState> CREATOR =
                new Parcelable.Creator<WallpaperViewSavedState>() {
                    public WallpaperViewSavedState createFromParcel(Parcel in) {
                        return new WallpaperViewSavedState(in);
                    }
                    public WallpaperViewSavedState[] newArray(int size) {
                        return new WallpaperViewSavedState[size];
                    }
                };
    }

    private boolean isInBounds(float x, float y) {
        return (x >= 0 && x < getWidth() && y >= 0 && y < getHeight());
    }

    private boolean rayTowardsWindow(float x, float y, float dx, float dy) {
        /* Returns true iff the ray from (x,y) in the direction (dx, dy) moves "towards" the window
           in the sense that all these conditions are satisfied:
             if x is to the left of the window, then dx > 0.
             if x is to the right of the window, then dx < 0.
             if y is above the window, then dy > 0.
             if y is below the window, then dy < 0.

           This does not necessarily mean that the ray intersects the window, but it is
           sufficient for our purposes.
         */

        if (x < 0 && dx <= 0)
            return false;
        if (x >= getWidth() && dx >= 0)
            return false;
        if (y < 0 && dy <= 0)
            return false;
        if (y >= getHeight() && dy >= 0)
            return false;
        return true;
    }

    private boolean doSingleDirectionTranslation(Canvas canvas, Path tempPath, float centerX, float centerY, float dx, float dy) {
        /* This finds all translations of (centerX, centerY) in the direction of positive (dx,dy) which are within the view window.
           i.e. Finds all integers n>=0 such that (centerX + n*dx, centerY + n*dy) are within the view window.
           The corresponding translations of tempPath are made.
           Returns true if at least one translation was made, otherwise false.

           Before returning, tempPath is returned to its original location.
        */
        
        boolean inBoundsFound = false;
        int n = 0;

        while(rayTowardsWindow(centerX, centerY, dx, dy)) {
            if (isInBounds(centerX, centerY)) {
                inBoundsFound = true;
                canvas.drawPath(tempPath, drawPaint);
            }
            centerX += dx;
            centerY += dy;
            n++;
            tempPath.offset(dx, dy);
        }
        tempPath.offset(-n*dx, -n*dy);

        return inBoundsFound;
    }

    private void applyTranslations(Canvas canvas, Path path) {
        Path tempPath = new Path(path);

        float d1x = gp.getTranslationX()[0];
        float d1y = gp.getTranslationY()[0];
        float d2x = gp.getTranslationX()[1];
        float d2y = gp.getTranslationY()[1];

        RectF bounds = new RectF();
        path.computeBounds(bounds, true);

        float centerX = bounds.centerX();
        float centerY = bounds.centerY();

        int n=0;
        boolean posDirectionFound = true, negDirectionFound = true;
        while (posDirectionFound || negDirectionFound) {
            posDirectionFound = doSingleDirectionTranslation(canvas, tempPath, centerX, centerY, d1x, d1y);
            negDirectionFound = doSingleDirectionTranslation(canvas, tempPath, centerX, centerY, -d1x, -d1y);
            n++;
            centerX += d2x;
            centerY += d2y;
            tempPath.offset(d2x, d2y);
        }

        centerX -= (n+1)*d2x;
        centerY -= (n+1)*d2y;
        tempPath.offset(-(n+1)*d2x, -(n+1)*d2y); // go one past the original
        posDirectionFound = true;
        negDirectionFound = true;
        while (posDirectionFound || negDirectionFound) {
            posDirectionFound = doSingleDirectionTranslation(canvas, tempPath, centerX, centerY, d1x, d1y);
            negDirectionFound = doSingleDirectionTranslation(canvas, tempPath, centerX, centerY, -d1x, -d1y);
            // don't need to count n in the second stage
            centerX -= d2x;
            centerY -= d2y;
            tempPath.offset(-d2x, -d2y);
        }
    }

    private void applySymmetriesToCurrentPath(Canvas canvas) {
        Path path = new Path(currentPath);
        applyTranslations(canvas, path);
        for (Matrix m : gp.getCosetReps()) {
            path.rewind();
            currentPath.transform(m, path);
            applyTranslations(canvas, path);
        }
    }

    protected void onDraw(Canvas canvas) {
        applySymmetriesToCurrentPath(drawCanvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
        gp.getFundamentalRegion().draw(canvas, fundamentalRegionPaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (currentPath.isEmpty())
                    currentPath.moveTo(touchX, touchY);
                else currentPath.lineTo(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                currentPath.reset();
                break;
            default:
                return false;
        }

        return true;
    }
}
