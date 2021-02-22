package com.unipi.p17172p17168p17164.efruit.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.permissionx.guolindev.PermissionX;
import com.unipi.p17172p17168p17164.efruit.R;

public class PermissionsUtils {

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

    public static void requestPermissions(Context) {
        PermissionX.init(this)
                .permissions(permissions)
                .onExplainRequestReason((scope, deniedList) ->
                        scope.showRequestReasonDialog(deniedList, getString(R.string.permission_allow_ask_reason),
                                getString(R.string.general_ok), getString(R.string.general_cancel)))
                .onForwardToSettings((scope, deniedList) ->
                        scope.showForwardToSettingsDialog(deniedList, getString(R.string.permissions_allow_manually),
                                getString(R.string.general_ok), getString(R.string.general_cancel)))
                .request((allGranted, grantedList, deniedList) -> {
                    if (!allGranted) {
                        Toast toast = Toast.makeText(this, getString(R.string.permissions_some_denied), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 400);
                        toast.show();
                    }
                    /*else if (allGranted && startService) {
                        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                        this.onLocationChanged(null);
                    }*/
                });
    }
}
