package com.atlaaya.evdrecharge.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.atlaaya.evdrecharge.model.ModelOperator;
import com.atlaaya.evdrecharge.model.ModelService;
import com.atlaaya.evdrecharge.model.ModelVoucherPlan;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "evdHighlightMain.db";

    private static final String TABLE_SERVICE = "services";
    private static final String TABLE_OPERATOR = "operators";
    private static final String TABLE_VOUCHER_AMOUNTS = "voucher_amounts";


    private static final String KEY_ID = "id";
    private static final String KEY_SERVICE_NAME = "service_name";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SERVICE_ID = "service_id";
    private static final String KEY_OPERATOR_CODE = "operator_code";
    private static final String KEY_OPERATOR_ID = "operator_id";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MODIFIED = "modified";
    private static final String KEY_CREATED = "created";

    // Creating table query
    private static final String CREATE_TABLE_SERVICE = "create table " + TABLE_SERVICE + "(" +
            KEY_ID + " integer PRIMARY KEY," +
            KEY_SERVICE_NAME + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_OPERATOR = "create table " + TABLE_OPERATOR + "(" +
            KEY_ID + " integer PRIMARY KEY," +
            KEY_TITLE + " TEXT," +
            KEY_SERVICE_ID + " integer," +
            KEY_OPERATOR_CODE + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_VOUCHER_AMOUNTS = "create table " + TABLE_VOUCHER_AMOUNTS + "(" +
            KEY_ID + " integer PRIMARY KEY," +
            KEY_OPERATOR_ID + " integer," +
            KEY_AMOUNT + " REAL," +
            KEY_STATUS + " INTEGER," +
            KEY_MODIFIED + " TEXT," +
            KEY_CREATED + " TEXT);";

    // Creating table query
/*    private static final String CREATE_TABLE_WALL_BY_FAV = "create table " + TABLE_WALL_BY_FAV + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_PID + " TEXT UNIQUE," +
            TAG_CAT_ID + " TEXT," +
            TAG_COLORS + " TEXT);";*/


    //    SQLiteDatabase db = this.getWritableDatabase();
    private static DBHelper dbHelper_Main = null;

    private String[] columns_service = new String[]{KEY_ID, KEY_SERVICE_NAME};
    private String[] columns_operator = new String[]{KEY_ID, KEY_TITLE, KEY_SERVICE_ID, KEY_OPERATOR_CODE};
    private String[] columns_voucher_amounts = new String[]{KEY_ID, KEY_OPERATOR_ID, KEY_AMOUNT, KEY_STATUS, KEY_MODIFIED, KEY_CREATED};

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    //  Create Instance and save of Database to avoid again and again instance creation
    public static DBHelper getInstance(Context context) {
        if (dbHelper_Main == null) {
            dbHelper_Main = new DBHelper(context.getApplicationContext());
        }

        return dbHelper_Main;
    }

    public void onCreate(SQLiteDatabase db) {
        try {
//            db.execSQL("CREATE TABLE stickerbook ( _id INTEGER primary key AUTOINCREMENT, identifier String, json Text);");

            db.execSQL(CREATE_TABLE_SERVICE);
            db.execSQL(CREATE_TABLE_OPERATOR);
            db.execSQL(CREATE_TABLE_VOUCHER_AMOUNTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
//        db.execSQL("DROP TABLE IF EXISTS stickers");
//        db.execSQL("CREATE TABLE stickers ( _id INTEGER primary key AUTOINCREMENT,sid String, sidentifier String);");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICE);
        db.execSQL(CREATE_TABLE_SERVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPERATOR);
        db.execSQL(CREATE_TABLE_OPERATOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOUCHER_AMOUNTS);
        db.execSQL(CREATE_TABLE_VOUCHER_AMOUNTS);
    }


    public void insertOrUpdateServices(List<ModelService> arrayList) {
        removeAllServices();
        SQLiteDatabase db = this.getWritableDatabase();
        if (arrayList != null) {
            for (ModelService data : arrayList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_ID, data.getId());
                contentValues.put(KEY_SERVICE_NAME, data.getService_name());

                db.insert(TABLE_SERVICE, null, contentValues);
            }
        }
    }

    public ArrayList<ModelService> getAllServices() {
        ArrayList<ModelService> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SERVICE, columns_service, null, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    ModelService service = new ModelService();
                    service.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    service.setService_name(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_NAME)));
                    arrayList.add(service);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } finally {
            db.close();
        }
        return arrayList;
    }

    public void removeAllServices() {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(TABLE_SERVICE, null, null);
        }
    }


    public void insertOrUpdateOperatorsOfService(List<ModelOperator> arrayList, int serviceId) {
        removeAllOperatorsOfService(serviceId);
        SQLiteDatabase db = this.getWritableDatabase();
        if (arrayList != null) {
            for (ModelOperator data : arrayList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_ID, data.getId());
                contentValues.put(KEY_TITLE, data.getTitle());
                contentValues.put(KEY_SERVICE_ID, data.getService_id());
                contentValues.put(KEY_OPERATOR_CODE, data.getOperator_code());

                db.insert(TABLE_OPERATOR, null, contentValues);
            }
        }
    }

    public ArrayList<ModelOperator> getAllOperatorsOfService(int serviceId) {
        ArrayList<ModelOperator> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_OPERATOR, columns_operator,
                KEY_SERVICE_ID + "=?", new String[]{String.valueOf(serviceId)},
                null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    ModelOperator data = new ModelOperator();
                    data.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    data.setService_id(cursor.getInt(cursor.getColumnIndex(KEY_SERVICE_ID)));
                    data.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                    data.setOperator_code(cursor.getString(cursor.getColumnIndex(KEY_OPERATOR_CODE)));
                    arrayList.add(data);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } finally {
            db.close();
        }
        return arrayList;
    }

    public void removeAllOperatorsOfService(int serviceId) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(TABLE_OPERATOR, KEY_SERVICE_ID + "=?", new String[]{String.valueOf(serviceId)});
        }
    }


    public void insertOrUpdateVoucherAmountsOperator(List<ModelVoucherPlan> arrayList, int operatorId) {
        removeAllVoucherAmountsOperator(operatorId);
        SQLiteDatabase db = this.getWritableDatabase();
        if (arrayList != null) {
            for (ModelVoucherPlan data : arrayList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_ID, data.getId());
                contentValues.put(KEY_OPERATOR_ID, data.getOperator_id());
                contentValues.put(KEY_AMOUNT, data.getAmount());
                contentValues.put(KEY_STATUS, data.isStatus());
                contentValues.put(KEY_CREATED, data.getCreated());
                contentValues.put(KEY_MODIFIED, data.getModified());

                db.insert(TABLE_VOUCHER_AMOUNTS, null, contentValues);
            }
        }
    }

    public ArrayList<ModelVoucherPlan> getAllVoucherAmountsOperator(int operatorId) {
        ArrayList<ModelVoucherPlan> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VOUCHER_AMOUNTS, columns_voucher_amounts,
                KEY_OPERATOR_ID + "=?", new String[]{String.valueOf(operatorId)},
                null, null, KEY_AMOUNT+" ASC");
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    ModelVoucherPlan data = new ModelVoucherPlan();
                    data.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    data.setOperator_id(cursor.getInt(cursor.getColumnIndex(KEY_OPERATOR_ID)));
                    data.setAmount(cursor.getDouble(cursor.getColumnIndex(KEY_AMOUNT)));
                    data.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_STATUS)) != 0);
                    data.setCreated(cursor.getString(cursor.getColumnIndex(KEY_CREATED)));
                    data.setModified(cursor.getString(cursor.getColumnIndex(KEY_MODIFIED)));
                    arrayList.add(data);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } finally {
            db.close();
        }
        return arrayList;
    }

    public void removeAllVoucherAmountsOperator(int operatorId) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(TABLE_VOUCHER_AMOUNTS, KEY_OPERATOR_ID + "=?", new String[]{String.valueOf(operatorId)});
        }
    }

}