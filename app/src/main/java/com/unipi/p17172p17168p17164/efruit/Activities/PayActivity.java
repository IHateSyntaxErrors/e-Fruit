package com.unipi.p17172p17168p17164.efruit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.CreditCard;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.databinding.ActivityPayBinding;

import java.util.List;

public class PayActivity extends AppCompatActivity implements Validator.ValidationListener {

    // ~~~~~~~VARIABLES~~~~~~~
    private ActivityPayBinding binding;
    private String shopId;
    private String grandTotal;
    private String pickup_timestamp;

    @Length(max = 19, message = "You can only enter 19 digits.")
    @CreditCard(messageResId = R.string.validation_digits)
    TextInputEditText txtInputCardNumber;

    @Length(max = 19, message = "You can only enter 19 digits.")
    @com.mobsandgeeks.saripaar.annotation.Pattern(regex = "[/\\p{L}/u\\s]+", messageResId = R.string.validation_digits)
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
        grandTotal = getIntent().getStringExtra("GRAND_TOTAL");
        shopId = getIntent().getStringExtra("SHOP_ID");
        pickup_timestamp = getIntent().getStringExtra("PICKUP_TIMESTAMP");

        Toast.makeText(this, pickup_timestamp, Toast.LENGTH_SHORT).show();
        txtInputCardNumber = binding.txtInputPayCardNumber;
        txtInputSecurityCode = binding.txtInputPaySecurityCode;
        txtInputExpireDate = binding.txtInputPayExpireDate;

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    private void updateUI() {
        binding.imageViewPayBackButton.setOnClickListener(v -> onBackPressed());
        binding.textViewPayGrandTotalValue.setText(grandTotal);
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
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void pay() {
        Intent intent = new Intent(PayActivity.this, PaymentConfirmationActivity.class);
        intent.putExtra("GRAND_TOTAL", grandTotal);
        intent.putExtra("SHOP_ID", shopId);
        intent.putExtra("PICKUP_TIMESTAMP", pickup_timestamp);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                                       R.anim.anim_slide_out_right);
    }
}