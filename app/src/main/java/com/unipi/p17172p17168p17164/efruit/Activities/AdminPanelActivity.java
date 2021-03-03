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
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminPanelActivity extends AppCompatActivity{
    private static final String CHANNEL_ID = "Customer Notification";
    private FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        db = FirebaseFirestore.getInstance();
        myMethod();
    }

    public void myMethod(){
        Query queryOrders = db.collection("orders");

        Query userRef = db.collection("users");
        Query queryShops = db.collection("shops");
        Task<QuerySnapshot> q1 = queryOrders.get();
        Task<QuerySnapshot> q2 = userRef.get();
        Task<QuerySnapshot> q3 = queryShops.get();


        Tasks.whenAllComplete(q1, q2, q3).addOnSuccessListener(list -> {
            for (DocumentSnapshot documentOrders: q1.getResult()) {
                boolean orderComplete = documentOrders.getBoolean("is_completed");
                if (orderComplete == false){
                    String userIdOrder = documentOrders.getString("userId");
                    String getOrderId = documentOrders.getId();
                    for (DocumentSnapshot documentUsers: q2.getResult()){
                        String getUserId = documentUsers.getString("userId");
                        System.out.println(getUserId);
                        if (getUserId.equals(userIdOrder)){
                            System.out.println("Fix");
                            String shop = documentOrders.getString("shopId");
                            switch (shop){
                                case "shop1":
                                    System.out.println("you are in shop1");
                                    for(DocumentSnapshot documentShops:q3.getResult()){
                                        String idShop = documentShops.getString("shopId");
                                        if (idShop.equals("shop1")){
                                            GeoPoint shopLoc = documentShops.getGeoPoint("coords");
                                            Location shopsLocation = new Location("shopsLocation");
                                            shopsLocation.setLatitude(shopLoc.getLatitude());
                                            shopsLocation.setLongitude(shopLoc.getLongitude());

                                            GeoPoint locUser = documentUsers.getGeoPoint("coords");
                                            Location locationUser = new Location("locationUser");
                                            locationUser.setLatitude(locUser.getLatitude());
                                            locationUser.setLongitude(locUser.getLongitude());

                                            float distance = locationUser.distanceTo(shopsLocation);
                                            double km = convertMetersToKms(distance);
                                            DecimalFormat df = new DecimalFormat("#.##");
                                            System.out.println("Η απόσταση είναι: "+df.format(km));
                                            if (km <= 1){
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                                        .setSmallIcon(R.drawable.ic_apple_black)
                                                        .setContentTitle("Customer Notification")
                                                        .setContentText("The customer "+ " variable" + " with the following order ID: "
                                                                + "variable"+" is approaching the store.")
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            }

                                        }

                                    }
                                    break;
                                case "shop2":
                                    System.out.println("you are in shop2");
                                    for(DocumentSnapshot documentShops:q3.getResult()){
                                        String idShop = documentShops.getString("shopId");
                                        if (idShop.equals("shop2")){
                                            GeoPoint shopLoc = documentShops.getGeoPoint("coords");
                                            Location shopsLocation = new Location("shopsLocation");
                                            shopsLocation.setLatitude(shopLoc.getLatitude());
                                            shopsLocation.setLongitude(shopLoc.getLongitude());

                                            GeoPoint locUser = documentUsers.getGeoPoint("coords");
                                            Location locationUser = new Location("locationUser");
                                            locationUser.setLatitude(locUser.getLatitude());
                                            locationUser.setLongitude(locUser.getLongitude());

                                            float distance = locationUser.distanceTo(shopsLocation);
                                            double km = convertMetersToKms(distance);
                                            DecimalFormat df = new DecimalFormat("#.##");
                                            System.out.println("Η απόσταση είναι: "+df.format(km));
                                            if (km <= 1){
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                                        .setSmallIcon(R.drawable.ic_apple_black)
                                                        .setContentTitle("Customer Notification")
                                                        .setContentText("The customer "+ " variable" + " with the following order ID: "
                                                                + "variable"+" is approaching the store.")
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            }

                                        }

                                    }
                                    break;
                                case "shop3":
                                    System.out.println("you are in shop3");
                                    for(DocumentSnapshot documentShops:q3.getResult()){
                                        String idShop = documentShops.getString("shopId");
                                        if (idShop.equals("shop3")){
                                            GeoPoint shopLoc = documentShops.getGeoPoint("coords");
                                            Location shopsLocation = new Location("shopsLocation");
                                            shopsLocation.setLatitude(shopLoc.getLatitude());
                                            shopsLocation.setLongitude(shopLoc.getLongitude());

                                            GeoPoint locUser = documentUsers.getGeoPoint("coords");
                                            Location locationUser = new Location("locationUser");
                                            locationUser.setLatitude(locUser.getLatitude());
                                            locationUser.setLongitude(locUser.getLongitude());

                                            float distance = locationUser.distanceTo(shopsLocation);
                                            double km = convertMetersToKms(distance);
                                            DecimalFormat df = new DecimalFormat("#.##");
                                            System.out.println("Η απόσταση είναι: "+df.format(km));
                                            if (km <= 1){
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                                        .setSmallIcon(R.drawable.ic_apple_black)
                                                        .setContentTitle("Customer Notification")
                                                        .setContentText("The customer "+ " variable" + " with the following order ID: "
                                                                + "variable"+" is approaching the store.")
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            }

                                        }

                                    }
                                    break;

                            }
                        }
                    }
                    System.out.println(userIdOrder);
                }else{
                    System.out.println("didn't find");
                    return;
                }
            }
        });
    }

    private double convertMetersToKms(double distanceInKm) {
        return distanceInKm / 1000.000;
    }
}

