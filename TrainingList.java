package fr.gerdevstudio.runningapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * List of Trainings
 */
public class TrainingList implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TrainingList> CREATOR = new Parcelable.Creator<TrainingList>() {
        @Override
        public TrainingList createFromParcel(Parcel in) {
            return new TrainingList(in);
        }

        @Override
        public TrainingList[] newArray(int size) {
            return new TrainingList[size];
        }
    };
    private List<Training> mTrainingList;

    /**
     * Constructors
     */

    public TrainingList() {
        this.mTrainingList = new ArrayList<>();
    }

    /**
     * Parcelable Interface implementation
     */
    protected TrainingList(Parcel in) {
        if (in.readByte() == 0x01) {
            mTrainingList = new ArrayList<Training>();
            in.readList(mTrainingList, Training.class.getClassLoader());
        } else {
            mTrainingList = null;
        }
    }

    /**
     * Getters and Setters
     */
    public List<Training> getTrainings() {
        if (mTrainingList.isEmpty()) {
            return new ArrayList<>();
        } else {
            return mTrainingList;
        }
    }

    public void removeTrainingAt(int position)

    {
        if (position > -1 && mTrainingList.size() > position) {
            this.mTrainingList.remove(position);
        }
    }

    public void addTraining(Training t) {
        this.mTrainingList.add(t);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mTrainingList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mTrainingList);
        }
    }
}