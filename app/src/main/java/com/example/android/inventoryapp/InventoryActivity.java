package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;

    private InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView inventoryListView = (ListView) findViewById(R.id.items_list);

        View emptyView = findViewById(R.id.empty_view);

        /**
         * Set empty view for the ListView
         */
        inventoryListView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this, null, new ItemUtilities(inventoryListView));

        inventoryListView.setAdapter(mCursorAdapter);

        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);

                Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                intent.setData(currentItemUri);
                startActivity(intent);

            }
        });


        getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryEntry.COLUMN_ITEM_SUPPLIER};

        return new CursorLoader(this, InventoryEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_delete_all_items:
                deleteAllItems();
                return true;
            case R.id.action_insert_dummy:
                insertDummy();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllItems() {

        getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Toast.makeText(this, R.string.all_items_deleted_toast, Toast.LENGTH_SHORT).show();
    }

    private void insertDummy() {
        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_ITEM_NAME, "Hammer");
        values.put(InventoryEntry.COLUMN_ITEM_PRICE, 17);
        values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, 30);
        values.put(InventoryEntry.COLUMN_ITEM_SUPPLIER, "BOSCH");
        values.put(InventoryEntry.COLUMN_ITEM_DESCRIPTION, "");

        getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        Toast.makeText(this, R.string.dummy_data_toast, Toast.LENGTH_SHORT).show();
    }


    public class ItemUtilities {

        private ListView mList;

        ItemUtilities(ListView list) {
            mList = list;
        }

        public ListView getList() {
            return mList;
        }

        public void handleClick(int quantity, long id) {

            if (quantity > 0) {
                quantity--;
            }

            ContentValues values = new ContentValues();

            Uri newUri = Uri.withAppendedPath(InventoryEntry.CONTENT_URI, String.valueOf(id));

            values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, quantity);

            getContentResolver().update(newUri, values, null, null);

            Toast toast = Toast.makeText(InventoryActivity.this, R.string.item_sold, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


}
