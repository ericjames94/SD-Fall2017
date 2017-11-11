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

import java.util.ArrayList;

/**
 * Created by ericjames on 11/10/17.
 */



public class EpcCreateItem extends Fragment {
    private ListView mListView;
    private ArrayList<String> serialNumbers;
    private String serialNum;
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
            // Query the database for the serialNumber
                boolean exists = checkInventory();
                if (exists) {
                    //Edit item entry

                }
                else {
                    //Create new item
                    ((MainActivity)getActivity()).serialNum = serialNumbers.get(position);
                    createNewItem();
                }
            }
        });

        populateListView();

        return view;
    }

    private void populateListView () {
        serialNumbers.clear();
        serialNumbers = ((MainActivity) getActivity()).serialNumbers;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, serialNumbers);
        mListView.setAdapter(adapter);
    }

    // Check if the serial number exists in the database
    private boolean checkInventory() {
        ArrayList<String> temp = new ArrayList<>();
        try {
            InventoryDBAdapter db = new InventoryDBAdapter(this.getContext());
            db.openToRead();
            Cursor c = db.findSerialNumber(serialNum);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        if (c.getString(c.getColumnIndex("Serial_Num")) == serialNum)
                            temp.add(c.getString(c.getColumnIndex("Serial_Num")));
                    } while (c.moveToNext());
                }
            }
            db.close();
        } catch (SQLiteException e) {}
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

}
