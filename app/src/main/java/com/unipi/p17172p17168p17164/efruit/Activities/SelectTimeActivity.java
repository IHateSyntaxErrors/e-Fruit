package com.unipi.p17172p17168p17164.efruit.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;
import com.unipi.p17172p17168p17164.efruit.databinding.ActivitySelectTimeBinding;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private String grandTotal;

    SimpleDateFormat dateFormatter;
    SimpleDateFormat timeFormatter;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    int Year, Month, Day, Hour, Minute;
    Calendar calendar ;
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
        updateUI();
    }

    // Initialize variables
    private void init() {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        nightModeFlags =  getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        shopId = getIntent().getStringExtra("SHOP_ID");
        grandTotal = getIntent().getStringExtra("GRAND_TOTAL");

        defaultDate = true;
        defaultTime = true;

        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);

        // button on click listeners
        binding.imgBtnSelectTimeDate.setOnClickListener(v -> openDatePicker());
        binding.imgBtnSelectTimeTime.setOnClickListener(v -> openTimePicker(Hour, Minute));
        binding.constraintLayoutSelectTimeNext.setOnClickListener(v -> pay());
    }

    private void openDatePicker() {
        Calendar now = Calendar.getInstance();
        if (defaultDate) {
            datePickerDialog = DatePickerDialog.newInstance(
                    SelectTimeActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }
        else {
            datePickerDialog = DatePickerDialog.newInstance(
                    SelectTimeActivity.this,
                    Year,
                    Month,
                    Day
            );
        }


        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            datePickerDialog.setThemeDark(true);

        // Setting Min Date to today date
        Calendar min_date_c = Calendar.getInstance();
        datePickerDialog.setMinDate(min_date_c);
        // Setting Max Date to next 1 year
        Calendar max_date_c = Calendar.getInstance();
        max_date_c.set(Calendar.YEAR, Year + 1);
        datePickerDialog.setMaxDate(max_date_c);

        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);

        //Disable all SUNDAYS and SATURDAYS between Min and Max Dates
        for (Calendar loopdate = min_date_c; min_date_c.before(max_date_c); min_date_c.add(Calendar.DATE, 1), loopdate = min_date_c) {
            int dayOfWeek = loopdate.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                Calendar[] disabledDays =  new Calendar[1];
                disabledDays[0] = loopdate;
                datePickerDialog.setDisabledDays(disabledDays);
            }
        }
        datePickerDialog.show(getSupportFragmentManager(), "DatePicker");
    }

    private void openTimePicker() {
        Calendar now = Calendar.getInstance();
        timePickerDialog = TimePickerDialog.newInstance(
                SelectTimeActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                now.get(0),
                false
        );

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            timePickerDialog.setThemeDark(true);


        if (now.get(Calendar.HOUR_OF_DAY) < 8
                && (Day == now.get(Calendar.DAY_OF_MONTH))
                && (Month == now.get(Calendar.MONTH))
                && (Year == now.get(Calendar.YEAR)))
            timePickerDialog.setMinTime(8, 30, 0);
        else
            timePickerDialog.setMinTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE + 30, Calendar.SECOND);
        timePickerDialog.setMaxTime(18, 30, 0);
        timePickerDialog.show(getSupportFragmentManager(), "TimePicker");
        binding.imgBtnSelectTimeTime.setVisibility(View.VISIBLE);
        binding.textViewSelectTimeTime.setVisibility(View.VISIBLE);
    }

    private void openTimePicker(int hour, int minutes) {
        Calendar now = Calendar.getInstance();
        timePickerDialog = TimePickerDialog.newInstance(
                SelectTimeActivity.this,
                hour,
                minutes,
                0,
                false
        );

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            timePickerDialog.setThemeDark(true);


        if (now.get(Calendar.HOUR_OF_DAY) < 8)
            timePickerDialog.setMinTime(8, 30, 0);
        else
            timePickerDialog.setMinTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE + 30, Calendar.SECOND);
        timePickerDialog.setMaxTime(18, 30, 0);
        timePickerDialog.show(getSupportFragmentManager(), "TimePicker");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Day = dayOfMonth; Month = monthOfYear; Year = year;
        defaultDate = false;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, monthOfYear);

        binding.textViewSelectTimeDate.setText(String.format(Locale.getDefault(), "%02d, %s, %02d",
                dayOfMonth,
                cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
                year));
        openTimePicker();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Hour = hourOfDay; Minute = minute;
        defaultTime = false;

        binding.textViewSelectTimeTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
        binding.textViewSelectTimeTime.setVisibility(View.VISIBLE);
        binding.imgBtnSelectTimeTime.setVisibility(View.VISIBLE);
    }

    private void pay() {
        if (defaultDate || defaultTime) {
            Dialog dialog = new Toolbox().showDialogDateTimeWarning(this);
            dialog.show();
        }
        else {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Year);
            cal.set(Calendar.MONTH, Month);
            cal.set(Calendar.DAY_OF_MONTH, Day);
            cal.set(Calendar.HOUR_OF_DAY, Hour);
            cal.set(Calendar.MINUTE, Minute);
            cal.set(Calendar.SECOND, 0);
            Date dateRepresentation = cal.getTime();

            Intent intent = new Intent(SelectTimeActivity.this, PayActivity.class);
            intent.putExtra("GRAND_TOTAL", grandTotal);
            intent.putExtra("SHOP_ID", shopId);
            intent.putExtra("PICKUP_TIMESTAMP", dateRepresentation.getTime());
            startActivity(intent);
        }

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