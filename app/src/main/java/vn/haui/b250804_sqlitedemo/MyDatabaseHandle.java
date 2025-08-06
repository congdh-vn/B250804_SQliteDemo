package vn.haui.b250804_sqlitedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.List;

public class MyDatabaseHandle extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "khong_gian_so2.db";
    private static final String TABLE_NAME = "tbl_product";
    private static final int VERSION = 1;

    public MyDatabaseHandle(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "Create table " + TABLE_NAME + "(productId integer primary key, productName text, price float)";
        try {
            sqLiteDatabase.execSQL(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int newVersion, int oldVersion) {
        if (newVersion > oldVersion) {
            sqLiteDatabase.execSQL("drop table if exists " + TABLE_NAME);
        }
    }

    List<Product> getAllProduct(SQLiteDatabase sqLiteDatabase, List<Product> lst) {
        String sql = "Select * from " + TABLE_NAME;
        lst.clear();
        sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Product obj = new Product();
            obj.setProductId(cursor.getInt(0));
            obj.setProductName(cursor.getString(1));
            obj.setPrice(cursor.getFloat(2));
            lst.add(obj);
        }
        return lst;
    }

    long insertProduct(SQLiteDatabase sqLiteDatabase, Product obj) {
        sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("productId", obj.getProductId());
        contentValues.put("productName", obj.getProductName());
        contentValues.put("price", obj.getPrice());
        long n = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
        return n;
    }

    public int deleteProduct(SQLiteDatabase sqLiteDatabase, int productId) {
        sqLiteDatabase = getWritableDatabase();
        int n = sqLiteDatabase.delete(TABLE_NAME, "productId=?", new String[]{String.valueOf(productId)});
        sqLiteDatabase.close();
        return n;
    }

    public int updateProduct(SQLiteDatabase sqLiteDatabase, Product obj) {
        sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("productId", obj.getProductId());
        contentValues.put("productName", obj.getProductName());
        contentValues.put("price", obj.getPrice());
        int n = sqLiteDatabase.update(TABLE_NAME, contentValues, "productId=?", new String[]{String.valueOf(obj.getProductId())});
        sqLiteDatabase.close();
        return n;
    }

}
