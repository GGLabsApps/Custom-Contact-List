package com.gglabs.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by GG on 03/12/2017.
 */

public class DbHandler extends SQLiteOpenHelper {

    private static final String TAG = "DbHandler";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contacts.db";
    private static final String TABLE_NAME = "Contacts";
    //Columns
    private static final String COL1_ID = "id";
    private static final String COL2_POS = "pos";
    private static final String COL3_NAME = "name";
    private static final String COL4_PHONE = "phone";
    private static final String COL5_IMAGE = "image";
    private static final String COL6_COLOR = "color";

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( " +
                COL1_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2_POS + " INTEGER, " +
                COL3_NAME + " TEXT, " +
                COL4_PHONE + " TEXT, " +
                COL5_IMAGE + " INTEGER, " +
                COL6_COLOR + " INTEGER" +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void insert(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(COL2_POS, contact.getPosition());
        values.put(COL3_NAME, contact.getName());
        values.put(COL4_PHONE, contact.getPhone());
        values.put(COL5_IMAGE, contact.getImage());
        values.put(COL6_COLOR, contact.getColor());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    void insert(int position, String name, String phone, int image, int color) {
        ContentValues values = new ContentValues();
        values.put(COL2_POS, position);
        values.put(COL3_NAME, name);
        values.put(COL4_PHONE, phone);
        values.put(COL5_IMAGE, image);
        values.put(COL6_COLOR, color);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    void update(Contact contact){
        ContentValues values = new ContentValues();
        values.put(COL2_POS, contact.getPosition());
        values.put(COL3_NAME, contact.getName());
        values.put(COL4_PHONE, contact.getPhone());
        values.put(COL5_IMAGE, contact.getImage());
        values.put(COL6_COLOR, contact.getColor());

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NAME, values, COL4_PHONE + " = \"" + contact.getPhone() + "\"", null);
    }

    void delete(Contact contact){
        SQLiteDatabase db = getWritableDatabase();

        String query = "DELETE FROM " + TABLE_NAME +
                " WHERE " + COL4_PHONE + " = \"" + contact.getPhone() + "\"";
        db.execSQL(query);
        db.close();
    }

    void delete(String phone){
        SQLiteDatabase db = getWritableDatabase();

        String query = "DELETE FROM " + TABLE_NAME +
                " WHERE " + COL4_PHONE + " = \"" + phone + "\"";
        db.execSQL(query);
        db.close();
    }

    String SelectAllFromTable(String tableName) {
        StringBuilder output = new StringBuilder();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + tableName;//TABLE_NAME = "Contacts";
        Cursor cursor = db.rawQuery(query, null);

        String stringBuffer;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            stringBuffer = cursor.getString(cursor.getColumnIndex(COL4_PHONE));
            if (stringBuffer != null) output.append(stringBuffer);

            cursor.moveToNext();
            if (!cursor.isAfterLast()) output.append("\n");
        }

        cursor.close();
        db.close();
        return output.toString();
    }

    ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> output = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getColumnCount() < 1) return null;

        String name, phone;
        int position, image, color;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            position = cursor.getInt(cursor.getColumnIndex(COL2_POS));
            name = cursor.getString(cursor.getColumnIndex(COL3_NAME));
            phone = cursor.getString(cursor.getColumnIndex(COL4_PHONE));
            image = cursor.getInt(cursor.getColumnIndex(COL5_IMAGE));
            color = cursor.getInt(cursor.getColumnIndex(COL6_COLOR));

            Contact contact = new Contact(name, phone, image, color);
            if (phone != null) output.add(contact);

            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        //Log.d(TAG, "getAllContacts().size() = " + output.size());
        return output;
    }


}
