package com.example.android.bookshopinventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.bookshopinventoryapp.Data.ProductContract.ProductEntry;
import com.squareup.picasso.Picasso;

public class ManageProductActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private final static String PRODUCT_NEW =
            "Great news everyone: more wonderful books have just arrived! " +
            "Check out this bestseller, it'/s a must read!:)";

    private Uri currentProductUri;

    private String currentPhotoUri = "no image";

    private TextView numberProd;
    private TextView priceProd;
    private TextView supplierProd;
    private TextView salesProd;
    private EditText stockProd;
    private ImageView imageProd;
    private ImageView supplierRequest;
    private TextView quantityProd;

    private int quantity = 0;
    private int clicks = 0;
    private Snackbar snackbar;
    private Boolean request = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_product);

        numberProd = (TextView) findViewById(R.id.tv_product_name);
        priceProd = (TextView) findViewById(R.id.textview_text_price);
        supplierProd = (TextView) findViewById(R.id.textview_text_provider);
        salesProd = (TextView) findViewById(R.id.sales_product);
        stockProd = (EditText) findViewById(R.id.quantity);
        Button buttonRestart = (Button) findViewById(R.id.restart_stock);
        Button buttonSum = (Button) findViewById(R.id.sum_stock);
        imageProd = (ImageView) findViewById(R.id.image_product);
        supplierRequest = (ImageView) findViewById(R.id.supplier_merchandise);
        quantityProd = (TextView) findViewById(R.id.product_quantity);

        final Intent intent = getIntent();
        currentProductUri = intent.getData();

        getLoaderManager().initLoader(0, null, this);

        buttonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityProd.setText(getString(R.string.request_quantity));
                UpdateStock();
            }
        });

        buttonSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityProd.setText(getString(R.string.request_quantity));
                SumStock();
            }
        });

        supplierRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRequestMerchandise();
                request = true;
            }
        });
    }

    private void updateStockProduct() {
        if (request != false) {
            String quantity = stockProd.getText().toString();

            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_QUANTITY_PRODUCT, quantity);

            if (currentProductUri != null) {
                int rowUpdated = getContentResolver().update(currentProductUri, values, null, null);

                if (rowUpdated == 0) {
                    Toast.makeText(this, R.string.error_stock, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.stock_correct, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);

                    finish();
                }
            }
        } else {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_note_supply,
                    (ViewGroup) findViewById(R.id.container_toast_supply));
            //Toast
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CLIP_HORIZONTAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }

    private void UpdateStock() {
        quantity = Integer.parseInt(stockProd.getText().toString());
        if (quantity >= 10) {
            int updateStock = quantity - 10;
            String stockUpdate = String.valueOf(updateStock);
            stockProd.setText(stockUpdate);
            clicks ++;

            if (clicks % 2 == 1) {
                snackbar = Snackbar
                        .make(getCurrentFocus(), R.string.note,
                                Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.colorStock));
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                snackbar.setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
        }
        if (quantity < 10) {
            int restartStock = quantity - quantity;
            String stockRestart = String.valueOf(restartStock);
            stockProd.setText(stockRestart);
            clicks++;

            if (clicks % 2 == 1) {
                snackbar = Snackbar
                        .make(getCurrentFocus(), R.string.note,
                                Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.colorStock));
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                snackbar.setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
            if (quantity == 0) {
                Toast.makeText(this, R.string.empty_stock, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void SumStock() {
        quantity = Integer.parseInt(stockProd.getText().toString());
        int algo = quantity + 10;
        String sum = String.valueOf(algo);
        stockProd.setText(sum);
        clicks++;

        if (clicks % 2 == 0) {
            snackbar = Snackbar
                    .make(getCurrentFocus(), R.string.note,
                            Snackbar.LENGTH_LONG)
                    .setActionTextColor(getResources().getColor(R.color.colorStock));
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            snackbar.setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            })
                    .show();
        }
    }

    private void supplierRequest() {
        String product = numberProd.getText().toString();
        String supplier = supplierProd.getText().toString();
        String supplierAddress = "www." + supplier + ".eu";
        String[] destination = {supplierAddress};
        String stockRequest = stockProd.getText().toString();

        StringBuilder builder = new StringBuilder();
        builder.append("Greetings " + supplier + " :\n");
        builder.append("I would like to rder the following products:\n");
        builder.append("PRODUCT: " + product + "\n");
        builder.append("QUANTITY: " + stockRequest + "\n");
        builder.append("\nThanks in advance and best regards.");
        String request = builder.toString();

        Intent intentStock = new Intent(Intent.ACTION_SEND);
        intentStock.setData(Uri.parse("mailto:"));
        intentStock.setType("text/plain");
        intentStock.putExtra(Intent.EXTRA_EMAIL, destination);
        intentStock.putExtra(Intent.EXTRA_SUBJECT, "Supplier Request ");
        intentStock.putExtra(Intent.EXTRA_TEXT, request);
        startActivity(Intent.createChooser(intentStock, "Supplier Request"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_products, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_product_attributes:
                modifyProductFields();
                return true;
            case R.id.saved_stock:
                updateStockProduct();
                return true;
            case R.id.action_share:
                shareProduct();
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareProduct() {
        String product = numberProd.getText().toString();
        String supplier = supplierProd.getText().toString();
        String price = priceProd.getText().toString();

        StringBuilder builderNewProduct = new StringBuilder();
        builderNewProduct.append(PRODUCT_NEW + " \n\n"
                + product + "\n" + supplier + "\n" + price);

        String smth = builderNewProduct.toString();

        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("text/plain");
        intentShare.putExtra(Intent.EXTRA_SUBJECT, "New Product");
        intentShare.putExtra(Intent.EXTRA_TEXT, smth);
        startActivity(Intent.createChooser(intentShare, "Share a New Product"));
    }

    private void modifyProductFields() {
        Intent intent = new Intent(ManageProductActivity.this, EditProductActivity.class);
        intent.setData(ContentUris.withAppendedId(ProductEntry.CONTENT_URI,
                       ContentUris.parseId(this.currentProductUri)));
        startActivity(intent);
        finish();
    }

    private void showDialogRequestMerchandise() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.supplier_request);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                supplierRequest();
            }
        });
        builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("REQUEST SUPPLY");
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_IMAGE_PRODUCT,
                ProductEntry.COLUMN_NAME_PRODUCT,
                ProductEntry.COLUMN_PRICE_PRODUCT,
                ProductEntry.COLUMN_PROVIDER_PRODUCT,
                ProductEntry.COLUMN_QUANTITY_PRODUCT,
                ProductEntry.COLUMN_PRODUCT_SALES};

        return new CursorLoader(this, currentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE_PRODUCT);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME_PRODUCT);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE_PRODUCT);
            int providerColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PROVIDER_PRODUCT);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY_PRODUCT);
            int salesColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SALES);

            String name = cursor.getString(nameColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            String provider = cursor.getString(providerColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            currentPhotoUri = cursor.getString(imageColumnIndex);
            double pvp = cursor.getDouble(salesColumnIndex);

            numberProd.setText(name);
            priceProd.setText(String.valueOf(price) + " €");
            supplierProd.setText(provider.toUpperCase());
            salesProd.setText(String.valueOf(pvp) + " €");
            stockProd.setText(String.valueOf(quantity));

            Picasso.with(this).load(currentPhotoUri)
                    .placeholder(R.drawable.new_image)
                    .fit()
                    .into(imageProd);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        numberProd.setText("");
        priceProd.setText(String.valueOf(""));
        supplierProd.setText("");
        salesProd.setText("");
        quantityProd.setText(R.string.stock_correct);
    }

    public void noteQuantityImage (View view) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_note_change_image,
                (ViewGroup) findViewById(R.id.container_toast));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CLIP_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

}
