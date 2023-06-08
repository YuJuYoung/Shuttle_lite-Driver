package com.shuttlelite.driver;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ChoiceActivity extends AppCompatActivity {

    private MyAppInfo myAppInfo = MyAppInfo.getInstance();

    private ShuttleStatus shuttleStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        shuttleStatus = myAppInfo.getShuttleStatus();

        findViewById(R.id.go_house_btn).setOnClickListener(view -> {
            Intent newIntent = new Intent(this, OccupantListActivity.class);
            DialogFactory.showStartDialog(this, R.id.go_house_btn, newIntent);
        });

        findViewById(R.id.go_school_btn).setOnClickListener(view -> {
            Intent newIntent = new Intent(this, MapsActivity.class);
            DialogFactory.showStartDialog(this, R.id.go_school_btn, newIntent);
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }
}
