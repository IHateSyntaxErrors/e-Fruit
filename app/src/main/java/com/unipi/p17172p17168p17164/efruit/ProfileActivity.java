package com.unipi.p17172p17168p17164.efruit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.unipi.p17172p17168p17164.efruit.efruit.Utils.Toolbox;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.overridePendingTransition(R.anim.anim_slide_in_left,
                                       R.anim.anim_slide_out_left);

        Toolbox toolbox = new Toolbox();
        TextView textViewProfileSignOut = findViewById(R.id.textViewProfileSignOut);

        TextInputEditText textInputEditTextProfileFullName = findViewById(R.id.textInputEditTextProfileFullName);
        textInputEditTextProfileFullName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                toolbox.hideKeyboard(v, ProfileActivity.this);
            }
        });

        TextInputEditText textInputEditTextProfileAddress = findViewById(R.id.textInputEditTextProfileAddress);
        textInputEditTextProfileAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                toolbox.hideKeyboard(v, ProfileActivity.this);
            }
        });

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                                       R.anim.anim_slide_out_right);
    }
}