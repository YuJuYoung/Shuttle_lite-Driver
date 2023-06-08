package com.shuttlelite.driver;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String[] permissions;

    private final ActivityResultLauncher<String[]> permissionRequest =
            registerForActivityResult(new ActivityResultContracts
                    .RequestMultiplePermissions(), result -> {
                        for (String permission : permissions) {
                            Boolean permissionGranted = result.getOrDefault(permission, false);

                            if (permissionGranted == null || !permissionGranted) {
                                finish();
                                return;
                            }
                        }
                        startNextActivity();
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionManager permissionManager = new PermissionManager(this);
        permissions = permissionManager.getDeniedPermissions();

        if (permissions == null) {
            startNextActivity();
        } else {
            permissionRequest.launch(permissions);
        }
    }

    private void startNextActivity() {
        Intent newIntent = new Intent(this, AuthActivity.class);
        startActivity(newIntent);
    }
}
