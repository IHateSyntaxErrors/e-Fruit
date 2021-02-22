package com.unipi.p17172p17168p17164.efruit.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.unipi.p17172p17168p17164.efruit.Models.ModelUsers;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    // ~~~~~~~VARIABLES~~~~~~~
    private FirebaseUser firebaseUser;
    private Toolbox toolbox;
    private FirebaseFirestore db;

    @BindView(R.id.textViewProfileSignOut)
    TextView textViewProfileSignOut;
    @BindView(R.id.imageViewProfileBackButton)
    ImageView imageViewBackButton;
    @BindView(R.id.txtViewProfile_FullName)
    TextView txtViewProfile_FullName;
    @BindView(R.id.txtInputProfile_FullName)
    TextInputEditText txtInputProfile_FullName;
    @BindView(R.id.txtInputProfile_Phone)
    TextInputEditText txtInputProfile_Phone;

    @BindView(R.id.buttonProfile_Save)
    MaterialButton btnSaveProfile;

    @BindView(R.id.circleImgProfile_Profile_Photo)
    CircleImageView circleImgViewUserProfilePhoto;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Activity opening animation when opened
        this.overridePendingTransition(R.anim.anim_slide_in_left,
                                       R.anim.anim_slide_out_left);
        ButterKnife.bind(this);
        init();
        updateUI();

        textViewProfileSignOut.setOnClickListener(v -> { signOut(); });

        imageViewBackButton.setOnClickListener(v -> {
            onBackPressed();
        });
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
        toolbox = new Toolbox();
    }

    public void updateUI() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String tokenId = firebaseUser.getUid();

            DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ModelUsers modelUsers = document.toObject(ModelUsers.class);

                        txtViewProfile_FullName.setText(modelUsers.getFull_name());

                        txtInputProfile_FullName.setText(modelUsers.getFull_name());
                        txtInputProfile_Phone.setText(modelUsers.getPhone_number());
                    }
                }
            });

            txtInputProfile_FullName.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    Toolbox.hideKeyboard(v, this);
                }
            });

            txtInputProfile_Phone.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    Toolbox.hideKeyboard(v, this);
                }
            });

            btnSaveProfile.setOnClickListener(v-> {
                saveProfile(tokenId);
            });

            // Load user img from google account if they haven't upload one.
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

    private void saveProfile(String tokenId) {
        String full_name = String.valueOf(txtInputProfile_FullName.getText());
        String phone = String.valueOf(txtInputProfile_Phone.getText());

        Map< String, Object > updatedUser = new HashMap< >();
        updatedUser.put("full_name", full_name);
        updatedUser.put("phone_number", phone);
        updatedUser.put("tokenId", tokenId);

        db.collection("users").document(tokenId).update(updatedUser).addOnSuccessListener(s -> {
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
}