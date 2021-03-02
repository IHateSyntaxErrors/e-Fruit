package com.unipi.p17172p17168p17164.efruit.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unipi.p17172p17168p17164.efruit.Fragments.FragmentOrders;
import com.unipi.p17172p17168p17164.efruit.Fragments.FragmentSettings;
import com.unipi.p17172p17168p17164.efruit.Fragments.FragmentShops;
import com.unipi.p17172p17168p17164.efruit.Models.ModelUsers;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.PermissionsUtils;
import com.unipi.p17172p17168p17164.efruit.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // ~~~~~~~VARIABLES~~~~~~~
    private ActivityMainBinding binding;
    private FirebaseUser firebaseUser;

    private static final int REQUEST_CODE_SPEECH_INPUT = 10;
    private FirebaseFirestore db;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        updateUI(); // Update UI with user's info.

        if (!PermissionsUtils.hasPermissions(this))
            PermissionsUtils.requestPermissions(this); // Check if permissions are allowed.

        setSupportActionBar(binding.toolbar.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_top);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar.getRoot(),
                                                                 R.string.nav_drawer_close, R.string.nav_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        // Change drawer arrow icon
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.colorIcon));
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);
        binding.navView.setCheckedItem(R.id.nav_item_products);

        // Add the home fragment to show it in the frame layout of main activity.
        FragmentShops fragmentDefault = new FragmentShops();
        setFragment(fragmentDefault, "FRAGMENT_SHOPS");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        if (id == R.id.nav_item_products)
        {
            // Products Fragment
            fragment = new FragmentShops();
            setFragment(fragment, "FRAGMENT_SHOPS");
        }
        else if (id == R.id.nav_item_cart)
        {
            // Cart Activity
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
            return false;
        }
        else if (id == R.id.nav_item_profile)
        {
            // Profile Activity
            Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intentProfile);
            return false;
        }
        else if (id == R.id.nav_item_orders)
        {
            // Orders Fragment
            fragment = new FragmentOrders();
            setFragment(fragment, "FRAGMENT_ORDERS");
        }
        else if (id == R.id.nav_item_settings)
        {
            // Settings Fragment
            fragment = new FragmentSettings();
            setFragment(fragment, "FRAGMENT_SETTINGS");
        }
        else if (id == R.id.nav_item_exit)
        {
            this.finish();
            System.exit(0);
            return false;
        }

        if (fragment == null) {
            fragment = new FragmentShops();
            setFragment(fragment, "FRAGMENT_SHOPS");
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as we have specified a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.nav_item_profile)
        {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    // Method that will change fragments when a item is selected from the navigation drawer.
    private void setFragment(Fragment fragment, String tagName)
    {
        //get current fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        //get fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //set new fragment in fragment_container (FrameLayout)
        fragmentTransaction.replace(R.id.fragment_container, fragment, tagName);
        fragmentTransaction.commit();
    }

    public void speechToText() {
        // SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.nav_top_title_speech_recognition));

        try {
            startActivityForResult(speechRecognizerIntent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast toastDeviceNotSupported = Toast.makeText(MainActivity.this,
                                                           getString(R.string.toast_speech_recognition_not_supported),
                                                           Toast.LENGTH_LONG);
            toastDeviceNotSupported.setGravity(Gravity.CENTER, 0, 0);
            toastDeviceNotSupported.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                // get text array from voice input
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (containsCaseInsensitive("products", result)
                        || containsCaseInsensitive("go to products", result)
                        || containsCaseInsensitive("go to shops", result)
                        || containsCaseInsensitive("shops", result)
                        || containsCaseInsensitive("show shops", result)
                        || containsCaseInsensitive("fruits", result)) {
                    FragmentShops fragment = new FragmentShops();
                    setFragment(fragment, "FRAGMENT_SHOPS");
                }
                else if (containsCaseInsensitive("settings", result)
                        || containsCaseInsensitive("go to settings", result)) {
                    // Change to settings fragment
                    FragmentSettings fragmentSettings = new FragmentSettings();
                    setFragment(fragmentSettings, "FRAGMENT_SETTINGS");
                }
                else if (containsCaseInsensitive("orders", result)
                        || containsCaseInsensitive("go to orders", result)) {
                    // Change to settings fragment
                    FragmentOrders fragmentSettings = new FragmentOrders();
                    setFragment(fragmentSettings, "FRAGMENT_ORDERS");
                }
                else if (containsCaseInsensitive("exit", result)) {
                    finish();
                    System.exit(0);
                }
                else {
                    Toast invalidToast = Toast.makeText(MainActivity.this,
                                                        getString(R.string.toast_speech_recognition_invalid_action),
                                                        Toast.LENGTH_LONG);
                    invalidToast.setGravity(Gravity.CENTER, 0, 0);
                    invalidToast.show();
                }
            }
        }
    }

    public void init() {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public void updateUI() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        ModelUsers modelUsers = document.toObject(ModelUsers.class);

                        View headerView = binding.navView.getHeaderView(0);

                        TextView textViewName = headerView.findViewById(R.id.textViewNavBar_Name);
                        textViewName.setText(Objects.requireNonNull(modelUsers).getFull_name());

                        TextView textViewPhone = headerView.findViewById(R.id.textViewNavBar_Phone);
                        textViewPhone.setText(modelUsers.getPhone_number());

                        if (modelUsers.getIs_admin()) { // If user is admin, show the admin UI.
                            binding.toolbar.actionBarAdminPanel.setVisibility(View.VISIBLE);
                            binding.navView.getMenu().getItem(2).setVisible(true);
                        }
                    }
                }
            });

            binding.toolbar.actionBarMic.setOnClickListener(v -> {
                speechToText();
            });
            binding.toolbar.actionBarCart.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            });

            // With the help of glide library we are able to load user profile picture into our app.
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(binding.toolbar.actionBarCircleImgViewProfile);
            // Add a click event to redirect the user to profile settings if the user profile icon is clicked
            binding.toolbar.actionBarCircleImgViewProfile.setOnClickListener(v -> {
                Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
            });
        }
        // Else the user is not logged in - do actions
        // else {}
    }

//    public void requestPermissions() {
//        PermissionX.init(this)
//                .permissions(permissions)
//                .onExplainRequestReason((scope, deniedList) ->
//                        scope.showRequestReasonDialog(deniedList, getString(R.string.permission_allow_ask_reason),
//                                                      getString(R.string.general_ok), getString(R.string.general_cancel)))
//                .onForwardToSettings((scope, deniedList) ->
//                        scope.showForwardToSettingsDialog(deniedList, getString(R.string.permissions_allow_manually),
//                                                          getString(R.string.general_ok), getString(R.string.general_cancel)))
//                .request((allGranted, grantedList, deniedList) -> {
//                    if (!allGranted) {
//                        Toast toast = Toast.makeText(this, getString(R.string.permissions_some_denied), Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 400);
//                        toast.show();
//                    }
//                    /*else if (allGranted && startService) {
//                        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//                        this.onLocationChanged(null);
//                    }*/
//                });
//    }

    @Override
    public void onBackPressed() {
        // If the drawer navigation is open, close it if the back button is pressed
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    public boolean containsCaseInsensitive(String strToCompare, ArrayList<String> list)
    {
        for(String str:list)
        {
            if(str.equalsIgnoreCase(strToCompare))
            {
                return(true);
            }
        }
        return(false);
    }
}