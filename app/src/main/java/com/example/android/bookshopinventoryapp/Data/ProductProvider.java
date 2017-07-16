package com.example.android.bookshopinventoryapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.bookshopinventoryapp.Data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {

    public static final String LOG_TAG = ProductEntry.class.getSimpleName();

    private static final int TABLE_COMPLETE = 100;

    private static final int ID_TABLE = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT,
                TABLE_COMPLETE);

        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT
                + "/#", ID_TABLE);
    }

    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TABLE_COMPLETE:
              cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ID_TABLE:
                   selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TABLE_COMPLETE:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {
        String name = values.getAsString(ProductEntry.COLUMN_NAME_PRODUCT);
        if (name == null) {
            throw new IllegalArgumentException("Product needs a number.");
        }

        Float price = values.getAsFloat(ProductEntry.COLUMN_PRICE_PRODUCT);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product needs a price.");
        }

        String provider = values.getAsString(ProductEntry.COLUMN_PROVIDER_PRODUCT);
        if (provider == null) {
            throw new IllegalArgumentException("Please mention the SUPPLIER");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TABLE_COMPLETE:
                return updateProduct(uri, values, selection, selectionArgs);
            case ID_TABLE:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ProductEntry.COLUMN_NAME_PRODUCT)) {
            String name = values.getAsString(ProductEntry.COLUMN_NAME_PRODUCT);
            if (name == null) {
                return 0;
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRICE_PRODUCT)) {
            Float price = values.getAsFloat(ProductEntry.COLUMN_PRICE_PRODUCT);
            if (price == null || price == 0) {
                return 0;
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PROVIDER_PRODUCT)) {
            String name = values.getAsString(ProductEntry.COLUMN_PROVIDER_PRODUCT);
            if (name == null) {
                return 0;
            }
        }

          if (values.size() == 0) {
            return 0;
        }

         SQLiteDatabase database = mDbHelper.getWritableDatabase();

         int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TABLE_COMPLETE:
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ID_TABLE:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TABLE_COMPLETE:
                return ProductEntry.CONTENT_LIST_TYPE;
            case ID_TABLE:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
