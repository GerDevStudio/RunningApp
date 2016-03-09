package fr.gerdevstudio.runningapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contains elements of a training
 */
public class Training implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Training> CREATOR = new Parcelable.Creator<Training>() {
        @Override
        public Training createFromParcel(Parcel in) {
            return new Training(in);
        }

        @Override
        public Training[] newArray(int size) {
            return new Training[size];
        }
    };
    private Date mDate;
    private long mDuree;
    private float mDistance;
    private float mVitesse;
    private ArrayList<LatLng> mCoords = new ArrayList<>();


    /**
     * Constructors
     */

    public Training(String date, long duree, float distance, float vitesse, List<LatLng> coords) {
        Date d = DateUtils.StringToDate(date);
        this.mDate = d;
        this.mDuree = duree;
        this.mDistance = distance;
        this.mVitesse = vitesse;
        if (coords != null) {
            mCoords.addAll(coords);
        }
    }

    protected Training(Parcel in) {
        long tmpMDate = in.readLong();
        mDate = tmpMDate != -1 ? new Date(tmpMDate) : null;
        mDuree = in.readLong();
        mDistance = in.readFloat();
        mVitesse = in.readFloat();
        if (in.readByte() == 0x01) {
            mCoords = new ArrayList<LatLng>();
            in.readList(mCoords, LatLng.class.getClassLoader());
        } else {
            mCoords = null;
        }
    }

    /**
     * Getters and Setters
     */
    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public long getDuree() {
        return mDuree;
    }

    public void setDuree(long mDuree) {
        this.mDuree = mDuree;
    }

    public float getDistance() {
        return mDistance;
    }

    public void setDistance(float mDistance) {
        this.mDistance = mDistance;
    }

    public float getVitesse() {
        return mVitesse;
    }

    public void setVitesse(float mVitesse) {
        this.mVitesse = mVitesse;
    }

    public ArrayList<LatLng> getCoords() {
        return mCoords;
    }

    public void setCoords(List<LatLng> coords) {
        mCoords.clear();
        mCoords.addAll(coords);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mDate != null ? mDate.getTime() : -1L);
        dest.writeLong(mDuree);
        dest.writeFloat(mDistance);
        dest.writeFloat(mVitesse);
        if (mCoords == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mCoords);
        }
    }
}