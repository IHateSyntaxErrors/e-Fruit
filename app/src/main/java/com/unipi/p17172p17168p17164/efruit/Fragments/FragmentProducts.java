package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.unipi.p17172p17168p17164.efruit.Activities.CartActivity;
import com.unipi.p17172p17168p17164.efruit.Models.ModelProducts;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.DBHelper;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;
import com.unipi.p17172p17168p17164.efruit.databinding.FragmentProductsBinding;
import com.unipi.p17172p17168p17164.efruit.databinding.ItemProductBinding;

import java.text.MessageFormat;
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
    boolean isCartShop;
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
        updateUI();
        getProductsList();

        return view;
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        // Animations
        binding.constraintLayoutProductsGoToCart.animate().translationY(binding.constraintLayoutProductsGoToCart.getHeight());

        productsListRecycler = binding.recyclerViewProducts;

        productsListRecycler.setLayoutManager(linearLayoutManager);
        productsListRecycler.setHasFixedSize(true);
    }

    private void updateUI() {
        binding.constraintLayoutProductsGoToCart.setOnClickListener(v -> {
            Intent intent = new Intent(context, CartActivity.class);
            startActivity(intent);
        });
    }

    public void getProductsList(){
        final String TAG = "[FragmentProducts]";

        Query queryProducts = db.collection("shops")
                                .document(shopId)
                                .collection("products");

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
                     .into(holder.itemProductBinding.imageViewProductsProductImage);
                holder.itemProductBinding.textViewProductsProductName.setText(model.getName());
                holder.itemProductBinding.textViewProductsProductPrice.setText(String.format(context.getString(R.string.recycler_var_product_price), model.getPrice() + ""));
                holder.itemProductBinding.textViewProductsProductPricePerKg.setText(String.format(context.getString(R.string.recycler_var_product_price_per_kg), model.getPrice() + ""));
                holder.itemProductBinding.textViewProductsProductQuantityNum.setText(MessageFormat.format("{0}", model.getQuantity()));

                isCartShop = true;
                db.collection("carts")
                  .whereEqualTo("userId", firebaseUser.getUid())
                  .get()
                  .addOnCompleteListener(taskCheck -> {
                      if (taskCheck.isSuccessful()) {
                          for (DocumentSnapshot documentCartItem : taskCheck.getResult())
                              isCartShop = shopId.equals(documentCartItem.getString("shopId"));

                          // BUTTONS
                          // We need to check if each item is added in cart so we change the add to cart button to the selection operators.
                          Query queryCartItem = DBHelper.getCartItem(db, firebaseUser.getUid(), model.getProductId());

                          queryCartItem.get().addOnCompleteListener(task -> {
                              if (task.isSuccessful()) {
                                  if (!task.getResult().isEmpty() && isCartShop) { // Checking if the query returned nothing
                                      binding.constraintLayoutProductsGoToCart.setVisibility(View.VISIBLE);
                                      for (DocumentSnapshot documentCartItem : task.getResult()) {
                                          // Hide the add to cart button completely
                                          holder.itemProductBinding.btnRecyclerItemAddToCart.setVisibility(View.INVISIBLE);
                                          holder.itemProductBinding.linearLayoutProductsSelectAmount.setVisibility(View.VISIBLE);
                                          holder.itemProductBinding.imgBtnRecyclerProductsAmountDelete.setVisibility(View.VISIBLE);
                                          holder.itemProductBinding.textViewProductsSelectedAmount.setText(String.valueOf(Objects.requireNonNull(documentCartItem.getData()).get("amount")));
                                      }
                                  }
                                  else {
                                      // Show the add to cart button completely
                                      holder.itemProductBinding.btnRecyclerItemAddToCart.setVisibility(View.VISIBLE);
                                      holder.itemProductBinding.imgBtnRecyclerProductsAmountDelete.setVisibility(View.INVISIBLE);
                                      holder.itemProductBinding.linearLayoutProductsSelectAmount.setVisibility(View.INVISIBLE);
                                  }
                              }
                          });

                          // ADD TO CART BUTTON
                          holder.itemProductBinding.btnRecyclerItemAddToCart.setOnClickListener(v -> {
                              if (isCartShop) {
                                  Query cardItem = DBHelper.getCartItem(db, firebaseUser.getUid(), model.getProductId());
                                  cardItem.get().addOnCompleteListener(task -> {
                                      if (task.isSuccessful()) {
                                          DBHelper.setCartItem(db, firebaseUser.getUid(), shopId, model.getProductId(), model.getPrice(), 1)
                                                  .addOnCompleteListener(taskAdd -> {
                                                      if (taskAdd.isSuccessful()) {
                                                          notifyItemChanged(holder.getAdapterPosition());
                                                          adapter.notifyDataSetChanged();
                                                      }
                                                  });
                                      }
                                  });
                              }
                              else {
                                  DBHelper.getShopName(db, shopId).get().addOnCompleteListener(taskGet -> {
                                      Dialog dialog = new Toolbox().showDialogWrongShopWarning(getContext(), taskGet.getResult().getString("name"));
                                      dialog.show();
                                  });
                              }
                          });
                      }
                  });

                // (-) MINUS BUTTON
                holder.itemProductBinding.imgBtnRecyclerProductsSelectAmountMinus.setOnClickListener(v -> {
                    int currentCount = Integer.parseInt((String) holder.itemProductBinding.textViewProductsSelectedAmount.getText());
                    int count = currentCount - 1;

                    if (count >= 1)
                        DBHelper.setSelectedItemAmount(db, firebaseUser.getUid(), model.getProductId(), count).addOnCompleteListener(taskUpdatePlus -> {
                            if (taskUpdatePlus.isSuccessful()) {
                                notifyItemChanged(holder.getAdapterPosition());
                                adapter.notifyDataSetChanged();
                            }
                        });
                });
                // (+) PLUS BUTTON
                holder.itemProductBinding.imgBtnRecyclerProductsSelectAmountPlus.setOnClickListener(v -> {
                    int currentCount = Integer.parseInt((String) holder.itemProductBinding.textViewProductsSelectedAmount.getText());
                    int count = currentCount + 1;

                    if (count <= model.getQuantity())
                        DBHelper.setSelectedItemAmount(db, firebaseUser.getUid(), model.getProductId(), count).addOnCompleteListener(taskUpdatePlus -> {
                            if (taskUpdatePlus.isSuccessful()) {
                                notifyItemChanged(holder.getAdapterPosition());
                                adapter.notifyDataSetChanged();
                            }
                        });
                });
                // DELETE/TRASH BUTTON
                holder.itemProductBinding.imgBtnRecyclerProductsAmountDelete.setOnClickListener(v -> {
                    DBHelper.getCartItemRef(db, firebaseUser.getUid(), model.getProductId())
                            .delete()
                            .addOnSuccessListener(taskDelete -> {
                                holder.itemProductBinding.imgBtnRecyclerProductsAmountDelete.setVisibility(View.INVISIBLE);
                                notifyItemChanged(holder.getAdapterPosition());
                                adapter.notifyDataSetChanged();
                            });
                    // After deletion we need to check again if the products collection
                    // is empty so we completely remove the created cart document.
                    DBHelper.getTotalCartItems(db, firebaseUser.getUid())
                            .get()
                            .addOnCompleteListener(taskCheck -> {
                                // If the cart is empty of products, switch to the empty cart view.
                                if (Objects.requireNonNull(taskCheck.getResult()).isEmpty()) {
                                    DBHelper.getCartDetails(db, firebaseUser.getUid()).delete();
                                    binding.constraintLayoutProductsGoToCart.setVisibility(View.INVISIBLE);
                                }
                            });
                });
            }

            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ItemProductBinding view = ItemProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private final ItemProductBinding itemProductBinding;

        public ProductsViewHolder(ItemProductBinding itemProductBinding) {
            super(itemProductBinding.getRoot());
            this.itemProductBinding = itemProductBinding;
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
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}