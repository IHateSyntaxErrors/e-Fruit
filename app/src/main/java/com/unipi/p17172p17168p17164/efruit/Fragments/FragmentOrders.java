package com.unipi.p17172p17168p17164.efruit.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.unipi.p17172p17168p17164.efruit.Models.ModelOrders;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.DBHelper;
import com.unipi.p17172p17168p17164.efruit.databinding.FragmentOrdersBinding;
import com.unipi.p17172p17168p17164.efruit.databinding.ItemOrderBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
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
        SimpleDateFormat simpleDateFormat;

        private ViewFlipper viewFlipper;
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
            viewFlipper = binding.viewFlipperOrders;

            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            ordersListRecycler = binding.recyclerViewOrders;
            ordersListRecycler.setLayoutManager(linearLayoutManager);
            ordersListRecycler.setHasFixedSize(true);
        }

        private void updateUI() {}

        public void getOrdersList(){
            Query queryUserOrders = DBHelper.getUserOrders(db, firebaseUser.getUid());

            // RecyclerOptions
            FirestoreRecyclerOptions<ModelOrders> recyclerOptions = new FirestoreRecyclerOptions.Builder<ModelOrders>()
                    .setQuery(queryUserOrders, ModelOrders.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<ModelOrders, OrdersViewHolder>(recyclerOptions) {
                @Override
                protected void onBindViewHolder(@NonNull OrdersViewHolder holder, int position, @NonNull ModelOrders model) {

                    Task<QuerySnapshot> q1 = queryUserOrders.get();
                    Task<QuerySnapshot> q2 = DBHelper.getOrderShopName(db, model.getShopId()).get();

                    Tasks.whenAllComplete(q1, q2).addOnSuccessListener(list -> {
                        if (Objects.requireNonNull(q1.getResult()).isEmpty()) {
                            viewFlipper.setDisplayedChild(1);
                            return;
                        }
                        holder.itemOrderBinding.textViewOrderItemDateValue.setText(simpleDateFormat.format(model.getPickup_timestamp().toDate()));
                        holder.itemOrderBinding.textViewOrderItemShopNameValue.setText(q2.getResult().getDocuments().get(0).getString("name"));

                        // Products List
                        ArrayList<String> productsList = new ArrayList<>(model.getProducts());
                        StringBuilder jokeStringBuilder = new StringBuilder();
                        String productLast = productsList.get(productsList.size() - 1);
                        productsList.remove(productsList.size() - 1);
                        for (String product : productsList)
                            jokeStringBuilder.append("● ").append(product).append(",\n\n");
                        jokeStringBuilder.append("● ").append(productLast);
                        holder.itemOrderBinding.textViewOrderItemProductsValue.setText(jokeStringBuilder);

                        if (model.isIs_completed())
                            holder.itemOrderBinding.textViewOrderItemStatusValue.setText(context.getString(R.string.recycler_item_order_status_completed));
                        else
                            holder.itemOrderBinding.textViewOrderItemStatusValue.setText(context.getString(R.string.recycler_item_order_status_processing));
                    });
                }

                @NonNull
                @Override
                public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    ItemOrderBinding view = ItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                    return new OrdersViewHolder(view);
                }

                @Override
                public void onError(@NonNull FirebaseFirestoreException e) {
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