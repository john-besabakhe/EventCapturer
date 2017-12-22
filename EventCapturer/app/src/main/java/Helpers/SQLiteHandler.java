package Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by john.sibanyoni on 21/12/2017.
 */

public class SQLiteHandler extends SQLiteOpenHelper {
  private static final String TAG = SQLiteHandler.class.getSimpleName();

  // All Static variables
  // Database Version
  private static final int DATABASE_VERSION = 1;

  // Database Name
  private static final String DATABASE_NAME = "android_api";

  // Login table name
  private static final String TABLE_USER = "user";

  // Login Table Columns names
  private static final String KEY_ID = "id";
  private static final String KEY_NAME = "name";
  private static final String KEY_SURNAME = "surname";
  private static final String KEY_PASSWORD_QUESTION = "passwordQuestion";
  private static final String KEY_PASSWORD_ANSWER = "passwordAnswer";
  private static final String KEY_STAFF_NUMBER = "staffNumber";
  private static final String KEY_EMAIL = "email";
  private static final String KEY_UID = "uid";
  private static final String KEY_CREATED_AT = "created_at";

  public SQLiteHandler(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // Creating Tables
  @Override
  public void onCreate(SQLiteDatabase db) {
    String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
    + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"+ KEY_SURNAME + " TEXT,"
    + KEY_PASSWORD_QUESTION + " TEXT,"+ KEY_PASSWORD_ANSWER + " TEXT,"
    + KEY_STAFF_NUMBER + " TEXT,"
    + KEY_CREATED_AT + " TEXT" + ")";
    db.execSQL(CREATE_LOGIN_TABLE);

    Log.d(TAG, "Database tables created");
  }

  // Upgrading database
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Drop older table if existed
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

    // Create tables again
    onCreate(db);
  }

  /**
   * Storing user details in database
   * */
  public void addUser(String name, String surname, String staffNumber, String passwordQuestion, String passwordAnswer, String email, String uid, String created_at) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(KEY_NAME, name);
    values.put(KEY_SURNAME, surname);// Surname
    values.put(KEY_PASSWORD_QUESTION, passwordQuestion);// Password Question
    values.put(KEY_PASSWORD_ANSWER, passwordAnswer);// Password Answer
    values.put(KEY_STAFF_NUMBER, staffNumber);// Staff Number
    values.put(KEY_EMAIL, email); // Email
    values.put(KEY_UID, uid); // Email
    values.put(KEY_CREATED_AT, created_at); // Created At

    // Inserting Row
    long id = db.insert(TABLE_USER, null, values);
    db.close(); // Closing database connection

    Log.d(TAG, "New user inserted into sqlite: " + id);
  }

  /**
   * Getting user data from database
   * */
  public HashMap<String, String> getUserDetails() {
    HashMap<String, String> user = new HashMap<String, String>();
    String selectQuery = "SELECT  * FROM " + TABLE_USER;

    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);
    // Move to first row
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {
      user.put("name", cursor.getString(1));
      user.put("email", cursor.getString(2));
      user.put("uid", cursor.getString(3));
      user.put("created_at", cursor.getString(4));
    }
    cursor.close();
    db.close();
    // return user
    Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

    return user;
  }

  /**
   * Re crate database Delete all tables and create them again
   * */
  public void deleteUsers() {
    SQLiteDatabase db = this.getWritableDatabase();
    // Delete All Rows
    db.delete(TABLE_USER, null, null);
    db.close();

    Log.d(TAG, "Deleted all user info from sqlite");
  }
}
