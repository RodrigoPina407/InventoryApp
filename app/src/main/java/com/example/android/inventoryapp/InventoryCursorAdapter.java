package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;


public class InventoryCursorAdapter extends CursorAdapter {

    private InventoryActivity.ItemUtilities mItem;
    private Cursor mCursor;

    public InventoryCursorAdapter(Context context, Cursor cursor, InventoryActivity.ItemUtilities item) {
        super(context, cursor, 0);
        mItem = item;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView itemNameView = view.findViewById(R.id.item_name);
        TextView itemPriceView = view.findViewById(R.id.item_price);
        TextView itemQuantityView = view.findViewById(R.id.item_quantity);
        TextView itemSupplierView = view.findViewById(R.id.item_supplier);

        ImageView sellButton = view.findViewById(R.id.sell_button);

        mCursor = cursor;

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
        int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPPLIER);

        String name = cursor.getString(nameColumnIndex);
        String price = String.valueOf(cursor.getInt(priceColumnIndex)) + context.getString(R.string.currency_symbol);
        String quantityStr = String.valueOf(cursor.getInt(quantityColumnIndex));
        String supplier = cursor.getString(supplierColumnIndex);

        itemNameView.setText(name);
        itemPriceView.setText(price);
        itemQuantityView.setText(quantityStr);
        itemSupplierView.setText(supplier);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ListView list = mItem.getList();
                View item = (View) view.getParent();
                int pos = list.getPositionForView(item);
                long id = list.getItemIdAtPosition(pos);

                mCursor.moveToPosition(pos);
                int quantityColumnIndex = mCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);

                int quantity = mCursor.getInt(quantityColumnIndex);

                mItem.handleClick(quantity, id);
            }
        });

    }



}
