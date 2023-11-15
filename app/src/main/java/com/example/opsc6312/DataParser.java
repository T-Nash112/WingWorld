package com.example.opsc6312;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        try {
            JSONArray jRoutes = jObject.getJSONArray("routes");

            for (int i = 0; i < jRoutes.length(); i++) {
                JSONArray jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<>();

                for (int j = 0; j < jLegs.length(); j++) {
                    JSONArray jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = (String) ((JSONObject) jSteps.get(k)).getJSONObject("polyline").get("points");
                        List<LatLng> list = decodePoly(polyline);

                        for (LatLng latLng : list) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString(latLng.latitude));
                            hm.put("lng", Double.toString(latLng.longitude));
                            path.add(hm);
                        }
                    }
                }
                routes.add(path);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle the exception gracefully, e.g., return an empty list.
            return new ArrayList<>();
        }
        return routes;
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0;
        int len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng point = new LatLng(lat / 1E5, lng / 1E5);
            poly.add(point);
        }
        return poly;
    }

    public PolylineOptions parseToPolyline(JSONObject jsonObject) {
        try {
            JSONArray routes = jsonObject.getJSONArray("routes");
            if (routes.length() > 0) {
                List<LatLng> points = new ArrayList<>();

                JSONArray legs = ((JSONObject) routes.get(0)).getJSONArray("legs");
                JSONArray steps = ((JSONObject) legs.get(0)).getJSONArray("steps");

                for (int i = 0; i < steps.length(); i++) {
                    JSONObject step = (JSONObject) steps.get(i);
                    JSONObject polyline = step.getJSONObject("polyline");
                    String encodedString = polyline.getString("points");

                    List<LatLng> decodedPath = decodePoly(encodedString);
                    points.addAll(decodedPath);
                }

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(points);
                polylineOptions.width(8f);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);

                return polylineOptions;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception gracefully, e.g., return null.
        }
        return null;
    }
}