package com.shuttlelite.driver;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestHTTPURLConnection {

    private String _url;
    private JSONObject requestBody;
    private JSONObject responseBody = null;

    public RequestHTTPURLConnection(String _url, JSONObject body) {
        this._url = _url;
        requestBody = body;
    }

    public JSONObject getResponseBody() {
        return responseBody;
    }

    public void request() {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(_url);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(8000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();
            os.close();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();

                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(line);
                }
                br.close();
                inputStreamReader.close();
                is.close();

                responseBody = new JSONObject(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

}
