package com.unipi.p17172p17168p17164.efruit.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;
import com.unipi.p17172p17168p17164.efruit.R;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class PermissionsUtils extends Fragment{
    private static final String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.RECORD_AUDIO};

    public static boolean hasPermissions(Context context) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void requestPermissions(Activity activity) {
        PermissionX.init((FragmentActivity) activity)
                .permissions(permissions)
                .onExplainRequestReason((scope, deniedList) ->
                        scope.showRequestReasonDialog(deniedList, activity.getString(R.string.permission_allow_ask_reason),
                                activity.getString(R.string.general_ok), activity.getString(R.string.general_cancel)))
                .onForwardToSettings((scope, deniedList) ->
                        scope.showForwardToSettingsDialog(deniedList, activity.getString(R.string.permissions_allow_manually),
                                activity.getString(R.string.general_ok), activity.getString(R.string.general_cancel)))
                .request((allGranted, grantedList, deniedList) -> {
                    if (!allGranted) {
                        Toast toast = Toast.makeText(activity, activity.getString(R.string.permissions_some_denied), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 400);
                        toast.show();
                    }

                });
    }
    public static void requestPermissions(String fragment_tag, Fragment fragment, Context context) {
        PermissionX.init(fragment.getParentFragmentManager().findFragmentByTag(fragment_tag))
                .permissions(permissions)
                .onExplainRequestReason((scope, deniedList) ->
                        scope.showRequestReasonDialog(deniedList, context.getString(R.string.permission_allow_ask_reason),
                                context.getString(R.string.general_ok), context.getString(R.string.general_cancel)))
                .onForwardToSettings((scope, deniedList) ->
                        scope.showForwardToSettingsDialog(deniedList, context.getString(R.string.permissions_allow_manually),
                                context.getString(R.string.general_ok), context.getString(R.string.general_cancel)))
                .request((allGranted, grantedList, deniedList) -> {
                    if (!allGranted) {
                        Toast toast = Toast.makeText(context, context.getString(R.string.permissions_some_denied), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 400);
                        toast.show();
                    }
                });
    }
}
