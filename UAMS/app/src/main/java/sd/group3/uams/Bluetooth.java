package sd.group3.uams;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by borislam on 10/7/17.
 * Bluetooth Class - To connect UAMS and retrieve data
 */

public class Bluetooth extends Fragment {
    Button enableConnection, disableConnection, deviceList;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView lv;

    protected void onCreateView(Bundle savedInstanceState){
        super.onCreate((savedInstanceState));

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView) lv.findViewById(R.id.PairedDeviceList1);

        enableConnection = (Button) enableConnection.findViewById(R.id.EnableButton);
        disableConnection = (Button) disableConnection.findViewById(R.id.DisableButton);
        deviceList = (Button) deviceList.findViewById(R.id.DeviceListButton);
    }

    //Enable Bluetooth Connection
    public void enable(View v){
        if(!BA.isEnabled()){
            Intent bluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothOn, 0);
            Intent discoverable = new Intent(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            startActivityForResult(discoverable, 0);
            Toast.makeText(getActivity(), "Enabled Bluetooth Connection", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(), "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
        }
    }

    //Disable Bluetooth Connection
    public void disable(View v){
        BA.disable();
        Toast.makeText(getActivity(), "Disabled Bluetooth Connection", Toast.LENGTH_SHORT).show();
    }

    //Paired List View
    public void list(View v){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bd : pairedDevices){
            list.add(bd.getName());
        }

        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
    }

}