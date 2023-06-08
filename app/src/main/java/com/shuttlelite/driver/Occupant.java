package com.shuttlelite.driver;

import org.json.JSONException;
import org.json.JSONObject;

public class Occupant {

    // 원번
    private String number;

    // 이름
    private String name;

    // 탑승 장소 이름
    private String boardingPlaceName;

    // 보호자 전화번호
    private String protectorPhoneNumber;

    // 탑승 장소 위도, 경도
    private double latitude, longitude;

    // 보호자 이름
    private String protectorName;

    private boolean isBoarding = false;

    public Occupant(JSONObject data) throws JSONException {
        number = data.getString("number");
        name = data.getString("name");
        boardingPlaceName = data.getString("boarding_place_name");
        protectorPhoneNumber = data.getString("protector_phone_number");
        latitude = data.getDouble("latitude");
        longitude = data.getDouble("longitude");
        protectorName = data.getString("protector_name");
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getBoardingPlaceName() {
        return boardingPlaceName;
    }

    public String getProtectorPhoneNumber() {
        return protectorPhoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getProtectorName() {
        return protectorName;
    }

    public void setBoarding(boolean isBoarding) {
        this.isBoarding = isBoarding;
    }

    public boolean isBoarding() {
        return isBoarding;
    }

    @Override
    public boolean equals(Object obj) {
        return number.equals(((Occupant) obj).getNumber());
    }
}
