package com.unipi.p17172p17168p17164.efruit.Utils;

import android.content.Context;
import android.content.res.Configuration;

import com.orhanobut.hawk.Hawk;

import java.util.Locale;

public class PrefsUtils {
    private Context context;

    public PrefsUtils(Context context){
        this.context = context;
    }

    public final static String SWITCH_DARK_MODE_KEY = "SWITCH_DARK_MODE";
    public final static String SWITCH_LANGUAGE_KEY = "SWITCH_DARK_MODE";

    public void initKeys() {
        if (!Hawk.contains(SWITCH_DARK_MODE_KEY)) {
            int nightModeFlags =
                    context.getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    Hawk.put(SWITCH_DARK_MODE_KEY, true);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    Hawk.put(SWITCH_DARK_MODE_KEY, false);
                    break;
            }
        }
        if (!Hawk.contains(SWITCH_LANGUAGE_KEY)) {
            String lang = Locale.getDefault().getISO3Language();
            switch (lang){
                case "eng":
                    Hawk.put(SWITCH_LANGUAGE_KEY, "Eng");
                    break;
                case "ell":
                    Hawk.put(SWITCH_LANGUAGE_KEY, "Ell");
                    break;
            }
        }
    }
}
