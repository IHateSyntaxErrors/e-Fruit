package com.unipi.p17172.nikolaspateras.efruit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unipi.p17172.nikolaspateras.efruit.Adapters.ViewPagerAdapter;
import com.unipi.p17172.nikolaspateras.efruit.Fragments.FragmentHome;
import com.unipi.p17172.nikolaspateras.efruit.Fragments.FragmentSettings;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPagerFragments;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient googleSignInClient;

    MenuItem prevMenuItem;

    private static final int REQUEST_CODE_SPEECH_INPUT = 10;
    // fragments
    final Fragment fragmentHome = new FragmentHome();
    final Fragment fragmentSettings = new FragmentSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        updateUI();

        viewPagerFragments = findViewById(R.id.viewPagerFragments);
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        ImageView imageViewMicrophone = findViewById(R.id.imageViewMicrophone);
        CircleImageView circleImageViewProfileImage = findViewById(R.id.circleImageViewProfileImage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    viewPagerFragments.setCurrentItem(0);
                    break;
                case R.id.nav_settings:
                    viewPagerFragments.setCurrentItem(1);
                    break;
            }
            return true;
        });

        viewPagerFragments.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);

                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        setupViewPager(viewPagerFragments);

        imageViewMicrophone.setOnClickListener(v -> speechToText());
        circleImageViewProfileImage.setOnClickListener(v -> {
            Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intentProfile);
        });
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragmentHome);
        adapter.addFragment(fragmentSettings);
        viewPager.setAdapter(adapter);
    }

    public void speechToText() {
//        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
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

                if (containsCaseInsensitive("home", result)) {
                    viewPagerFragments.setCurrentItem(0);
                }
                else if (containsCaseInsensitive("violations", result)
                        || containsCaseInsensitive("go to violations", result)) {
                    viewPagerFragments.setCurrentItem(1);
                }
                else if (containsCaseInsensitive("settings", result)
                        || containsCaseInsensitive("go to settings", result)) {
                    viewPagerFragments.setCurrentItem(2);
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
            TextInputEditText txtInputFullName = findViewById(R.id.textInputEditTextFullName);
            TextInputEditText txtInputAddress = findViewById(R.id.textInputEditTextAddress);

            txtInputFullName.setText(firebaseUser.getDisplayName());
            ImageView userImage = findViewById(R.id.circleImageViewProfileImage);
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(userImage);

        }
        // Else the user is not logged in - do actions
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