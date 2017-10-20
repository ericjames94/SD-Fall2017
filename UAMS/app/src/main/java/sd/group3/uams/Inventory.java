package sd.group3.uams;

//Libraries needed to utilize Fragments
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;


//Misc Libraries for desired functionality
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;

/**
 * Created by ericjames on 9/16/17.
 */

public class Inventory extends Fragment {
    private ListView mListView;
    private ArrayList<String> itemNames = new ArrayList<String>();
    private ArrayList<Integer> serialNums = new ArrayList<Integer>();
    private int warehouseId;

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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
                // Set serialNum to make query for item information
                ((MainActivity)getActivity()).serialNum = serialNums.get(position);

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.content_frame, new ItemInfo());
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        warehouseId = ((MainActivity)getActivity()).warehouseId;
        displayItems();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Inventory");
    }

    private void displayItems() {
        itemNames.clear();
        getInventoryList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, itemNames);
        mListView.setAdapter(adapter);
    }

    private void getInventoryList() {
        try {
            InventoryDBAdapter db = new InventoryDBAdapter(this.getContext());
            db.openToRead();
            Cursor c = db.getAssociatedItems(warehouseId);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        itemNames.add(c.getString(c.getColumnIndex("Name")));
                        serialNums.add(c.getInt(c.getColumnIndex("_id")));
                    } while (c.moveToNext());
                }
            }
            db.close();
        } catch (SQLiteException e) {
            System.out.println("Error accessing database.");
        }
    }
}
