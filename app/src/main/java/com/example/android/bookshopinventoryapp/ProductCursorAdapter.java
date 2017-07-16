package com.example.android.bookshopinventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookshopinventoryapp.R;
import com.example.android.bookshopinventoryapp.Data.ProductContract.ProductEntry;
import com.squareup.picasso.Picasso;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.products_list, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        ImageView imageViewProduct = (ImageView) view.findViewById(R.id.image_product);
        TextView textViewNameP = (TextView) view.findViewById(R.id.product_name);
        TextView textViewPriceP = (TextView) view.findViewById(R.id.price);
        TextView textViewSupplierP = (TextView) view.findViewById(R.id.product_supplier);
        TextView textViewQuantityP = (TextView) view.findViewById(R.id.quantity);
        final TextView totalSalesProduct = (TextView) view.findViewById(R.id.text_view_sales);
        ImageView buttonBuyProduct = (ImageView) view.findViewById(R.id.buy_button);

        int pictureColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE_PRODUCT);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME_PRODUCT);
        final int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE_PRODUCT);
        int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PROVIDER_PRODUCT);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY_PRODUCT);
        int salesColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SALES);

        int id = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        Uri productPicture = Uri.parse(cursor.getString(pictureColumnIndex));
        final String productName = cursor.getString(nameColumnIndex);
        final double pricePvp = cursor.getDouble(priceColumnIndex);
        String productPrice = "PFP: " + cursor.getString(priceColumnIndex) + " €";
        String productSupplier = cursor.getString(supplierColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        final double salesTotalProductValue = cursor.getDouble(salesColumnIndex);
        String productQuantity = "Stock\n" + cursor.getString(quantityColumnIndex);
        String salesTotalProduct = "Sales: " + cursor.getString(salesColumnIndex) + " €";

        final Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

        textViewNameP.setText(productName);
        textViewPriceP.setText(productPrice);
        textViewSupplierP.setText(productSupplier);
        textViewQuantityP.setText(productQuantity);
        totalSalesProduct.setText(salesTotalProduct);

        Picasso.with(context).load(productPicture)
                .placeholder(R.drawable.new_image)

                .fit()
                .into(imageViewProduct);

        buttonBuyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver resolver = v.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                if (quantity > 0) {
                    int stock = quantity;
                    double pvp = pricePvp;
                    double totalSum = salesTotalProductValue + pvp;
                    values.put(ProductEntry.COLUMN_PRODUCT_SALES, totalSum);
                    values.put(ProductEntry.COLUMN_QUANTITY_PRODUCT, --stock);
                    resolver.update(
                            currentProductUri,
                            values,
                            null,
                            null
                    );
                    context.getContentResolver().notifyChange(currentProductUri, null);
                } else {
                    Toast.makeText(context, R.string.text_empty_view, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
