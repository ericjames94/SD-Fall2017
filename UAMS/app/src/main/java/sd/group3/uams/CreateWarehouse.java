package sd.group3.uams;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ericjames on 10/17/17.
 */

public class CreateWarehouse extends Fragment {
    EditText name, location;
    Button submitWarehouse, cancelSubmit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_warehouse, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View contentView = view;

        final TextView appName = getActivity().findViewById(R.id.application_name),
                fragmentHeader = getActivity().findViewById(R.id.create_warehouse_header),
                fragmentMessage = getActivity().findViewById(R.id.create_warehouse_message);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    appName.setVisibility(View.INVISIBLE);
                    fragmentHeader.setVisibility(View.INVISIBLE);
                    fragmentMessage.setVisibility(View.INVISIBLE);
                }
                else {
                    // keyboard is closed
                    appName.setVisibility(View.VISIBLE);
                    fragmentHeader.setVisibility(View.VISIBLE);
                    fragmentMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        getActivity().setTitle("Add New Warehouse");

        name =  getActivity().findViewById(R.id.create_warehouse_name);
        location = getActivity().findViewById(R.id.create_warehouse_location);

        submitWarehouse = getActivity().findViewById(R.id.warehouse_submit_button);
        cancelSubmit = getActivity().findViewById(R.id.warehouse_cancel_button);
        buttonListener();
    }

    public void buttonListener(){
        submitWarehouse.setOnClickListener(new View.OnClickListener() {
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
        //Add entity to warehouse table and create inventory
        WarehouseDBAdapter db = new WarehouseDBAdapter(this.getContext());
        db.openToWrite();
        db.createWarehouseEntry(name.getText().toString(), location.getText().toString());
        db.close();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Warehouse Created!");
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
    }

    public void cancel(View v) {
        getFragmentManager().popBackStackImmediate();
    }
}
