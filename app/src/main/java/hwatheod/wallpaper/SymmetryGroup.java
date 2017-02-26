package hwatheod.wallpaper;

import android.graphics.Matrix;

import java.util.HashMap;
import java.util.Map;

public class SymmetryGroup {
    private static HashMap<Integer, String> conwayGroupSymbolMap = new HashMap<Integer, String>();
    private static HashMap<Integer, String> crystallographicGroupSymbolMap = new HashMap<Integer, String>();

    private Polygon fundamentalRegion; // fundamental region for the symmetry group
    private float centerX, centerY;  // center of fundamental tile for the translation subgroup
    private float[] translationX;
    private float[] translationY;  // the 2 translation vectors for the translation subgroup
    private Matrix[] cosetReps; // coset representatives of the translation subgroup in the symmetry group,
                        // which can be applied to the fundamental region to get the fundamental tile
                        // Identity matrix is NOT included.
    private int id;
    
    static private void setReflectionMatrix(Matrix m, float[] p1, float[] p2) {
        /* Sets m to be the matrix for reflection about the line through p1 and p2. */

        float dx = p2[0] - p1[0];
        float dy = p2[1] - p1[1];
        /* orthogonal direction is (dy, -dx) */
        float unitNormal[] = {dy / (float)Math.sqrt(dy*dy + dx*dx), -dx / (float)Math.sqrt(dy*dy + dx*dx)};

        /* The reflection matrix fixes the 2 points while taking the unit normal to its negative. */
        m.setPolyToPoly(new float[] {p1[0], p1[1], p2[0], p2[1], p1[0] + unitNormal[0], p1[1] + unitNormal[1]}, 0,
                new float[] {p1[0], p1[1], p2[0], p2[1], p1[0] - unitNormal[0], p1[1] - unitNormal[1]}, 0, 3
                );
    }

    static public void init() {
        conwayGroupSymbolMap.put(R.id.group_o, "o");
        conwayGroupSymbolMap.put(R.id.group_xx, "xx");
        conwayGroupSymbolMap.put(R.id.group_sx, "*x");
        conwayGroupSymbolMap.put(R.id.group_ss, "**");
        conwayGroupSymbolMap.put(R.id.group_632, "632");
        conwayGroupSymbolMap.put(R.id.group_s632, "*632");
        conwayGroupSymbolMap.put(R.id.group_333, "333");
        conwayGroupSymbolMap.put(R.id.group_s333, "*333");
        conwayGroupSymbolMap.put(R.id.group_3s3, "3*3");
        conwayGroupSymbolMap.put(R.id.group_442, "442");
        conwayGroupSymbolMap.put(R.id.group_s442, "*442");
        conwayGroupSymbolMap.put(R.id.group_4s2, "4*2");
        conwayGroupSymbolMap.put(R.id.group_2222, "2222");
        conwayGroupSymbolMap.put(R.id.group_22x, "22x");
        conwayGroupSymbolMap.put(R.id.group_22s, "22*");
        conwayGroupSymbolMap.put(R.id.group_s2222, "*2222");
        conwayGroupSymbolMap.put(R.id.group_2s22, "2*22");

        crystallographicGroupSymbolMap.put(R.id.group_o, "p1");
        crystallographicGroupSymbolMap.put(R.id.group_xx, "pg");
        crystallographicGroupSymbolMap.put(R.id.group_sx, "cm");
        crystallographicGroupSymbolMap.put(R.id.group_ss, "pm");
        crystallographicGroupSymbolMap.put(R.id.group_632, "p6");
        crystallographicGroupSymbolMap.put(R.id.group_s632, "p6mm");
        crystallographicGroupSymbolMap.put(R.id.group_333, "p3");
        crystallographicGroupSymbolMap.put(R.id.group_s333, "p3m1");
        crystallographicGroupSymbolMap.put(R.id.group_3s3, "p31m");
        crystallographicGroupSymbolMap.put(R.id.group_442, "p4");
        crystallographicGroupSymbolMap.put(R.id.group_s442, "p4mm");
        crystallographicGroupSymbolMap.put(R.id.group_4s2, "p4mg");
        crystallographicGroupSymbolMap.put(R.id.group_2222, "p2");
        crystallographicGroupSymbolMap.put(R.id.group_22x, "p2gg");
        crystallographicGroupSymbolMap.put(R.id.group_22s, "p2mg");
        crystallographicGroupSymbolMap.put(R.id.group_s2222, "p2mm");
        crystallographicGroupSymbolMap.put(R.id.group_2s22, "c2mm");
    }

    static protected Map<Integer, String> getConwayGroupSymbolMap() {
        return conwayGroupSymbolMap;
    }

    static public String getConwaySymbol(int symmetryGroupId) {
        return conwayGroupSymbolMap.get(symmetryGroupId);
    }

    static public String getCrystallographicSymbol(int symmetryGroupId) {
        return crystallographicGroupSymbolMap.get(symmetryGroupId);
    }

    SymmetryGroup(int symmetryGroupId, int width, int height) {
        id = symmetryGroupId;
        
        float d1x, d1y, d2x, d2y, offsetX, offsetY;
        switch(symmetryGroupId) {
            case R.id.group_o:                
                d1x = 200; d1y = 0; d2x = 80; d2y = 200;
                offsetX = width/2 - (d1x + d2x) / 2;
                offsetY = height/2 - (d1y + d2y)/2;
                fundamentalRegion = new Polygon(new float[][] {{0,0}, {d1x, d1y}, {d1x+d2x, d1y+d2y}, {d2x, d2y}}, offsetX, offsetY);
                centerX = (d1x + d2x) / 2 + offsetX;
                centerY = (d1y + d2y) / 2 + offsetY;
                translationX = new float[] {d1x, d2x};
                translationY = new float[] {d1y, d2y};
                cosetReps = new Matrix[] {};
                break;
            case R.id.group_2222:
                d1x = 200; d1y = 0; d2x = 80; d2y = 200;
                offsetX = width/2 - (d1x + d2x)/2;
                offsetY = height/2 - (d1y + d2y)/2;
                fundamentalRegion = new Polygon(new float[][] {{0,0}, {d1x, d1y}, {d1x+d2x, d1y+d2y}, {d2x, d2y}}, offsetX, offsetY);
                centerX = d1x / 2 + offsetX;
                centerY = d1y / 2 + offsetY;
                translationX = new float[] {d1x, d2x*2};
                translationY = new float[] {d1y, d2y*2};
                cosetReps = new Matrix[1];
                cosetReps[0] = new Matrix(); cosetReps[0].setRotate(180, centerX, centerY);
                break;
            case R.id.group_333:
                float hexSize = 300;
                d1x = 3*hexSize/4; d1y = hexSize * ((float)Math.sqrt(3)/4); d2x = d1x; d2y = -d1y;
                offsetX = width/2;
                offsetY = height/2;
                fundamentalRegion = new Polygon(new float[][] {{0,0}, {hexSize/2 * 1/2, hexSize/2 * (float)(Math.sqrt(3)/2) },
                                                   {hexSize/2,0},{hexSize/2 * 1/2, -hexSize/2 * (float)Math.sqrt(3)/2}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {d1x, d2x};
                translationY = new float[] {d1y, d2y};
                cosetReps = new Matrix[2];
                cosetReps[0] = new Matrix(); cosetReps[0].setRotate(120, centerX, centerY);
                cosetReps[1] = new Matrix(); cosetReps[1].setRotate(240, centerX, centerY);
                break;
            case R.id.group_442:                
                float squareSize = 300;
                d1x = squareSize; d1y = 0; d2x = 0; d2y = squareSize;
                offsetX = width/2;
                offsetY = height/2;
                fundamentalRegion = new Polygon(new float[][] {{0,0}, {d2x/2, d2y/2}, {(d1x+d2x)/2,(d1y+d2y)/2},{d1x/2, d1y/2}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {d1x, d2x};
                translationY = new float[] {d1y, d2y};
                cosetReps = new Matrix[3];
                cosetReps[0] = new Matrix(); cosetReps[0].setRotate(90, centerX, centerY);
                cosetReps[1] = new Matrix(); cosetReps[1].setRotate(180, centerX, centerY);
                cosetReps[2] = new Matrix(); cosetReps[2].setRotate(270, centerX, centerY);
                break;
            case R.id.group_632:
                hexSize = 400;
                d1x = 3*hexSize/4; d1y = hexSize * ((float)Math.sqrt(3)/4); d2x = d1x; d2y = -d1y;
                offsetX = width/2;
                offsetY = height/2;
                fundamentalRegion = new Polygon(new float[][] {{0,0}, {0, hexSize/2 * (float)Math.sqrt(3)/2},
                                        {hexSize/4,hexSize/2 * (float)Math.sqrt(3)/2},{3*hexSize/8, hexSize*(float)Math.sqrt(3)/8}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {d1x, d2x};
                translationY = new float[] {d1y, d2y};
                cosetReps = new Matrix[5];
                for (int i=0; i<5; i++ ) {
                    cosetReps[i] = new Matrix();
                    cosetReps[i].setRotate(60 * (i+1), centerX, centerY);
                }
                break;
            case R.id.group_s2222:
                float dx = 400, dy = 200;
                offsetX = width/2 - dx/4;
                offsetY = height/2 - dy/4;
                fundamentalRegion = new Polygon(new float[][] {{0,0}, {dx/2, 0}, {dx/2, dy/2}, {0, dy/2}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {dx, 0};
                translationY = new float[] {0, dy};
                cosetReps = new Matrix[3];
                cosetReps[0] = new Matrix(); setReflectionMatrix(cosetReps[0], fundamentalRegion.getPoint(0), fundamentalRegion.getPoint(1));
                cosetReps[1] = new Matrix(); setReflectionMatrix(cosetReps[1], fundamentalRegion.getPoint(0), fundamentalRegion.getPoint(3));
                cosetReps[2] = new Matrix(); cosetReps[2].setRotate(180, offsetX, offsetY);
                break;
            case R.id.group_s333:
                hexSize = 500;
                d1x = 3*hexSize/4; d1y = hexSize * ((float)Math.sqrt(3)/4); d2x = d1x; d2y = -d1y;
                offsetX = width/2;
                offsetY = height/2;
                fundamentalRegion = new Polygon(new float[][] {{0,0}, {hexSize/2 * 1/2, hexSize/2 * (float)(Math.sqrt(3)/2) },
                        {hexSize/2,0}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {d1x, d2x};
                translationY = new float[] {d1y, d2y};
                cosetReps = new Matrix[5];
                cosetReps[0] = new Matrix(); cosetReps[0].setRotate(120, centerX, centerY);
                cosetReps[1] = new Matrix(); cosetReps[1].setRotate(240, centerX, centerY);
                cosetReps[2] = new Matrix(); setReflectionMatrix(cosetReps[2], fundamentalRegion.getPoint(2), fundamentalRegion.getPoint(0));
                cosetReps[3] = new Matrix(cosetReps[2]); cosetReps[3].postConcat(cosetReps[0]);
                cosetReps[4] = new Matrix(cosetReps[2]); cosetReps[4].postConcat(cosetReps[1]);
                break;
            case R.id.group_s442:
                squareSize = 400;
                d1x = squareSize; d1y = 0; d2x = 0; d2y = squareSize;
                offsetX = width/2;
                offsetY = height/2;
                fundamentalRegion = new Polygon(new float[][] {{0,0}, {d2x/2, d2y/2}, {(d1x+d2x)/2,(d1y+d2y)/2}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {d1x, d2x};
                translationY = new float[] {d1y, d2y};
                cosetReps = new Matrix[7];
                cosetReps[0] = new Matrix(); cosetReps[0].setRotate(90, centerX, centerY);
                cosetReps[1] = new Matrix(); cosetReps[1].setRotate(180, centerX, centerY);
                cosetReps[2] = new Matrix(); cosetReps[2].setRotate(270, centerX, centerY);
                cosetReps[3] = new Matrix(); setReflectionMatrix(cosetReps[3], fundamentalRegion.getPoint(1), fundamentalRegion.getPoint(2));
                for (int i=4; i<7; i++) {
                    cosetReps[i] = new Matrix(cosetReps[3]); cosetReps[i].postConcat(cosetReps[i-4]);
                }
                break;
            case R.id.group_s632:
                hexSize = 500;
                d1x = 3*hexSize/4; d1y = hexSize * ((float)Math.sqrt(3)/4); d2x = d1x; d2y = -d1y;
                offsetX = width/2;
                offsetY = height/2;
                fundamentalRegion = new Polygon(new float[][] {{0,0}, {0, hexSize/2 * (float)Math.sqrt(3)/2},
                        {hexSize/4,hexSize/2 * (float)Math.sqrt(3)/2}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {d1x, d2x};
                translationY = new float[] {d1y, d2y};
                cosetReps = new Matrix[11];
                for (int i=0; i<5; i++ ) {
                    cosetReps[i] = new Matrix();
                    cosetReps[i].setRotate(60 * (i+1), centerX, centerY);
                }
                cosetReps[5] = new Matrix(); setReflectionMatrix(cosetReps[5], fundamentalRegion.getPoint(0), fundamentalRegion.getPoint(2));
                for (int i=6; i<11; i++) {
                    cosetReps[i] = new Matrix(cosetReps[5]); cosetReps[i].postConcat(cosetReps[i-6]);
                }
                break;
            case R.id.group_ss:
                dx = 300; dy = 120;
                offsetX = width / 2 - dx / 4;
                offsetY = height / 2 - dy / 2;
                fundamentalRegion = new Polygon(new float[][]{{0,0}, {dx/2, 0}, {dx/2, dy}, {0, dy}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {dx, 0};
                translationY = new float[] {0, dy};
                cosetReps = new Matrix[1];
                cosetReps[0] = new Matrix(); setReflectionMatrix(cosetReps[0], fundamentalRegion.getPoint(0), fundamentalRegion.getPoint(3));
                break;
            case R.id.group_sx:
                dx = 150; dy = 120;
                offsetX = width / 2 - dx / 2;
                offsetY = height / 2 - dy / 2;
                fundamentalRegion = new Polygon(new float[][]{{0,0}, {dx, 0}, {dx, dy}, {0, dy}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {dx, dx};
                translationY = new float[] {dy, -dy};
                cosetReps = new Matrix[1];
                cosetReps[0] = new Matrix(); setReflectionMatrix(cosetReps[0], fundamentalRegion.getPoint(0), fundamentalRegion.getPoint(3));
                break;
            case R.id.group_xx:
                dx = 150; dy = 120;
                offsetX = width / 2 - dx / 2;
                offsetY = height / 2 - dy / 2;
                fundamentalRegion = new Polygon(new float[][]{{0,0}, {dx, 0}, {dx, dy}, {0, dy}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {dx, 0};
                translationY = new float[] {0, 2*dy};
                cosetReps = new Matrix[1];
                cosetReps[0] = new Matrix();
                setReflectionMatrix(cosetReps[0], new float[] {dx/2 + offsetX, 0 + offsetY}, new float[] { dx/2 + offsetX, dy + offsetY});
                cosetReps[0].postTranslate(0, dy);
                break;
            case R.id.group_22s:
                dx = 150; dy = 120;
                offsetX = width / 2 - dx / 2;
                offsetY = height / 2 - dy / 2;
                fundamentalRegion = new Polygon(new float[][]{{0,0}, {dx, 0}, {dx, dy}, {0, dy}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {2*dx, 0};
                translationY = new float[] {0, 2*dy};
                cosetReps = new Matrix[3];
                cosetReps[0] = new Matrix(); setReflectionMatrix(cosetReps[0], fundamentalRegion.getPoint(1), fundamentalRegion.getPoint(2));
                cosetReps[1] = new Matrix(); cosetReps[1].setRotate(180, dx/2 + offsetX, 0 + offsetY);
                cosetReps[2] = new Matrix(cosetReps[1]); cosetReps[2].postConcat(cosetReps[0]);
                break;
            case R.id.group_22x:
                dx = 150; dy = 120;
                offsetX = width / 2 - dx / 2;
                offsetY = height / 2 - dy / 2;
                fundamentalRegion = new Polygon(new float[][]{{0,0}, {dx, 0}, {dx, dy}, {0, dy}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {2*dx, 0};
                translationY = new float[] {0, 2*dy};
                cosetReps = new Matrix[3];
                cosetReps[0] = new Matrix();
                setReflectionMatrix(cosetReps[0], fundamentalRegion.getPoint(1), fundamentalRegion.getPoint(2));
                cosetReps[0].postTranslate(0, dy);
                cosetReps[1] = new Matrix(); cosetReps[1].setRotate(180, dx/2 + offsetX, 0 + offsetY);
                cosetReps[2] = new Matrix(cosetReps[1]); cosetReps[2].postConcat(cosetReps[0]);
                break;
            case R.id.group_2s22:
                dx = 150; dy = 120;
                offsetX = width / 2 - dx / 2;
                offsetY = height / 2 - dy / 2;
                fundamentalRegion = new Polygon(new float[][]{{0,0}, {dx, 0}, {dx, dy}, {0, dy}}, offsetX, offsetY);
                centerX = width/2;
                centerY = height/2;
                translationX = new float[] {dx, dx};
                translationY = new float[] {2*dy, -2*dy};
                cosetReps = new Matrix[3];
                cosetReps[0] = new Matrix(); setReflectionMatrix(cosetReps[0], fundamentalRegion.getPoint(1), fundamentalRegion.getPoint(2));
                cosetReps[1] = new Matrix(); cosetReps[1].setRotate(180, dx / 2 + offsetX, 0 + offsetY);
                cosetReps[2] = new Matrix(cosetReps[0]); cosetReps[2].postConcat(cosetReps[1]);
                break;
            case R.id.group_3s3:
                float baseSize = 300;
                offsetX = width / 2 - baseSize / 2;
                offsetY = height / 2;
                fundamentalRegion = new Polygon(new float[][]{{0,0}, {baseSize, 0}, {baseSize/2, (baseSize / 2) * (float)Math.sqrt(3)/3}}, offsetX, offsetY);
                centerX = 3*baseSize / 4 + offsetX;
                centerY = baseSize * (float)Math.sqrt(3)/4 + offsetY;
                translationX = new float[] {baseSize, baseSize/2};
                translationY = new float[] {0, baseSize * (float)Math.sqrt(3)/2};
                cosetReps = new Matrix[5];
                cosetReps[0] = new Matrix(); cosetReps[0].setRotate(120, fundamentalRegion.getPoint(2)[0], fundamentalRegion.getPoint(2)[1]);
                cosetReps[1] = new Matrix(); cosetReps[1].setRotate(240, fundamentalRegion.getPoint(2)[0], fundamentalRegion.getPoint(2)[1]);
                cosetReps[2] = new Matrix(); setReflectionMatrix(cosetReps[2], fundamentalRegion.getPoint(1), new float[] {centerX, centerY});
                cosetReps[3] = new Matrix(cosetReps[0]); cosetReps[3].postConcat(cosetReps[2]);
                cosetReps[4] = new Matrix(cosetReps[1]); cosetReps[4].postConcat(cosetReps[2]);
                break;
            case R.id.group_4s2:
                squareSize = 150;
                offsetX = width/2 - squareSize / 2;
                offsetY = height/2 - squareSize / 2;
                fundamentalRegion = new Polygon(new float[][]{{0,0},{0,squareSize},{squareSize,squareSize}, {squareSize,0}}, offsetX, offsetY);
                centerX = 0 + offsetX;
                centerY = 0 + offsetY;
                translationX = new float[] {2 * squareSize, 2 * squareSize};
                translationY = new float[] {2 * squareSize, -2 * squareSize};
                cosetReps = new Matrix[7];
                for (int i=0; i<3; i++) {
                    cosetReps[i] = new Matrix();
                    cosetReps[i].setRotate(90 * (i + 1), centerX, centerY);
                }
                cosetReps[3] = new Matrix(); setReflectionMatrix(cosetReps[3], fundamentalRegion.getPoint(2), fundamentalRegion.getPoint(3));
                for (int i=4; i<7; i++) {
                    cosetReps[i] = new Matrix(cosetReps[i-4]);
                    cosetReps[i].postConcat(cosetReps[3]);
                }
                break;
        }
    }

    public Polygon getFundamentalRegion() {
        return fundamentalRegion;
    }

    public float[] getTranslationX() {
        return translationX;
    }

    public float[] getTranslationY() {
        return translationY;
    }

    public Matrix[] getCosetReps() {
        return cosetReps;
    }

    public int getId() {
        return id;
    }
}
