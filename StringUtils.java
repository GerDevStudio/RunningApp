package fr.gerdevstudio.runningapp;

/**
 * Created by NasTV on 04/01/2016.
 */
public class StringUtils {

    /**
     * converts timing in millis to a String
     *
     * @param duree in mS
     * @return 01:02:56
     */
    public static String timeToString(Long duree) {
        int seconds = (int) (duree / 1000) % 60;
        int minutes = (int) ((duree / (1000 * 60)) % 60);
        int hours = (int) ((duree / (1000 * 60 * 60)) % 24);

        String result = "" + (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
        return result;
    }

    /**
     * converts distance in meters to String
     *
     * @param distance in m
     * @return 1, 23 km
     */
    public static String distanceToString(float distance) {
        String result = String.format("%.2f", distance / 1000F) + " km";
        return result;
    }

    public static String speedToString(float speed) {
        String result = String.format("%.2f", speed) + " km/h";
        return result;
    }


}
