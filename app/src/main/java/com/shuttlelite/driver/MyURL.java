package com.shuttlelite.driver;

public class MyURL {

    public static final String CHECK_AUTH = BuildConfig.APP_SERVER_IP + "/users/employee/driver/auth";
    // public static final String SEND_MESSAGE = "/users/employee/driver/message/send";
    // public static final String GET_MESSAGE_LIST = "/message/list";
    public static final String SEND_MY_LOCATION = BuildConfig.APP_SERVER_IP + "/shuttle/location/send";
    public static final String GET_OCCUPANT_LIST = BuildConfig.APP_SERVER_IP + "/users/employee/driver/occupant/all";
    public static final String SEND_NOTIFICATION = BuildConfig.APP_SERVER_IP + "/shuttle/notification/send";
    public static final String SEND_DETAILS = BuildConfig.APP_SERVER_IP + "/shuttle/get_on_off_details/save";

}
