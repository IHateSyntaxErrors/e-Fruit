package com.unipi.p17172p17168p17164.efruit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.databinding.ActivityPayBinding;

public class PayActivity extends AppCompatActivity {

    // ~~~~~~~VARIABLES~~~~~~~
    private ActivityPayBinding binding;
    private String grandTotal;
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
        binding.btnPayPay.setOnClickListener(v -> {
            Intent intent = new Intent(PayActivity.this, PaymentConfirmationActivity.class);
            intent.putExtra("GRAND_TOTAL", grandTotal);
            startActivity(intent);
        });
    }

    private void updateUI() {
        binding.imageViewPayBackButton.setOnClickListener(v -> onBackPressed());
        binding.textViewPayGrandTotalValue.setText(grandTotal);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                                       R.anim.anim_slide_out_right);
    }
}