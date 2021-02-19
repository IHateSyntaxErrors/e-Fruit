package com.unipi.p17172p17168p17164.efruit.Adapters;

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
import com.unipi.p17172p17168p17164.efruit.Items.ItemProduct;
import com.unipi.p17172p17168p17164.efruit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataAdapterProducts extends RecyclerView.Adapter<DataAdapterProducts.SetViewHolder> {
    private final Activity activity;

    List<ItemProduct> itemProducts;

    public DataAdapterProducts(Activity activity, List<ItemProduct> itemProducts) {
        this.activity = activity;
        this.itemProducts = itemProducts;
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_single_item_sms_types, parent, false);
        return new SetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        holder.bind(itemProducts.get(position));
        holder.viewTextViewNumber.setText(String.format(activity.getResources().getString(R.string.recyclerview_sms_number_text), itemProducts.get(position).getSmsNumber()));
        holder.viewTextViewSmsExplanation.setText(itemProducts.get(position).getSmsReason());
        View.OnClickListener rbClick = v -> {
            RadioButton checked_rb = (RadioButton) v;
            /*if (lastCheckedRB != null
                    && checked_rb != lastCheckedRB) {
                lastCheckedRB.setChecked(false);
            }
            lastCheckedRB = checked_rb;
            checkedPosition = position;*/
        };
        holder.radioButtonTypeSelection.setOnClickListener(rbClick);
    }

    public void setItemProducts(ArrayList<ItemProduct> itemProducts){
        this.itemProducts = new ArrayList<>();
        this.itemProducts = itemProducts;
        notifyDataSetChanged();
    }

    public ItemProduct getSelected() {
        /*if (checkedPosition != -1)
            return itemProducts.get(checkedPosition);*/
        return null;
    }

    @Override
    public int getItemCount() {
        return itemProducts.size();
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
        void bind(final ItemProduct items) {
            /*if (checkedPosition == -1) {
                radioButtonTypeSelection.setChecked(false);
            }
            else {
                if (checkedPosition == getAdapterPosition()) {
                    radioButtonTypeSelection.setChecked(true);
                }
                else
                    radioButtonTypeSelection.setChecked(false);
            }*/
            itemView.setOnClickListener(v -> {
                /*if (checkedPosition != getAdapterPosition()) {
                    notifyItemChanged(checkedPosition);
                    checkedPosition = getAdapterPosition();
                    lastCheckedRB = radioButtonTypeSelection;
                    radioButtonTypeSelection.setChecked(true);
                }*/
            });
        }
    }
}
