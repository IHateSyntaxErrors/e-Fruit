package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.unipi.p17172p17168p17164.efruit.Models.ModelOrders;
import com.unipi.p17172p17168p17164.efruit.databinding.FragmentOrdersBinding;
import com.unipi.p17172p17168p17164.efruit.databinding.ItemOrderBinding;

import java.util.Objects;

    public class FragmentOrders extends Fragment {
        // ~~~~~~~VARIABLES~~~~~~~
        private FragmentOrdersBinding binding;
        private Context context;
        public View view;

        private FirebaseFirestore db;
        private FirebaseUser firebaseUser;
        private FirestoreRecyclerAdapter adapter;
        RecyclerView ordersListRecycler;

        public LinearLayoutManager linearLayoutManager;
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            context = requireActivity();
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            binding = FragmentOrdersBinding.inflate(inflater, container, false);
            View view = binding.getRoot();

            init();
            updateUI();
            getOrdersList();

            return view;
        }

        private void init() {
            db = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);


            ordersListRecycler = binding.recyclerViewOrders;

            ordersListRecycler.setLayoutManager(linearLayoutManager);
            ordersListRecycler.setHasFixedSize(true);
        }

        private void updateUI() {}

        public void getOrdersList(){
            final String TAG = "[FragmentOrders]";

            Query queryUserOrders = db.collection("shops").whereEqualTo("userId", firebaseUser.getUid());

            queryUserOrders.addSnapshotListener((snapshots, e) -> {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return; }

                for (DocumentChange dc : Objects.requireNonNull(snapshots).getDocumentChanges())
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "New Order: " + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified Order: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed Order: " + dc.getDocument().getData());
                            break; }
            });

            // RecyclerOptions
            FirestoreRecyclerOptions<ModelOrders> recyclerOptions = new FirestoreRecyclerOptions.Builder<ModelOrders>()
                    .setQuery(queryUserOrders, ModelOrders.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<ModelOrders, OrdersViewHolder>(recyclerOptions) {
                @Override
                protected void onBindViewHolder(@NonNull OrdersViewHolder holder, int position, @NonNull ModelOrders model) {



                    /*holder.singleItemProductsBinding.textViewProductsProductName.setText(model.getName());
                    holder.singleItemProductsBinding.textViewProductsProductPrice.setText(String.format(context.getString(R.string.recycler_var_product_price), model.getPrice() + ""));
                    holder.singleItemProductsBinding.textViewProductsProductPricePerKg.setText(String.format(context.getString(R.string.recycler_var_product_price_per_kg), model.getPrice() + ""));
                    holder.singleItemProductsBinding.textViewProductsProductQuantityNum.setText(MessageFormat.format("{0}", model.getQuantity()));

*/
                }

                @NonNull
                @Override
                public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    ItemOrderBinding view = ItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                    return new OrdersViewHolder(view);
                }

                @Override
                public void onError(FirebaseFirestoreException e) {
                    Log.e("Error", e.getMessage());
                }
            };
            adapter.notifyDataSetChanged();
            ordersListRecycler.setAdapter(adapter);
        }

        public static class OrdersViewHolder extends RecyclerView.ViewHolder {
            private final ItemOrderBinding itemOrderBinding;

            public OrdersViewHolder(ItemOrderBinding itemOrderBinding) {
                super(itemOrderBinding.getRoot());
                this.itemOrderBinding = itemOrderBinding;
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