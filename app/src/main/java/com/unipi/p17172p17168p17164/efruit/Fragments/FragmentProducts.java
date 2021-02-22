package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.unipi.p17172p17168p17164.efruit.Models.ModelProducts;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentProducts extends Fragment {
    // ~~~~~~~VARIABLES~~~~~~~
    private Context context;
    private View view;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    String shopId;

    @BindView(R.id.recyclerViewProducts) RecyclerView productsList;

    private LinearLayoutManager linearLayoutManager;
    private List<ModelProducts> modelProductsList = new ArrayList<>();

    @BindView(R.id.editTxtInputProducts_SearchBar)
    TextInputEditText txtInputProducts_SearchBar;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    public FragmentProducts(String shopId) {
        this.shopId = shopId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products, container, false);

        ButterKnife.bind(this, view);

        init();
        getProductsList();

        return view;
    }

    private void init() {
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        productsList.setLayoutManager(linearLayoutManager);
        productsList.setHasFixedSize(true);
        db = FirebaseFirestore.getInstance();

        txtInputProducts_SearchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Toolbox.hideKeyboard(v, context);
            }
        });
    }

    public void getProductsList(){
        final String TAG = "[FragmentProducts]";

        Query queryProducts = db.collection("shops").document(shopId).collection("products");

        queryProducts.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "listen:error", e);
                return;
            }

            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        Log.d(TAG, "New Product: " + dc.getDocument().getData());
                        break;
                    case MODIFIED:
                        Log.d(TAG, "Modified Product: " + dc.getDocument().getData());
                        break;
                    case REMOVED:
                        Log.d(TAG, "Removed Product: " + dc.getDocument().getData());
                        break;
                }
            }

        });

        // RecyclerOptions
        FirestoreRecyclerOptions<ModelProducts> recyclerOptions = new FirestoreRecyclerOptions.Builder<ModelProducts>()
                .setQuery(queryProducts, ModelProducts.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<ModelProducts, ProductsViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull ModelProducts model) {
                Glide.with(context)
                        .load(model.getImgUrl())
                        .into(holder.viewHolderProducts_ImgProductImage);
                holder.viewHolderProducts_TxtProductName.setText(model.getName());
                holder.viewHolderProducts_TxtProductPrice.setText(String.format(context.getString(R.string.recycler_var_product_price), model.getPrice() + ""));
                holder.viewHolderProducts_TxtProductPricePerKg.setText(String.format(context.getString(R.string.recycler_var_product_price_per_kg), model.getPrice() + ""));
                holder.viewHolderProducts_TxtProductQuantity.setText(MessageFormat.format("{0}", model.getQuantity()));
                holder.viewHolderProducts_btnAddToCart.setOnClickListener(v -> {
                    Toast.makeText(context, model.getName() + " " + model.getPrice(), Toast.LENGTH_SHORT).show();
                });
            }

            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_single_item_products, parent, false);
                return new ProductsViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("Error", e.getMessage());
            }
        };
        adapter.notifyDataSetChanged();
        productsList.setAdapter(adapter);
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageViewProducts_ProductImage)
        ImageView viewHolderProducts_ImgProductImage;
        @BindView(R.id.textViewProducts_ProductName)
        TextView viewHolderProducts_TxtProductName;
        @BindView(R.id.textViewProducts_ProductPrice)
        TextView viewHolderProducts_TxtProductPrice;
        @BindView(R.id.textViewProducts_ProductPricePerKg)
        TextView viewHolderProducts_TxtProductPricePerKg;
        @BindView(R.id.textViewProducts_ProductQuantityNum)
        TextView viewHolderProducts_TxtProductQuantity;
        @BindView(R.id.btnRecyclerItemAddToCart)
        MaterialButton viewHolderProducts_btnAddToCart;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}