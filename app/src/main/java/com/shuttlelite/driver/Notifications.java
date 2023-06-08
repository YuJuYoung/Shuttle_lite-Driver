package com.shuttlelite.driver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Notifications {

    public static void startOperation() {
        MyAppInfo myAppInfo = MyAppInfo.getInstance();
        ShuttleStatus shuttleStatus = myAppInfo.getShuttleStatus();

        List<String> occupantNumbers = new ArrayList<>();

        for (Occupant occupant : myAppInfo.getOccupants()) {
            if (shuttleStatus.getStatus() == ShuttleStatus.GOING_HOME && !occupant.isBoarding()) {
                continue;
            }
            occupantNumbers.add(occupant.getNumber());
        }

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("occupantNumbers", new JSONArray(occupantNumbers));

            RequestHTTPURLConnection requestHTTPURLConnection
                    = new RequestHTTPURLConnection(MyURL.SEND_NOTIFICATION, requestBody);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    requestHTTPURLConnection.request();
                }
            }).start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void finishOperation() {

    }

}
