package com.unipi.p17172p17168p17164.efruit.Items;

import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17172p17168p17164.efruit.R;

public class SetViewHolder extends RecyclerView.ViewHolder {
    public TextView viewTextViewNumber;
    public TextView viewTextViewSmsExplanation;
    public RadioButton radioButtonTypeSelection;

    public SetViewHolder(@NonNull View itemView) {
        super(itemView);
        viewTextViewNumber = itemView.findViewById(R.id.textViewNumber);
        viewTextViewSmsExplanation = itemView.findViewById(R.id.textViewAlertLangEn);
        radioButtonTypeSelection = itemView.findViewById(R.id.radioButtonEll);
    }
}