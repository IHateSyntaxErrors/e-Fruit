package com.unipi.p17172p17168p17164.efruit.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.GeoPoint;
import com.orhanobut.hawk.Hawk;
import com.unipi.p17172p17168p17164.efruit.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Toolbox {
    public static void hideKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public Dialog showDialogPersonalInfoSaved(Context context) {
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_profile_saved);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MaterialButton btnAlertLangOk = dialog.findViewById(R.id.btnAlertProfileSaved_Ok);
        btnAlertLangOk.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    public Dialog showDialogWrongShopWarning(Context context, String shopName) {
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        // VIEW BINDING
        dialog.setContentView(R.layout.alert_shop_warning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txtShopName = dialog.findViewById(R.id.textView_AlertShopWarning_ShopName);
        MaterialButton btnAlertDismiss = dialog.findViewById(R.id.btn_AlertShopWarning_Dismiss);

        txtShopName.setText(shopName);
        btnAlertDismiss.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    public Dialog showDialogDateTimeWarning(Context context) {
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        // VIEW BINDING
        dialog.setContentView(R.layout.alert_date_time_warning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MaterialButton btnAlertDismiss = dialog.findViewById(R.id.btn_AlertDateTime_Dismiss);
        btnAlertDismiss.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    public static Date getDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        try {
            return formatter.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static GeoPoint LatLonPoint(double latitude, double longitude) {
        int lat = (int) (latitude * 1E6);
        int lng = (int) (longitude * 1E6);
        return new GeoPoint(lat, lng);
    }

    public Dialog showDialogLangChange(Context context) {
        final Dialog dialog = new Dialog(context);
        final String ENG ="Eng";
        final String ELL = "Ell";

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_lang_change);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RadioButton radioBtnAlertLangEn = dialog.findViewById(R.id.radioButtonEn);
        RadioButton radioBtnAlertLangEll = dialog.findViewById(R.id.radioButtonEll);
        System.out.println((String) Hawk.get(context.getString(R.string.switch_lang)));
        if (((String) Hawk.get(context.getString(R.string.switch_lang))).equalsIgnoreCase(ENG))
            radioBtnAlertLangEn.setChecked(true);
        else if (((String) Hawk.get(context.getString(R.string.switch_lang))).equalsIgnoreCase(ELL))
            radioBtnAlertLangEll.setChecked(true);

        radioBtnAlertLangEn.setOnClickListener(v -> {
            Hawk.put(context.getString(R.string.switch_lang), ENG);
            radioBtnAlertLangEll.setChecked(false);
        });
        radioBtnAlertLangEll.setOnClickListener(v -> {
            Hawk.put(context.getString(R.string.switch_lang), ELL);
            radioBtnAlertLangEn.setChecked(false);
        });
        MaterialButton btnAlertLangOk = dialog.findViewById(R.id.buttonAlertLangOK);
        btnAlertLangOk.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    public AlertDialog buildAlertMessageNoGps(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        return builder.create();
    }

    public static String getTimeDate(long timestamp){
        try{
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'");
            Date netDate = (new Date(timestamp));
            return dateFormat.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

/*    public void updateLanguage(String language, Context context)
    {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.createConfigurationContext(config);
        context.getResources().getDisplayMetrics().setTo(new DisplayMetrics());
    }*/

}
