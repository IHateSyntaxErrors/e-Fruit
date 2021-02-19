package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17172p17168p17164.efruit.Adapters.DataAdapterProducts;
import com.unipi.p17172p17168p17164.efruit.Items.ItemProduct;
import com.unipi.p17172p17168p17164.efruit.R;

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

        view = inflater.inflate(R.layout.fragment_home, container, false);
        arrayListProducts = new ArrayList<>();

        return view;
    }

    public void initRecyclerView(){
        RecyclerView recyclerViewSmsType = view.findViewById(R.id.recyclerViewProducts);
        // Attaching data to Recycler
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        dataAdapterProducts = new DataAdapterProducts(getActivity(), arrayListProducts);
        recyclerViewSmsType.setHasFixedSize(true);
        recyclerViewSmsType.setLayoutManager(linearLayoutManager);
        recyclerViewSmsType.setAdapter(dataAdapterProducts);
    }

    @Override
    public void onStart() {
        super.onStart();
        initRecyclerView();
    }
}