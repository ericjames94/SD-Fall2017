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
    public static final String TEXT_TYPE = " TEXT";

    //Store the image
    public static final String IMAGE_NAME = "image_name";
    public static final String IMAGE_DATA = "image_data";
    public static final String COMMA = ",";
    /*=====================================
      Inner class to define Warehouse Table
      =====================================
    */
    public static class Warehouses implements BaseColumns {
        public static final String TABLE_NAME = "Warehouses";
        public static final String COLUMN_NAME_1 = "Name";
        public static final String COLUMN_NAME_2 = "Location";
        public static final String COLUMN_NAME_3 = "Inventory";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + "INTEGER PRIMARY KEY," +
                COLUMN_NAME_1 + TEXT_TYPE + COMMA +
                COLUMN_NAME_2 + TEXT_TYPE + COMMA +
                COLUMN_NAME_3 + TEXT_TYPE + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /*=================================
      Inner class to define Items Table
      =================================
        The Items table will hold all of the items that are stored within the system. The
        Warehouse_ID column will hold the id of the warehouse that houses the item.
        When retrieving the inventory for a warehouse, we will look for all entries that
        match the Warehouse_ID of a particular warehouse.
    */

    public static class Items implements BaseColumns {
        public static final String TABLE_NAME = "Items";
        public static final String COLUMN_NAME_1 = "Name";
        public static final String COLUMN_NAME_2 = "Quantity";
        public static final String COLUMN_NAME_3 = "Description";
        public static final String COLUMN_NAME_4 = "Image";
        public static final String COLUMN_NAME_5 = "Warehouse_ID";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + "INTEGER PRIMARY KEY" +
                COLUMN_NAME_1 + TEXT_TYPE + COMMA +
                COLUMN_NAME_2 + TEXT_TYPE + COMMA +
                COLUMN_NAME_3 + TEXT_TYPE + COMMA +
                COLUMN_NAME_4 + TEXT_TYPE + COMMA +
                COLUMN_NAME_5 + TEXT_TYPE + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
