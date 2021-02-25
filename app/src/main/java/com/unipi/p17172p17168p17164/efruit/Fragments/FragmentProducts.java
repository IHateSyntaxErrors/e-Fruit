package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.app.Dialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.ServerTimestamp;
import com.unipi.p17172p17168p17164.efruit.Models.ModelCart;
import com.unipi.p17172p17168p17164.efruit.Models.ModelProducts;
import com.unipi.p17172p17168p17164.efruit.Models.ModelUsers;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.databinding.FragmentProductsBinding;
import com.unipi.p17172p17168p17164.efruit.databinding.RecyclerSingleItemProductsBinding;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FragmentProducts extends Fragment {
    // ~~~~~~~VARIABLES~~~~~~~
    private FragmentProductsBinding binding;
    private Context context;
    public View view;

    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
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
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        productsListRecycler = binding.recyclerViewProducts;
        searchViewProducts_SearchBar = binding.searchViewProducts;

        productsListRecycler.setLayoutManager(linearLayoutManager);
        productsListRecycler.setHasFixedSize(true);
    }

    public void getProductsList(){
        final String TAG = "[FragmentProducts]";

        Query queryProducts = db.collection("shops")
                                .document(shopId)
                                .collection("products").limit(1);

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

                // We need to load the cart data when the fragment opens
                DocumentReference itemInCart_on_load = db.collection("carts")
                        .document(firebaseUser.getUid())
                        .collection("products")
                        .document(model.getProductId());

                itemInCart_on_load.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Hide the add to cart button completely
                                    holder.singleItemProductsBinding.btnRecyclerItemAddToCart.setVisibility(View.INVISIBLE);
                                    holder.singleItemProductsBinding.linearLayoutProductsSelectAmount.setVisibility(View.VISIBLE);
                                    holder.singleItemProductsBinding.textViewProductsSelectedAmount.setText(document.getData().get("amount").toString());

                                    holder.singleItemProductsBinding.imgBtnRecyclerProductsSelectAmountMinus.setOnClickListener(v -> {
                                        int currentCount = Integer.parseInt((String) holder.singleItemProductsBinding.textViewProductsSelectedAmount.getText());
                                        int count = currentCount - 1;

                                        if (count >= 1) {
                                            db.collection("carts")
                                                    .document(firebaseUser.getUid())
                                                    .collection("products")
                                                    .document(model.getProductId())
                                                    .update("amount", count).addOnCompleteListener(task2 -> {
                                                        holder.singleItemProductsBinding.textViewProductsSelectedAmount.setText(String.valueOf(count));
                                                    });;
                                        }

                                    });
                                    holder.singleItemProductsBinding.imgBtnRecyclerProductsSelectAmountPlus.setOnClickListener(v -> {
                                        int currentCount = Integer.parseInt((String) holder.singleItemProductsBinding.textViewProductsSelectedAmount.getText());
                                        int count = currentCount + 1;

                                        db.collection("carts")
                                                .document(firebaseUser.getUid())
                                                .collection("products")
                                                .document(model.getProductId())
                                                .update("amount", count).addOnCompleteListener(task2 -> {
                                                    holder.singleItemProductsBinding.textViewProductsSelectedAmount.setText(String.valueOf(count));
                                                });
                                    });
                                    holder.singleItemProductsBinding.imgBtnRecyclerProductsAmountDelete.setOnClickListener(v -> {
                                        db.collection("carts")
                                                .document(firebaseUser.getUid())
                                                .collection("products")
                                                .document(model.getProductId())
                                                .delete().addOnSuccessListener(task2 -> {
                                                    notifyItemRemoved(holder.getAdapterPosition());
                                                    adapter.notifyDataSetChanged();
                                                });
                                    });
                                }
                                // Else if its not added in cart
                                else {
                                    // Show the add to cart button completely
                                    holder.singleItemProductsBinding.btnRecyclerItemAddToCart.setVisibility(View.VISIBLE);
                                    holder.singleItemProductsBinding.linearLayoutProductsSelectAmount.setVisibility(View.INVISIBLE);
                                }
                            } else
                                Log.d(TAG, "Failed with: ", task.getException());
                });

                // We need to run checks every time the add to cart button is clicked
                holder.singleItemProductsBinding.btnRecyclerItemAddToCart.setOnClickListener(v -> {
                    DocumentReference itemInCart = db.collection("carts")
                                    .document(firebaseUser.getUid())
                                    .collection("products")
                                    .document(model.getProductId());

                    itemInCart.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Hide the add to cart button completely
                                holder.singleItemProductsBinding.btnRecyclerItemAddToCart.setVisibility(View.INVISIBLE);
                                holder.singleItemProductsBinding.linearLayoutProductsSelectAmount.setVisibility(View.VISIBLE);
                                holder.singleItemProductsBinding.textViewProductsSelectedAmount.setText(document.getData().get("amount").toString());

                                holder.singleItemProductsBinding.imgBtnRecyclerProductsSelectAmountMinus.setOnClickListener(s -> {
                                    int currentCount = Integer.parseInt((String) holder.singleItemProductsBinding.textViewProductsSelectedAmount.getText());
                                    int count = currentCount - 1;

                                    if (count >= 1) {
                                        db.collection("carts")
                                                .document(firebaseUser.getUid())
                                                .collection("products")
                                                .document(model.getProductId())
                                                .update("amount", count).addOnCompleteListener(task2 -> {
                                            holder.singleItemProductsBinding.textViewProductsSelectedAmount.setText(count);
                                        });;
                                    }

                                });
                                holder.singleItemProductsBinding.imgBtnRecyclerProductsSelectAmountPlus.setOnClickListener(s -> {
                                    int currentCount = Integer.parseInt((String) holder.singleItemProductsBinding.textViewProductsSelectedAmount.getText());
                                    int count = currentCount + 1;

                                    db.collection("carts")
                                            .document(firebaseUser.getUid())
                                            .collection("products")
                                            .document(model.getProductId())
                                            .update("amount", count).addOnCompleteListener(task2 -> {
                                                holder.singleItemProductsBinding.textViewProductsSelectedAmount.setText(String.valueOf(count));
                                            });
                                });
                                holder.singleItemProductsBinding.imgBtnRecyclerProductsAmountDelete.setOnClickListener(s -> {
                                    db.collection("carts")
                                            .document(firebaseUser.getUid())
                                            .collection("products")
                                            .document(model.getProductId())
                                            .delete().addOnSuccessListener(task3 -> {
                                                notifyItemRemoved(holder.getAdapterPosition());
                                                adapter.notifyDataSetChanged();
                                            });
                                });
                            }
                            // Else if its not added in cart
                            else {
                                // Show the add to cart button
                                holder.singleItemProductsBinding.btnRecyclerItemAddToCart.setVisibility(View.VISIBLE);
                                holder.singleItemProductsBinding.linearLayoutProductsSelectAmount.setVisibility(View.INVISIBLE);
                                // Insert user data into cloud.
                                DocumentReference cartCreatedRef = db.collection("carts").document(firebaseUser.getUid());
                                cartCreatedRef.get().addOnCompleteListener(task2 ->  {
                                    DocumentSnapshot documentCart = task.getResult();
                                    Map< String, Object > updatedCart = new HashMap< >();
                                    updatedCart.put("amount", 1);
                                    updatedCart.put("imgUrl", model.getImgUrl());
                                    updatedCart.put("name", model.getName());
                                    updatedCart.put("productId", model.getProductId());
                                    updatedCart.put("price", model.getPrice());
                                    updatedCart.put("quantity", model.getShopId());

                                    if (documentCart.exists()) {
                                        db.collection("carts")
                                                .document(firebaseUser.getUid())
                                                .collection("products")
                                                .document(model.getProductId())
                                                .set(updatedCart);
                                    }
                                    else {
                                        HashMap< String, Object > newCart = new HashMap< >();
                                        newCart.put("created_at", ServerValue.TIMESTAMP);
                                        newCart.put("shopId", model.getShopId());
                                        newCart.put("userId", firebaseUser.getUid());
                                        db.collection("carts")
                                                .document(firebaseUser.getUid())
                                                .set(newCart).addOnCompleteListener(task3 -> {
                                                    db.collection("carts")
                                                            .document(firebaseUser.getUid())
                                                            .collection("products")
                                                            .document(model.getProductId())
                                                            .set(updatedCart);
                                                });
                                    }
                                    notifyItemChanged(holder.getAdapterPosition());
                                    adapter.notifyDataSetChanged();
                                });
                            }
                        }
                        else
                            Log.d(TAG, "Failed with: ", task.getException());
                    });
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