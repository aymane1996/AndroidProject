package com.aymane.android;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.aymane.android.Adapters.LayoutAdapter;
import com.aymane.android.Adapters.MapLocationAdapter;
import com.aymane.android.Models.MapLocation;
import com.aymane.android.Models.Report;
import com.aymane.android.ViewFragments.ReportFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TargetApi(23)
public class MainActivity extends MapListActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, AAH_FabulousFragment.AnimationListener, AAH_FabulousFragment.Callbacks {


    public Location location;
    public String lat;
    public GoogleApiClient googleApiClient;
    public LocationRequest locationRequest;
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 100;
    private long UPDATE_INTERVAL = 3000, FASTEST_INTERVAL = 3000;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1000;


    private ReportFragment reportDailog;
    FloatingActionButton fabReport;

    DatabaseReference reference;
    TextView text;
    RecyclerView recyclerView;
    LayoutAdapter mAdapter;
    MapLocationAdapter layoutAdapter;
    ArrayList<Report> list;
    ArrayList<MapActivity.NamedLocation> namedLocations;
    ArrayList<Report> reportsAswer;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView.setHasFixedSize(true);

        text = (TextView) findViewById(R.id.text);

        // Checking the Location Permissions:
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(permissionsToRequest.size() > 0){
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }


        // Building the Google Api Client:
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        fabReport = (FloatingActionButton) findViewById(R.id.fabreport);


        /* BottomNavigation Section */
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_home:
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;

                    case R.id.ic_messages:
                        Intent intent1 = new Intent(MainActivity.this, MessagingActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;

                    case R.id.ic_navigation:
                        Intent intent2 = new Intent(MainActivity.this, NavigationActivity.class);
                        startActivity(intent2);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;

                    case R.id.ic_notifications:
                        Intent intent4 = new Intent(MainActivity.this, NotificationActivity.class);
                        startActivity(intent4);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                }


                return false;
            }
        });
        /* BottomNavigation Section */
    }


    public Location setUpLocation(Location location){
        this.location = location;
        return this.location;
    }



    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions){

        ArrayList<String> result = new ArrayList<>();

        for(String perm : wantedPermissions){
            if(!hasPermission(perm)){
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String perm) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }


    @Override
    protected MapLocationAdapter createMapListAdapter() {

        /*ArrayList<MapLocation> mapLocations = new ArrayList<>(LIST_LOCATIONS.length);
        mapLocations.addAll(Arrays.asList(LIST_LOCATIONS));

        MapLocationAdapter adapter = new MapLocationAdapter();
        adapter.setMapLocations(mapLocations);

        return adapter;*/

        return null;
    }


    @Override
    public void onLocationChanged(Location location) {

        if(location != null){
            Log.d("Locations", Double.toString(location.getLongitude()));
            setUpLocation(location);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(googleApiClient != null){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if(!checkPlayServices()){
            Log.d("Services Google", "Install Google play Services");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stopping location Updates:
        if(googleApiClient != null && googleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if(result != ConnectionResult.SUCCESS){
            if(googleApiAvailability.isUserResolvableError(result)){
                googleApiAvailability.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST);
            }
            else{
                finish();
            }

            return false;
        }

        return true;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        // Permissions Ok, we can request Location:
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if(location != null){
            text.setText(location.getLongitude() + " ," + location.getLatitude());
            lat = Double.toString(location.getLatitude());

            reportDailog = ReportFragment.newInstance();
            reportDailog.setParentFab(fabReport);

            // Trigger the Report Animation when the FAB is clicked:
            fabReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportDailog.show(getSupportFragmentManager(), reportDailog.getTag());
                    //startActivity(new Intent(MainActivity.this, MapActivity.class));
                }
            });
        }
        
        startLocationUpdates();
    }

    private void startLocationUpdates() {

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            Log.d("Enable Permissions", "Enable Permisssions");
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){

            case ALL_PERMISSIONS_RESULT:
                for(String perm : permissionsToRequest){
                    if(!hasPermission(perm)){
                        permissionsRejected.add(perm);
                    }
                }

                if(permissionsRejected.size() > 0){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(!shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("You have Enable Location Permissions")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]),
                                                        ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", null).create().show();

                            return;

                        }
                    }
                }
                else
                {
                    if(googleApiClient != null){
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }

    /**
     * Faboulous Filter Animation Methods*/
    @Override
    public void showMapDetails(View view) {

    }


    @Override
    public void onOpenAnimationStart() {

    }

    @Override
    public void onOpenAnimationEnd() {

    }

    @Override
    public void onCloseAnimationStart() {

    }

    @Override
    public void onCloseAnimationEnd() {

    }

    @Override
    public void onResult(Object result) {

    }
}
