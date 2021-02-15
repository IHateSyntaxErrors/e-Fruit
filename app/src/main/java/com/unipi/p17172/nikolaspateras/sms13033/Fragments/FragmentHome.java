package com.unipi.p17172.nikolaspateras.sms13033.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unipi.p17172.nikolaspateras.sms13033.Adapters.DataAdapter;
import com.unipi.p17172.nikolaspateras.sms13033.Items.Item;
import com.unipi.p17172.nikolaspateras.sms13033.R;
import com.unipi.p17172.nikolaspateras.sms13033.Utils.DBHelper;
import com.unipi.p17172.nikolaspateras.sms13033.Utils.Toolbox;

import java.util.ArrayList;

public class FragmentHome extends Fragment {
    private Context context;
    private View view;
    private ArrayList<Item> arrayListSmsType;
    private DataAdapter dataAdapterSmsType;

    private TextInputEditText textInputEditTextFullName;
    private TextInputEditText textInputEditTextAddress;
    private ViewFlipper viewFlipperSmsType;
    private MaterialButton buttonSendSms;

    private Toolbox toolbox;
    private DBHelper dbHelper;
    LocationManager locationManager;

    private final String SMS_RECEIVER_PHONE_NUMBER = "+3013033";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity();
        dbHelper = new DBHelper(context);
    }

    public void initializeRecyclerView(){
        RecyclerView recyclerViewSmsType = view.findViewById(R.id.recyclerViewSmsType);
        // Attaching data to Recycler
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        dataAdapterSmsType = new DataAdapter(getActivity(), arrayListSmsType);
        recyclerViewSmsType.setHasFixedSize(true);
        recyclerViewSmsType.setLayoutManager(linearLayoutManager);
        recyclerViewSmsType.setAdapter(dataAdapterSmsType);
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeRecyclerView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        buttonSendSms = view.findViewById(R.id.materialButtonSendSms);
        textInputEditTextFullName = view.findViewById(R.id.textInputEditTextFullName);
        textInputEditTextAddress = view.findViewById(R.id.textInputEditTextAddress);
        TextInputLayout textInputLayoutFullName = view.findViewById(R.id.textInputLayoutFullName);
        TextInputLayout textInputLayoutAddress = view.findViewById(R.id.textInputLayoutAddress);
        viewFlipperSmsType = view.findViewById(R.id.viewFlipperSmsType);
        arrayListSmsType = new ArrayList<>();

        toolbox = new Toolbox();

        buttonSendSms.setOnClickListener(v -> sendButtonClick());

        textInputEditTextFullName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                toolbox.hideKeyboard(v, context);
            }
        });
        textInputEditTextAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                toolbox.hideKeyboard(v, context);
            }
        });

        textInputLayoutFullName.setEndIconOnClickListener(v -> textInputEditTextFullName.setText(""));
        textInputLayoutAddress.setEndIconOnClickListener(v -> textInputEditTextAddress.setText(""));


        if (dbHelper.numberOfRows() == 0) {
            toolbox.insertDefaultRows(dbHelper, context);
        }
        loadAllDatabaseData();

        return view;
    }

    public void loadAllDatabaseData() {
        Cursor cursor = dbHelper.readAllData();
        if (cursor != null && cursor.getCount() > 0) {
            // Setting the view to the sms types found mode.
            viewFlipperSmsType.setDisplayedChild(0);
            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setSmsNumber(cursor.getString(1));
                    item.setSmsReason(cursor.getString(2));
                    arrayListSmsType.add(item);
                } while (cursor.moveToNext());
            }
        }
        else {
            // Setting the view to show that nothing was found in the database.
            viewFlipperSmsType.setDisplayedChild(1);
        }
    }

    public void sendButtonClick() {
        if (!buttonSendSms.isEnabled()) {
            // TODO Warning dialog
            return;
        }

        String smsNumber = dataAdapterSmsType.getSelected().getSmsNumber();
        if (dataAdapterSmsType.getSelected() != null) {
            /* WARNING: This will send a message */
            /*sendSMS(smsNumber,
                    String.valueOf(textInputEditTextFullName.getText()),
                    String.valueOf(textInputEditTextAddress.getText()));*/
        }
        else {
            // TODO show error dialog
        }

        Dialog dialogSmsSentConfirmation = toolbox.showDialogActionSuccessful(context,
                getString(R.string.dialog_sms_sent_title),
                getString(R.string.dialog_sms_sent_description, smsNumber));

        MaterialButton mDialogNo = dialogSmsSentConfirmation.findViewById(R.id.buttonAlertSuccessButtonOK);
        mDialogNo.setOnClickListener(v -> {
            dialogSmsSentConfirmation.dismiss();
        });
        dialogSmsSentConfirmation.show();
    }

    public void sendSMS(String smsNumber, String smsFullName, String smsAddress) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String msg = smsNumber + " " + smsFullName + " " + smsAddress;

            smsManager.sendTextMessage(SMS_RECEIVER_PHONE_NUMBER, null, msg, null, null);
            // TODO Success dialog
        }
        catch (Exception ex) {
            ex.printStackTrace();
            // TODO Fail dialog
        }
    }
}