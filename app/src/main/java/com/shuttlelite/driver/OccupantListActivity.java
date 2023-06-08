package com.shuttlelite.driver;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OccupantListActivity extends AppCompatActivity {

    private MyAppInfo appInfo = MyAppInfo.getInstance();
    private ShuttleStatus shuttleStatus = appInfo.getShuttleStatus();

    private RecyclerView occupantList;
    private OccupantListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occupant_list);

        occupantList = findViewById(R.id.occupant_list);
        occupantList.setLayoutManager(new LinearLayoutManager(this));

        if (shuttleStatus.getStatus() == ShuttleStatus.GOING_SCHOOL) {
            TextView notice = findViewById(R.id.notice_occupant_list);
            notice.setText("하차시킬 탑승자 선택");

            List<Occupant> occupants = appInfo.getOccupants();

            for (int i = occupants.size() - 1; i >= 0; i--) {
                if (!occupants.get(i).isBoarding()) {
                    occupants.remove(i);
                }
            }
            adapter = new OccupantListAdapter(occupants);
        } else {
            adapter = new OccupantListAdapter(appInfo.getOccupants());
        }
        occupantList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        findViewById(R.id.decide_occupants_btn).setOnClickListener(view -> {
            List<Occupant> occupants = appInfo.getOccupants();
            boolean[] isChecked = adapter.getIsChecked();

            GetOnOffDetails details = appInfo.getGetOnOffDetails();

            LocalDateTime localDateTime = LocalDateTime.now();
            String dateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (shuttleStatus.getStatus() == ShuttleStatus.GOING_HOME) {
                shuttleStatus.setStatus(ShuttleStatus.GOING_HOME);

                for (int i = occupants.size() - 1; i >= 0; i--) {
                    if (!isChecked[i]) {
                        occupants.remove(i);
                    } else {
                        String occupantNumber = occupants.get(i).getNumber();
                        details.writeDetail(occupantNumber, dateTime, GetOnOffDetails.GET_ON);
                    }
                }

                Intent newIntent = new Intent(this, MapsActivity.class);
                startActivity(newIntent);
            } else {
                for (int i = occupants.size() - 1; i >= 0; i--) {
                    if (isChecked[i]) {
                        String occupantNumber = occupants.get(i).getNumber();
                        details.writeDetail(occupantNumber, dateTime, GetOnOffDetails.GET_OFF);
                    }
                }

                appInfo.sendDetails();
                appInfo.reset();

                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
    }

}
