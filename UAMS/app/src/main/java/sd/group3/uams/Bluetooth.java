package sd.group3.uams;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by borislam on 10/7/17.
 * Bluetooth Class - To connect UAMS and retrieve data
 */

public class Bluetooth extends Fragment {
    Button enableConnection, disableConnection, getDevice;
    private BluetoothAdapter BA = null;
    private Set<BluetoothDevice> pairableDevices;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        BA = BluetoothAdapter.getDefaultAdapter();

        if(BA == null){
            Toast.makeText(getActivity(), "Bluetooth Not Available", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Bluetooth");

        enableConnection = getActivity().findViewById(R.id.enableButton);
        disableConnection = getActivity().findViewById(R.id.disableButton);
        getDevice = getActivity().findViewById(R.id.getDeviceButton);

        buttonListener();
    }

    public void buttonListener(){
        enableConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enable(v);
            }
        });

        disableConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disable(v);
            }
        });

        getDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDevice(v);
            }
        });
    }

    //Enable Bluetooth Connection
    public void enable(View v){
        if(!BA.isEnabled()){
            Intent bluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothOn, 0);
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

    //Get Device(s)
    public void getDevice(View v){
        Intent bluetoothSettings = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(bluetoothSettings);

        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }
}