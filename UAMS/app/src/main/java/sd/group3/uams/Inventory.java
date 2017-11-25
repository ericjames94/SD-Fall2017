package sd.group3.uams;

//Libraries needed to utilize Fragments
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;


//Misc Libraries for desired functionality
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;

/**
 * Created by ericjames on 9/16/17.
 */

public class Inventory extends Fragment {
    private ListView mListView;
    private ArrayList<Integer> ids = new ArrayList<>();
    private ArrayList<String> itemNames = new ArrayList<String>();
    private boolean isBackFromB;
    private boolean didSearch = false;
    private ArrayList<String> itemDescriptions = new ArrayList<String>();
    private ArrayList<String> itemImages = new ArrayList<String>();
    private ArrayList<String> serialNums = new ArrayList<String>();

    public Inventory() {
        setHasOptionsMenu(true);
    }

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
        isBackFromB = false;

        if (((MainActivity)getActivity()).editable) {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
                    //Set warehouse ID to make query for warehouse information
                    ((MainActivity)getActivity()).itemId = ids.get(position);

                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.content_frame, new ItemInfo());
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

        } else {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {

                    System.out.println("Row ID: " + arg3);
                    if (didSearch) {
                        ((MainActivity)getActivity()).itemId = ((MainActivity)getActivity()).tempIds.get((int) arg3);
                    }
                    else {
                        // Set serialNum to make query for item information
                        ((MainActivity) getActivity()).itemId = ids.get((int) arg3 );
                        System.out.println("Item Id: " + ((MainActivity) getActivity()).itemId);
                    }
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.content_frame, new ItemInfo());
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }

        String searchText = ((MainActivity)getActivity()).searchText;
        if (searchText == null) {
            displayItems();
        }
        else {
            displayItems(searchText);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (((MainActivity)getActivity()).editable)
            getActivity().setTitle("Edit Items");
        else
            getActivity().setTitle("Inventory");
    }

    private void displayItems() {
        itemNames.clear();
        getInventoryList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, itemNames);
        mListView.setAdapter(adapter);
    }

    private void displayItems(String searchText) {
        itemNames.clear();
        getInventoryList(searchText);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, itemNames);
        mListView.setAdapter(adapter);
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
            didSearch = false;
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }

    private void getInventoryList() {
        try {
            InventoryDBAdapter db = new InventoryDBAdapter(this.getContext());
            db.openToRead();
            Cursor c = db.getAssociatedItems(((MainActivity)getActivity()).activeWarehouseId);
            System.out.println("Cursor C: " + c);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        ids.add(c.getInt((c.getColumnIndex("_id"))));
                        itemNames.add(c.getString(c.getColumnIndex("Name")));
                        itemDescriptions.add(c.getString(c.getColumnIndex("Description")));
                        itemImages.add(c.getString(c.getColumnIndex("Image")));
                        serialNums.add(c.getString(c.getColumnIndex("Serial_Num")));
                    } while (c.moveToNext());
                }
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println("Error accessing database.");
        }
    }

    // Query the database for items that match the user entered search string
    private void getInventoryList(String searchText) {
        didSearch = true;
        try {
            ((MainActivity)getActivity()).tempIds = new ArrayList<>();
            InventoryDBAdapter db = new InventoryDBAdapter(this.getContext());
            db.openToRead();
            Cursor c = db.findMatchingString(searchText);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        ((MainActivity)getActivity()).tempIds.add(c.getInt((c.getColumnIndex("_id"))));
                        itemNames.add(c.getString(c.getColumnIndex("Name")));
                        itemDescriptions.add(c.getString(c.getColumnIndex("Description")));
                        itemImages.add(c.getString(c.getColumnIndex("Image")));
                        serialNums.add(c.getString(c.getColumnIndex("Serial_Num")));
                    } while (c.moveToNext());
                }
            }
            else {
                //Show view informing the user there were no matches
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println("Error accessing database.");
        }
    }
}
