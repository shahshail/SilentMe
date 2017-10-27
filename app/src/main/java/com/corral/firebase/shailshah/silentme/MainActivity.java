package com.corral.firebase.shailshah.silentme;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.corral.firebase.shailshah.silentme.provider.SilentContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView mTextMessage;
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;
    private SilentAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mRecyclerView = (RecyclerView) findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SilentAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this,this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.v(TAG,"Connection Successful...");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG,"Connection Suspended...");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.v(TAG, "Connection Failed...");
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
    }

    public void onLocationPermissionClicked(View view) {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_FINE_LOCATION);
    }
    }

