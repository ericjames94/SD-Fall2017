package sd.group3.uams;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by ericjames on 10/10/17.
 */

public class WarehouseInfo extends Fragment {
    private int warehouseId;
    private EditText name, location;
    private Button submit, cancel, delete;
    private boolean isBackFromB = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_warehouse_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (((MainActivity)getActivity()).editable) {
            getActivity().setTitle("Edit Warehouse");
            editSelectedWarehouse();
        }

        getActivity().setTitle("Warehouse Details");
        name = getActivity().findViewById(R.id.info_warehouse_name);
        location = getActivity().findViewById(R.id.info_warehouse_location);
        warehouseId = ((MainActivity)getActivity()).tempWarehouseId;
        getWarehouseInfo();
    }



    private void getWarehouseInfo() {
        try {
            WarehouseDBAdapter db = new WarehouseDBAdapter(this.getContext());
            db.openToRead();
            Cursor c = db.getWarehouseById(warehouseId);

            if (c != null) {
                if (c.moveToFirst()) {
                    name.setText(c.getString(c.getColumnIndex("Name")));
                    location.setText(c.getString(c.getColumnIndex("Location")));
                }
            }

            db.close();
        }
        catch(SQLiteException e) {
            System.out.println("Error querying database");
        }
    }

    private void editSelectedWarehouse() {
        //Add a button, get the field values on submission,
        submit = getActivity().findViewById(R.id.button_submit_warehouse_changes);
        cancel = getActivity().findViewById(R.id.button_cancel_warehouse_changes);
        delete = getActivity().findViewById(R.id.button_delete_warehouse);

        submit.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { submitWarehouseChanges(); }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage("Delete " + name.getText().toString() + "?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteSelectedWarehouse();
                                dialog.cancel();
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
    }

    private void submitWarehouseChanges() {
        try {
            WarehouseDBAdapter db = new WarehouseDBAdapter(this.getContext());
            db.openToWrite();
            db.editWarehouseEntry(warehouseId, name.getText().toString(),
                    location.getText().toString());
            db.close();

            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage("Warehouse Updated!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

            getFragmentManager().popBackStackImmediate();
        } catch(SQLiteException e) {}

    }

    private void deleteSelectedWarehouse(){
        try {
            WarehouseDBAdapter db = new WarehouseDBAdapter(this.getContext());
            db.openToWrite();
            db.deleteWarehouseEntry(warehouseId);
            db.close();

            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage("Warehouse Deleted!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

            getFragmentManager().popBackStackImmediate();
        } catch (SQLiteException e) {}
    }
}
