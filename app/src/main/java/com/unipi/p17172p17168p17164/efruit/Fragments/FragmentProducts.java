package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.unipi.p17172p17168p17164.efruit.Models.ModelProducts;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.databinding.FragmentProductsBinding;
import com.unipi.p17172p17168p17164.efruit.databinding.RecyclerSingleItemProductsBinding;

import java.text.MessageFormat;
import java.util.Objects;

public class FragmentProducts extends Fragment {
    // ~~~~~~~VARIABLES~~~~~~~
    private FragmentProductsBinding binding;
    private Context context;
    public View view;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    String shopId;

    RecyclerView productsListRecycler;

    public LinearLayoutManager linearLayoutManager;

    SearchView searchViewProducts_SearchBar;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        init();
        getProductsList();

        return view;
    }

    private void init() {
        db = FirebaseFirestore.getInstance();

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        productsListRecycler = binding.recyclerViewProducts;
        searchViewProducts_SearchBar = binding.searchViewProducts;

        productsListRecycler.setLayoutManager(linearLayoutManager);
        productsListRecycler.setHasFixedSize(true);
    }

    public void getProductsList(){
        final String TAG = "[FragmentProducts]";

        Query queryProducts = db.collection("shops").document(shopId).collection("products");

        queryProducts.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "listen:error", e);
                return;
            }

            for (DocumentChange dc : Objects.requireNonNull(snapshots).getDocumentChanges()) {
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
                        .into(holder.singleItemProductsBinding.imageViewProductsProductImage);
                holder.singleItemProductsBinding.textViewProductsProductName.setText(model.getName());
                holder.singleItemProductsBinding.textViewProductsProductPrice.setText(String.format(context.getString(R.string.recycler_var_product_price), model.getPrice() + ""));
                holder.singleItemProductsBinding.textViewProductsProductPricePerKg.setText(String.format(context.getString(R.string.recycler_var_product_price_per_kg), model.getPrice() + ""));
                holder.singleItemProductsBinding.textViewProductsProductQuantityNum.setText(MessageFormat.format("{0}", model.getQuantity()));
                holder.singleItemProductsBinding.btnRecyclerItemAddToCart.setOnClickListener(v -> {
                    Toast.makeText(context, model.getName() + " " + model.getPrice(), Toast.LENGTH_SHORT).show();
                });
            }

            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                RecyclerSingleItemProductsBinding view = RecyclerSingleItemProductsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

                return new ProductsViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("Error", e.getMessage());
            }
        };
        adapter.notifyDataSetChanged();
        productsListRecycler.setAdapter(adapter);
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerSingleItemProductsBinding singleItemProductsBinding;

        public ProductsViewHolder(RecyclerSingleItemProductsBinding singleItemProductsBinding) {
            super(singleItemProductsBinding.getRoot());
            this.singleItemProductsBinding = singleItemProductsBinding;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}