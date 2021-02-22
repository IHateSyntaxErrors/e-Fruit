package com.unipi.p17172p17168p17164.efruit.Activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.permissionx.guolindev.PermissionX;
import com.unipi.p17172p17168p17164.efruit.Fragments.FragmentHome;
import com.unipi.p17172p17168p17164.efruit.Fragments.FragmentProducts;
import com.unipi.p17172p17168p17164.efruit.Fragments.FragmentSettings;
import com.unipi.p17172p17168p17164.efruit.Fragments.FragmentShops;
import com.unipi.p17172p17168p17164.efruit.Models.ModelUsers;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.PermissionsUtils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // ~~~~~~~VARIABLES~~~~~~~
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private FirebaseUser firebaseUser;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private static final String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                                                 Manifest.permission.ACCESS_FINE_LOCATION,
                                                 Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND,
                                                 Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                                 Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_CODE_SPEECH_INPUT = 10;
    private FirebaseFirestore db;
    @BindView(R.id.action_bar_circleimgview_profile)
    CircleImageView circleImgViewUserAccount;
    @BindView(R.id.action_bar_mic)
    ImageButton imgBtnMic;
    @BindView(R.id.action_bar_cart)
    ImageButton imgBtnCart;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        init();

        updateUI(); // Update UI with user's info.

        if (!PermissionsUtils.hasPermissions(this, permissions))
            requestPermissions(); // Check if permissions are allowed.

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_top);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                                                                 R.string.nav_drawer_close, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        // Change drawer arrow icon
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.colorIcon));
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_item_home);

        // Add the home fragment to show it in the frame layout of main activity.
        FragmentHome fragmentHome = new FragmentHome();
        setFragment(fragmentHome);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = new FragmentHome();
        if (id == R.id.nav_item_home)
        {
            // Home Fragment
            fragment = new FragmentHome();
        }
        else if (id == R.id.nav_item_products)
        {
            // Products Fragment
            fragment = new FragmentShops();
        }
        else if (id == R.id.nav_item_cart)
        {
            // Home Fragment
            fragment = new FragmentShops();
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
            // Home Fragment
            fragment = new FragmentHome();
        }
        else if (id == R.id.nav_item_settings)
        {
            // Home Fragment
            fragment = new FragmentSettings();
        }
        else if (id == R.id.nav_item_exit)
        {
            this.finish();
            System.exit(0);
            return false;
        }

        setFragment(fragment);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.nav_item_profile)
        {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    // Method that will change fragments when a item is selected from the navigation drawer.
    private void setFragment(Fragment fragment)
    {
        //get current fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        //get fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //set new fragment in fragment_container (FrameLayout)
        fragmentTransaction.replace(R.id.fragment_container, fragment);
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

                // Todo fix this
                if (containsCaseInsensitive("home", result)) {
                    // Change to home fragment
                    FragmentHome fragmentHome = new FragmentHome();
                    setFragment(fragmentHome);
                }
                else if (containsCaseInsensitive("products", result)
                        || containsCaseInsensitive("go to products", result)
                        || containsCaseInsensitive("fruits", result)) {
                    // Change to products fragment
                   /* FragmentProducts fragmentProducts = new FragmentProducts();
                    setFragment(fragmentProducts);*/
                }
                else if (containsCaseInsensitive("settings", result)
                        || containsCaseInsensitive("go to settings", result)) {
                    // Change to settings fragment
                    FragmentSettings fragmentSettings = new FragmentSettings();
                    setFragment(fragmentSettings);
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
                    if (document.exists()) {
                        ModelUsers modelUsers = document.toObject(ModelUsers.class);

                        View headerView = navigationView.getHeaderView(0);
                        TextView textViewName = headerView.findViewById(R.id.textViewNavBar_Name);
                        textViewName.setText(modelUsers.getFull_name());

                        TextView textViewPhone = headerView.findViewById(R.id.textViewNavBar_Phone);
                        textViewPhone.setText(modelUsers.getPhone_number());
                    }
                }
            });

            imgBtnMic.setOnClickListener(v -> {
                speechToText();
            });
            imgBtnCart.setOnClickListener(v -> {
                // Todo add on cart icon press actions.
            });

            // With the help of glide library we are able to load user profile picture into our app.
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(circleImgViewUserAccount);
            // Add a click event to redirect the user to profile settings if the user profile icon is clicked
            circleImgViewUserAccount.setOnClickListener(v -> {
                Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
            });
        }
        // Else the user is not logged in - do actions
        // else {}
    }

    public void requestPermissions() {
        PermissionX.init(this)
                .permissions(permissions)
                .onExplainRequestReason((scope, deniedList) ->
                        scope.showRequestReasonDialog(deniedList, getString(R.string.permission_allow_ask_reason),
                                                      getString(R.string.general_ok), getString(R.string.general_cancel)))
                .onForwardToSettings((scope, deniedList) ->
                        scope.showForwardToSettingsDialog(deniedList, getString(R.string.permissions_allow_manually),
                                                          getString(R.string.general_ok), getString(R.string.general_cancel)))
                .request((allGranted, grantedList, deniedList) -> {
                    if (!allGranted) {
                        Toast toast = Toast.makeText(this, getString(R.string.permissions_some_denied), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 400);
                        toast.show();
                    }
                    /*else if (allGranted && startService) {
                        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                        this.onLocationChanged(null);
                    }*/
                });
    }

    @Override
    public void onBackPressed() {
        // If the drawer navigation is open, close it if the back button is pressed
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
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