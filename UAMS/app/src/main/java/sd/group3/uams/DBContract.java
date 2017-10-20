package sd.group3.uams;

import android.provider.BaseColumns;

/**
 * Created by ericjames on 10/7/17.
 */

final class DBContract {
    //Private contructor to prevent accidentally instantiating the contract class
    private DBContract() {};

    static final int dbVersion = 1;
    static final String dbName = "UAMS.db";

    //Constants for easy declaration of queries
    private static final String TEXT_TYPE = " TEXT";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA = ",";

    /* ========================
           WAREHOUSE TABLE
       =======================*/
    static class Warehouses implements BaseColumns {

        //Declare of table and column names
        static final String TABLE_NAME = "Warehouses";
        static final String COLUMN_NAME_1 = "Name";
        static final String COLUMN_NAME_2 = "Location";

        //Create Table Query
        static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_1 + TEXT_TYPE + COMMA +
                COLUMN_NAME_2 + TEXT_TYPE + " )";

        //Delete Table Query
        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /* ========================
             ITEMS TABLE
       =======================
        The Items table will hold all of the items that are stored within the system. The
        Warehouse_ID column will hold the id of the warehouse that houses the item.
        When retrieving the inventory for a warehouse, we will look for all entries that
        match the Warehouse_ID of a particular warehouse.
    */

    static class Items implements BaseColumns {

        //Declare table `and column names
        static final String TABLE_NAME = "Items";
        static final String COLUMN_NAME_1 = "Name";
        static final String COLUMN_NAME_2 = "Quantity";
        static final String COLUMN_NAME_3 = "Description";
        static final String COLUMN_NAME_4 = "Image";
        static final String COLUMN_NAME_5 = "Warehouse_ID";

        //Create Table Query
        static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_1 + TEXT_TYPE + COMMA +
                COLUMN_NAME_2 + TEXT_TYPE + COMMA +
                COLUMN_NAME_3 + TEXT_TYPE + COMMA +
                COLUMN_NAME_4 + BLOB_TYPE + COMMA +
                COLUMN_NAME_5 + TEXT_TYPE + " )";

        //Delete Table Query
        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
