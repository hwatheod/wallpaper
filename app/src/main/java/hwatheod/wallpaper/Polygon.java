package hwatheod.wallpaper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class Polygon
{
    // Polygon coordinates.
    private float[] polyX, polyY;

    // Number of sides in the polygon.
    private int polySides;

    /**
     * @param points {{0,0}, {10,0}, {10,10}, {0,10}} Coordinates of points in order (clockwise or counterclockwise).
     * @param offsetX X offset to be applied to each point.
     * @param offsetY Y offset to be applied to each point.
     */

    public Polygon( float[][] points, float offsetX, float offsetY) {
        polySides = points.length;
        polyX = new float[polySides];
        polyY = new float[polySides];

        for (int i=0; i<polySides; i++) {
            polyX[i] = points[i][0] + offsetX;
            polyY[i] = points[i][1] + offsetY;
        }
    }

    public float[] getPoint(int i) {
        return new float[] { polyX[i], polyY[i] };
    }

    public float area() {
        // used only for the SymmetryGroup test
        float sum = 0;
        for (int i=0; i<polySides; i++) {
            sum += this.polyX[i] * this.polyY[(i+1) % polySides] - this.polyY[i] * this.polyX[(i+1) % polySides];
        }
        sum /= 2;
        sum = Math.abs(sum);
        return sum;
    }

    public void draw(Canvas c, Paint paint) {
        if (polySides == 0)
            return;

        Path path = new Path();

        path.moveTo(polyX[0], polyY[0]);
        for (int i=1; i < polySides; i++)
            path.lineTo(polyX[i], polyY[i]);
        path.close();

        c.drawPath(path, paint);
    }
}
