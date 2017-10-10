package sd.group3.uams;

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
        db.execSQL(DBContract.Warehouses.CREATE_TABLE);
        db.execSQL(DBContract.Items.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
    /* Need to change so that we delete the item table that is associated with the warehouse table*/
        db.execSQL(DBContract.Warehouses.DELETE_TABLE);
        db.execSQL(DBContract.Items.DELETE_TABLE);
        onCreate(db);
    }
}
