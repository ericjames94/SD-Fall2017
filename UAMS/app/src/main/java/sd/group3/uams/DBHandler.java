package sd.group3.uams;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static sd.group3.uams.DBContract.dbName;
import static sd.group3.uams.DBContract.dbVersion;

/**
 * Created by ericjames on 10/7/17.
 */

public class DBHandler extends SQLiteOpenHelper {

    public DBHandler(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Warehouses and Items tables
        db.execSQL(DBContract.Warehouses.CREATE_TABLE);
        db.execSQL(DBContract.Items.CREATE_TABLE);
    }

    public void insertItem (int serialNum, String itemName, int quantity, String description,
                            byte[] image, int warehouseId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_id", serialNum);
        values.put("Name", itemName);
        values.put("Quantity", quantity);
        values.put("Description", description);
        values.put("Image", image);
        values.put("Warehouse_ID", warehouseId);
        database.insert("Items", null, values);
        database.close(); }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        onCreate(db);
    }
}
