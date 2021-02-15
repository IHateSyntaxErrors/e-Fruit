package com.unipi.p17172.nikolaspateras.efruit.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.unipi.p17172.nikolaspateras.efruit.Items.Item;
import com.unipi.p17172.nikolaspateras.efruit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.SetViewHolder> {
    private final Activity activity;

    private int checkedPosition = -1; // -1: no default selection, 0: first item selected
    private RadioButton lastCheckedRB = null;
    List<Item> items;

    public DataAdapter(Activity activity, List<Item> items) {
        this.activity = activity;
        this.items = items;
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_single_item_sms_types, parent, false);
        return new SetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        holder.bind(items.get(position));
        holder.viewTextViewNumber.setText(String.format(activity.getResources().getString(R.string.recyclerview_sms_number_text), items.get(position).getSmsNumber()));
        holder.viewTextViewSmsExplanation.setText(items.get(position).getSmsReason());
        View.OnClickListener rbClick = v -> {
            RadioButton checked_rb = (RadioButton) v;
            if (lastCheckedRB != null
                    && checked_rb != lastCheckedRB) {
                lastCheckedRB.setChecked(false);
            }
            lastCheckedRB = checked_rb;
            checkedPosition = position;
            MaterialButton buttonSendSms = activity.findViewById(R.id.materialButtonSendSms);
            buttonSendSms.setEnabled(true);
            buttonSendSms.setBackground(Objects.requireNonNull(ContextCompat.getDrawable(activity, R.drawable.selector_login_large_button)));
        };
        holder.radioButtonTypeSelection.setOnClickListener(rbClick);
    }

    public void setItems(ArrayList<Item> items){
        this.items = new ArrayList<>();
        this.items = items;
        notifyDataSetChanged();
    }

    public Item getSelected() {
        if (checkedPosition != -1)
            return items.get(checkedPosition);
        return null;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SetViewHolder extends RecyclerView.ViewHolder {
        public TextView viewTextViewNumber;
        public TextView viewTextViewSmsExplanation;
        public RadioButton radioButtonTypeSelection;

        public SetViewHolder(@NonNull View itemView) {
            super(itemView);
            viewTextViewNumber = itemView.findViewById(R.id.textViewNumber);
            viewTextViewSmsExplanation = itemView.findViewById(R.id.textViewSmsExplanation);
            radioButtonTypeSelection = itemView.findViewById(R.id.radioButtonSmsType);
        }
        void bind(final Item items) {
            if (checkedPosition == -1) {
                radioButtonTypeSelection.setChecked(false);
            }
            else {
                if (checkedPosition == getAdapterPosition()) {
                    radioButtonTypeSelection.setChecked(true);
                }
                else
                    radioButtonTypeSelection.setChecked(false);
            }
            itemView.setOnClickListener(v -> {
                if (checkedPosition != getAdapterPosition()) {
                    notifyItemChanged(checkedPosition);
                    checkedPosition = getAdapterPosition();
                    lastCheckedRB = radioButtonTypeSelection;
                    radioButtonTypeSelection.setChecked(true);
                }
            });
        }
    }
}
