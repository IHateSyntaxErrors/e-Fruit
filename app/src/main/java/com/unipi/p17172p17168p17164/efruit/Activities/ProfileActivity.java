package com.unipi.p17172p17168p17164.efruit.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.unipi.p17172p17168p17164.efruit.Models.ModelUsers;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;
import com.unipi.p17172p17168p17164.efruit.databinding.ActivityProfileBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements Validator.ValidationListener {

    // ~~~~~~~VARIABLES~~~~~~~
    private ActivityProfileBinding binding;
    private FirebaseUser firebaseUser;
    private Toolbox toolbox;
    private FirebaseFirestore db;

    TextView textViewProfileSignOut;
    ImageView imageViewBackButton;
    TextView txtViewProfile_FullName;

    String userId;

    @Length(max = 50, message = "You can only enter 50 characters.")
    @com.mobsandgeeks.saripaar.annotation.Pattern(regex = "[/\\p{L}/u\\s]+", messageResId = R.string.validation_full_name)
    TextInputEditText txtInputProfile_FullName;

    @Length(max = 20, message = "You can only enter 20 characters.")
    @com.mobsandgeeks.saripaar.annotation.Pattern(regex = "[+0-9]+", messageResId = R.string.validation_digits)
    TextInputEditText txtInputProfile_Phone;

    TextInputLayout layoutTxtInput_FullName;
    TextInputLayout layoutTxtInput_Phone;
    MaterialButton btnSaveProfile;
    CircleImageView circleImgViewUserProfilePhoto;
    Validator validator;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Activity opening animation when opened
        this.overridePendingTransition(R.anim.anim_slide_in_left,
                                       R.anim.anim_slide_out_left);

        validator = new Validator(this);
        validator.setValidationListener(this);

        init();
        updateUI();
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intentSignInActivity = new Intent(ProfileActivity.this, SignInActivity.class);
        finish();
        startActivity(intentSignInActivity);
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = Objects.requireNonNull(firebaseUser).getUid();
        toolbox = new Toolbox();

        textViewProfileSignOut = binding.textViewProfileSignOut;
        imageViewBackButton = binding.imageViewProfileBackButton;
        txtViewProfile_FullName = binding.txtViewProfileFullName;
        txtInputProfile_FullName = binding.txtInputProfileFullName;
        txtInputProfile_Phone = binding.txtInputProfilePhone;
        btnSaveProfile = binding.buttonProfileSave;
        circleImgViewUserProfilePhoto = binding.circleImgProfileProfilePhoto;
        layoutTxtInput_FullName = binding.txtInputLayoutProfileFullName;
        layoutTxtInput_Phone = binding.txtInputLayoutProfilePhone;
    }

    public void updateUI() {
        imageViewBackButton.setOnClickListener(v -> onBackPressed());

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (Objects.requireNonNull(document).exists()) {
                        ModelUsers modelUsers = document.toObject(ModelUsers.class);

                        txtViewProfile_FullName.setText(Objects.requireNonNull(modelUsers).getFull_name());

                        txtInputProfile_FullName.setText(modelUsers.getFull_name());
                        txtInputProfile_Phone.setText(modelUsers.getPhone_number());
                    }
                }
            });

            layoutTxtInput_FullName.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    Toolbox.hideKeyboard(v, this);
                }
            });

            layoutTxtInput_Phone.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    Toolbox.hideKeyboard(v, this);
                }
            });

            btnSaveProfile.setOnClickListener(v-> validator.validate());

            // Load user img from google account if they haven't upload one.
            // With the help of glide library we are able to load user profile picture into our app.
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(circleImgViewUserProfilePhoto);
            // Add a click event to redirect the user to profile settings if the user profile icon is clicked
            // Todo make it so that the user can upload/remove/change image picture.
            /*circleImgViewUserProfilePhoto.setOnClickListener(v -> {
                Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
            });*/

            textViewProfileSignOut.setOnClickListener(v -> signOut());
        }
    }

    private void saveProfile(String userId) {
        String full_name = String.valueOf(txtInputProfile_FullName.getText());
        String phone = String.valueOf(txtInputProfile_Phone.getText());

        Map< String, Object > updatedUser = new HashMap< >();
        updatedUser.put("full_name", full_name);
        updatedUser.put("phone_number", phone);
        updatedUser.put("userId", userId);

        db.collection("users").document(userId).update(updatedUser).addOnCompleteListener(s -> {
            Dialog dialog = toolbox.showDialogPersonalInfoSaved(this);
            dialog.show();
            txtViewProfile_FullName.setText(full_name);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                                       R.anim.anim_slide_out_right);
    }

    @Override
    public void onValidationSucceeded() {
        saveProfile(userId);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}