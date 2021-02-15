package com.unipi.p17172.nikolaspateras.efruit.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17172.nikolaspateras.efruit.Items.ItemSmsTypesEdit;
import com.unipi.p17172.nikolaspateras.efruit.Items.SetViewHolderSmsTypesEdit;
import com.unipi.p17172.nikolaspateras.efruit.R;

import java.util.Collections;
import java.util.List;

public class DataAdapterSmsTypesEdit extends RecyclerView.Adapter<SetViewHolderSmsTypesEdit> {
    private final Activity activity;

    List<ItemSmsTypesEdit> items = Collections.emptyList();

    public DataAdapterSmsTypesEdit(Activity activity, List<ItemSmsTypesEdit> items) {
        this.activity = activity;
        this.items = items;
    }

    @NonNull
    @Override
    public SetViewHolderSmsTypesEdit onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_single_item_sms_types_edit, parent, false);
        return new SetViewHolderSmsTypesEdit(v, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolderSmsTypesEdit holder, int position) {
        holder.viewTextViewNumberEdit.setText(String.format(activity.getResources().getString(R.string.recyclerview_sms_number_text), items.get(position).getSmsNumber()));
        holder.viewTextViewSmsExplanationEdit.setText(items.get(position).getSmsReason());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
