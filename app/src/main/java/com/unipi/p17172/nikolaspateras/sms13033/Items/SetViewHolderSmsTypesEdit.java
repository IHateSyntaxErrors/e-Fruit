package com.unipi.p17172.nikolaspateras.sms13033.Items;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.unipi.p17172.nikolaspateras.sms13033.R;
import com.unipi.p17172.nikolaspateras.sms13033.Utils.Toolbox;

public class SetViewHolderSmsTypesEdit extends RecyclerView.ViewHolder {
    public TextView viewTextViewNumberEdit;
    public TextView viewTextViewSmsExplanationEdit;
    public ImageView editSms;
    public ImageView deleteSms;
    private Toolbox toolbox;
    private Context context;

    public SetViewHolderSmsTypesEdit(@NonNull View itemView, @NonNull Context context) {
        super(itemView);
        this.context = context;

        toolbox = new Toolbox();

        viewTextViewNumberEdit = itemView.findViewById(R.id.textViewNumberEdit);
        viewTextViewSmsExplanationEdit = itemView.findViewById(R.id.textViewSmsExplanationEdit);
        editSms = itemView.findViewById(R.id.imageViewEditSmsType);
        deleteSms = itemView.findViewById(R.id.imageViewEditDeleteSmsType);

        editSms.setOnClickListener(v -> {
            String num = (String) viewTextViewNumberEdit.getText();
            editSms(Integer.parseInt(num.replace(".","")));
        });

        deleteSms.setOnClickListener(v -> {
            deleteSms();
        });

    }

    public void editSms(int smsNumber) {
        Dialog dialogEditSms = toolbox.showDialogActionEditSms(context, smsNumber);

        MaterialButton btnSaveSms = dialogEditSms.findViewById(R.id.buttonAlertSuccessButtonOK);

        btnSaveSms.setOnClickListener(v -> {
            // TODO save it to sqlite
            dialogEditSms.dismiss();
        });
        dialogEditSms.show();
    }
    public void deleteSms() {

    }
}