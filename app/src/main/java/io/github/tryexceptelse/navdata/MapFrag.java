package io.github.tryexceptelse.navdata;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Observable;

import io.github.tryexceptelse.navdata.data.Path;
import io.github.tryexceptelse.navdata.data.Waypoint;

public class MapFrag extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Map Fragment"; // used as debugging identifier

    private GoogleMap mMap;

    // store list of observers to be notified when a waypoint is added or removed.
    private Observable wpAddNotifier, wpRemoveNotifier;
    private Path path; // currently set path. Begins as null.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_frag);
        // Obtain the SupportMapFragment and get notified when the map_frag is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // instantiate activity fields
        wpAddNotifier = new Observable();
        wpRemoveNotifier = new Observable();
        path = null;
    }


    /**
     * Manipulates the map_frag once available.
     * This callback is triggered when the map_frag is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // move camera to current (or last known) position
        setCameraToStartPos();

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        // connect controller methods
        mMap.setOnMapClickListener(this::onMapClick);
        mMap.setOnMarkerClickListener(this::onMarkerClick);
    }

    @Nullable
    public Path path(){
        return path;
    }

    public boolean setPath(@Nullable Path path){
        if (path == this.path){
            return false;
        } else {
            this.path = path;
            return true;
        }
    }

    protected boolean addWp(Marker marker){
        if (path == null){
            return false;
        }
        final LatLng point = marker.getPosition();
        final Waypoint wp = new Waypoint(point);
        path.add(wp); // add wp to end of path
        wpAddNotifier.notifyObservers(wp);
        return true;
    }

    protected boolean removeWp(Marker marker){
        if (path == null){
            return false;
        }
        final LatLng point = marker.getPosition();
        final Waypoint wp = new Waypoint(point);
        path.remove(wp); // remove wp from path
        wpRemoveNotifier.notifyObservers(wp);
        return true;
    }

    protected void setCameraToStartPos(){
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        try{
            Location location = locationManager.getLastKnownLocation(
                    locationManager.getBestProvider(criteria, false));
            if (location != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()),
                        13));

                CameraPosition newCamPos = new CameraPosition.Builder()
                        .target(new LatLng(         // Sets map focus to user position
                                location.getLatitude(),
                                location.getLongitude()
                        ))
                        .zoom(17)                   // Sets the zoom
                        .bearing(0)                 // Sets the orientation of the camera to north
                        .build();                   // Create CameraPosition
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos));
            }
        } catch (SecurityException ignore) {
            // if we can't get position, don't move the camera
            System.out.println("Could not get position: security exception");
        }
    }


    // CONTROLLER METHODS

    // connected locally, these methods can be protected / private.

    /**
     * Called when user clicks the map (not when it is scrolled, zoomed, etc)
     * @param point: LatLng
     * @return boolean (whether to consume event or not) -> always false
     */
    protected boolean onMapClick(LatLng point){
        return false; // don't consume event
    }

    /**
     * Called when user clicks a marker on the map.
     * (But not when pressed for long duration, dragged, etc)
     * @param marker: Marker
     * @return boolean (whether to consume event or not) -> always false
     */
    protected boolean onMarkerClick(Marker marker){
        return false; // don't consume event
    }
}
