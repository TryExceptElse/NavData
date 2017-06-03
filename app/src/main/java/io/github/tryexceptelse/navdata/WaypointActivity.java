package io.github.tryexceptelse.navdata;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.File;

import io.github.tryexceptelse.navdata.data.Path;
import io.github.tryexceptelse.navdata.data.PathModel;

public class WaypointActivity extends AppCompatActivity {

    // name of directory in which path files are stored.
    private static final String PATH_DIR_NAME = "paths";
    // key for storing last used path in preferences
    private static final String PATH_PERSISTENCE_KEY = "last_path_name";

    private PathModel pathModel; // essentially final

    private Path activePath; // currently selected path

    // ui elements
    private Spinner pathSelector; // used to select path to be edited.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waypoint);

        // create model for accessing and manipulating paths
        final File pathDir = new File(getFilesDir(), PATH_DIR_NAME);
        pathModel = new PathModel(pathDir);
        activePath = reopenLastPath();

        // find ui instances
        pathSelector = (Spinner) findViewById(R.id.pathSelector);

        // setup ui
        populatePathSelector();
        pathSelector.setOnTouchListener(this::pathSelectorClicked);
    }

    protected void populatePathSelector(){
        final String options[] = (pathModel.isEmpty()) ?
                new String[]{getString(R.string.empty_stylized)} : pathModel.getPathNames();
        // make adapter that serves as a data model for the spinner.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pathSelector.setAdapter(adapter);
        // if previously selected path exists, set pathSelector to that path
        if (activePath != null){
            pathSelector.setSelection(adapter.getPosition(activePath.name()));
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

    // these need to be public to be referenced from the layout

    public void addPathPressed(View v){
        showCreatePathDlg();
    }

    public void rmvPathPressed(View v){
        final String selectedPathName = (String) pathSelector.getSelectedItem();
        if (selectedPathName.equals(getString(R.string.empty_stylized))){
            // if selected string is the 'empty' string, tell user we're afraid we can't do that.
            showCannotDeleteNoPathsExistDlg();
        } else {
            showConfirmDeleteDlg(selectedPathName);
        }
    }

    public void addWpPressed(View v){
        if (pathModel.isEmpty()){
            // inform user that no paths exist,
            // ask them if they would like to create one.
            showCreatePathQueryDlg();
            return;
        }
        // todo: add waypoint
    }

    public void rmvWpPressed(View v){
        if (pathModel.isEmpty()){
            // inform user that no paths exist,
            // ask them if they would like to create one.
            showCreatePathQueryDlg();
            return;
        }
        // todo: remove waypoint
    }

    // these can be private, as they are connected to ui widgets
    // from within this class

    private boolean pathSelectorClicked(View view, MotionEvent motionEvent) {
        populatePathSelector(); // update items in spinner
        return false; // this should pass on the motionEvent, not consume it.
    }

    // supporting methods for controller methods

    /**
     * Attempts to remove path indicated by path name,
     * and if path can not be removed, informs user.
     * @param pathName: String
     * @return boolean: whether or not removal succeeded.
     */
    private boolean removePath(String pathName){
        if (pathModel.remove(pathName)){
            return true; // if removal succeeds, do nothing more.
        }
        // otherwise give user a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        // Add the buttons
        builder.setPositiveButton(R.string.ok, null);
        // Set other dialog fields
        builder.setTitle(R.string.could_not_remove_path_title);
        builder.setMessage(getString(R.string.could_not_remove_path, pathName));

        final AlertDialog dialog = builder.create(); // Create the AlertDialog
        dialog.show();
        return false;
    }

    /**
     * Creates a new path of passed name.
     * @param name: String
     */
    private void createPath(@NonNull String name){
        if (pathModel.contains(name)){
            // if name already exists, inform user and ask them what to do
            showOverwritePathDlg(name);
            return; // overwrite path dialog will re-call this method if needed.
        }
        final Path newPath = new Path(name);
        pathModel.add(newPath);
    }

    // dialog methods
    // these methods create and show a single dialog method

    /**
     * Informs user that we can't delete a path if no paths exist.
     * Method called by rmvPathPressed if no paths currently exist
     */
    private void showCannotDeleteNoPathsExistDlg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        // Add the buttons
        builder.setPositiveButton(
                R.string.ok, // string appearing on btn
                // if clicked, call path remove method
                null);
        // Set other dialog fields
        builder.setTitle(R.string.no_paths_exist_title);
        builder.setMessage(R.string.cannot_delete_no_paths_exist);

        final AlertDialog dialog = builder.create(); // Create the AlertDialog
        dialog.show();
    }

    /**
     * Asks user to confirm that they wish to delete the selected path.
     */
    private void showConfirmDeleteDlg(final String selectedPathName){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        // Add the buttons
        builder.setPositiveButton(
                R.string.delete_btn_str, // string appearing on btn
                // if clicked, call path remove method
                (dialog, id) -> removePath(selectedPathName));
        builder.setNegativeButton(
                R.string.cancel, // string appearing on button
                null); // do nothing special if dialog is closed.
        // Set other dialog fields
        builder.setTitle(R.string.confirm_delete_title);
        builder.setMessage(getString(R.string.confirm_delete_msg, selectedPathName));

        final AlertDialog dialog = builder.create(); // Create the AlertDialog
        dialog.show();
    }

    /**
     * Informs user that no paths currently exist, and asks them if
     * they would like to create one now.
     *
     * Intended to be called by activity onCreate() if no paths exist,
     * or when they try to do something that requires a path.
     */
    private void showCreatePathQueryDlg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        // Add the buttons
        builder.setPositiveButton(
                R.string.yes, // string appearing on btn
                // if clicked, create a 'create path' dlg
                (dialog, id) -> showCreatePathDlg());
        builder.setNegativeButton(
                R.string.no, // string appearing on button
                null); // do nothing special if dialog is closed.
        // Set other dialog fields
        builder.setTitle(R.string.create_path_query_title);
        builder.setMessage(R.string.create_path_query_msg);

        final AlertDialog dialog = builder.create(); // Create the AlertDialog
        dialog.show();
    }

    /**
     * Asks user for name of new path, and creates it.
     */
    private void showCreatePathDlg(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        // create text entry field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Add the buttons
        builder.setPositiveButton(
                R.string.create, // string appearing on btn
                // if clicked, create a 'create path' dlg
                (dialog, id) -> showCreatePathDlg());
        builder.setNegativeButton(
                R.string.cancel, // string appearing on button
                null); // do nothing special if dialog is closed.

        // Set other dialog fields
        builder.setTitle(R.string.create_path_title);
        builder.setMessage(R.string.create_path_msg);

        final AlertDialog dialog = builder.create(); // Create the AlertDialog
        dialog.show();
    }

    /**
     * Inform user that a path already exists with the name that
     * was entered and asks them to confirm that it should
     * be overwritten.
     * @param name: String name of path
     * be overwritten
     */
    private void showOverwritePathDlg(@NonNull String name){
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        // Add the buttons
        builder.setPositiveButton(
                R.string.overwrite, // string appearing on btn
                // if clicked, create a 'create path' dlg
                (dialog, id) -> {
                    // remove pre-existing path of passed name
                    pathModel.remove(name);
                    // re-call create path method
                    createPath(name);
                });
        builder.setNeutralButton(
                R.string.back,
                // re-show create path dialog, allowing user to enter a different name
                (dialog, id) -> showCreatePathDlg()
        );
        builder.setNegativeButton(
                R.string.cancel, // string appearing on button
                null); // do nothing special if user selects 'cancel'.

        // Set other dialog fields
        builder.setTitle(R.string.overwrite_path_title);
        builder.setMessage(getString(R.string.overwrite_path_msg, name));

        builder.show();
    }
}