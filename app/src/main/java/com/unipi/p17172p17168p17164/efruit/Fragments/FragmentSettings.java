package com.unipi.p17172p17168p17164.efruit.efruit.Fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.unipi.p17172p17168p17164.efruit.efruit.ActivitySettingsMessages;
import com.unipi.p17172p17168p17164.efruit.efruit.R;

public class FragmentSettings extends Fragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        Resources res = getResources();
        String value = res.getString(R.string.app_versionText) + " " + res.getString(R.string.app_versionCode);
        TextView textViewAppVersion = view.findViewById(R.id.textViewAppVersion);
        textViewAppVersion.setText(value);

        TextView textViewGeneralMessages = view.findViewById(R.id.textViewGeneralMessages);
        textViewGeneralMessages.setOnClickListener(v -> {
            Intent intentProfile = new Intent(getActivity(), ActivitySettingsMessages.class);
            startActivity(intentProfile);
        });
        return view;
    }
}