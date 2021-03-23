package com.alexey_freelancee.delivery.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;
import java.util.Locale;

public class SortPlaces implements Comparator<LatLng> {
    LatLng currentLoc;

    public SortPlaces(LatLng current){
        currentLoc = current;
    }
    @Override
    public int compare(final LatLng place1, final LatLng place2) {
        double lat1 = place1.latitude;
        double lon1 = place1.longitude;
        double lat2 = place2.latitude;
        double lon2 = place2.longitude;

        double distanceToPlace1 = distance(currentLoc.latitude, currentLoc.longitude, lat1, lon1);
        double distanceToPlace2 = distance(currentLoc.latitude, currentLoc.longitude, lat2, lon2);
        return (int) (distanceToPlace1 - distanceToPlace2);
    }

    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, *in meters*
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }
}
