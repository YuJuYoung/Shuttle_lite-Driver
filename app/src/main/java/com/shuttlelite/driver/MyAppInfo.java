package com.shuttlelite.driver;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyAppInfo {

    private static MyAppInfo myAppInfo = new MyAppInfo();

    private String employeeNumber = null;
    private List<Occupant> occupants;
    private ShuttleStatus shuttleStatus;
    private GetOnOffDetails getOnOffDetails;

    private MyAppInfo() {
        shuttleStatus = new ShuttleStatus();
        getOnOffDetails = new GetOnOffDetails();
        occupants = new ArrayList<>();
    }

    public static MyAppInfo getInstance() {
        return myAppInfo;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public ShuttleStatus getShuttleStatus() { return shuttleStatus; }

    public List<Occupant> getOccupants() {
        return occupants;
    }

    public GetOnOffDetails getGetOnOffDetails() {
        return getOnOffDetails;
    }

    public boolean initOccupants(Context context) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("driverNumber", employeeNumber);

            RequestHTTPURLConnection requestHTTPURLConnection =
                    new RequestHTTPURLConnection(MyURL.GET_OCCUPANT_LIST, requestBody);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    requestHTTPURLConnection.request();
                }
            });

            thread.start();
            thread.join(8000);

            JSONObject responseBody = requestHTTPURLConnection.getResponseBody();

            if (responseBody != null) {
                JSONArray jsonArr = responseBody.getJSONArray("occupants");

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    Occupant occupant = new Occupant(jsonObj);

                    if (shuttleStatus.getStatus() != ShuttleStatus.STOP) {
                        String detail = getOnOffDetails.getDetail(occupant.getNumber());

                        /*if (detail != null) {
                            if (shuttleStatus.getStatus() == ShuttleStatus.GOING_HOME) {
                                if (detail.equals(GetOnOffDetails.GET_OFF)) {
                                    continue;
                                }
                            } else {
                                if (detail.equals(GetOnOffDetails.GET_ON)) {
                                    continue;
                                }
                            }
                        } else {
                            continue;
                        }*/

                        if (detail != null && detail.equals(GetOnOffDetails.GET_ON)) {
                            occupant.setBoarding(true);
                        }
                    }
                    occupants.add(occupant);
                }
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendDetails() {
        getOnOffDetails.sendDetails();
    }

    public void reset() {
        shuttleStatus.reset();
        getOnOffDetails.reset();
    }
}
