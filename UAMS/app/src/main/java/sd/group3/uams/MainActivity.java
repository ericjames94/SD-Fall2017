package sd.group3.uams;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.ArrayList;

import static sd.group3.uams.R.id.action_settings;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
/*  ==========================================
    =           SYSTEM VARIABLES             =
    ==========================================
 */
    private DBAdapter dbHandler;                                // DB Adapter for opening/closing database
    private TextView activeWarehouse;                           // Displays active warehouse name in Nav Drawer
    protected String warehouseName;                             // Warehouse name for showing current warehouse
    protected String searchText;
    protected int warehouseId;                                  // Warehouse Id for database queries
    protected boolean editable = false;                         // If true, allow updating of table entities
    protected String serialNum;                                 // Serial number for database queries
    protected ArrayList<String> serialNumbers;                  // ArrayList for creating items from read EPCs

    // Declare Menu ids for custom Menu items
    private static final int MENU_ADD_ITEM = Menu.FIRST;
    private static final int MENU_ADD_WAREHOUSE = Menu.FIRST + 1;
    private static final int MENU_EDIT_WAREHOUSE = Menu.FIRST + 2;
    private static final int MENU_GET_DATABASE = Menu.FIRST + 3;
    private static final int MENU_DELETE_WAREHOUSE = Menu.FIRST + 4;

    // Declare boolean variable to control dynamic menu
    private boolean itemFocus = false;
    private boolean warehouseFocus = false;
    private boolean reportFocus = false;
    private boolean sendFocus = false;

    // Custom REQUEST_CODE for checking READ_EXTERNAL_STORAGE permission
    private int REQUEST_CODE_PERMISSION = 101;

//      ==========================================
//      =        EVENT OVERRIDE FUNCTIONS        =
//      ==========================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        handleIntent(getIntent());


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        activeWarehouse = (TextView) headerView.findViewById(R.id.active_warehouse);

        // Display inventory menu on create
        navigationView.setCheckedItem(R.id.nav_warehouse);
        displaySelectedScreen(R.id.nav_warehouse);


        // Create Warehouses and Items tables
        dbHandler = new DBAdapter(this);

        checkReadingPermission();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);


        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_item_search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // Using menu.add to create Menu Items
        // menu.add(int: groupId, int: itemId, int: order, CharSequence/int: titleResource)
        if(itemFocus) {
            menu.add(0, MENU_ADD_ITEM, Menu.NONE, R.string.menu_add_item);
        }
        if(warehouseFocus) {
            menu.add(Menu.NONE, MENU_ADD_WAREHOUSE, Menu.NONE, "Add Warehouse");
            menu.add(Menu.NONE, MENU_EDIT_WAREHOUSE, Menu.NONE, "Edit Warehouses");
            menu.add(Menu.NONE, MENU_GET_DATABASE, Menu.NONE, "Print Warehouses");
            menu.add(Menu.NONE, MENU_DELETE_WAREHOUSE, Menu.NONE, "Delete Warehouses");
        }
        if (reportFocus) {

        }
        if (sendFocus) {

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case action_settings:
                break;
            case MENU_ADD_ITEM:
                createNewInventoryItem();
                break;
            case MENU_ADD_WAREHOUSE:
                createNewWarehouse();
                break;
            case MENU_EDIT_WAREHOUSE:
                editExistingWarehouses();
                break;
            case MENU_GET_DATABASE:
                printItemsInDatabase();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (getSupportFragmentManager().popBackStackImmediate()) {
                searchText = null;
                return true;
            }
            else
                moveTaskToBack(true);
        }

        return super.onKeyDown(keyCode, event);
    }

    // Handle navigation view item clicks here.
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
            } else {
                // permission wasn't granted
            }
        }
    }

/*  ==========================================
    =           CREATED FUNCTIONS            =
    ==========================================
 */

    private void displaySelectedScreen(int itemId) {
        // Create fragment object
        Fragment fragment = null;

        switch (itemId) {
            case R.id.nav_inv:
                itemFocus = true;
                warehouseFocus = false;
                reportFocus = false;
                sendFocus = false;
                fragment = new Inventory();
                break;
            case R.id.nav_report:
                itemFocus = false;
                warehouseFocus = false;
                reportFocus = true;
                sendFocus = false;
                //fragment = new Report();
                break;
            case R.id.nav_warehouse:
                itemFocus = false;
                warehouseFocus = true;
                reportFocus = false;
                sendFocus = false;
                fragment = new Warehouses();
                break;
            case R.id.nav_bluetooth:
                itemFocus = false;
                warehouseFocus = false;
                reportFocus = false;
                sendFocus = false;
                fragment = new Bluetooth();
                break;
            case R.id.nav_receive_data:
                itemFocus = false;
                warehouseFocus = false;
                reportFocus = false;
                sendFocus = false;
                fragment = new BluetoothConnectionService();
                break;
            case R.id.nav_manage:
                itemFocus = false;
                warehouseFocus = false;
                reportFocus = false;
                sendFocus = true;
                //fragment = new Settings();
                break;
        }

        // Replace current fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchText = query;
            searchInventoryTable();
        }
    }

    // Update Navigation Drawer and Inventory to correspond to active warehouse
    protected void updateViews() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // Set active warehouse name
        activeWarehouse.setText(warehouseName);
        // Switch to the Inventory List
        navigationView.setCheckedItem(R.id.nav_inv);
        displaySelectedScreen(R.id.nav_inv);
    }

    // Check for READ_EXTERNAL_STORAGE Permission, grant permission if necessary
    private void checkReadingPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // permission wasn't granted
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        }
    }

/*  ==========================================
    =      FRAGMENT TRANSITION FUNCTIONS     =
    ==========================================
 */

    private void createNewInventoryItem() {
        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.content_frame, new CreateItem());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void createNewWarehouse() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.content_frame, new CreateWarehouse());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void searchInventoryTable() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.content_frame, new Inventory());
        ft.addToBackStack(null);
        ft.commit();
    }

    //For testing
    private void editExistingWarehouses() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.content_frame, new Warehouses());
        ft.addToBackStack(null);
        ft.commit();
    }
    //For testing
    private void printWarehousesInDatabase() {
        //Add entity to warehouse table and create inventory
        WarehouseDBAdapter db = new WarehouseDBAdapter(this);
        db.openToRead();
        try {
            System.out.println(DatabaseUtils.dumpCursorToString(db.getAllWarehouses()));
        } catch (SQLiteException se) {
            System.out.println(se);
        }
        db.close();
    }

    private void printItemsInDatabase() {
        InventoryDBAdapter db = new InventoryDBAdapter(this);
        db.openToRead();
        try {
            System.out.println(DatabaseUtils.dumpCursorToString(db.getAllItems()));
        } catch (SQLiteException se) {
            System.out.println(se);
        }
        db.close();
    }
}
// <----------------------------------------------------------------------------------------------->