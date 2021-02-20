package com.unipi.p17172p17168p17164.efruit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private Toolbox toolbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Activity opening animation when opened
        this.overridePendingTransition(R.anim.anim_slide_in_left,
                                       R.anim.anim_slide_out_left);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        toolbox = new Toolbox();
        updateUI();

        Toolbox toolbox = new Toolbox();
        TextView textViewProfileSignOut = findViewById(R.id.textViewProfileSignOut);

        textViewProfileSignOut.setOnClickListener(v -> { signOut(); });

        ImageView imageViewBackButton = findViewById(R.id.imageViewProfileBackButton);
        imageViewBackButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intentProfile = new Intent(ProfileActivity.this, SignInActivity.class);
        finish();
        startActivity(intentProfile);
    }

    public void updateUI() {
        if (firebaseUser != null) {
            TextView txtViewProfile_FullName = findViewById(R.id.txtViewProfile_FullName);
            txtViewProfile_FullName.setText(firebaseUser.getDisplayName());

            TextInputEditText txtInputProfile_FullName = findViewById(R.id.txtInputProfile_FullName);
            txtInputProfile_FullName.setText(firebaseUser.getDisplayName());
            txtInputProfile_FullName.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    toolbox.hideKeyboard(v, this);
                }
            });

            TextInputEditText txtInputProfile_Phone = findViewById(R.id.txtInputProfile_Phone);
            // Todo make it so the user phone is updated correctly.
//            txtInputProfile_Phone.setText(firebaseUser.getDisplayName());
            txtInputProfile_Phone.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    toolbox.hideKeyboard(v, this);
                }
            });

            TextInputLayout txtInputLayoutProfile_FullName = findViewById(R.id.txtInputLayoutProfile_FullName);
            txtInputLayoutProfile_FullName.setEndIconOnClickListener(v -> {
                txtInputProfile_FullName.setText("");
            });
            TextInputLayout txtInputLayoutProfile_Phone = findViewById(R.id.txtInputLayoutProfile_Phone);
            txtInputLayoutProfile_Phone.setEndIconOnClickListener(v -> {
                txtInputProfile_Phone.setText("");
            });

            // Load user img from google account if they haven't upload one.
            CircleImageView circleImgViewUserProfilePhoto = findViewById(R.id.circleImgProfile_Profile_Photo);
            // With the help of glide library we are able to load user profile picture into our app.
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(circleImgViewUserProfilePhoto);
            // Add a click event to redirect the user to profile settings if the user profile icon is clicked
            // Todo make it so that the user can upload/remove/change image picture.
            /*circleImgViewUserProfilePhoto.setOnClickListener(v -> {
                Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
            });*/
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                                       R.anim.anim_slide_out_right);
    }
}