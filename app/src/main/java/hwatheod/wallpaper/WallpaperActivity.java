package hwatheod.wallpaper;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

public class WallpaperActivity extends ActionBarActivity {

    public static final String TAG = "WallpaperActivity";
    private Menu theMenu;
    private int symbolNamesMenuId = R.id.conway;
    private HashMap<Integer, Integer> menuIdToColor = new HashMap<>();
    private HashMap<Integer, Integer> colorToMenuId = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SymmetryGroup.init();
        initColorMap();

        setContentView(R.layout.activity_wallpaper);
    }

    private void initColorMap() {
        menuIdToColor.put(R.id.color_blue, Color.BLUE);
        menuIdToColor.put(R.id.color_red, Color.RED);
        menuIdToColor.put(R.id.color_yellow, Color.YELLOW);
        menuIdToColor.put(R.id.color_green, Color.GREEN);
        menuIdToColor.put(R.id.color_clear, Color.WHITE);

        for (Map.Entry<Integer, Integer> entry : menuIdToColor.entrySet()) {
            int menuItemId = entry.getKey();
            int color = entry.getValue();
            colorToMenuId.put(color, menuItemId);
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        Menu settingsMenu = theMenu.findItem(R.id.action_settings).getSubMenu();
        for (int i=0; i<settingsMenu.size(); i++) {
            MenuItem item = settingsMenu.getItem(i);
            if (item.getGroupId() == R.id.group_symbol_names && item.isChecked()) {
                outState.putInt("symbolNamesMenuId", item.getItemId());
                break;
            }
        }
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        symbolNamesMenuId = savedInstanceState.getInt("symbolNamesMenuId");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        theMenu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wallpaper, menu);

        int symmetryGroupId = ((WallpaperView)findViewById(R.id.wallpaper_view)).getSymmetryGroupId();
        int color = ((WallpaperView)findViewById(R.id.wallpaper_view)).getColor();
        onOptionsItemSelected(menu.findItem(symmetryGroupId));
        onOptionsItemSelected(menu.findItem(symbolNamesMenuId));
        onOptionsItemSelected(menu.findItem(colorToMenuId.get(color)));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.conway || id == R.id.crystallographic) {
            item.setChecked(true);
            Menu groupMenu = theMenu.findItem(R.id.action_change_group).getSubMenu();
            for (int i=0; i < groupMenu.size(); i++) {
                MenuItem menuItem = groupMenu.getItem(i);
                int symmetryGroupId = menuItem.getItemId();
                if (id == R.id.conway)
                     menuItem.setTitle(SymmetryGroup.getConwaySymbol(symmetryGroupId));
                else menuItem.setTitle(SymmetryGroup.getCrystallographicSymbol(symmetryGroupId));
                if (menuItem.isChecked())
                    theMenu.findItem(R.id.action_change_group).setTitle(menuItem.getTitle());
            }
            return true;
        }

        if (item.getGroupId() == R.id.group_change_group) {
            ((WallpaperView)findViewById(R.id.wallpaper_view)).setSymmetryGroupId(id);
            theMenu.findItem(R.id.action_change_group).setTitle(item.getTitle());
            item.setChecked(true);
            return true;
        }

        if (item.getGroupId() == R.id.group_change_color) {
            int color = menuIdToColor.get(id);
            ((WallpaperView)findViewById(R.id.wallpaper_view)).setColor(color);
            theMenu.findItem(R.id.action_change_color).getIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            item.setChecked(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}