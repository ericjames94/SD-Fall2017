package sd.group3.uams;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by ericjames on 10/20/17.
 */

public class CreateItem extends Fragment {
    EditText name, quantity, location, description;
    ImageView image;
    Button submitItem, cancelSubmit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        getActivity().setTitle("Create New Item");

//        name = getActivity().findViewById();
//        quantity = getActivity().findViewById();
//        location = getActivity().findViewById();
//        description = getActivity().findViewById();
//        image = getActivity().findViewById();

//        submitItem = getActivity().findViewById(R.id.item_submit_button);
//        cancelSubmit = getActivity().findViewById(R.id.item_cancel_button);
    }

    public void buttonListener(){
        submitItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(v);
            }
        });

        cancelSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(v);
            }
        });
    }

    public void submit(View v) {
        InventoryDBAdapter db = new InventoryDBAdapter(this.getContext());
        db.openToWrite();

    }

    public void cancel(View v) {

    }
}
