package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.orhanobut.hawk.Hawk;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.PrefsUtils;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;

public class FragmentSettings extends Fragment {
    // ~~~~~~~VARIABLES~~~~~~~
    private Context context;
    private PrefsUtils prefsUtils;
    private Toolbox toolbox;
    private String prefNightModeKey;
    private String prefLanguageKey;
    private SwitchCompat switchCompatNightMode;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = getActivity(); // Initalize the context from the main activity.

        toolbox = new Toolbox();
        prefsUtils = new PrefsUtils(context);
        Hawk.init(requireActivity()).build(); // Initializing Hawk API to make possible all data sync.
        prefsUtils.initKeys(); // Add keys to the prefs if they don't exists.
        prefNightModeKey = context.getResources().getString(R.string.switch_night_mode);
        prefLanguageKey = context.getResources().getString(R.string.switch_lang);

        // Languages text view that acts as a button.
        TextView textView_Lang = view.findViewById(R.id.textViewLanguages);
        String langIsoCode = Hawk.get(context.getString(R.string.switch_lang));
        if (langIsoCode.equalsIgnoreCase("Eng")) {
            textView_Lang.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_united_kingdom, 0, R.drawable.ic_more_right_sign, 0);
        }
        else if (langIsoCode.equalsIgnoreCase("Ell")) {
            textView_Lang.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_greece, 0, R.drawable.ic_more_right_sign, 0);
        }

        textView_Lang.setOnClickListener(v -> {
            Dialog dialog = toolbox.showDialogLangChange(context);
            dialog.show();
            dialog.setOnDismissListener(d -> {
                reloadFragment();
            });
        });

        switchCompatNightMode = view.findViewById(R.id.switchBtnNightMode);
        switchCompatNightMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Hawk.put(prefNightModeKey, true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else {
                Hawk.put(prefNightModeKey, false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // App version text view
        Resources res = getResources();
        String value = res.getString(R.string.app_versionText) + " " + res.getString(R.string.app_versionCode);
        TextView textViewAppVersion = view.findViewById(R.id.textViewAppVersion);
        textViewAppVersion.setText(value);

        loadSettingsData(); // Now load the according data to the views.

        return view;
    }

    private void loadSettingsData() {
        switchCompatNightMode.setChecked(Hawk.get(prefNightModeKey));
    }

    @Override
    public void onResume() {
        super.onResume();
        prefsUtils.initKeys(); // Add keys to the preferences if they don't exists.
        loadSettingsData(); // Reload the data on fragment resume.
    }

    public void reloadFragment() {
        // Reload current fragment
        assert getFragmentManager() != null;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }
}