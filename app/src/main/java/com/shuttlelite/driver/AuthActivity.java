package com.shuttlelite.driver;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {

    private static final String LOGIN_FAILED = "인증 정보 찾을 수 없음";
    private static final String DOING_LOGIN = "로그인 시도 중입니다";
    private static final String FAIL_LOAD_OCCUPANTS = "탑승자 목록 로드 실패";
    private static final String OCCUPANTS_EMPTY = "배정된 탑승자가 없음";

    private MyAppInfo myAppInfo = MyAppInfo.getInstance();
    private boolean loginEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        TextView login_failed_text = findViewById(R.id.login_failed_text);

        findViewById(R.id.login_btn).setOnClickListener(view -> {
            login_failed_text.setText(DOING_LOGIN);

            if (loginEnable) {
                loginEnable = false;

                String email =
                        ((TextView) findViewById(R.id.email)).getText().toString();
                String employeeNumber =
                        ((TextView) findViewById(R.id.employee_number)).getText().toString();

                String result = checkAuth(email, employeeNumber);

                if (result.equals(ResultMessage.SUCCESS)) {
                    myAppInfo.setEmployeeNumber(employeeNumber);

                    if (myAppInfo.initOccupants(this)) {
                        if (!myAppInfo.getOccupants().isEmpty()) {
                            startNextActivity();
                        } else {

                            login_failed_text.setText(OCCUPANTS_EMPTY);
                        }
                    } else {
                        login_failed_text.setText(FAIL_LOAD_OCCUPANTS);
                    }
                } else {
                    login_failed_text.setText(result);
                }
                loginEnable = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }

    public void startNextActivity() {
        ShuttleStatus shuttleStatus = myAppInfo.getShuttleStatus();
        Intent newIntent;

        if (shuttleStatus.getStatus() == ShuttleStatus.STOP) {
            newIntent = new Intent(this, ChoiceActivity.class);
            startActivity(newIntent);
        } else {
            newIntent = new Intent(this, MapsActivity.class);
            startActivity(newIntent);
        }
        startActivity(newIntent);
    }

    private String checkAuth(String email, String employeeNumber) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("email", email);
            requestBody.put("employeeNumber", employeeNumber);

            RequestHTTPURLConnection requestHTTPURLConnection
                    = new RequestHTTPURLConnection(MyURL.CHECK_AUTH, requestBody);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    requestHTTPURLConnection.request();
                }
            });
            thread.start();

            try {
                thread.join(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject responseBody = requestHTTPURLConnection.getResponseBody();

            if (responseBody != null) {
                boolean result = responseBody.getBoolean("result");
                return result ? ResultMessage.SUCCESS : LOGIN_FAILED;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ResultMessage.ERROR;
    }

}
