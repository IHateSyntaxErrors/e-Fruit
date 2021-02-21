package com.unipi.p17172p17168p17164.efruit.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orhanobut.hawk.Hawk;
import com.unipi.p17172p17168p17164.efruit.Models.ModelUsers;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.PrefsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity {
    // ~~~~~~~VARIABLES~~~~~~~
    private GoogleSignInClient googleSignInClient;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @BindView(R.id.materialButtonSignUpGoogle)
    MaterialButton btnGoogleSignIn;
    @BindView(R.id.imgFacebook)
    ImageView imgFacebook;
    @BindView(R.id.imgInstagram)
    ImageView imgInstagram;
    @BindView(R.id.imgTwitter)
    ImageView imgTwitter;
    private static final int RC_SIGN_IN = 101;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);
        init();

        Hawk.init(this).build(); // Initializing Hawk API.
        new PrefsUtils(this).initKeys(); // Add keys to the prefs if they don't exists.

        // Listener to check if user is logged in, on activity create.
        if (GoogleSignIn.getLastSignedInAccount(this) != null && firebaseUser != null)
            isSignedIn(firebaseAuth);

        // Configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btnGoogleSignIn.setOnClickListener(v -> {
            signIn();
        });

        /* Social Media buttons -- Real pages haven't been specified, this is just an example of
        how we would do it*/
        imgFacebook.setOnClickListener(v -> {
            openFacebookPage();
        });
        imgInstagram.setOnClickListener(v -> {
            openInstagramPage();
        });
        imgTwitter.setOnClickListener(v -> {
            openTwitterPage();
        });
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void isSignedIn(FirebaseAuth auth) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // if the user is not null which means they logged in before, redirect them to main
            // activity automatically.
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase.
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }
            catch (ApiException e) {
                // Google Sign In failed.
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in successful.

                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                            String tokenId = account.getIdToken();

                            ModelUsers modelUsers = new ModelUsers(userName, tokenId, "", "");
                            // Insert user data into cloud.
                            db.collection("users").document(userId).set(modelUsers);

                            // Create the intent for the new activity and redirect the user to main activity.
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "Sign in failed!", Toast.LENGTH_LONG).show();
                        }
                    });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is already logged in.
        if (GoogleSignIn.getLastSignedInAccount(this) != null && firebaseUser != null) {
            isSignedIn(firebaseAuth);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // if back button pressed, just close the app.
    }

    private void openFacebookPage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://www.facebook.com"));
        startActivity(intent);
    }
    private void openTwitterPage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://www.twitter.com"));
        startActivity(intent);
    }
    private void openInstagramPage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://www.instagram.com"));
        startActivity(intent);
    }
}