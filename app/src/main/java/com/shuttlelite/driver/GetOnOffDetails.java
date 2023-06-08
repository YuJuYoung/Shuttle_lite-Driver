package com.shuttlelite.driver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetOnOffDetails {

    public static final String GET_ON = "on", GET_OFF = "off";

    private static final String DIRECTORY_PATH = "/data/data/com.shuttlelite.driver/files";
    private static final String GET_ON_OFF_DETAILS_FILE_NAME = "get_on_off_details.json";

    private List<GetOnOffDetail> details = new ArrayList<>();

    public GetOnOffDetails() {
        readFile();
    }

    public List<GetOnOffDetail> getDetails() {
        return details;
    }

    public boolean isEmpty() {
        return details.isEmpty();
    }

    public void readFile() {
        File file = new File(DIRECTORY_PATH, GET_ON_OFF_DETAILS_FILE_NAME);

        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();

                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(line);
                }
                br.close();

                JSONArray arr = new JSONArray(sb.toString());

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject json = arr.getJSONObject(i);

                    GetOnOffDetail detail = new GetOnOffDetail(json);
                    details.add(detail);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean writeFile(JSONObject json) {
        File file = new File(DIRECTORY_PATH, GET_ON_OFF_DETAILS_FILE_NAME);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line);
            }
            String fileText = sb.toString();

            JSONArray arr = fileText.equals("") ? new JSONArray() : new JSONArray(fileText);
            arr.put(json);

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(arr.toString());
            br.close();
            bw.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void writeDetail(String occupantNumber, String dateTime, String GetOnOff) {
        GetOnOffDetail detail = new GetOnOffDetail(occupantNumber, dateTime, GetOnOff);

        try {
            writeFile(detail.toJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        File file = new File(DIRECTORY_PATH, GET_ON_OFF_DETAILS_FILE_NAME);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write("");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDetail(String occupantNumber) {
        for (int i = details.size() - 1; i >= 0; i--) {
            GetOnOffDetail detail = details.get(i);

            if (detail.getOccupantNumber().equals(occupantNumber)) {
                if (detail.getOnOrOff().equals(GET_ON)) {
                    return GET_ON;
                } else {
                    return GET_OFF;
                }
            }
        }
        return null;
    }

    public void sendDetails() {
        File file = new File(DIRECTORY_PATH, GET_ON_OFF_DETAILS_FILE_NAME);

        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();

                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(line);
                }
                br.close();

                JSONArray arr = new JSONArray(sb.toString());
                JSONObject requestBody = new JSONObject();
                requestBody.put("details", arr);

                RequestHTTPURLConnection requestHTTPURLConnection
                        = new RequestHTTPURLConnection(MyURL.SEND_DETAILS, requestBody);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        requestHTTPURLConnection.request();
                    }
                }).start();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
