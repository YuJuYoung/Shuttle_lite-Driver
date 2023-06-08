package com.shuttlelite.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("MissingPermission")
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NfcAdapter.ReaderCallback {

    private GoogleMap mMap;
    // private ActivityMapsBinding binding;
    private LocationManager locationManager;
    private NfcAdapter nfcAdapter;

    private MyAppInfo appInfo = MyAppInfo.getInstance();
    private List<Occupant> occupants;
    private ShuttleStatus shuttleStatus;
    private GetOnOffDetails details;

    private List<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(getApplicationContext(), "NFC 사용 불가능", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!nfcAdapter.isEnabled()) {
            DialogFactory.showNFCDialog(this);
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            DialogFactory.showGPSDialog(this);
        }

        occupants = appInfo.getOccupants();
        shuttleStatus = appInfo.getShuttleStatus();
        details = appInfo.getGetOnOffDetails();

        markers = new ArrayList<>();

        /*binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());*/

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button finishBtn = findViewById(R.id.finish_nav_btn);

        if (shuttleStatus.getStatus() == ShuttleStatus.GOING_SCHOOL) {
            finishBtn.setText("하차");
        }

        finishBtn.setOnClickListener(view -> {
            DialogFactory.getFinishDialogBuilder(this)
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int a) {
                            if (shuttleStatus.getStatus() == ShuttleStatus.GOING_HOME) {
                                appInfo.sendDetails();
                                appInfo.reset();

                                finishAffinity();
                                System.runFinalization();
                                System.exit(0);
                            } else {
                                if (!markers.isEmpty()) {
                                    for (Marker marker : markers) {
                                        for (int i = 0; i < occupants.size(); i++) {
                                            Occupant occupant = occupants.get(i);

                                            if (occupant.equals(marker.getTag())) {
                                                occupants.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                }
                                Intent newIntent = new Intent(MapsActivity.this, OccupantListActivity.class);
                                startActivity(newIntent);
                            }
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create()
                    .show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableReaderMode(this, this,
                NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nfcAdapter.disableReaderMode(this);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        for (Occupant occupant : occupants) {
            if (shuttleStatus.getStatus() == ShuttleStatus.GOING_HOME) {
                if (details.getDetail(occupant.getNumber()) != null && !occupant.isBoarding()) {
                    continue;
                }
            } else {
                if (occupant.isBoarding()) {
                    continue;
                }
            }

            LatLng latLng = new LatLng(occupant.getLatitude(), occupant.getLongitude());

            MarkerOptions markerOptions =
                    new MarkerOptions()
                            .position(latLng)
                            .title(occupant.getName())
                            .snippet(occupant.getBoardingPlaceName());
            Marker marker = mMap.addMarker(markerOptions);

            MarkerTag markerTag = new MarkerTag(occupant);
            marker.setTag(markerTag);
            markers.add(marker);
        }

        startMyLocationService();
    }

    private void startMyLocationService() {
        try {
            MyLocationListener myLocationListener = new MyLocationListener();

            long minTime = 3000;
            float minDistance = 0;
            String provider = null;

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            } else {
                provider = LocationManager.GPS_PROVIDER;
            }
            locationManager.requestLocationUpdates(provider, minTime, minDistance, myLocationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);

        try {
            isoDep.connect();

            byte[] command = IsoDepConnectionUtils.BuildSelectApdu(BuildConfig.MY_AID);
            String result = new String(isoDep.transceive(command), StandardCharsets.UTF_8);

            isoDep.close();

            for (int i = 0; i < markers.size(); i++) {
                Marker marker = markers.get(i);
                MarkerTag markerTag = (MarkerTag) marker.getTag();
                Occupant occupant = markerTag.getOccupant();

                if (result.equals(occupant.getNumber())) {
                    marker.remove();
                    markers.remove(i);

                    LocalDateTime localDateTime = LocalDateTime.now();
                    String dateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    String getOnOff =
                            shuttleStatus.getStatus() == ShuttleStatus.GOING_HOME
                                    ? GetOnOffDetails.GET_OFF
                                    : GetOnOffDetails.GET_ON;

                    details.writeDetail(occupant.getNumber(), dateTime, getOnOff);
                    Toast.makeText(this, occupant.getName() + " 태그 완료", Toast.LENGTH_SHORT).show();

                    occupant.setBoarding(
                            shuttleStatus.getStatus() == ShuttleStatus.GOING_HOME ? false : true
                    );
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            JSONObject requestBody = new JSONObject();

            String[] occupantNumbers = new String[markers.size()];

            for (int i = 0; i < occupantNumbers.length; i++) {
                MarkerTag markerTag = (MarkerTag) markers.get(i).getTag();
                Occupant occupant = markerTag.getOccupant();
                occupantNumbers[i] = occupant.getNumber();
            }

            try {
                requestBody.put("latitude", latitude);
                requestBody.put("longitude", longitude);
                requestBody.put("occupantNumbers", occupantNumbers);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestHTTPURLConnection requestHTTPURLConnection
                    = new RequestHTTPURLConnection(MyURL.SEND_MY_LOCATION, requestBody);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    requestHTTPURLConnection.request();
                }
            }).start();

            // 2km 내의 탑승자 보호자에게 메세지 전송
            for (Marker marker : markers) {
                if (getDistance(location, marker) > 20000.0) {

                }
            }
        }
    }

    private double getDistance(Location location, Marker marker) {
        Location marKerLocation = new Location("marker");
        LatLng latLng = marker.getPosition();

        marKerLocation.setLatitude(latLng.latitude);
        marKerLocation.setLongitude(latLng.longitude);

        return location.distanceTo(marKerLocation);
    }

    private class MarkerTag {
        private Occupant occupant;
        private boolean isNotified;

        public MarkerTag(Occupant occupant) {
            this.occupant = occupant;
            isNotified = false;
        }

        public Occupant getOccupant() {
            return occupant;
        }

        public boolean isNotified() {
            return isNotified;
        }
    }
}