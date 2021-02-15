package com.unipi.p17172.nikolaspateras.efruit.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.unipi.p17172.nikolaspateras.efruit.R;

public class Toolbox {
    public void hideKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public Dialog showDialogActionSuccessful(Context context, String alertTitle, String textParagraph) {
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_sms_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView textViewAlertSuccessTitle = dialog.findViewById(R.id.textViewAlertSuccessTitle);
        TextView textViewAlertSuccessTextParagraph = dialog.findViewById(R.id.textViewAlertSuccessTextParagraph);;

        textViewAlertSuccessTitle.setText(alertTitle);
        textViewAlertSuccessTextParagraph.setText(textParagraph);

        return dialog;
    }

    public Dialog showDialogActionEditSms(Context context, int smsNumber) {
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_sms_edit_sms);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        DBHelper dbHelper = new DBHelper(context);

        TextInputEditText textInputSmsNumber = dialog.findViewById(R.id.textInputEditTextEditPageSmsNumber);
        TextInputEditText textInputSmsReason = dialog.findViewById(R.id.textInputEditTextEditPageSmsReason);

        Cursor cursor = dbHelper.readSmsReason(smsNumber);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            textInputSmsNumber.setText(String.valueOf(smsNumber));
            textInputSmsReason.setText(cursor.getString(0));
        }

        return dialog;
    }

    public void insertDefaultRows(DBHelper dbHelper, Context context) {
        dbHelper.insertRow(1,  context.getString(R.string.sms_format_1));
        dbHelper.insertRow(2,  context.getString(R.string.sms_format_2));
        dbHelper.insertRow(3,  context.getString(R.string.sms_format_3));
        dbHelper.insertRow(4,  context.getString(R.string.sms_format_4));
        dbHelper.insertRow(5,  context.getString(R.string.sms_format_5));
        dbHelper.insertRow(6,  context.getString(R.string.sms_format_6));
    }
}
