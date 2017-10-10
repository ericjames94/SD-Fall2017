package sd.group3.uams;

import android.provider.BaseColumns;

/**
 * Created by ericjames on 10/7/17.
 */

public final class DBContract {
    //Private contructor to prevent accidentally instantiating the contract class
    private DBContract() {};

    public static final int dbVersion = 1;
    public static final String dbName = "UAMS.db";

    //Constants for easy declaration of queries
    public static final String TEXT_TYPE = " TEXT";
    public static final String BLOB_TYPE = " BLOB";
    public static final String COMMA = ",";

    /* ========================
           WAREHOUSE TABLE
       =======================*/
    public static class Warehouses implements BaseColumns {

        //Declare of table and column names
        public static final String TABLE_NAME = "Warehouses";
        public static final String COLUMN_NAME_1 = "Name";
        public static final String COLUMN_NAME_2 = "Location";

        //Create Table Query
        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + "INTEGER PRIMARY KEY," +
                COLUMN_NAME_1 + TEXT_TYPE + COMMA +
                COLUMN_NAME_2 + TEXT_TYPE + " )";

        //Delete Table Query
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /* ========================
             ITEMS TABLE
       =======================*/
    public static class Items implements BaseColumns {

        //Declare table `and column names
        public static final String TABLE_NAME = "Items";
        public static final String COLUMN_NAME_1 = "Name";
        public static final String COLUMN_NAME_2 = "Quantity";
        public static final String COLUMN_NAME_3 = "Description";
        public static final String COLUMN_NAME_4 = "Image";

        //Create Table Query
        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + "INTEGER PRIMARY KEY," +
                COLUMN_NAME_1 + TEXT_TYPE + COMMA +
                COLUMN_NAME_2 + TEXT_TYPE + COMMA +
                COLUMN_NAME_3 + TEXT_TYPE + COMMA +
                COLUMN_NAME_4 + BLOB_TYPE + " )";

        //Delete Table Query
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
