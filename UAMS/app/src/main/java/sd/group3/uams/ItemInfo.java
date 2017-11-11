package sd.group3.uams;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.net.URI;

/**
 * Created by ericjames on 9/17/17.
 */

public class ItemInfo extends Fragment {
    private EditText name, quantity, location, description;
    private ImageView image;
    private int id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_info, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Item Details");
        name = getActivity().findViewById(R.id.item_name);
        quantity = getActivity().findViewById(R.id.item_quantity);
        location = getActivity().findViewById(R.id.item_location);
        description = getActivity().findViewById(R.id.item_description);
        image = getActivity().findViewById(R.id.item_image);
        id = ((MainActivity)getActivity()).serialNum;
        getInventory();



    }

    private void getInventory() {
        try {
            InventoryDBAdapter db = new InventoryDBAdapter(this.getContext());
            db.openToRead();
            Cursor c = db.getItem(id);

            if (c != null) {
                if (c.moveToFirst()) {
                    name.setText(c.getString(c.getColumnIndex("Name")));
                    quantity.setText(c.getString(c.getColumnIndex("Quantity")));
                    location.setText(c.getString(c.getColumnIndex("Location")));
                    description.setText(c.getString(c.getColumnIndex("Description")));
                    try {
                        image.setImageURI(Uri.parse(c.getString(c.getColumnIndex("Image"))));
                    } catch (NullPointerException e) {
                        System.out.println(e);
                    }
                }
            }
            db.close();
        }
        catch(SQLiteException e) {
            System.out.println("Error querying database");
        }
    }
}
