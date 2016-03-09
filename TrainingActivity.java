package fr.gerdevstudio.runningapp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// @todo mettre en place les fragments au lieu de l'activity
public class TrainingActivity extends AppCompatActivity implements ListenerService.CallBacks, OnMapReadyCallback {

    // saved instance state key, explaining if the training is running or not.
    private final static String STATE_RUNNING = "running";
    private final static String STATE_DATE = "date";
    private final static String STATE_DUREE = "duree";
    private final static String STATE_DISTANCE = "distance";
    private final static String STATE_VITESSE = "vitesse";
    private final static String STATE_VITESSE_MOY = "vitesse_moy";
    private final static String STATE_COORDINATES_LIST = "coordinates";

    private FloatingActionButton fab;
    private TextView mVitesseTv, mVitesseMoyTv, mDureeTv, mDistanceTv;

    private String mDate;
    private long mDuree;
    private float mDistance;
    private float mVitesse;
    private float mVitesseMoy;
    private boolean mTrainingRunning = false;
    private ArrayList<LatLng> mCoords = new ArrayList<>();

    private ListenerService mService;
    private Intent mIntentService;
    private Location mLocation = new Location(LocationManager.GPS_PROVIDER);
    private FrameLayout mMapContainer;
    private MapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ListenerService.LocalBinder binder = (ListenerService.LocalBinder) service;
            mService = binder.getServiceInstance();
            mService.registerClient(TrainingActivity.this);
            Toast.makeText(TrainingActivity.this, "Service connected", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(TrainingActivity.this, "Service disconnected", Toast.LENGTH_LONG).show();
            mService.unbindService(mServiceConnection);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_training);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // init text views
        mDureeTv = (TextView) findViewById(R.id.duree);
        mDistanceTv = (TextView) findViewById(R.id.distance);
        mVitesseTv = (TextView) findViewById(R.id.vitesse);
        mVitesseMoyTv = (TextView) findViewById(R.id.vitesse_moy);

        // masquage de la map qui sera montrée une fois chargée
        mMapContainer = (FrameLayout) findViewById(R.id.map_container);
        mMapContainer.setVisibility(View.INVISIBLE);

        // initialisation de la map
        mMapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mMapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mTrainingRunning) {
                    mService.stop();
                    mTrainingRunning = false;
                    // fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_play));

                    launchResultActivity();
                } else {
                    mTrainingRunning = true;
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_pause));
                    // notify Service to start chrono;
                    mIntentService.putExtra(ListenerService.START_SESSION, true);
                    startService(mIntentService);

                    // reseting the map
                    if (mGoogleMap != null) {
                        mGoogleMap.clear();
                    }

                    // initializing Date of training
                    Date date = DateUtils.getDate();
                    mDate = DateUtils.dateToString(date);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // reload members variables and actualize fab state.
        if (savedInstanceState != null) {
            mTrainingRunning = savedInstanceState.getBoolean(STATE_RUNNING, false);
            if (mTrainingRunning) {
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_pause));
            } else {
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_play));
            }

            mDate = savedInstanceState.getString(STATE_DATE);
            mDuree = savedInstanceState.getLong(STATE_DUREE);
            mDistance = savedInstanceState.getFloat(STATE_DISTANCE);
            mVitesseMoy = savedInstanceState.getFloat(STATE_VITESSE_MOY);
            mVitesse = savedInstanceState.getFloat(STATE_VITESSE);

            if (savedInstanceState.getParcelableArrayList(STATE_COORDINATES_LIST) != null) {
                mCoords = savedInstanceState.getParcelableArrayList(STATE_COORDINATES_LIST);
            }
        }
        // if gps is disabled, dialogbox appears to activate it.
        checkGpsStatus();
    }

    @Override
    public void updateDuree(long l) {
        mDuree = l;
        String duree = StringUtils.timeToString(l);
        mDureeTv.setText(duree);
    }

    @Override
    public void updatePos(Location location) {
        if (location != null) {
            mLocation = location;
            mCoords.add(new LatLng(location.getLatitude(), location.getLongitude()));

            if (mGoogleMap != null) {
                // move camera to position
                if (!mCoords.isEmpty()) {
                    Log.d("Training Activity", "Accurancy : " + location.getAccuracy());
                    LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());

                    CameraPosition camPos = new CameraPosition(pos, 16.7F, 0, 0);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), 900, null);
                }
            }
        }
    }

    @Override
    public void drawStroke(Location l1, Location l2) {
        if (mGoogleMap != null) {
            MapUtils.drawLine(mGoogleMap, l1, l2);
        }
    }

    @Override
    public void getAllPos(List<Location> pos) {
        mCoords.clear();
        for (Location l : pos) {
            mCoords.add(new LatLng(l.getLatitude(), l.getLongitude()))
            ;
        }
    }

    @Override
    public void updateDistance(float distance) {
        mDistance = distance;

        String text = StringUtils.distanceToString(distance);
        mDistanceTv.setText(text);
    }

    @Override
    public void updateVitesse(float vitesse) {
        mVitesse = vitesse;
        String text = StringUtils.speedToString(mVitesse);
        mVitesseTv.setText(text);
    }

    @Override
    public void updateVitesseMoy(float vitesseMoy) {
        mVitesseMoy = vitesseMoy;
        String text = StringUtils.speedToString(mVitesseMoy);
        mVitesseMoyTv.setText(text);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.mGoogleMap = map;

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        this.mGoogleMap.setMyLocationEnabled(true);

        // on centre la map sur les derniers coordonnées gps connues
        GpsListener gps = new GpsListener(this);
        Location loc = gps.getLastLocation();

        if (loc != null) {
            LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());

            CameraPosition camPos = new CameraPosition(pos, 16.7F, 0, 0);
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), 900, null);
        }

        // on redessine les polygones si il y a au moins 2 coordonnées en mémoire
        if (!mCoords.isEmpty() && mCoords.size() > 1) {
            for (int i = 1; i < mCoords.size(); i++) {
                LatLng latlng1 = mCoords.get(i);
                LatLng latlng2 = mCoords.get(i - 1);
                MapUtils.drawLine(mGoogleMap, latlng1, latlng2);
            }
        }

        // start animation on Map Container
        UiUtils.appear(mMapContainer, 1500);
    }

    private void launchResultActivity() {
        // start result activity
        Intent i = new Intent(this, ResultActivity.class);

        i.putExtra(ResultActivity.EXTRA_DISTANCE, mDistance);
        i.putExtra(ResultActivity.EXTRA_DUREE, mDuree);
        i.putExtra(ResultActivity.EXTRA_VITESSE, mVitesseMoy);
        i.putExtra(ResultActivity.EXTRA_DATE, mDate);
        i.putParcelableArrayListExtra(ResultActivity.EXTRA_COORDINATES_LIST, mCoords);

        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void checkGpsStatus() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            alertDialogBuilder
                    .setMessage("Le GPS est désactivé. Voulez-vous l'activer?")
                    .setCancelable(false)
                    .setPositiveButton("Activer",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("Annuler",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    @Override // pour sauvegarder l'état des boutons et de la carte
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RUNNING, mTrainingRunning);
        outState.putString(STATE_DATE, mDate);
        outState.putLong(STATE_DUREE, mDuree);
        outState.putFloat(STATE_DISTANCE, mDistance);
        outState.putFloat(STATE_VITESSE, mVitesseMoy);
        outState.putFloat(STATE_VITESSE_MOY, mVitesseMoy);

        outState.putParcelableArrayList(STATE_COORDINATES_LIST, mCoords);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // creating service
        mIntentService = new Intent(TrainingActivity.this, ListenerService.class);
        mIntentService.putExtra(ListenerService.GET_ALL_POS, true);
        startService(mIntentService);
        bindService(mIntentService, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        unbindService(mServiceConnection);
        super.onStop();
    }
}
