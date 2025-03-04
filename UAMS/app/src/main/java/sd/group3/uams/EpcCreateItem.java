package sd.group3.uams;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ericjames on 11/10/17.
 */


public class EpcCreateItem extends Fragment {
    private ListView mListView;
    private ArrayList<String> serialNumbers = new ArrayList<>();
    private int id;
    private String serialNum;
    private boolean isBackFromB;
    // Start new item entries using EPC
    // Display started entries in a listview
    // Allow the user onItemClick to editable ItemInfo or CreateItem
        // OnItemClick, query the database for the serial number
            // If it exists, using the serialnum, generate the editable ItemInfo fragment
            // Else, using the serial num, generate the CreateItem fragment
    // On submission of Edit or Creation, remove the clicked position from the list
    // When all entries are submitted, return to the Inventory screen

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        mListView = view.findViewById(R.id.inventory_list);
        serialNum = ((MainActivity)getActivity()).serialNum;
        isBackFromB = false;

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
            // Query the database for the serialNumber
                boolean exists = checkInventory(position);
                if (exists) {
                    //Edit item entry
                    editExistingItem();
                }
                else {
                    //Create new item
                    createNewItem();
                }
            }
        });

        populateListView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Receive Data");
    }

    @Override
    public void onPause() {
        super.onPause();
        isBackFromB = true;
    }

    @Override public void onResume() {
        super.onResume();
        if (isBackFromB) {
            isBackFromB = false;
            System.out.println("OnResume...");
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }

    private void populateListView () {
        System.out.println("populateListView called...");
        serialNumbers.clear();
        serialNumbers = new ArrayList<>(((MainActivity) getActivity()).serialNumbers);

        if (serialNumbers.isEmpty()) {
            ((MainActivity) getActivity()).updateViews();
        }

        Iterator<String> i = serialNumbers.iterator();
            while (i.hasNext()) {
                String temp = i.next();
                if (temp.equals("")) {
                    i.remove();
                }
            }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, serialNumbers);
        mListView.setAdapter(adapter);
    }

    // Check if the serial number exists in the database
    private boolean checkInventory(int position) {
        ArrayList<String> temp = new ArrayList<>();
        ((MainActivity)getActivity()).serialNum = serialNum = serialNumbers.get(position);
        System.out.println("Serial Number and Position: " + serialNum + ", " + position);
        try {
            InventoryDBAdapter db = new InventoryDBAdapter(this.getContext());
            db.openToRead();
            Cursor c = db.findSerialNumber(serialNum);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        if (c.getString(c.getColumnIndex("Serial_Num")).equals(serialNum) &&
                                c.getInt(c.getColumnIndex("Warehouse_ID")) ==
                                        ((MainActivity)getActivity()).activeWarehouseId) {
                            id = c.getInt(c.getColumnIndex("_id"));
                            temp.add(c.getString(c.getColumnIndex("Serial_Num")));
                        }
                    } while (c.moveToNext());
                }
            }
            db.close();
        } catch (SQLiteException e) {}

        if (temp.size() > 0) {
            Iterator<String> i = temp.iterator();
            int j = 0;
            while (i.hasNext()) {
                String next = i.next();
                if (next.equals(serialNum)){
                    ((MainActivity)getActivity()).itemId = id;
                    break;
                }
                j++;
            }
        }

        return (temp.size() > 0);
    }

    //Create a new entry in the inventory table
    private void createNewItem() {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.content_frame, new CreateItem());
        ft.addToBackStack(null);
        ft.commit();
    }

    //Edit an existing entry in the inventory table
    private void editExistingItem() {
        ((MainActivity)getActivity()).editable = true;
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.content_frame, new ItemInfo());
        ft.addToBackStack(null);
        ft.commit();
    }
}
