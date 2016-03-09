package fr.gerdevstudio.runningapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity implements OnMapReadyCallback

{

    public final static String EXTRA_DUREE = "duree";
    public final static String EXTRA_DISTANCE = "distance";
    public final static String EXTRA_VITESSE = "vitesse";
    public final static String EXTRA_DATE = "date";
    public final static String EXTRA_SAVE_TRAINING = "saveTraining";
    public final static String EXTRA_COORDINATES_LIST = "coordinatesList";
    public final static String EXTRA_TRAINING_POSITION = "trainingPosition";

    private TextView mDuree;
    private TextView mVitesse;
    private TextView mDistance;
    private TextView mDate;
    private ImageButton mDeleteButton;
    private FloatingActionButton mFab;
    private ArrayList<LatLng> mCoords = new ArrayList<>();
    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;
    private FrameLayout mMapContainer;

    // current Training
    private Training mTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init views and widgets
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        // hide delete button
        mDeleteButton = (ImageButton) findViewById(R.id.button_delete);
        mDeleteButton.setVisibility(View.INVISIBLE);

        // init text views
        mDuree = (TextView) findViewById(R.id.duree);
        mDistance = (TextView) findViewById(R.id.distance);
        mVitesse = (TextView) findViewById(R.id.vitesse);
        mDate = (TextView) findViewById(R.id.date);

        // init values
        Bundle b = getIntent().getExtras();
        if (b != null) {
            // init value of duree TextView
            Long duree = b.getLong(EXTRA_DUREE);
            String dureeText = StringUtils.timeToString(duree);
            mDuree.setText(dureeText);

            // init value of vitesse TextView
            Float vitesse = b.getFloat(EXTRA_VITESSE);
            String vitesseText = StringUtils.speedToString(vitesse);
            mVitesse.setText(vitesseText);

            // init value of distance TextView
            Float distance = b.getFloat(EXTRA_DISTANCE);
            String distanceText = StringUtils.distanceToString(distance);
            mDistance.setText(distanceText);

            // init value of Date TextView
            String date = b.getString(EXTRA_DATE);
            mDate.setText(date);


            if (b.getParcelableArrayList(EXTRA_COORDINATES_LIST) != null) {
                mCoords = b.getParcelableArrayList(EXTRA_COORDINATES_LIST);
            }

            // create mTraining
            mTraining = new Training(date, duree, distance, vitesse, mCoords);

            // saving mTraining if asked
            if (b.getBoolean(EXTRA_SAVE_TRAINING, true)) {
                SharedPrefsManager prefs = new SharedPrefsManager(this);
                prefs.addTraining(mTraining);

                // remove extra
                getIntent().putExtra(EXTRA_SAVE_TRAINING, false);
            }

            final int trainingPos = b.getInt(EXTRA_TRAINING_POSITION, -1);
            if (trainingPos > -1) {
                mDeleteButton.setVisibility(View.VISIBLE);
                mDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPrefsManager prefs = new SharedPrefsManager(ResultActivity.this);
                        prefs.removeTraining(trainingPos);
                        Toast.makeText(ResultActivity.this, "Entraînement supprimé", Toast.LENGTH_SHORT).show();
                        backToMainActivity();
                    }
                });
            }
        }

        // init fab
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        View.OnClickListener fabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainActivity();
            }
        };

        mFab.setOnClickListener(fabListener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // initialisation du container de la map
        mMapContainer = (FrameLayout) findViewById(R.id.map_container);
        mMapContainer.setVisibility(View.INVISIBLE);

        // initialisation de la map
        mMapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mMapFragment.getMapAsync(this);
    }

    private void backToMainActivity() {
        // this flags permits to have mainactivity at the bottom of the stack.
        final Intent i = new Intent(ResultActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        UiUtils.hideFab(mFab);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 300);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button to use activity shared elements transition
            case android.R.id.home:
                backToMainActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        if (!mCoords.isEmpty()) {
            // draw track
            MapUtils.drawTrack(googleMap, mCoords);

            //center the polygon drawed on map
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : mCoords) {
                builder.include(point);
            }

            LatLngBounds bounds = builder.build();
            int padding = 50;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mGoogleMap.moveCamera(cu);
        }

        // start animation on Map Container
        UiUtils.appear(mMapContainer, 1500);
    }

    @Override
    public void onBackPressed() {
        backToMainActivity();
    }
}
