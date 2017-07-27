package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditView;
    private EditText mPriceEditView;
    private EditText mQuantityEditView;
    private EditText mSupplierEditView;
    private EditText mDescriptionEditView;
    private ImageView mItemImage;
    private TextView mOrderTextView;
    private byte[] mByteImage;

    private boolean mItemHasChanged = false;

    Uri mCurrentItemUri;

    private int mOrderQuantity = 0;

    private int SELECT_PHOTO = 1;

    private int BITMAP_SIZE = 400;

    private static final int EXISTING_ITEM_LOADER = 0;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        mOrderTextView = (TextView) findViewById(R.id.counter);
        Button orderButton = (Button) findViewById(R.id.order_button);
        Button plusButton = (Button) findViewById(R.id.plus);
        Button minusButton = (Button) findViewById(R.id.minus);

        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.add_item));
            invalidateOptionsMenu();
            orderButton.setVisibility(View.INVISIBLE);
            plusButton.setVisibility(View.INVISIBLE);
            minusButton.setVisibility(View.INVISIBLE);
            mOrderTextView.setVisibility(View.INVISIBLE);
        } else {

            setTitle(getString(R.string.update_item));

            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        mItemImage = (ImageView) findViewById(R.id.item_image);
        mNameEditView = (EditText) findViewById(R.id.name_edit_view);
        mPriceEditView = (EditText) findViewById(R.id.price_edit_view);
        mQuantityEditView = (EditText) findViewById(R.id.quantity_edit_view);
        mSupplierEditView = (EditText) findViewById(R.id.supplier_edit_view);
        mDescriptionEditView = (EditText) findViewById(R.id.description_edit_view);


        mItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent email = new Intent(Intent.ACTION_SENDTO);

                String addresses[] = {"orderItem@gmail.com"};

                String quantity = String.valueOf(mOrderQuantity);

                email.setData(Uri.parse("mailto:"));
                email.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_i_want) + " " + quantity + " " + getString(R.string.email_this_item));
                email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order));
                email.putExtra(Intent.EXTRA_EMAIL, addresses);

                if (email.resolveActivity(getPackageManager()) != null) {
                    startActivity(email);
                }

            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusButton();
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusButton();
            }
        });

        mNameEditView.setOnTouchListener(mTouchListener);
        mPriceEditView.setOnTouchListener(mTouchListener);
        mQuantityEditView.setOnTouchListener(mTouchListener);
        mSupplierEditView.setOnTouchListener(mTouchListener);
        mDescriptionEditView.setOnTouchListener(mTouchListener);
        mItemImage.setOnTouchListener(mTouchListener);

    }

    private void plusButton() {
        mOrderQuantity++;
        mOrderTextView.setText(String.valueOf(mOrderQuantity));
    }

    private void minusButton() {
        if (mOrderQuantity > 0) {
            mOrderQuantity--;
        }
        mOrderTextView.setText(String.valueOf(mOrderQuantity));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryEntry.COLUMN_ITEM_SUPPLIER,
                InventoryEntry.COLUMN_ITEM_DESCRIPTION,
                InventoryEntry.COLUMN_ITEM_IMAGE};

        return new CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }


        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPPLIER);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_DESCRIPTION);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String price = String.valueOf(cursor.getInt(priceColumnIndex));
            String quantity = String.valueOf(cursor.getInt(quantityColumnIndex));
            String supplier = cursor.getString(supplierColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);

            byte[] byteImage = cursor.getBlob(imageColumnIndex);

            mByteImage = byteImage;

            if (byteImage != null) {
                Bitmap image = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
                mItemImage.setImageBitmap(image);
            }

            mNameEditView.setText(name);
            mPriceEditView.setText(price);
            mQuantityEditView.setText(quantity);
            mSupplierEditView.setText(supplier);
            mDescriptionEditView.setText(description);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditView.setText("");
        mPriceEditView.setText("");
        mQuantityEditView.setText("");
        mSupplierEditView.setText("");
        mDescriptionEditView.setText("");
        mItemImage.setImageResource(R.drawable.camera);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_delete_item:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_save_item:
                saveItem();
                finish();
                return true;

            case android.R.id.home:

                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_dialog);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, null);

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.items_deleted);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {

        if (mCurrentItemUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.item_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.item_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    private void saveItem() {

        String nameStr = mNameEditView.getText().toString().trim();
        String priceStr = mPriceEditView.getText().toString().trim();
        String quantityStr = mQuantityEditView.getText().toString().trim();
        String supplierStr = mSupplierEditView.getText().toString().trim();
        String descriptionStr = mDescriptionEditView.getText().toString().trim();

        if ((TextUtils.isEmpty(nameStr) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(quantityStr)
                || TextUtils.isEmpty(supplierStr) || mByteImage == null)) {
            Toast.makeText(this, getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_ITEM_NAME, nameStr);

        int price = 0;

        if (!TextUtils.isEmpty(priceStr))
            price = Integer.parseInt(priceStr);

        int quantity = 0;

        if (!TextUtils.isEmpty(quantityStr))
            quantity = Integer.parseInt(quantityStr);

        values.put(InventoryEntry.COLUMN_ITEM_PRICE, price);
        values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_ITEM_SUPPLIER, supplierStr);
        values.put(InventoryEntry.COLUMN_ITEM_DESCRIPTION, descriptionStr);
        values.put(InventoryEntry.COLUMN_ITEM_IMAGE, mByteImage);

        if (mCurrentItemUri == null) {

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.error_insert_item),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.new_item_add,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.error_update_item,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.update_item_success,
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SELECT_PHOTO) {

            if (resultCode == RESULT_OK) {
                if (intent != null) {
                    // Get the URI of the selected file
                    final Uri uri = intent.getData();
                    try {
                        useImage(uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, intent);

        }
    }

    public void useImage(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        bitmap = getResizedBitmap(bitmap, BITMAP_SIZE, BITMAP_SIZE);
        mByteImage = getBitmapAsByteArray(bitmap);
        mItemImage.setImageBitmap(bitmap);
    }


    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }
}
