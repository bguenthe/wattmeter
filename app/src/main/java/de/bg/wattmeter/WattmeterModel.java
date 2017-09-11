package de.bg.wattmeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class WattmeterModel {

    private SQLiteDatabase myDB;
    final static String MY_DB_NAME = "wattmeter";
    final static String MY_DB_TABLE = "devices";
    public static final String KEY_ROWID = "id";
    public static final String KEY_DEVICE = "device";
    public static final String KEY_POWERCONSUMPITION = "powerconsumption";
    public static final String KEY_AVERAGEUSAGE = "averageusage";
    public static final String KEY_PICTURE = "picture";
    private static final int DATABASE_VERSION = 3;

    private final Context context;

    private DatabaseHelper DBHelper;

    public WattmeterModel(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, MY_DB_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase myDB) {
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + MY_DB_TABLE
                    + " (id integer primary key autoincrement,"
                    + "device varchar(100)," + "powerconsumption real,"
                    + "averageusage int," + "picture blob" + ");");
            ContentValues initialValues = createContentValues("PC", 330, 2,
                    null);
            myDB.insert(MY_DB_TABLE, null, initialValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE " + MY_DB_TABLE);
            onCreate(db);
        }
    }

    public ArrayList<DeviceAide> getAllDevices() {
        myDB = DBHelper.getWritableDatabase();
        Cursor c = myDB.query(MY_DB_TABLE, new String[]{KEY_ROWID,
                KEY_DEVICE, KEY_POWERCONSUMPITION, KEY_AVERAGEUSAGE,
                KEY_PICTURE}, null, null, null, null, null);
        ArrayList<DeviceAide> al = new ArrayList<DeviceAide>();
        while (c.moveToNext() == true) {
            DeviceAide da = new DeviceAide(c);
            al.add(da);
        }
        return al;
    }

    public DeviceAide getDeviceById(String id) {
        DeviceAide da = null;
        myDB = DBHelper.getWritableDatabase();
        Cursor c = myDB.query(MY_DB_TABLE, new String[]{KEY_ROWID,
                KEY_DEVICE, KEY_POWERCONSUMPITION, KEY_AVERAGEUSAGE,
                KEY_PICTURE}, "ID = " + id, null, null, null, null);
        while (c.moveToFirst() == true) {
            da = new DeviceAide(c);
            break;
        }
        return da;
    }

    private ContentValues createContentValues(String device,
                                              float powerconsumtion, int averageusage, Bitmap picture) {
        ContentValues values = new ContentValues();
        if (picture != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, out);
            values.put(KEY_PICTURE, out.toByteArray());
        } else {
            values.putNull(KEY_PICTURE);
        }
        values.put(KEY_DEVICE, device);
        values.put(KEY_POWERCONSUMPITION, powerconsumtion);
        values.put(KEY_AVERAGEUSAGE, averageusage);

        return values;
    }

    public long insert(DeviceAide da) {
        myDB = DBHelper.getWritableDatabase();
        ContentValues values = createContentValues(da.getDevice(),
                da.getPowerConsumption(), da.getAverageusage(), da.getPicture());
        long deviceId = myDB.insert(MY_DB_TABLE, null, values);
        da.setId(deviceId);

        return deviceId;
    }

    public void update(DeviceAide da) {
        myDB = DBHelper.getWritableDatabase();
        ContentValues values = createContentValues(da.getDevice(),
                da.getPowerConsumption(), da.getAverageusage(), da.getPicture());
        myDB.update(MY_DB_TABLE, values, "id=?", new String[]{da.getId()});
    }

    public void delete(String activeDeviceId) {
        myDB = DBHelper.getWritableDatabase();
        myDB.delete(MY_DB_TABLE, "id=?", new String[]{activeDeviceId});
    }
}