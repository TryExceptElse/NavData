package io.github.tryexceptelse.navdata.data;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing a specific position for navigation purposes.
 */
public class Waypoint {
    private LatLng pos;

    public Waypoint(LatLng pos){
        this.pos = pos;
    }

    /**
     * Position (LatLng) getter
     * @return LatLng
     */
    public LatLng pos(){
        return this.pos;
    }

    /**
     * Position (LatLng) setter
     * @param pos: LatLng
     */
    public void setPos(LatLng pos){
        this.pos = pos;
    }

    // JSON methods:

    public JSONObject toJsonObj() throws JSONException{
        JSONObject jo = new JSONObject();
        jo.put("lat", pos.latitude);
        jo.put("lng", pos.longitude);
        return jo;
    }

    /**
     * Factory method which creates a Waypoint obj from a passed json object.
     * @param jsonObject: JSONObject
     * @return Waypoint
     */
    public static Waypoint fromJsonObj(JSONObject jsonObject) throws JSONException{
        final Double lat, lng;
        lat = jsonObject.getDouble("lat");
        lng = jsonObject.getDouble("lng");
        // check values for validity
        if (lat > 90 || lat < -90){ // latitude outside bounds
            throw new JSONException("Expected Latitude value between -90 and 90. Got: " + lat);
        } else if (lng > 180 || lng < 180){ // longitude outside bounds
            throw new JSONException("Expected Longitude value between -180 and 180. Got " + lng);
        }
        return new Waypoint(new LatLng(lat, lng));
    }
}
