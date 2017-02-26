package hwatheod.wallpaper;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class PolygonTest {
    private static final double ERROR_THRESHOLD = 0.001;

    @Test
    public void testGetPoint() throws Exception {
        float[][] rectangle = {{0,0}, {10,0}, {10,25}, {0,25}};
        Polygon p = new Polygon(rectangle, -5, 19);
        assertEquals(p.getPoint(0), new float[] {-5, 19});
        assertEquals(p.getPoint(1), new float[] {5, 19});
        assertEquals(p.getPoint(2), new float[] {5, 44});
        assertEquals(p.getPoint(3), new float[] {-5, 44});
    }

    @Test
    public void testArea1() {
        float[][] rectangle = {{0,0}, {10,0}, {10,25}, {0,25}};
        Polygon p = new Polygon(rectangle, -5.234f, 19.456f);
        assertEquals(p.area(), 10*25, ERROR_THRESHOLD);
    }

    @Test
    public void testArea2() {
        float[][] triangle = {{0,0}, {3,4}, {4,-3}};
        Polygon p = new Polygon(triangle, -1.234f, 3.567f);
        assertEquals(p.area(), 5*5./2, ERROR_THRESHOLD);
    }
}