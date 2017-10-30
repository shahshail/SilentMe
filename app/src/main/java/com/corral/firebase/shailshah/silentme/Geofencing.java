package com.corral.firebase.shailshah.silentme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shailshah on 10/30/17.
 */

public class Geofencing implements ResultCallback {
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private static final float GEOFENCE_RADIUS = 50; // 50 meters
    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // 24 hours

    public Geofencing(Context context, GoogleApiClient client) {
        this.mGoogleApiClient = client;
        mContext = context;
        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;

    }

    public void registerAllGeofences() {
        // CHeck that API client is already connectd or not

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected() || mGeofenceList == null || mGeofenceList.size() == 0) {
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofenceRequest(),
                    getmGeofencePendingIntent()).setResultCallback(this);

        }
        catch (SecurityException s)
        {
            Log.v(Geofencing.class.getSimpleName(), s.getMessage());
        }
    }

    public void unRegisterAllGeofences()
    {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
        {
            return;
        }
        try
        {
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,mGeofencePendingIntent).setResultCallback(this);

        }
        catch (SecurityException s)
        {
            Log.e(Geofencing.class.getSimpleName(), s.getMessage());

        }
    }


public void updateGeofencesList(PlaceBuffer places)
{
    mGeofenceList = new ArrayList<>();
    if (places == null || places.getCount() ==0 ) return;
    for (Place place : places){

        //Read the information from the database cursor..

       String placeUID = place.getId();
        String placename = place.getName().toString();
        double placeLat = place.getLatLng().latitude;
        double placeLong = place.getLatLng().longitude;

        //Build geofence Object...
        Geofence geofence = new Geofence.Builder()
                .setRequestId(placeUID)
                .setExpirationDuration(GEOFENCE_TIMEOUT)
                .setCircularRegion(placeLat,placeLong,GEOFENCE_RADIUS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        // Add geofence object to the list...
        mGeofenceList.add(geofence);


    }
}

private GeofencingRequest getGeofenceRequest()
{
    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
    builder.addGeofences(mGeofenceList);
    return builder.build();
}

private PendingIntent getmGeofencePendingIntent()
{
    if (mGeofencePendingIntent !=null)
    {
        return mGeofencePendingIntent;
    }

    Intent intent = new Intent(mContext,GeofenceBroadcastReceiver.class);
    mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
    return mGeofencePendingIntent;
}

    @Override
    public void onResult(@NonNull Result result) {

        Log.v(Geofencing.class.getSimpleName(),"Error adding/Removien geofences..."+result.getStatus().toString());
    }
}
