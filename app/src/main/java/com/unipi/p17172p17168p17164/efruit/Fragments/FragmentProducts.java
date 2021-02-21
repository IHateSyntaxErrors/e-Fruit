package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.unipi.p17172p17168p17164.efruit.Adapters.DataAdapterProducts;
import com.unipi.p17172p17168p17164.efruit.Items.ItemProduct;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;

import java.util.ArrayList;

public class FragmentProducts extends Fragment {
    // ~~~~~~~VARIABLES~~~~~~~
    private Context context;
    private View view;

    private ArrayList<ItemProduct> arrayListProducts;
    private DataAdapterProducts dataAdapterProducts;

    LocationManager locationManager;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products, container, false);

        arrayListProducts = new ArrayList<>();
        TextInputEditText txtInputProducts_SearchBar = view.findViewById(R.id.txtInputProducts_SearchBar);
        txtInputProducts_SearchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Toolbox.hideKeyboard(v, context);
            }
        });

        return view;
    }

    public void initRecyclerView(){
        RecyclerView recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        // Attaching data to Recycler
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        dataAdapterProducts = new DataAdapterProducts(getActivity(), arrayListProducts);
        recyclerViewProducts.setHasFixedSize(true);
        recyclerViewProducts.setLayoutManager(linearLayoutManager);
        recyclerViewProducts.setAdapter(dataAdapterProducts);
    }

    private void loadProducts() {

    }

    @Override
    public void onStart() {
        super.onStart();
        initRecyclerView();
    }
}