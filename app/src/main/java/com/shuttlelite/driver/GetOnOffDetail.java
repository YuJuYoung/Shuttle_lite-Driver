package com.shuttlelite.driver;

import org.json.JSONException;
import org.json.JSONObject;

public class GetOnOffDetail {

    private String occupantNumber;
    private String dateTime;
    private String onOrOff;

    public GetOnOffDetail(JSONObject json) throws JSONException {
        occupantNumber = json.getString("occupantNumber");
        dateTime = json.getString("dateTime");
        onOrOff = json.getString("OnOrOff");
    }

    public GetOnOffDetail(String occupantNumber, String dateTime, String onOrOff) {
        this.occupantNumber = occupantNumber;
        this.dateTime = dateTime;
        this.onOrOff = onOrOff;
    }

    public String getOccupantNumber() {
        return occupantNumber;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getOnOrOff() {
        return onOrOff;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("occupantNumber", occupantNumber);
        json.put("dateTime", dateTime);
        json.put("onOrOff", onOrOff);
        return json;
    }

}
