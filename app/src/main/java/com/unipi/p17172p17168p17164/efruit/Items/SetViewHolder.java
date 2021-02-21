package com.unipi.p17172p17168p17164.efruit.Items;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17172p17168p17164.efruit.R;

public class SetViewHolder extends RecyclerView.ViewHolder {
    public ImageView viewHolderProducts_TxtProductIcon;
    public TextView viewHolderProducts_TxtProductName;
    public TextView viewHolderProducts_TxtProductPrice;
    public TextView viewHolderProducts_TxtProductPricePerKg;
    public TextView viewHolderProducts_TxtProductQuantity;

    public SetViewHolder(@NonNull View itemView) {
        super(itemView);
        viewHolderProducts_TxtProductIcon = itemView.findViewById(R.id.imageViewProductIcon);
        viewHolderProducts_TxtProductName = itemView.findViewById(R.id.textViewProducts_ProductName);
        viewHolderProducts_TxtProductPrice = itemView.findViewById(R.id.textViewProducts_ProductPrice);
        viewHolderProducts_TxtProductPricePerKg = itemView.findViewById(R.id.textViewProducts_ProductPricePerKg);
        viewHolderProducts_TxtProductQuantity = itemView.findViewById(R.id.textViewProducts_ProductQuantityNum);
    }
}