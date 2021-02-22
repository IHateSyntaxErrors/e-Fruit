package com.unipi.p17172p17168p17164.efruit.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.unipi.p17172p17168p17164.efruit.Models.ModelCart;
import com.unipi.p17172p17168p17164.efruit.Models.ModelProducts;
import com.unipi.p17172p17168p17164.efruit.Models.ModelUsers;
import com.unipi.p17172p17168p17164.efruit.R;
import com.unipi.p17172p17168p17164.efruit.Utils.Toolbox;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CartActivity extends AppCompatActivity {

    // ~~~~~~~VARIABLES~~~~~~~
    private FirebaseUser firebaseUser;
    private Toolbox toolbox;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    @BindView(R.id.recyclerViewCart)
    RecyclerView cartList;

    @BindView(R.id.imageViewCart_BackButton)
    ImageView imageViewBackButton;
    @BindView(R.id.viewFlipperCart)
    ViewFlipper viewFlipper;
    private LinearLayoutManager linearLayoutManager;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Activity opening animation when opened
        this.overridePendingTransition(R.anim.anim_slide_in_left,
                                       R.anim.anim_slide_out_left);
        ButterKnife.bind(this);
        init();
        getCartList();
        updateUI();

        imageViewBackButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cartList.setLayoutManager(linearLayoutManager);
        cartList.setHasFixedSize(true);
    }

    private void getCartList() {
        final String TAG = "[CartActivity]";

        Query queryCartProducts = db.collection("carts").document(firebaseUser.getUid()).collection("products");

        /*queryCartProducts.get().addOnCompleteListener(task -> {
            if (task.getResult().isEmpty()) {
                viewFlipper.setDisplayedChild(0);
            }
        });*/

        queryCartProducts.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "listen:error", e);
                return;
            }

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
                Glide.with(getApplicationContext())
                        .load(model.getImgUrl())
                        .into(holder.viewHolderCart_ImgProductImage);
                holder.viewHolderCart_TxtProductName.setText(model.getName());
                holder.viewHolderCart_TxtProductPrice.setText(String.format(getString(R.string.recycler_var_product_price), model.getPrice() + ""));
                holder.viewHolderCart_TxtProductPricePerKg.setText(String.format(getString(R.string.recycler_var_product_price_per_kg), model.getPrice() + ""));
                holder.viewHolderCart_TxtProductQuantity.setText(MessageFormat.format("{0}", model.getQuantity()));
                holder.viewHolderCart_btnRemoveItem.setOnClickListener(v -> {

                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_single_item_cart, parent, false);
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

    public class CartViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageViewCart_ProductImage)
        ImageView viewHolderCart_ImgProductImage;
        @BindView(R.id.textViewCart_ProductName)
        TextView viewHolderCart_TxtProductName;
        @BindView(R.id.textViewCart_ProductPrice)
        TextView viewHolderCart_TxtProductPrice;
        @BindView(R.id.textViewCart_ProductPricePerKg)
        TextView viewHolderCart_TxtProductPricePerKg;
        @BindView(R.id.textViewCart_ProductQuantityNum)
        TextView viewHolderCart_TxtProductQuantity;
        /*@BindView(R.id.textViewCart_SelectedAmtNumber)
        TextView viewHolderProducts_TxtSelectedAmtNumber;*/
        @BindView(R.id.btnCart_RemoveItem)
        TextView viewHolderCart_btnRemoveItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void updateUI() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                                       R.anim.anim_slide_out_right);
    }
}