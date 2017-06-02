package io.github.tryexceptelse.navdata;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.File;

import io.github.tryexceptelse.navdata.data.Path;
import io.github.tryexceptelse.navdata.data.PathModel;

public class WaypointActivity extends AppCompatActivity {

    // name of directory in which path files are stored.
    private static final String PATH_DIR_NAME = "paths";
    // key for storing last used path in preferences
    private static final String PATH_PERSISTENCE_KEY = "last_path_name";

    private File pathDir; // essentially final
    private PathModel pathModel; // essentially final

    private Path activePath; // currently selected path

    // ui elements
    private Spinner pathSelector; // used to select path to be edited.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waypoint);

        // create model for accessing and manipulating paths
        pathDir = new File(getFilesDir(), PATH_DIR_NAME);
        pathModel = new PathModel(pathDir);
        activePath = reopenLastPath();

        // find ui instances
        pathSelector = (Spinner) findViewById(R.id.pathSelector);

        // setup ui
    }

    protected void populatePathSelector(){
        final String options[] = (pathModel.isEmpty()) ?
                new String[]{"---None---"} : pathModel.getPathNames();
        // make adapter that serves as a data model for the spinner.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pathSelector.setAdapter(adapter);
        // if previously selected path exists, set pathSelector to that path
        if (activePath != null){
            pathSelector.setSelection(adapter.getPosition(activePath.name()));
        } else {
            pathSelector.setSelection(0); // 0 index will always exist
        }
    }

    /**
     * Returns last path that was being manipulated in app.
     * @return Path
     */
    @Nullable
    private Path reopenLastPath(){
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        final String lastPathName = prefs.getString(PATH_PERSISTENCE_KEY, null);
        return (pathModel.contains(lastPathName)) ? pathModel.get(lastPathName) : null;
    }

    // controller methods, called upon user interaction with views

    public void pathSelectorClicked(View v){
    }

    public void addPathPressed(View v){
    }

    public void rmvPathPressed(View v){
    }

    public void addWpPressed(View v){
    }

    public void rmvWpPressed(View v){
    }
}
