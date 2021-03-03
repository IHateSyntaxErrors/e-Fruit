package com.unipi.p17172p17168p17164.efruit.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.DBHelper;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

public class AdminPanelActivity extends AppCompatActivity implements LocationListener{
    private FirebaseFirestore db;
    public LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Toolbox toolbox;
    private LocationManager locationManager;
    private Context context;
    public String provider;
    private FirestoreRecyclerAdapter adapter;
    RecyclerView recyclerList;



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
       // recyclerList.setLayoutManager(linearLayoutManager);
        //recyclerList.setHasFixedSize(true);


        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        toolbox = new Toolbox();
        Query queryOrders = db.collection("orders");
        Task<QuerySnapshot> q1 = queryOrders.get();
        Task<QuerySnapshot> q2 = DBHelper.getOrderShopName(db, model.getShopId()).get();
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getLocation() {
//        System.out.println("done4");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        System.out.println("done1");
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
            if (location != null) {
                onLocationChanged(location);
            }
        } else {
            AlertDialog alertDialog = toolbox.buildAlertMessageNoGps(context);
            alertDialog.show();
        }
//        System.out.println("done2");
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
//        System.out.println("done3");
        double latUser = location.getLatitude();
        double lngUser = location.getLongitude();
        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latUser, lngUser));
        Map<String, Object> updates = new HashMap<>();
        updates.put("location", new GeoPoint(latUser, lngUser));
        updates.put("geohash", hash);
        DocumentReference locationRef = db.collection("users").document(firebaseUser.getUid());
        locationRef.update(updates)
                .addOnCompleteListener(task -> {

                });
    }
    private double convertMetersToKms(double distanceInKm) {
        return distanceInKm / 1000.000;
    }
    public void getShopsList() {
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

        queryShops.get().addOnCompleteListener(taskShop -> {
            if (taskShop.isSuccessful()) {
                for (DocumentSnapshot documentShopLocation : taskShop.getResult()) {

                    GeoPoint locShop = documentShopLocation.getGeoPoint("coords");

                    Location locationShops = new Location("point B");
                    locationShops.setLatitude(locShop.getLatitude());
                    locationShops.setLongitude(locShop.getLongitude());

                    userRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            GeoPoint locUser = document.getGeoPoint("coords");

                            Location locationUser = new Location("point A");

                            locationUser.setLatitude(locUser.getLatitude());
                            locationUser.setLongitude(locUser.getLongitude());

                            float distance = locationUser.distanceTo(locationShops);
                            double km = convertMetersToKms(distance);
                            System.out.println(" The distance is " + "[ " + distance + " ]");
                            System.out.println(km + " km");

                        }
                    });
                }
            }
        });
    }
    public void onProviderDisabled(String provider) {

    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onProviderEnabled(String provider) {
        getLocation();
    }
}

