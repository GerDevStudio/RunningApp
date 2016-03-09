package fr.gerdevstudio.runningapp;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by NasTV on 18/01/2016.
 */
public class MapUtils {

    public static void drawLine(GoogleMap map, Location l1, Location l2) {
        LatLng latlng1 = new LatLng(l1.getLatitude(), l1.getLongitude());
        LatLng latlng2 = new LatLng(l2.getLatitude(), l2.getLongitude());
        drawLine(map, latlng1, latlng2);
    }

    public static void drawLine(GoogleMap map, LatLng latlng1, LatLng latlng2) {
        if (map != null) {
            map.addPolyline(new PolylineOptions()
                    .add(latlng1, latlng2)
                    .width(15)
                    .color(Color.BLUE));
        }
    }

    // on redessine les polygones si il y a au moins 2 coordonnées en mémoire
    public static void drawTrack(GoogleMap map, ArrayList<LatLng> coords) {

        if (!coords.isEmpty() && coords.size() > 1)

        {
            for (int i = 1; i < coords.size(); i++) {
                LatLng latlng1 = coords.get(i);
                LatLng latlng2 = coords.get(i - 1);
                drawLine(map, latlng1, latlng2);
            }
        }
    }
}
