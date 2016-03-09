package fr.gerdevstudio.runningapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Helper class to save Trainings using shared preferences
 */
public class SharedPrefsManager {

    // key for shared prefs identification
    private final static String TRAININGPREF = "fr.gerdevstudio.RunningApp.Trainings";

    // key for accessing trainings information
    private final static String TRAININGS = "trainings";

    private Context mApplicationContext;
    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;

    // Constructor to get context from Activity

    public SharedPrefsManager(Context mContext) {
        this.mApplicationContext = mContext.getApplicationContext();
        mSharedPrefs = mApplicationContext.getSharedPreferences(TRAININGPREF, Context.MODE_PRIVATE);
        mEditor = mSharedPrefs.edit();
    }

    // public accessor
    public void addTraining(Training t) {
        TrainingList trainingList = getTrainings();
        trainingList.addTraining(t);
        saveTrainings(trainingList);
    }

    public void removeTraining(int position) {
        TrainingList trainingList = getTrainings();
        trainingList.removeTrainingAt(position);
        saveTrainings(trainingList);
    }

    // private methods to manage saving and loading of trainings list

    public TrainingList getTrainings() {
        String s = mSharedPrefs.getString(TRAININGS, "");

        Gson gson = new GsonBuilder().create();
        TrainingList trainings = gson.fromJson(s, TrainingList.class);

        // checking nullpointer exeption that happens when no training is registered
        if (trainings == null) {
            return new TrainingList();
        } else {
            return trainings;
        }
    }

    private void saveTrainings(TrainingList trainings) {
        Gson gson = new GsonBuilder().create();
        String s = gson.toJson(trainings, TrainingList.class);
        mEditor.putString(TRAININGS, s);
        mEditor.commit();
    }
}
