package io.github.tryexceptelse.navdata;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.util.NoSuchElementException;

import io.github.tryexceptelse.navdata.data.Path;
import io.github.tryexceptelse.navdata.data.PathModel;

public class WaypointActivity extends AppCompatActivity {

    // name of directory in which path files are stored.
    private static final String PATH_DIR_NAME = "paths";
    // key for storing last used path in preferences
    private static final String PATH_PERSISTANCE_KEY = "last_path_name";

    private File pathDir; // essentially final
    private PathModel pathModel; // essentially final

    private Path activePath; // currently selected path

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waypoint);

        // create model for accessing and manipulating paths
        pathDir = new File(getFilesDir(), PATH_DIR_NAME);
        pathModel = new PathModel(pathDir);
        activePath = reopenLastPath();

        populatePath();
    }

    protected void populatePath(){

    }

    /**
     * Returns last path that was being manipulated in app.
     * @return Path
     */
    private Path reopenLastPath(){
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        final String lastPathName = prefs.getString(PATH_PERSISTANCE_KEY, null);
        return (pathModel.contains(lastPathName)) ? pathModel.get(lastPathName) : null;
    }
}
