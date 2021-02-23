package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.unipi.p17172p17168p17164.efruit.Activities.MainActivity;
import com.unipi.p17172p17168p17164.efruit.Models.ModelShops;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.PermissionsUtils;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LOCATION_SERVICE;

public class FragmentShops extends Fragment implements LocationListener {
    // ~~~~~~~VARIABLES~~~~~~~
    private Context context;
    private View view;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    FirebaseUser firebaseUser;

    @BindView(R.id.recyclerViewShops)
    RecyclerView recyclerShops;

    private LinearLayoutManager linearLayoutManager;

    @BindView(R.id.editTxtInputShops_SearchBar)
    TextInputEditText editTxtInputShops_SearchBar;

    LocationManager locationManager;
    private Map<String, Object> updates;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity();

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shops, container, false);

        ButterKnife.bind(this, view);

        init();
        getShopsList();

        //Check if location is enabled.
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (!PermissionsUtils.hasPermissions(context))
            PermissionsUtils.requestPermissions("FRAGMENT_SHOPS", this, context); // Check if permissions are allowed.
        else {
            String le = Context.LOCATION_SERVICE;
            locationManager = (LocationManager) getContext().getSystemService(le);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast toast =
                Toast.makeText(getContext(),
                        "LOCATION DISABLED", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return view;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);

        }
        return view;
    }

    private void init() {
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerShops.setLayoutManager(linearLayoutManager);
        recyclerShops.setHasFixedSize(true);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        editTxtInputShops_SearchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Toolbox.hideKeyboard(v, context);
            }
        });
    }

    public void getShopsList(){
        final String TAG = "[FragmentShops]";

        Query queryShops = db.collection("shops");

        queryShops.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "listen:error", e);
                return;
            }

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
            @Override
            protected void onBindViewHolder(@NonNull ShopsViewHolder holder, int position, @NonNull ModelShops model) {
                holder.viewHolderImgShops_ShopImage.setBackgroundResource(R.drawable.fruit_shop);
                holder.viewHolderTxtViewShops_ShopName.setText(model.getName());
                holder.viewHolderTxtViewShops_ShopPhone.setText(model.getPhone());
                holder.viewHolderTxtViewShops_ShopAddress.setText(model.getAddress());
                holder.viewHolderTxtViewShops_ShopRegion.setText(model.getRegion());
                holder.viewHolderTxtViewShops_ShopZip.setText(String.format(context.getString(R.string.recycler_var_shops_zip), model.getZip() + ""));

                holder.itemView.setOnClickListener(v -> {
                    FragmentProducts fragment = new FragmentProducts(model.getShopId());
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_single_item_shops, parent, false);
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

    /**
     * Called when the location has changed.
     *
     * @param location the updated location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latUser = location.getLatitude();
        double lngUser = location.getLongitude();
        //System.out.println(latUser + lngUser);
        Map<String, Object>updates = new HashMap<>();
        updates.put("location", new GeoPoint(latUser, lngUser));
        DocumentReference locationRef = db.collection("users").document(firebaseUser.getUid());
        locationRef.update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ... εν μπουν στην βαση τι κανω
                        //έλεγχος για την αποσταση
                    }
                });
        locationManager.removeUpdates(this); //If the location changes it will not get the new coordinates.
    }

    public class ShopsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageViewShops_ShopImage)
        ImageView viewHolderImgShops_ShopImage;
        @BindView(R.id.textViewShops_ShopName)
        TextView viewHolderTxtViewShops_ShopName;
        @BindView(R.id.textViewShops_ShopPhone)
        TextView viewHolderTxtViewShops_ShopPhone;
        @BindView(R.id.textViewShops_ShopAddress)
        TextView viewHolderTxtViewShops_ShopAddress;
        @BindView(R.id.textViewShops_ShopRegion)
        TextView viewHolderTxtViewShops_ShopRegion;
        @BindView(R.id.textViewShops_ShopZip)
        TextView viewHolderTxtViewShops_ShopZip;

        public ShopsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /*public void onShopListener(int pos) {

    }

    public interface onShopListener {
        void onShopClick(int pos);
    }*/

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
}