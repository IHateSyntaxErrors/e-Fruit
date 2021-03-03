package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.unipi.p17172p17168p17164.efruit.Models.ModelShops;
import com.unipi.p17172p17168p17164.efruit.Models.ModelUsers;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;
import com.unipi.p17172p17168p17164.efruit.databinding.FragmentShopsBinding;
import com.unipi.p17172p17168p17164.efruit.databinding.ItemShopBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static android.content.Context.LOCATION_SERVICE;
import static android.location.Location.distanceBetween;

public class FragmentShops extends Fragment implements LocationListener {
    // ~~~~~~~VARIABLES~~~~~~~
    private FragmentShopsBinding binding;
    private Context context;
    public View view;
    private Toolbox toolbox;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    public FirebaseUser firebaseUser;

    RecyclerView recyclerShops;
    public LinearLayoutManager linearLayoutManager;

    public String provider;
    private LocationManager locationManager;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShopsBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        init();
        getLocation();
        getShopsList();

        return view;
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        toolbox = new Toolbox();

        recyclerShops = binding.recyclerViewShops;

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerShops.setLayoutManager(linearLayoutManager);
        recyclerShops.setHasFixedSize(true);

    }

    public void getShopsList(){
        final String TAG = "[FragmentShops]";

        Query queryShops = db.collection("shops");
        DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

        queryShops.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "listen:error", e);
                return;
            }

            assert snapshots != null;
            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        Log.d(TAG, "New Shop: " + dc.getDocument().getData());
                        break;
                    case MODIFIED:
                        Log.d(TAG, "Modified Shop: " + dc.getDocument().getData());
                        break;
                    case REMOVED:
                        Log.d(TAG, "Removed Product: " + dc.getDocument().getData());
                        break;
                }
            }
        });



        // RecyclerOptions
        FirestoreRecyclerOptions<ModelShops> recyclerOptions = new FirestoreRecyclerOptions.Builder<ModelShops>()
                .setQuery(queryShops, ModelShops.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<ModelShops, ShopsViewHolder>(recyclerOptions) {
            ArrayList<Object> list = new ArrayList<>();
            AtomicReference<Double> valueKM = new AtomicReference<>(); //Δήλωση atomicReference για να μπορέσω να περάσω μεταβλητές έξω από το Lambda
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull ShopsViewHolder holder, int position, @NonNull ModelShops model) {
                holder.itemShopsBinding.CardViewShopsShopImage.setBackgroundResource(R.drawable.fruit_shop);
                holder.itemShopsBinding.textViewShopsShopName.setText(model.getName());
                holder.itemShopsBinding.textViewShopsShopPhone.setText(model.getPhone());
                holder.itemShopsBinding.textViewShopsShopAddress.setText(model.getAddress());
                holder.itemShopsBinding.textViewShopsShopRegion.setText(model.getRegion());
                holder.itemShopsBinding.textViewShopsShopZip.setText(String.format(context.getString(R.string.recycler_var_shops_zip), model.getZip() + ""));

               // Double[] valueArrayKM = { null };

                queryShops.get().addOnCompleteListener(taskShop -> {
                    if (taskShop.isSuccessful()) {
                        //holder.itemShopsBinding.textViewShopsShopDistance.setText(String.format("%s km", df.format(km)));
                        for (DocumentSnapshot documentShopLocation : taskShop.getResult()) {

                            GeoPoint locShop = documentShopLocation.getGeoPoint("coords");

                            Location locationShops = new Location("point B");
                            locationShops.setLatitude(locShop.getLatitude());
                            locationShops.setLongitude(locShop.getLongitude());
                            System.out.println(locationShops);
                            userRef.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    GeoPoint locUser = document.getGeoPoint("coords");

                                    Location locationUser = new Location("point A");

                                    locationUser.setLatitude(locUser.getLatitude());
                                    locationUser.setLongitude(locUser.getLongitude());

                                    float distance = locationUser.distanceTo(locationShops);
                                    double km = convertMetersToKms(distance);

                                    DecimalFormat df = new DecimalFormat("#.##");
                                   // df.format(km);
                                    //valueKM.set(km);
                                    System.out.println("km " +km);
                                    list.forEach(s -> {
                                        valueKM.set(km);
                                    });

                                    System.out.println(valueKM);
                                    System.out.println(" The distance is " + "[ " + distance + " ]");
                                    System.out.println(df.format(km) + " km");
                                }
                            });
                            System.out.println("PRINT VALUEKM: " + valueKM);
                            AtomicReference x = valueKM;
                            System.out.println("PRINT X: " + x);
                            holder.itemShopsBinding.textViewShopsShopDistance.setText(String.format("%s km", x));
                        }
                    }
                });




                holder.itemView.setOnClickListener(v -> {
                    FragmentProducts fragment = new FragmentProducts(model.getShopId());
                    FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

                    //get fragment transaction
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    //set new fragment in fragment_container (FrameLayout)
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                });



            }

            @NonNull
            @Override
            public ShopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ItemShopBinding view = ItemShopBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ShopsViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("Error", e.getMessage());
            }
        };
        adapter.notifyDataSetChanged();
        recyclerShops.setAdapter(adapter);
    }

    private double convertMetersToKms(double distanceInKm) {
        return distanceInKm / 1000.000;
    }

    public static class ShopsViewHolder extends RecyclerView.ViewHolder {
        private final ItemShopBinding itemShopsBinding;

        public ShopsViewHolder(ItemShopBinding itemShopBinding) {
            super(itemShopBinding.getRoot());
            this.itemShopsBinding = itemShopBinding;
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getLocation() {

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (enabled) {
            // Define the criteria how to select the location provider -> use
            // default
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
            provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            if (location != null) {
                onLocationChanged(location);
            }
        }
        else {
            AlertDialog alertDialog = toolbox.buildAlertMessageNoGps(context);
            alertDialog.show();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latUser = location.getLatitude();
        double lngUser = location.getLongitude();
        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latUser, lngUser));
        Map<String, Object>updates = new HashMap<>();
        updates.put("coords", new GeoPoint(latUser, lngUser));
        updates.put("geohash", hash);
        DocumentReference locationRef = db.collection("users").document(firebaseUser.getUid());
        locationRef.update(updates)
                .addOnCompleteListener(task -> {

                });
        locationManager.removeUpdates(this); //If the location changes it will not get the new coordinates.
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1234:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
                break;
        }
    }

    public void onProviderDisabled(String provider) {

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onProviderEnabled(String provider) {
        getLocation();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /* Request updates at startup */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onResume() {
        super.onResume();
        getLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}