package hwatheod.wallpaper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.graphics.Color;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import android.view.View;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ApplicationTest {
    @Rule
    public ActivityTestRule<WallpaperActivity> mActivityRule = new ActivityTestRule<>(WallpaperActivity.class);

    @Test
    public void testConway() {
        onView(withId(R.id.action_settings)).perform(click());
        onData(hasToString("Conway")).perform(click());
        onView(withId(R.id.action_change_group)).perform(click());
        onData(hasToString("4*2")).perform(click());
        onView(withId(R.id.action_change_group)).check(matches(withText("4*2")));
    }

    @Test
    public void testCrystallographic() {
        onView(withId(R.id.action_settings)).perform(click());
        onData(hasToString("Crystallographic")).perform(click());
        onView(withId(R.id.action_change_group)).perform(click());
        onData(hasToString("p6mm")).perform(click());
        onView(withId(R.id.action_change_group)).check(matches(withText("p6mm")));
    }

    @Test
    public void testColorChange() {
        onView(withId(R.id.action_change_color)).perform(click());
        onData(hasToString("red")).perform(click());
        onView(withId(R.id.wallpaper_view)).perform(swipeCenter(10, 10));
        onView(withId(R.id.wallpaper_view)).check(
            new ViewAssertion() {
                @Override
                public void check(View view, NoMatchingViewException noViewFoundException) {
                    if (view == null) {
                        throw noViewFoundException;
                    }
                    int centerX = view.getWidth() / 2;
                    int centerY = view.getHeight() / 2;

                    int color = ((WallpaperView)view).getCanvasBitmap().getPixel(centerX, centerY);
                    if (color != Color.RED) {
                        throw new AssertionError("Pixel did not change to red");
                    }
                }
            }
        );
    }

    // based on: http://stackoverflow.com/questions/22177590/click-by-bounds-coordinates/22798043#22798043
    private static CoordinatesProvider viewCenterToScreenCoordinates(final int deltaX, final int deltaY) {
        return new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                final int[] screenPos = new int[2];
                view.getLocationOnScreen(screenPos);

                final int screenX = screenPos[0] + view.getWidth()/2 + deltaX;
                final int screenY = screenPos[1] + view.getHeight()/2 + deltaY;
                float[] coordinates = {screenX, screenY};

                return coordinates;
            }
        };
    }

    private static ViewAction swipeCenter(final int deltaX, final int deltaY) {
        return new GeneralSwipeAction(
            Swipe.FAST,
            viewCenterToScreenCoordinates(0, 0),
            viewCenterToScreenCoordinates(deltaX, deltaY),
            Press.FINGER
        );
    }
}