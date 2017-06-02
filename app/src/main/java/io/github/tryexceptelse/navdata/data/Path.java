package io.github.tryexceptelse.navdata.data;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;

/**
 * Stores series of sequential waypoints
 *
 * Path class is used to store and pass data into json file both for
 * storing, and for passing as json over connection.
 */
public class Path extends ArrayDeque<Waypoint>{

    private String name;

    /** Default constructor */
    public Path(){
        this("Path");
    }

    public Path(String name){
        super();
        this.name = name;
    }

    /**
     * Constructor taking initial size as arg.
     * @param initialSize: int
     */
    public Path(int initialSize){
        this("Path", initialSize);
    }

    public Path(String name, int initialSize){
        super(initialSize);
        this.name = name;
    }

    // JSON methods

    /**
     * Creates a Path obj from a json string.
     * @param jsonStr: String
     * @return Path
     * @throws JSONException
     */
    public static Path fromJsonStr(@NonNull String jsonStr) throws JSONException{
        return fromJsonObj(new JSONObject(jsonStr));
    }

    public static Path fromJsonObj(JSONObject jsonObject) throws JSONException{
        final JSONArray wpArray = jsonObject.getJSONArray("waypoints");
        final String name = jsonObject.getString("name");
        final Path resultPath = new Path(name, wpArray.length()); // create Path to be returned
        resultPath.populateFromJsonArr(wpArray);
        return resultPath;
    }

    /**
     * Populates Path with waypoints as detailed in passed array.
     */
    protected void populateFromJsonArr(JSONArray jsonArray) throws JSONException{
        JSONObject waypointJsonObj; // var to hold JsonObj parsed from each item in jsonArr
        Waypoint wp; // var to hold each waypoint produced from JsonObj
        for (int i = 0; i < jsonArray.length(); i++){
            // for each index in jsonArray, get the object at that
            // position and create a Waypoint from it.
            waypointJsonObj = jsonArray.getJSONObject(i);
            wp = Waypoint.fromJsonObj(waypointJsonObj);
            add(wp); // adds waypoint to end of path
        }
    }

    protected JSONArray waypointArr() throws JSONException{
        final JSONArray arr = new JSONArray(); // array to populate that will be converted to str
        for(Waypoint wp : this){
            arr.put(wp.toJsonObj());
        }
        return arr;
    }

    public JSONObject toJsonObj() throws JSONException{
        JSONObject pathJsonObj = new JSONObject();
        pathJsonObj.put("name", name);
        pathJsonObj.put("waypoints", waypointArr());
        return pathJsonObj;
    }

    public String toJsonStr() throws JSONException{
        return toJsonObj().toString();
    }
}
