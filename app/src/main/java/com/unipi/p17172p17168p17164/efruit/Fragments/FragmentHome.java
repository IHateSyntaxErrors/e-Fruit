package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unipi.p17172p17168p17164.efruit.Adapters.DataAdapterProducts;
import com.unipi.p17172p17168p17164.efruit.Items.ItemProduct;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;

import java.util.ArrayList;

public class FragmentHome extends Fragment {
    // ~~~~~~~VARIABLES~~~~~~~
    private Context context;
    private View view;
    private ArrayList<ItemProduct> arrayListSmsType;
    private DataAdapterProducts dataAdapterProductsSmsType;

    private TextInputEditText textInputEditTextFullName;
    private TextInputEditText textInputEditTextAddress;
    private ViewFlipper viewFlipperSmsType;
    private MaterialButton buttonSendSms;

    private Toolbox toolbox;
    LocationManager locationManager;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }
}