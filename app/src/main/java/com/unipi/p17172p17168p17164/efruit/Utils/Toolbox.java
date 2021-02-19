package com.unipi.p17172p17168p17164.efruit.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.orhanobut.hawk.Hawk;
import com.unipi.p17172p17168p17164.efruit.R;

import java.util.Locale;

public class Toolbox {
    public void hideKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public Dialog showDialogActionSuccessful(Context context, String alertTitle, String textParagraph) {
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_sms_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView textViewAlertSuccessTitle = dialog.findViewById(R.id.textViewAlertLangChangeTitle);
        TextView textViewAlertSuccessTextParagraph = dialog.findViewById(R.id.textViewAlertSuccessTextParagraph);;

        textViewAlertSuccessTitle.setText(alertTitle);
        textViewAlertSuccessTextParagraph.setText(textParagraph);

        return dialog;
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
