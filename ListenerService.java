package fr.gerdevstudio.runningapp;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by NasTV on 08/11/2015.
 */
public class ListenerService extends Service {
    public final static String START_SESSION = "StartChrono";
    public final static String GET_ALL_POS = "GetAllPos";
    public final static int SECOND = 1000;
    private final static String TAG = "ListenerService";
    private Long mStartTime = 0L;
    private Long mTime = 0L;

    private GpsListener mGps;
    private List<Location> mCoords = new LinkedList<>();
    private Location mLocation = new Location(LocationManager.GPS_PROVIDER);
    private long mDuree = 0L;
    private float mDistance = 0F;
    private float mVitesse = 0F;
    private float mVitesseMoy = 0F;

    private Binder mBinder = new LocalBinder();
    private Handler mHandler = new Handler();
    private CallBacks mActivity;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // update duree
            mTime = System.currentTimeMillis();
            mDuree = mTime - mStartTime;

            // receive gps pos
            mLocation = mGps.getLastLocation();

            // add it in coords if accurancy better then 25 meters
            if (mLocation != null && mLocation.getAccuracy() < 25) {
                mCoords.add(mLocation);
                Log.d(TAG, "Latitude : " + Double.toString(mLocation.getLatitude()));
                Log.d(TAG, "Longitude : " + Double.toString(mLocation.getLongitude()));
            }

            // update distance, vitesse instantanée, vitesse moyenne
            int lastIndexCoords = mCoords.size() - 1;
            if (lastIndexCoords > 0) {
                Location loc1 = mCoords.get(lastIndexCoords);
                Location loc2 = mCoords.get(lastIndexCoords - 1);

                float deltaDistance = loc1.distanceTo(loc2);

                mDistance = mDistance + deltaDistance;

                // converting speed from m/ms to km/h for vitesse moyenne
                mVitesseMoy = 3.6F * 1000 * mDistance / mDuree;

                // calculating vitesse instantanée from last 3 positions if possible
                if (lastIndexCoords > 2) {
                    // There are more than 3 coordinates, we take last 3 seconds for calculating vitesse instantanée
                    loc2 = mCoords.get(lastIndexCoords - 3);
                    deltaDistance = loc1.distanceTo(loc2);
                    mVitesse = 3.6F * 1000 * deltaDistance / (3 * SECOND);
                }
            }

            // activity callbacks
            if (mActivity != null) {
                mActivity.updateDuree(mDuree);
                mActivity.updatePos(mLocation);

                if (lastIndexCoords > 0) {
                    Location loc1 = mCoords.get(lastIndexCoords);
                    Location loc2 = mCoords.get(lastIndexCoords - 1);

                    mActivity.updateDistance(mDistance);
                    mActivity.drawStroke(loc1, loc2);

                    mActivity.updateVitesseMoy(mVitesseMoy);
                    mActivity.updateVitesse(mVitesse);
                }
            }
            mHandler.postDelayed(mRunnable, SECOND);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "in OnBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mActivity = null;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {    // on vérifie que l'intent n'est pas nul pour éviter une null pointer exception si le service est START_STICKY

        if (intent != null) {
            if (intent.getBooleanExtra(START_SESSION, false)) {
                mStartTime = System.currentTimeMillis();
                mDistance = 0;
                start();
            } else if (intent.getBooleanExtra(GET_ALL_POS, false)) {
                if (!mCoords.isEmpty() & mActivity != null) {
                    mActivity.getAllPos(mCoords);
                }
            }
        }
        mGps = new GpsListener(getApplicationContext());

        return super.onStartCommand(intent, flags, startId);
    }

    public void start() {
        Log.d(TAG, "Service started.");

        mHandler.removeCallbacks(mRunnable);
        mHandler.post(mRunnable);
    }

    public void stop() {
        Log.d(TAG, "Service stopped.");
        // remove future updates
        mHandler.removeCallbacks(mRunnable);

        mGps.stopUsingGPS();

        stopSelf();
    }

    public void registerClient(CallBacks context) {
        mActivity = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    public interface CallBacks {

        void updateDuree(long duree);

        void updateDistance(float distance);

        void updatePos(Location location);

        void updateVitesse(float vitesse);

        void updateVitesseMoy(float vitesseMoy);

        void getAllPos(List<Location> pos);

        void drawStroke(Location l1, Location l2);
    }

    public class LocalBinder extends Binder {

        public ListenerService getServiceInstance() {
            return ListenerService.this;
        }
    }
}


