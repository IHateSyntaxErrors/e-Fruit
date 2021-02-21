package com.unipi.p17172p17168p17164.efruit.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.unipi.p17172p17168p17164.efruit.Items.ItemProduct;
import com.unipi.p17172p17168p17164.efruit.R;

import java.util.ArrayList;
import java.util.List;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_single_item_products, parent, false);
        return new SetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        holder.bind(itemProducts.get(position));
//        holder.imgViewRecycler_ProductIcon.setImageDrawable(ContextCompat.getDrawable(activity, itemProducts.get(position).getProductIcon()));
        holder.txtViewRecycler_ProductName.setText(itemProducts.get(position).getProductName());
        holder.txtViewRecycler_ProductPrice.setText(String.format(activity.getResources().getString(R.string.recycler_var_product_price), itemProducts.get(position).getProductPrice()));
        holder.txtViewRecycler_ProductPricePerKg.setText(String.format(activity.getResources().getString(R.string.recycler_var_product_price_per_kg), itemProducts.get(position).getProductPrice()));
        holder.txtViewProducts_ProductQuantityNum.setText(itemProducts.get(position).getProductQuantity());
        View.OnClickListener rbClick = v -> {
            MaterialButton checked_rb = (MaterialButton) v;
            /*if (lastCheckedRB != null
                    && checked_rb != lastCheckedRB) {
                lastCheckedRB.setChecked(false);
            }
            lastCheckedRB = checked_rb;
            checkedPosition = position;*/
        };
        holder.btnRecycler_AddToCart.setOnClickListener(rbClick);
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
        public ImageView imgViewRecycler_ProductIcon;
        public TextView txtViewRecycler_ProductName;
        public TextView txtViewRecycler_ProductPrice;
        public TextView txtViewRecycler_ProductPricePerKg;
        public TextView txtViewProducts_ProductQuantityNum;
        public MaterialButton btnRecycler_AddToCart;

        public SetViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewRecycler_ProductIcon = itemView.findViewById(R.id.imageViewProductIcon);
            txtViewRecycler_ProductName = itemView.findViewById(R.id.textViewProducts_ProductName);
            txtViewRecycler_ProductPrice = itemView.findViewById(R.id.textViewProducts_ProductPrice);
            txtViewRecycler_ProductPricePerKg = itemView.findViewById(R.id.textViewProducts_ProductPricePerKg);
            txtViewProducts_ProductQuantityNum = itemView.findViewById(R.id.textViewProducts_ProductQuantityNum);
            btnRecycler_AddToCart = itemView.findViewById(R.id.btnRecyclerItemAddToCart);
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
