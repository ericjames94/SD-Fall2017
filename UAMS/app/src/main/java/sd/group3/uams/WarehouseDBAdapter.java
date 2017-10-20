package sd.group3.uams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ericjames on 10/18/17.
 */

class WarehouseDBAdapter extends DBAdapter{
    private DBAdapter dbHelper;
    private SQLiteDatabase db;
    private final Context c;

    WarehouseDBAdapter(Context c) {
        super(c);
        this.c = c;
    }

    //Open database for updates
    public WarehouseDBAdapter openToWrite() throws SQLException {
        this.dbHelper = new DBAdapter(this.c);
        this.dbHelper.openToWrite();
        this.db = this.dbHelper.db;
        System.out.println("Database Opened");
        return this;
    }
    // Open database for reading
    public WarehouseDBAdapter openToRead() throws SQLException {
        this.dbHelper = new DBAdapter(this.c);
        this.dbHelper.openToRead();
        this.db = this.dbHelper.db;
        System.out.println("Database Opened");
        return this;
    }

    public void close () {
        this.dbHelper.close();
    }


    void createWarehouseEntry (String name, String location){
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Location", location);
        db.insert("Warehouses", null, values);
    }

    boolean deleteWarehouseEntry (long id) {
        return db.delete(DBContract.Warehouses.TABLE_NAME, "_ID =" + id, null) > 0;
    }

/* ====================================
       Syntax for query() method
   ====================================
Cursor query (String table, String[] columns, String selection, String[] selectionArgs,
                String groupBy, String having, String orderBy, String limit)
*/
    Cursor getAllWarehouses() {
        return db.query(DBContract.Warehouses.TABLE_NAME, null, null, null, null, null, null, null);
    }

    Cursor getWarehouseById (int id) {
        return db.query(DBContract.Warehouses.TABLE_NAME, null, "_id = " + id, null, null, null,
                null, null);
    }
}
