package sd.group3.uams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
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

    //****** GET QUERIES ******//

    Cursor getItem(int id) {
        return db.query(DBContract.Items.TABLE_NAME, null, "_id = " + id, null, null, null, null);
    }

    Cursor getAllItems() {
        return db.query(DBContract.Items.TABLE_NAME, null, null, null, null, null, null);
    }

    Cursor getAssociatedItems(int id) {
        return db.query(DBContract.Items.TABLE_NAME, null, "Warehouse_ID = " + id, null,
                null, null, null);
    }

    void createItemEntryWithImage(String itemName, int quantity, String description, String location,
                            String imagePath, int warehouseId, String serialNum) {
        ContentValues values = new ContentValues();
        values.put("Name", itemName);
        values.put("Quantity", quantity);
        values.put("Description", description);
        values.put("Location", location);
        values.put("Image", imagePath);
        values.put("Warehouse_ID", warehouseId);
        values.put("Serial_Num", serialNum);
        db.insert("Items", null, values);
        db.close();
    }

    void createItemEntryNoImage(String itemName, int quantity, String description, String location,
                                  int warehouseId, String serialNum) {
        ContentValues values = new ContentValues();
        values.put("Name", itemName);
        values.put("Quantity", quantity);
        values.put("Description", description);
        values.put("Location", location);
        values.put("Warehouse_ID", warehouseId);
        values.put("Serial_Num", serialNum);
        db.insert("Items", null, values);
        db.close(); }

    void editItemEntry(String itemName, int quantity, String description, String location,
                       int itemId) {
        String itemIdStr = Integer.toString(itemId);
        ContentValues values = new ContentValues();
        values.put("Name", itemName);
        values.put("Quantity", quantity);
        values.put("Description", description);
        values.put("Location", location);
        db.update(DBContract.Items.TABLE_NAME, values, "_id = ?", new String[] {itemIdStr});
        db.close();
    }

    Cursor findMatchingString (String searchText) {
        Cursor names = searchNames(searchText);
        Cursor descriptions = searchDescriptions(searchText);
        Cursor serialNums = searchSerialNumbers(searchText);
        Cursor results = new MergeCursor(new Cursor[]{names, descriptions, serialNums});

        return  results;
    }

    Cursor searchNames (String searchText) {
        Cursor names = db.query(DBContract.Items.TABLE_NAME, null, "Name LIKE \"%" + searchText + "%\"", null,
                null, null, null);
        return names;
    }

    Cursor searchDescriptions (String searchText) {
        Cursor descriptions = db.query(DBContract.Items.TABLE_NAME, null, "Description LIKE \"%"
                + searchText + "%\"", null, null, null, null);
        return descriptions;
    }

    Cursor searchSerialNumbers (String searchText) {
        Cursor serialNums = db.query(DBContract.Items.TABLE_NAME, null, "Serial_Num LIKE \"%"
                + searchText + "%\"", null, null, null, null);
        return serialNums;
    }
    Cursor findSerialNumber (String serialNum) {
        Cursor serialNums = db.query(DBContract.Items.TABLE_NAME, null, "Serial_Num LIKE \"%"
            + serialNum + "%\"", null, null, null, null);
        return serialNums;
    }
}
