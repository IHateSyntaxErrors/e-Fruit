package com.unipi.p17172p17168p17164.efruit.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionsUtils {
    public static final int REQUEST_PERMISSION_MULTIPLE = 0;
    public static final int REQUEST_PERMISSION_RECORD_AUDIO = 1;
    public static final int REQUEST_PERMISSION_LOCATION = 2;

    public static boolean checkAndRequestPermissions(Activity activity) {
        System.out.println("PermissionsUtils checkAndRequestPermissions()");

        int permissionCamera = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permissionLocation = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionWriteExternal = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // Permission List
        List<String> listPermissionsNeeded = new ArrayList<>();

        // Camera Permission
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                Toast.makeText(activity, "Camera Permission is required for this app to run", Toast.LENGTH_SHORT)
                        .show();
            }
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        // Read/Write Permission
        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        // Location Permission
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_PERMISSION_MULTIPLE);
            return false;
        }

        return true;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
