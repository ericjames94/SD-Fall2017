package sd.group3.uams;

//Libraries needed to utilize Fragments
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

//Misc Libraries for desired functionality
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
/*
 * Created by ericjames on 9/16/17.
 */

public class Warehouses extends Fragment {
    private ListView mListView;
    private ArrayList<String> warehouseNames = new ArrayList<String>();
    protected ArrayList<Integer> warehouseIds = new ArrayList<Integer>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warehouses, container, false);
        mListView = view.findViewById(R.id.warehouse_list);

//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
//                //Set warehouse ID to make query for warehouse information
//                ((MainActivity)getActivity()).warehouseId = warehouseIds.get(position);
//
//                final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.replace(R.id.content_frame, new WarehouseInfo());
//                ft.addToBackStack(null);
//                ft.commit();
//            }
//        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View arg1, final int position, long arg3) {
                //Ask the user if they want to set the active warehouse to the selected warehouse
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage("Set \"" + warehouseNames.get(position) + "\" as active warehouse?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((MainActivity)getActivity()).warehouseName = warehouseNames.get(position);
                                ((MainActivity)getActivity()).warehouseId = warehouseIds.get(position);
                                ((MainActivity)getActivity()).updateViews();
                            }
                        });
                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        displayWarehouses();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Warehouses");
    }

    private void displayWarehouses() {
        warehouseNames.clear();
        getWarehouseNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, warehouseNames);
        mListView.setAdapter(adapter);
    }

    private void getWarehouseNames() {
        try {
            WarehouseDBAdapter db = new WarehouseDBAdapter(this.getContext());
            db.openToRead();
            Cursor c = db.getAllWarehouses();
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        warehouseNames.add(c.getString(c.getColumnIndex("Name")));
                        warehouseIds.add(c.getInt(c.getColumnIndex("_id")));
                    } while (c.moveToNext());
                }
            }
            db.close();
        } catch(SQLiteException e) {
            System.out.println("Error opening database");
        }
    }
}
