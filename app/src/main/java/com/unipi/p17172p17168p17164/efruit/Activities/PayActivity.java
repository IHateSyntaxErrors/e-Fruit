package com.unipi.p17172p17168p17164.efruit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.DBHelper;
import com.unipi.p17172p17168p17164.efruit.databinding.ActivityPayBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PayActivity extends AppCompatActivity implements Validator.ValidationListener {

    // ~~~~~~~VARIABLES~~~~~~~
    private ActivityPayBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private String shopId;
    private String grandTotal;
    private Date pickup_timestamp;

    @Length(max = 19, message = "You can only enter 19 digits.")
    @com.mobsandgeeks.saripaar.annotation.Pattern(regex = "[+0-9]+", messageResId = R.string.validation_digits)
    TextInputEditText txtInputCardNumber;

    @Length(max = 4, message = "You can only enter 4 digits.")
    @com.mobsandgeeks.saripaar.annotation.Pattern(regex = "[+0-9]+", messageResId = R.string.validation_digits)
    TextInputEditText txtInputSecurityCode;

    TextInputEditText txtInputExpireDate;

    Validator validator;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Activity opening animation when opened
        this.overridePendingTransition(R.anim.anim_slide_in_left,
                                       R.anim.anim_slide_out_left);

        init();
        updateUI();
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        grandTotal = getIntent().getStringExtra("GRAND_TOTAL");
        shopId = getIntent().getStringExtra("SHOP_ID");
        pickup_timestamp = new Date();
        pickup_timestamp.setTime(getIntent().getLongExtra("PICKUP_TIMESTAMP", -1));

        txtInputCardNumber = binding.txtInputPayCardNumber;
        txtInputSecurityCode = binding.txtInputPaySecurityCode;
        txtInputExpireDate = binding.txtInputPayExpireDate;

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    private void updateUI() {
        binding.imageViewPayBackButton.setOnClickListener(v -> onBackPressed());
        binding.textViewPayGrandTotalValue.setText(String.valueOf(grandTotal));
        binding.btnPayPay.setOnClickListener(v -> validator.validate());
    }

    @Override
    public void onValidationSucceeded() { pay(); }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages
            if (view instanceof TextInputEditText)
                ((TextInputEditText) view).setError(message);
            else
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void pay() {
        Task<QuerySnapshot> queryCartProducts = DBHelper.getCartProducts(db, firebaseUser.getUid()).get();

        Tasks.whenAllComplete(queryCartProducts)
        .addOnSuccessListener(list -> {
            ArrayList<String> productsList = new ArrayList<>();
            for (DocumentSnapshot doc : queryCartProducts.getResult()) {
                        productsList.add(Objects.requireNonNull(doc.getData()).get("amount") + "x "
                              + doc.getString("name"));
            }

            DBHelper.createOrder(db, FieldValue.serverTimestamp(), Double.parseDouble(grandTotal.trim().replace("€", "")),
                    firebaseUser.getUid(), shopId, productsList, pickup_timestamp)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DBHelper.deleteCart(db, firebaseUser.getUid());
                            Intent intent = new Intent(PayActivity.this, PaymentConfirmationActivity.class);
                            startActivity(intent);
                        }
                    });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                                       R.anim.anim_slide_out_right);
    }
}