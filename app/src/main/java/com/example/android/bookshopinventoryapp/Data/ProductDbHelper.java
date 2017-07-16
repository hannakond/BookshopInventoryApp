package com.example.android.bookshopinventoryapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookshopinventoryapp.Data.ProductContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "myInventory.db";

    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_IMAGE_PRODUCT + " TEXT NOT NULL DEFAULT 'no image', "
                + ProductEntry.COLUMN_NAME_PRODUCT + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRICE_PRODUCT + " REAL NOT NULL, "
                + ProductEntry.COLUMN_PROVIDER_PRODUCT + " TEXT DEFAULT UNKOKNW, "
                + ProductEntry.COLUMN_QUANTITY_PRODUCT + " INTEGER DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_SALES + " REAL DEFAULT 0.0 );";

        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME);
        onCreate(db);
    }
}
