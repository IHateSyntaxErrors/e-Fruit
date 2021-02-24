package com.unipi.p17172p17168p17164.efruit.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.GeoPoint;
import com.orhanobut.hawk.Hawk;
import com.unipi.p17172p17168p17164.efruit.R;

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
