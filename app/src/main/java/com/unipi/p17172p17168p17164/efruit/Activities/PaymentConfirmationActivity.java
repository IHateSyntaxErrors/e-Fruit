package com.unipi.p17172p17168p17164.efruit.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.databinding.ActivityPaymentConfirmationBinding;

public class PaymentConfirmationActivity extends AppCompatActivity {

    // ~~~~~~~VARIABLES~~~~~~~
    private ActivityPaymentConfirmationBinding binding;
    private String grandTotal;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentConfirmationBinding.inflate(getLayoutInflater());
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
    }

    private void updateUI() {
        binding.imageViewPaymentSuccessBackButton.setOnClickListener(v -> onBackPressed());
        binding.btnPayBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                                       R.anim.anim_slide_out_right);
    }
}