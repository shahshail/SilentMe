package com.corral.firebase.shailshah.silentme;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.corral.firebase.shailshah.silentme.provider.SilentContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener  {

    private TextView mTextMessage;
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;
    private SilentAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private GoogleApiClient mClient;
    FrameLayout layout;
    int navigationstatus;
    private boolean mIsEnabled;
    PreferenceScreen screen;
    private Geofencing mGeofencing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SilentAdapter(this,null);
        mRecyclerView.setAdapter(mAdapter);

        //Initializing the switch that enable and disable of the switch state
        Switch onOffSwitch = (Switch) findViewById(R.id.enable_switch);
        mIsEnabled = getPreferences(MODE_PRIVATE) .getBoolean(getString(R.string.setting_enabled),false);
        onOffSwitch.setChecked(mIsEnabled);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.setting_enabled), isChecked);
                mIsEnabled = isChecked;
                editor.commit();
                if (isChecked) mGeofencing.registerAllGeofences();
                else mGeofencing.unRegisterAllGeofences();
            }

        });


        mClient  = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this,this)
                .build();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

         mGeofencing = new Geofencing(this,mClient);




    }

   public void onDemoButtonClicked(View view)
    {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.v(TAG,"Connection Successful...");
        refreshplacesData();
        //locationListFragment.onRefreshWSwapCursor(places);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG,"Connection Suspended...");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.v(TAG, "Connection Failed...");
    }


    public void refreshplacesData()
    {
        Uri uri = SilentContract.SilentEntry.CONTENT_URI;
        Cursor data = getContentResolver().query(uri,null,null,null,null);

        //Here we are simply loop over the cursor and add each ID to the list..
        if (data == null || data.getCount() == 0) return;
        List<String> guids = new ArrayList<String>();
        while (data.moveToNext())
        {
            guids.add(data.getString(data.getColumnIndex(SilentContract.SilentEntry.COLUMN_PLACE_ID)));

            //Place Buffer is nothing but a places API object that access the places...
            PendingResult<PlaceBuffer> placeBuffer = Places.GeoDataApi.getPlaceById(mClient,guids.toArray(new String[guids.size()]));

            placeBuffer.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {

                    mAdapter.swapPlaces(places);
                    mGeofencing.updateGeofencesList(places);
                    if (mIsEnabled) mGeofencing.registerAllGeofences();



                }
            });
        }

    }


    public void onAddPlaceButtonClicked(View view)
    {
        AlertDialog.Builder Alertbuilder;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, getString(R.string.need_location_permission_message), Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, getString(R.string.location_permissions_granted_message), Toast.LENGTH_SHORT).show();

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent i = null;
        try {
            i = builder.build(this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Alertbuilder = new AlertDialog.Builder(getApplicationContext(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                Alertbuilder = new AlertDialog.Builder(getApplicationContext());
            }
            Alertbuilder.setTitle("GooglePlay Services")
                    .setMessage("GooglePlay Services Not Available. Please Install and try again..:)")
                    .setCancelable(true)
                    .show();

        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Alertbuilder = new AlertDialog.Builder(getApplicationContext(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                Alertbuilder = new AlertDialog.Builder(getApplicationContext());
            }
            Alertbuilder.setTitle("GooglePlay Services")
                    .setMessage("GooglePlay Services Not Available. Please Install and try again..:)")
                    .setCancelable(true)
                    .show();
        }
        startActivityForResult(i,PLACE_PICKER_REQUEST);

    }

    /**
     *
     * @param requestcode REQUEST CODE PASSED WHEN CALLING STARTACTIVITYFORRESULT
     * @param resultCode The resut code specified by the socond activity
     *
     * @param data The intent the carries the intent data..
     */
    public void onActivityResult(int requestcode, int resultCode, Intent data)
    {
        if (requestcode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK)
        {
            Place place = PlacePicker.getPlace(this,data);
            if (place == null)
            {
                Log.i(TAG, "No place Selected");
                return;
            }

            //We are only allowed to store place id and the we can retrive it when we need it.. google simply prohibited to store any other information more than 30 days..
            //Extract the place information from the api

            String placeName = place.getName().toString();
            String placeId= place.getId();
            String placeAddress = place.getAddress().toString();

            //Insert a new Place into the Database...
            ContentValues contentValues = new ContentValues();
            contentValues.put(SilentContract.SilentEntry.COLUMN_PLACE_ID,placeId);
            getContentResolver().insert(SilentContract.SilentEntry.CONTENT_URI,contentValues);

            refreshplacesData();



        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckBox locationPermissions = (CheckBox) findViewById(R.id.location_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissions.setChecked(false);
        } else {
            locationPermissions.setChecked(true);
            locationPermissions.setEnabled(false);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.v(MainActivity.class.getSimpleName(),"Geo status" + sharedPreferences.getBoolean("geo_key",false));
        // Initialize ringer permissions checkbox
        CheckBox ringerPermissions = (CheckBox) findViewById(R.id.ringer_permissions_checkbox);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Check if the API supports such permission change and check if permission is granted
        if (android.os.Build.VERSION.SDK_INT >= 24 && !nm.isNotificationPolicyAccessGranted()) {
            ringerPermissions.setChecked(false);
        } else {
            ringerPermissions.setChecked(true);
            ringerPermissions.setEnabled(false);
        }

    }

    public void onLocationPermissionClicked(View view) {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_FINE_LOCATION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater  = getMenuInflater();
        inflater.inflate(R.menu.navigation,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();

        if (id == R.id.navigation_notifications)
        {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals(getString(R.string.pref_loc_key)))
        {
            boolean result_location = sharedPreferences.getBoolean(key,getResources().getBoolean(R.bool.pref_loc_default));

            if (result_location)
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_FINE_LOCATION);
            }


            Log.v(MainActivity.class.getSimpleName(), "the Location Preference is... " + result_location);

        }
        else if(key.equals(getString(R.string.pref_geo_key)))
        {
            boolean result_geo_location = sharedPreferences.getBoolean(key,getResources().getBoolean(R.bool.pref_loc_default));
            Log.v(MainActivity.class.getSimpleName(), "the Geo preference is ...  " + result_geo_location);
        }

    }
    public void onRingerPermissionsClicked(View view) {
        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}

