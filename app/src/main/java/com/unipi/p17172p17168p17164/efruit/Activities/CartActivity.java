package com.unipi.p17172p17168p17164.efruit.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.unipi.p17172p17168p17164.efruit.Models.ModelCart;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.DBHelper;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;
import com.unipi.p17172p17168p17164.efruit.databinding.ActivityCartBinding;
import com.unipi.p17172p17168p17164.efruit.databinding.RecyclerSingleItemCartBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {

    // ~~~~~~~VARIABLES~~~~~~~
    private ActivityCartBinding binding;
    private FirebaseUser firebaseUser;
    private Toolbox toolbox;
    private FirebaseFirestore db;
    public FirestoreRecyclerAdapter adapter;

    public RecyclerView cartList;
    public ViewFlipper viewFlipper;

    public LinearLayoutManager linearLayoutManager;
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Activity opening animation when opened
        this.overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);

        init();
        getCartList();
        updateUI();
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        cartList = binding.recyclerViewCart;
        viewFlipper = binding.viewFlipperCart;

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cartList.setLayoutManager(linearLayoutManager);
        cartList.setHasFixedSize(true);
    }

    private void getCartList() {
        final String TAG = "[CartActivity]";

        Query queryCartProducts = db.collection("carts")
                                    .document(firebaseUser.getUid())
                                    .collection("products");

        queryCartProducts.get().addOnCompleteListener(task -> {
            if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                viewFlipper.setDisplayedChild(1);
            }
        });

        queryCartProducts.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "listen:error", e);
                return;
            }

            assert snapshots != null;
            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        Log.d(TAG, "New Cart Item: " + dc.getDocument().getData());
                        break;
                    case MODIFIED:
                        Log.d(TAG, "Modified Cart Item: " + dc.getDocument().getData());
                        break;
                    case REMOVED:
                        Log.d(TAG, "Removed Cart Item: " + dc.getDocument().getData());
                        break;
                }
            }
        });

        // RecyclerOptions
        FirestoreRecyclerOptions<ModelCart> recyclerOptions = new FirestoreRecyclerOptions.Builder<ModelCart>()
                .setQuery(queryCartProducts, ModelCart.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ModelCart, CartViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull ModelCart model) {
                binding.constraintLayoutCartProgressBar.setVisibility(View.VISIBLE);
                // Load cart details
                DocumentReference docRefCartDetails = DBHelper.getCartDetails(db, firebaseUser.getUid());
                docRefCartDetails.get().addOnCompleteListener(task -> {
                    String shopId;
                    DocumentSnapshot documentSnapshotCartDetails = task.getResult();
                    if (task.isSuccessful()) {
                        if (documentSnapshotCartDetails.exists()) {
                            shopId = (String.valueOf(Objects.requireNonNull(documentSnapshotCartDetails.getData()).get("shopId")));
                            DocumentReference docRefShopDetails = DBHelper.getShopDetails(db, shopId);
                            docRefShopDetails.get().addOnCompleteListener(task2 -> {
                                DocumentSnapshot document = task2.getResult();

                                if (task2.isSuccessful()) {
                                    binding.textViewCartShopNameHeader.setText(String.valueOf(Objects.requireNonNull(document.getData()).get("name")));

                                    // Load product details
                                    DocumentReference docRefProductInfo = DBHelper.getProductInfo(db, shopId, model.getProductId());
                                    docRefProductInfo.get().addOnCompleteListener(task3 -> {
                                        DocumentSnapshot documentProduct = task3.getResult();
                                        if (task3.isSuccessful()) {
                                            Glide.with(getApplicationContext())
                                                    .load(String.valueOf(Objects.requireNonNull(documentProduct.getData()).get("imgUrl")))
                                                    .into(holder.singleItemCartBinding.imageViewCartProductImage);
                                            holder.singleItemCartBinding.textViewCartProductName.setText(String.valueOf(Objects.requireNonNull(documentProduct.getData()).get("name")));
                                            holder.singleItemCartBinding.textViewCartProductPrice.setText(String.format(getString(R.string.recycler_var_product_price),
                                                    Objects.requireNonNull(documentProduct.getData()).get("price")));
                                            holder.singleItemCartBinding.textViewCartProductPricePerKg.setText(String.format(getString(R.string.recycler_var_product_price_per_kg),
                                                    documentProduct.getData().get("price")));
                                            holder.singleItemCartBinding.textViewCartProductQuantityNum.setText(String.valueOf(Objects.requireNonNull(documentProduct.getData()).get("quantity")));

                                            // Calculate the total price of the products
                                            Query queryCartItems = DBHelper.getTotalCartItems(db, firebaseUser.getUid());
                                            queryCartItems.get().addOnCompleteListener(task4 -> {
                                                if (task4.isSuccessful()) {
                                                    double total_price = 0;
                                                    for (DocumentSnapshot documentCartItem : task4.getResult()) {
                                                        total_price += Objects.requireNonNull(documentCartItem.getDouble("price"))
                                                                * Objects.requireNonNull(documentCartItem.getDouble("amount"));
                                                    }
                                                    binding.textViewCartTotalPaymentNumber.setText(String.format(getString(R.string.page_cart_total_payment_number), total_price));
                                                }
                                            });

                                            // Load product amount & calculate total price
                                            Query docRefItemAmountInCart = DBHelper.getCartItem(db, firebaseUser.getUid(), model.getProductId());
                                            docRefItemAmountInCart.get().addOnCompleteListener(task4 -> {
                                                if (task4.isSuccessful()) {
                                                    for (DocumentSnapshot documentCartItem : task4.getResult()) {
                                                        holder.singleItemCartBinding.textViewCartSelectedAmount.setText(String.valueOf(Objects.requireNonNull(documentCartItem.getData()).get("amount")));
                                                    }
                                                }
                                            });
                                            // BUTTONS
                                            // DELETE/TRASH BUTTON
                                            holder.singleItemCartBinding.imgBtnRecyclerCartAmountDelete.setOnClickListener(v-> {
                                                DBHelper.getCartItemRef(db, firebaseUser.getUid(), model.getProductId())
                                                        .delete()
                                                        .addOnSuccessListener(taskDelete -> {
                                                            notifyItemRemoved(holder.getAdapterPosition());
                                                            adapter.notifyDataSetChanged();
                                                        });
                                                // After deletion we need to check again if the products collection
                                                // is empty so we completely remove the created cart document.
                                                DBHelper.getTotalCartItems(db, firebaseUser.getUid())
                                                        .get()
                                                        .addOnCompleteListener(taskCheck -> {
                                                            // If the cart is empty of products, switch to the empty cart view.
                                                            if (Objects.requireNonNull(taskCheck.getResult()).isEmpty()) {
                                                                DBHelper.getCartDetails(db, firebaseUser.getUid())
                                                                        .delete()
                                                                        .addOnCompleteListener(taskDeleteCart -> viewFlipper.setDisplayedChild(1));
                                                            }
                                                        });
                                            });
                                            // (-) MINUS BUTTON
                                            holder.singleItemCartBinding.imgBtnRecyclerCartSelectAmountMinus.setOnClickListener(v -> {
                                                int currentCount = Integer.parseInt((String) holder.singleItemCartBinding.textViewCartSelectedAmount.getText());
                                                int count = currentCount - 1;

                                                if (count >= 1) {
                                                    DBHelper.setSelectedItemAmount(db, firebaseUser.getUid(), model.getProductId(), count);
                                                    notifyItemChanged(holder.getAdapterPosition());
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                            // (+) PLUS BUTTON
                                            holder.singleItemCartBinding.imgBtnRecyclerCartSelectAmountPlus.setOnClickListener(v -> {
                                                int currentCount = Integer.parseInt((String) holder.singleItemCartBinding.textViewCartSelectedAmount.getText());
                                                int count = currentCount + 1;

                                                if (count <= Integer.parseInt(String.valueOf(documentProduct.getData().get("quantity")))) {
                                                    DBHelper.setSelectedItemAmount(db, firebaseUser.getUid(), model.getProductId(), count);
                                                    notifyItemChanged(holder.getAdapterPosition());
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }
                    binding.constraintLayoutCartProgressBar.setVisibility(View.INVISIBLE);
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                RecyclerSingleItemCartBinding view = RecyclerSingleItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

                return new CartViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("Error", e.getMessage());
            }
        };
        adapter.notifyDataSetChanged();
        cartList.setAdapter(adapter);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerSingleItemCartBinding singleItemCartBinding;

        public CartViewHolder(RecyclerSingleItemCartBinding singleItemCartBinding) {
            super(singleItemCartBinding.getRoot());
            this.singleItemCartBinding = singleItemCartBinding;
        }
    }

    public void updateUI() {
        binding.imageViewCartBackButton.setOnClickListener(v -> {
            onBackPressed();
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                                       R.anim.anim_slide_out_right);
    }

    private void refreshActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}