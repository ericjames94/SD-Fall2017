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
 */

public class Bluetooth extends Fragment {
    Button enableBluetooth, disableBluetooth, deviceList;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;

    ListView lv;

    @Override
    protected void onCreateView(Bundle savedInstanceState){


    }

}
