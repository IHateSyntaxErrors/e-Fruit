package com.unipi.p17172p17168p17164.efruit.Activities;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;
import com.unipi.p17172p17168p17164.efruit.databinding.ActivitySelectTimeBinding;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SelectTimeActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,
                   TimePickerDialog.OnTimeSetListener {

    // ~~~~~~~VARIABLES~~~~~~~
    private ActivitySelectTimeBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private boolean defaultDate, defaultTime;
    private int nightModeFlags;
    private String shopId;
    Calendar[] days;
    List<Calendar> blockedDays = new ArrayList<>();
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectTimeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Activity opening animation when opened
        this.overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);

        init();
        getCartList();
        updateUI();
    }

    // Initialize variables
    private void init() {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        nightModeFlags =  getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        shopId = getIntent().getStringExtra("SHOP_ID");
        days = blockedDays.toArray(new Calendar[0]);

        defaultDate = true;
        defaultTime = true;

        // button on click listeners
        binding.imgBtnSelectTimeDate.setOnClickListener(v -> {
            try {
                openDatePicker();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        binding.imgBtnSelectTimeTime.setOnClickListener(v -> openTimePicker());
        binding.constraintLayoutSelectTimeNext.setOnClickListener(v -> pay());
    }

    private void openDatePicker() throws ParseException {
        getNonWorkingDays();

        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                SelectTimeActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            datePickerDialog.setThemeDark(true);

        datePickerDialog.setMinDate(now);
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);

        // Code to disable particular date
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Date date = formatter.parse("03/05/2021");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        blockedDays.add(cal);

        datePickerDialog.setDisabledDays(days);
        datePickerDialog.show(getSupportFragmentManager(), "DatePicker");
    }

    private void openTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                SelectTimeActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            timePickerDialog.setThemeDark(true);

        timePickerDialog.show(getSupportFragmentManager(), "TimePicker");
    }

    private void getNonWorkingDays() {
        Task<QuerySnapshot> queryNonWorkingDays = db.collection("shops")
                .document(shopId)
                .collection("working_times")
                .whereEqualTo("is_open", false).get();

        Task<QuerySnapshot> queryReservedDatesAndHours = db.collection("orders")
                .whereEqualTo("shopId", shopId).get();

        Tasks.whenAllComplete(queryNonWorkingDays, queryReservedDatesAndHours).addOnSuccessListener(list -> {
            for (DocumentSnapshot doc : queryNonWorkingDays.getResult()) {

            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        defaultDate = false;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, monthOfYear);

        binding.textViewSelectTimeDate.setText(String.format(Locale.getDefault(), "%02d, %s, %02d",
                dayOfMonth,
                cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
                year));
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        defaultTime = false;
        binding.textViewSelectTimeTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
    }

    private void pay() {
        if (defaultDate || defaultTime) {
            Dialog dialog = new Toolbox().showDialogDateTimeWarning(this);
            dialog.show();
        }
        else {
            /*Intent intent = new Intent(SelectTimeActivity.this, PayActivity.class);
            startActivity(intent);*/
        }

    }

    private void getCartList() {

    }

    public void updateUI() {
        binding.imageViewSelectTimeBackButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                                       R.anim.anim_slide_out_right);
    }


}