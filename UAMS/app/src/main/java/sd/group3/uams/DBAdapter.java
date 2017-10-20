package sd.group3.uams;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static sd.group3.uams.DBContract.dbName;
import static sd.group3.uams.DBContract.dbVersion;

/**
 * Created by ericjames on 10/7/17.
 */

public class DBAdapter {
    private final Context context;
    private DBHelper dbHelper;
    protected SQLiteDatabase db;

    public DBAdapter(Context c) {
        this.context = c;
        this.dbHelper = new DBHelper(this.context);
    }

    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, dbName, null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DBContract.Warehouses.CREATE_TABLE);
            db.execSQL(DBContract.Items.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public DBAdapter openToWrite() throws SQLException
    {
        this.db = this.dbHelper.getWritableDatabase();
        return this;
    }

    public DBAdapter openToRead() throws SQLException
    {
        this.db = this.dbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        this.dbHelper.close();
    }

}
