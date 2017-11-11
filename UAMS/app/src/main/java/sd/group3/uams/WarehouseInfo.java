package sd.group3.uams;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * Created by ericjames on 10/10/17.
 */

public class WarehouseInfo extends Fragment {
    private int warehouseId;
    private EditText name, location;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warehouse_info, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Warehouse Details");
        name = getActivity().findViewById(R.id.info_warehouse_name);
        location = getActivity().findViewById(R.id.info_warehouse_location);
        warehouseId = ((MainActivity)getActivity()).warehouseId;
        getWarehouseInfo();

        //If editable, make the fields focusable. If not, make them read only.
        if (((MainActivity)getActivity()).editable) {
            name.setFocusable(true);
            location.setFocusable(true);
        }
        else {
            name.setFocusable(false);
            location.setFocusable(false);
        }
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
}
