package com.shuttlelite.driver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ShuttleStatus {

    private static final String DIRECTORY_PATH = "/data/data/com.shuttlelite.driver/files";
    private static final String SHUTTLE_STATUS_FILE_NAME = "shuttle_status.json";

    public static final int GOING_SCHOOL = 0;
    public static final int GOING_HOME = 1;
    public static final int STOP = 2;

    private int status = STOP;

    public ShuttleStatus() {
        readStatus();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        writeStatus(status);
    }

    public void readStatus() {
        File file = new File(DIRECTORY_PATH, SHUTTLE_STATUS_FILE_NAME);

        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();

                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(line);
                }
                br.close();

                JSONObject json = new JSONObject(sb.toString());
                status = json.getInt("status");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean writeStatus(int status) {
        if (status > 2) {
            return false;
        }
        File file = new File(DIRECTORY_PATH, SHUTTLE_STATUS_FILE_NAME);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            JSONObject json = new JSONObject();
            json.put("status", status);

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(json.toString());
            bw.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void reset() {
        status = STOP;

        File file = new File(DIRECTORY_PATH, SHUTTLE_STATUS_FILE_NAME);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write("");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
