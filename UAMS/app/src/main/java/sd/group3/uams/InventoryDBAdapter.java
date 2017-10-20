package sd.group3.uams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ericjames on 10/18/17.
 */

class InventoryDBAdapter extends DBAdapter{
    private DBAdapter dbHelper;
    private SQLiteDatabase db;
    private final Context c;

    InventoryDBAdapter(Context c) {
        super(c);
        this.c = c;
    }

    public InventoryDBAdapter openToWrite() throws SQLException {
        this.dbHelper = new DBAdapter(this.c);
        this.dbHelper.openToWrite();
        this.db = this.dbHelper.db;
        return this;
    }

    public InventoryDBAdapter openToRead() throws SQLException {
        this.dbHelper = new DBAdapter(this.c);
        this.dbHelper.openToRead();
        this.db = this.dbHelper.db;
        return this;
    }

    public void close() { this.dbHelper.close(); }

    Cursor getAssociatedItems(int id) {
        return db.query(DBContract.Items.TABLE_NAME, null, "Warehouse ID = " + id, null,
                null, null, null);
    }

    void insertItem (int serialNum, String itemName, int quantity, String description,
                            byte[] image, int warehouseId) {
        ContentValues values = new ContentValues();
        values.put("_id", serialNum);
        values.put("Name", itemName);
        values.put("Quantity", quantity);
        values.put("Description", description);
        values.put("Image", image);
        values.put("Warehouse_ID", warehouseId);
        db.insert("Items", null, values);
        db.close(); }
}
