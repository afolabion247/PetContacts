package com.cornez.petcontacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {//SQLiteOpenHelper automatically manages connections to the underlying database.
    //DEFINE THE DATABASE AND TABLE
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "petManager";
    private static final String TABLE_NAME = "contacts";

    //DEFINE THE COLUMN NAMES FOR THE TABLE
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DETAIL = "detail";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_IMAGEURI = "imageUri";

    //a constructor that calls its super, passing, among others,the Context, the database name and the version.

    public DBHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override//Overrides onCreate methods.
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_DETAIL + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_IMAGEURI + " TEXT)" );
    }

    @Override // Override the onUpgrade and onCreate methods.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.d("db", "onUpdate");
        // DROP OLDER TABLE IF EXISTS
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db); //calls onCreate() to create a new version of the contact table
    }

    //ADD DATA INTO THE CONTACT TABLE
    public void createContact(Pet pet){
        SQLiteDatabase db = getWritableDatabase();//an instance of SQLiteOpenHelper that read-write from the DB
      //INSERT COMMAND STRING
        String insert = "INSERT or replace INTO " + TABLE_NAME +  "("
                + KEY_NAME +", "
                + KEY_DETAIL + ", "
                + KEY_PHONE +", "
                + KEY_IMAGEURI + ") " +
                "VALUES('"
                + pet.getName() + "','"
                + pet.getDetails() + "','"
                + pet.getPhone() + "','"
                + pet.getPhotoURI() +"')" ;
        db.execSQL(insert); //EXCECUTES THE INSERT STATEMENT
        db.close(); //CLOSES THE DATABASE AFTER INSERTING DATA INTO THE TABLE
    }


    //RETRIEVE/QUERY A CONTACT BY ID
    public Pet getContact(int id){
        SQLiteDatabase db = getReadableDatabase();

        //using a Cursor object to return the value of the requested column.
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID, KEY_NAME, KEY_DETAIL, KEY_PHONE, KEY_IMAGEURI}, KEY_ID + "=?", new String[]{String.valueOf(id)},null,null,null,null);

        if(cursor!=null){
            cursor.moveToFirst();//The cursor is positioned on the first row by calling moveToFirst()
        }
        Pet pet = new Pet(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Uri.parse(cursor.getString(4)));
        db.close();
        cursor.close(); // close cursor

        return pet;
    }

    // DELETE THE ROW IN THE TABLE
    public void deleteContact(Pet pet){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + "=?", new String[]{String.valueOf(pet.getId())});
        db.close();
    }

    //COUNT THE CONTACT TABLE ROWS
    public int getContactsCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

 // UPDATE CONTACT DATA IN THE CONTACT TABLE
    public int updateContact(Pet pet){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, pet.getName());
        values.put(KEY_DETAIL, pet.getDetails());
        values.put(KEY_PHONE, pet.getPhone());
        values.put(KEY_IMAGEURI, pet.getPhotoURI().toString());

        int rowsAffected = db.update(TABLE_NAME, values, KEY_ID + "=?", new String[] {String.valueOf(pet.getId())});
        db.close();  //CLOSE THE DATABASE CONNECTION

        return rowsAffected;
    }

    //RETRIVE ALL DATA FROM THE TABLE
    public List<Pet> getAllContacts(){
        List<Pet> allPets = new ArrayList<Pet>();

        SQLiteDatabase db = getWritableDatabase();
        //SELECT ALL QUERY FROM THE TABLE
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);
        // LOOP THROUGH THE CONTACT TABLE
        //The Cursor object is used to return the value of the requested columns.
        if(cursor.moveToFirst()){

            do{
                allPets.add(new Pet(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Uri.parse(cursor.getString(4))));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return allPets;
    }

}
