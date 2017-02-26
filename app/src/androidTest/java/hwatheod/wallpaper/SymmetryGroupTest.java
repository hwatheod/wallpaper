package hwatheod.wallpaper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(Parameterized.class)
public class SymmetryGroupTest {
    @Parameters(name = "{1}")
    public static Iterable<Object[]> data() {
        List<Object[]> result = new ArrayList<>();
        SymmetryGroup.init();
        Map<Integer, String> symmetryGroups = SymmetryGroup.getConwayGroupSymbolMap();
        for (Map.Entry<Integer, String> entry : symmetryGroups.entrySet()) {
            result.add(new Object[] { entry.getKey(), entry.getValue()});
        }
        assertEquals(result.size(), 17);
        return result;
    }

    private int symmetryGroupId;
    private String symmetryGroupString;

    public SymmetryGroupTest(int symmetryGroupId, String symmetryGroupString) {
        this.symmetryGroupId = symmetryGroupId;
        this.symmetryGroupString = symmetryGroupString;
    }

    /*
      This is a simple test for self-consistency of the data generated by SymmetryGroup().
      Multiply the area of the fundamental region by the number of coset representatives (including
       the identity).
      The result should equal the area of the fundamental parallelogram generated by the
       translation vectors.
     */
    @Test
    public void verifySymmetryGroup() {
        SymmetryGroup g = new SymmetryGroup(symmetryGroupId, 600, 400);
        float fundamentalRegionArea = g.getFundamentalRegion().area();
        float expectedArea =
                Math.abs(g.getTranslationX()[0] * g.getTranslationY()[1] -
                        g.getTranslationX()[1] * g.getTranslationY()[0]);  // area of parallelogram formed by translation vectors
        float copiesOfFundamentalRegionArea = fundamentalRegionArea * (1 + g.getCosetReps().length);
        float relativeError = Math.abs((expectedArea - copiesOfFundamentalRegionArea) / expectedArea);
        assertTrue("Discrepancy for group " + symmetryGroupString, relativeError < 0.001);
    }
}
