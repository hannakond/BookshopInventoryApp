package com.example.android.bookshopinventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookshopinventoryapp.Data.ProductContract.ProductEntry;
import com.squareup.picasso.Picasso;

import java.io.File;

public class EditProductActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int PHOTO_REQUEST_CODE = 20;
    public static final int EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 21;
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri currentProductUri;
    private ImageView imageViewP;
    private EditText editTextNameP;
    private EditText editTextPriceP;
    private EditText editTextSupplier;
    private EditText editTextQuantity;
    private String currentPhotoUri = "no image";

    private int deletedRows = 0;

    private boolean productDataChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productDataChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);

        final ScrollView scrollview = (ScrollView) findViewById(R.id.scrollview);
        imageViewP = (ImageView) findViewById(R.id.image_product);
        editTextNameP = (EditText) findViewById(R.id.textview_product_name);
        editTextPriceP = (EditText) findViewById(R.id.textview_text_price);
        editTextSupplier = (EditText) findViewById(R.id.textview_text_supplier);
        editTextQuantity = (EditText) findViewById(R.id.quantity);
        final TextView textPhoto = (TextView) findViewById(R.id.text_image_product);
        final TextView instructionsPhoto = (TextView) findViewById(R.id.instructions_img);
        final View viewImageTab = findViewById(R.id.view_spacer);

        scrollview.fullScroll(ScrollView.FOCUS_UP);

        imageViewP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                productDataChanged = true;
                instructionsPhoto.setVisibility(View.GONE);
                return false;
            }
        });
        editTextNameP.setOnTouchListener(mTouchListener);
        editTextPriceP.setOnTouchListener(mTouchListener);
        editTextSupplier.setOnTouchListener(mTouchListener);
        editTextQuantity.setOnTouchListener(mTouchListener);

        imageViewP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProductPhoto(v);
            }
        });

        final Intent intent = getIntent();
        currentProductUri = intent.getData();

        if (currentProductUri == null) {
            setTitle(getString(R.string.title_activity_new_product));
            textPhoto.setVisibility(View.VISIBLE);
            viewImageTab.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.title_activity_modify_product));
            textPhoto.setVisibility(View.GONE);
            instructionsPhoto.setVisibility(View.GONE);
            editTextNameP.setHintTextColor(getResources().getColor(R.color.colorHint));
            viewImageTab.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
    }

    private void showDeleteConfirmationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteProduct() {
        if (currentProductUri != null) {
            getContentResolver().delete(currentProductUri, null, null);
            if (deletedRows == 0) {
                Toast.makeText(this, R.string.delete_product, Toast.LENGTH_SHORT).show();
                final Intent intent = new Intent(EditProductActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.error_cleaning_product, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void showDialogCancelChanges(
            DialogInterface.OnClickListener discardButtonClickListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!productDataChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showDialogCancelChanges(discardButtonClickListener);
    }

    public void updateProductPhoto(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                selectProductPhoto();
            } else {
                String[] permissionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissionRequest, EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);
            }
        } else {
            selectProductPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
            selectProductPhoto();
        } else {
            Toast.makeText(this, R.string.permission, Toast.LENGTH_LONG).show();
        }
    }

    private void selectProductPhoto() {
        final Intent photoSelector = new Intent(Intent.ACTION_PICK);

        final File photoDirectory = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES);
        final String pictureDirectoryPath = photoDirectory.getPath();
        final Uri data = Uri.parse(pictureDirectoryPath);

        photoSelector.setDataAndType(data, "image/*");

        startActivityForResult(photoSelector, PHOTO_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            final Uri productPhotoUri = data.getData();
            currentPhotoUri = productPhotoUri.toString();

            Picasso.with(this).load(productPhotoUri)
                    .placeholder(R.drawable.new_image)
                    .fit()
                    .into(imageViewP);
        }
    }

    private void addNewProduct() {
        final String number = editTextNameP.getText().toString();
        final String price = editTextPriceP.getText().toString();
        final String supplier = editTextSupplier.getText().toString();
        final String quantity = editTextQuantity.getText().toString();

        if (number.isEmpty() || price.isEmpty() || supplier.isEmpty() || quantity.isEmpty()) {
            Toast.makeText(this, R.string.reneval, Toast.LENGTH_SHORT).show();
            return;
        }
        final ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_IMAGE_PRODUCT, currentPhotoUri);
        values.put(ProductEntry.COLUMN_NAME_PRODUCT, number);
        values.put(ProductEntry.COLUMN_PRICE_PRODUCT, price);
        values.put(ProductEntry.COLUMN_PROVIDER_PRODUCT, supplier);
        values.put(ProductEntry.COLUMN_QUANTITY_PRODUCT, quantity);
        values.put(ProductEntry.COLUMN_PRODUCT_SALES, 0.0);

        if (currentProductUri == null) {
            Uri insertedRow = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (insertedRow == null) {
                Toast.makeText(this, R.string.error_saving_changes, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.total, Toast.LENGTH_LONG).show();
            }
        } else {
            final int rowUpdated = getContentResolver().update(currentProductUri, values, null, null);
            if (rowUpdated == 0) {
                Toast.makeText(this, R.string.error_saving_changes, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.modified, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_products, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentProductUri == null) {
            final MenuItem menuItem = menu.findItem(R.id.delete_product);
            menuItem.setVisible(false);
        }
        if (currentProductUri != null) {
            final MenuItem menuItem = menu.findItem(R.id.saved_product);
            menuItem.setIcon(R.drawable.ic_done_white_18dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saved_product:
                addNewProduct();
                finish();
                return true;
            case R.id.delete_product:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_IMAGE_PRODUCT,
                ProductEntry.COLUMN_NAME_PRODUCT,
                ProductEntry.COLUMN_PRICE_PRODUCT,
                ProductEntry.COLUMN_PROVIDER_PRODUCT,
                ProductEntry.COLUMN_QUANTITY_PRODUCT,
                ProductEntry.COLUMN_PRODUCT_SALES};

        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            final int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE_PRODUCT);
            final int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME_PRODUCT);
            final int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE_PRODUCT);
            final int providerColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PROVIDER_PRODUCT);
            final int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY_PRODUCT);

            final String name = cursor.getString(nameColumnIndex);
            final float price = cursor.getFloat(priceColumnIndex);
            final String provider = cursor.getString(providerColumnIndex);
            final int quantity = cursor.getInt(quantityColumnIndex);
            currentPhotoUri = cursor.getString(imageColumnIndex);

            editTextNameP.setText(name);
            editTextPriceP.setText(String.valueOf(price));
            editTextSupplier.setText(provider);
            editTextQuantity.setText(String.valueOf(quantity));

            Picasso.with(this).load(currentPhotoUri)
                    .placeholder(R.drawable.new_image)
                    .fit()
                    .into(imageViewP);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        editTextNameP.setText("");
        editTextSupplier.setText("");
        editTextQuantity.setText("");
        editTextPriceP.setText("");
    }
}
