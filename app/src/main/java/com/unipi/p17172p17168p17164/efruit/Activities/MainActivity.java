package com.unipi.p17172p17168p17164.efruit.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unipi.p17172p17168p17164.efruit.Fragments.FragmentHome;
import com.unipi.p17172p17168p17164.efruit.Fragments.FragmentSettings;
import com.unipi.p17172p17168p17164.efruit.R;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // ~~~~~~~VARIABLES~~~~~~~
    private DrawerLayout drawerLayout;
    private MenuItem prevMenuItem;

    private FirebaseUser firebaseUser;

    private static final int REQUEST_CODE_SPEECH_INPUT = 10;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        updateUI();

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_top);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                                                                 R.string.nav_drawer_close, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        // Change drawer arrow icon
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.colorIcon));
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
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

        if (id == R.id.nav_item_home)
        {
            // Home Fragment
            FragmentHome fragmentHome = new FragmentHome();
            setFragment(fragmentHome);
        }
        else if (id == R.id.nav_item_products)
        {
            // Products Fragment
            FragmentHome homeFragment = new FragmentHome();
            setFragment(homeFragment);
        }
        else if (id == R.id.nav_item_sales)
        {
            // Home Fragment
            FragmentHome homeFragment = new FragmentHome();
            setFragment(homeFragment);
        }
        else if (id == R.id.nav_item_cart)
        {
            // Home Fragment
            FragmentHome homeFragment = new FragmentHome();
            setFragment(homeFragment);
        }
        else if (id == R.id.nav_item_profile)
        {
            // Home Fragment
            FragmentHome homeFragment = new FragmentHome();
            setFragment(homeFragment);
        }
        else if (id == R.id.nav_item_previous_orders)
        {
            // Home Fragment
            FragmentHome homeFragment = new FragmentHome();
            setFragment(homeFragment);
        }
        else if (id == R.id.nav_item_settings)
        {
            // Home Fragment
            FragmentSettings fragmentSettings = new FragmentSettings();
            setFragment(fragmentSettings);
        }
        else if (id == R.id.nav_item_exit)
        {
            this.finish();
            System.exit(0);
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
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_item_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
                }
                else if (containsCaseInsensitive("products", result)
                        || containsCaseInsensitive("go to products", result)) {
                    // Change to products fragment
                }
                else if (containsCaseInsensitive("settings", result)
                        || containsCaseInsensitive("go to settings", result)) {
                    // Change to settings fragment
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

    public void updateUI() {
        if (firebaseUser != null) {
            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);

            TextView textViewName = headerView.findViewById(R.id.textViewNavBar_Name);
            textViewName.setText(firebaseUser.getDisplayName());
            // Todo make it so the user phone is updated correctly.
            TextView textViewPhone = headerView.findViewById(R.id.textViewNavBar_Phone);
            textViewPhone.setText(firebaseUser.getDisplayName());

            ImageButton imgBtnMic = findViewById(R.id.action_bar_mic);
            imgBtnMic.setOnClickListener(v -> {
                speechToText();
            });
            ImageButton imgBtnCart = findViewById(R.id.action_bar_cart);
            imgBtnCart.setOnClickListener(v -> {
                // Todo add on cart icon press actions.
            });

            // Load user img from google account if they haven't upload one.
            CircleImageView circleImgViewUserAccount = findViewById(R.id.action_bar_circleimgview_profile);
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

    @Override
    public void onBackPressed() {
        // If the drawer navigation is open, close it if the back button is pressed
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
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