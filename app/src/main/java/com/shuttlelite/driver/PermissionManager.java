package com.shuttlelite.driver;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private Context context;

    public PermissionManager(Context context) {
        this.context = context;
    }

    public String[] getDeniedPermissions() {
        List<String> permissions = new ArrayList<>();

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (Build.VERSION.SDK_INT < 30 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        for (int i = permissions.size() - 1; i >= 0; i--) {
            String permission = permissions.get(i);

            if(context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                permissions.remove(i);
            }
        }
        return permissions.isEmpty() ? null : permissions.toArray(new String[permissions.size()]);
    }

}
