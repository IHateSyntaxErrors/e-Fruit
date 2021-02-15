package com.unipi.p17172.nikolaspateras.sms13033;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17172.nikolaspateras.sms13033.Adapters.DataAdapterSmsTypesEdit;
import com.unipi.p17172.nikolaspateras.sms13033.Items.ItemSmsTypesEdit;
import com.unipi.p17172.nikolaspateras.sms13033.Utils.DBHelper;
import com.unipi.p17172.nikolaspateras.sms13033.Utils.Toolbox;

import java.util.ArrayList;

public class ActivitySettingsMessages extends Activity {
    private final ArrayList<ItemSmsTypesEdit> arrayListSmsTypeEdit = new ArrayList<>();
    private DBHelper dbHelper;
    private Toolbox toolbox;
    private ViewFlipper viewFlipperSmsTypeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_messages);

        this.overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);

        dbHelper = new DBHelper(this);
        toolbox = new Toolbox();

        ImageView imageViewBackButton = findViewById(R.id.imageViewSettingsMessagesBackButton);
        imageViewBackButton.setOnClickListener(v -> {
            onBackPressed();
        });

        viewFlipperSmsTypeEdit = findViewById(R.id.viewFlipperSmsTypeEdit);
        RecyclerView recyclerViewSmsTypeEdit = findViewById(R.id.recyclerViewSmsTypeEdit);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DataAdapterSmsTypesEdit dataAdapterSmsTypeEdit = new DataAdapterSmsTypesEdit(this, arrayListSmsTypeEdit);
        recyclerViewSmsTypeEdit.setHasFixedSize(true);
        recyclerViewSmsTypeEdit.setLayoutManager(linearLayoutManager);
        recyclerViewSmsTypeEdit.setAdapter(dataAdapterSmsTypeEdit);

        if (dbHelper.numberOfRows() == 0) {
            toolbox.insertDefaultRows(dbHelper, this);
        }
        loadAllDatabaseData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }
    public void loadAllDatabaseData() {
        Cursor cursor = dbHelper.readAllData();
        if (cursor != null && cursor.getCount() > 0) {
            // Setting the view to the sms types found mode.
            viewFlipperSmsTypeEdit.setDisplayedChild(0);
            if (cursor.moveToFirst()) {
                do {
                    ItemSmsTypesEdit item = new ItemSmsTypesEdit();
                    item.setSmsNumber(cursor.getString(1));
                    item.setSmsReason(cursor.getString(2));
                    arrayListSmsTypeEdit.add(item);
                } while (cursor.moveToNext());
            }
        } else {
            // Setting the view to show that nothing was found in the database.
            viewFlipperSmsTypeEdit.setDisplayedChild(1);
        }
    }

    public void insertSmsType() {
        toolbox.insertDefaultRows(dbHelper, this);
    }
    public void resetSmsTypes() {

    }
}