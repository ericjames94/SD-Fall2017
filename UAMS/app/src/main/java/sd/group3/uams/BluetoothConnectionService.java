package sd.group3.uams;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ericjames on 11/3/17.
 */

public class BluetoothConnectionService extends Fragment {
    private TextView message;
    BluetoothSocket mSocket;
    BluetoothDevice mDevice;
    BluetoothAdapter mBluetoothAdapter;
    InputStream mInputStream;
    OutputStream mOutputStream;
    private ArrayList<String> readTags = new ArrayList<>();
    private int storedStringIndex = 0;
    private int readBufferPosition = 0;
    private boolean stopWorker = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive_data, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button startScanning = getActivity().findViewById(R.id.button_startScanning);
        Button printTags = getActivity().findViewById(R.id.button_printTags);
        Button clearArray = getActivity().findViewById(R.id.button_clearArray);
        Button connect = getActivity().findViewById(R.id.button_connect);
        message = getActivity().findViewById(R.id.connection_message);

        //Set Listener for connect button
        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    openConnection();
                } catch (IOException e) {
                    //Inform the user connection failed
                    message.setText("Error establishing connection: " + e);
                }
            }
        });

        //Set Listener for scan button
        startScanning.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    startScanning();
                } catch (IOException e) {
                    message.setText("Error enabling scanner: " + e);
                }
            }
        });
        //Set Listener for print button
        printTags.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                try {
                    printTags();
                } catch (IOException e) {
                        //Alert the user of error
                        message.setText("Error printing tags: " + e);
                    }
            }
        });
        //Set Listener for clear button
        clearArray.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    clearArray();
                } catch (IOException e) {
                    //Alert the user of error
                    message.setText("Error clearing array: " + e);
                }
            }
        });
    }

    private void openConnection() throws IOException{
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            message.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("HC-05")) {
                    mDevice = device;
                    break;
                }
            }
        }

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
        mSocket.connect();
        mOutputStream = mSocket.getOutputStream();
        mInputStream = mSocket.getInputStream();

        message.setText("Connection Established");
    }

    private void startScanning() throws IOException {
            String command1 = "1";
            String userTime = ((MainActivity)getActivity()).timer;
            // Get second half of command from some field set by the user?
            String command2 = userTime != "" ? userTime : "010";
        if (mOutputStream != null)
            mOutputStream.write((command1 + command2).getBytes());
        else
            throw new IOException("Null Output Stream");

        message.setText("Scanning for tags...");
    }

    private void printTags() throws IOException {
            String command = "2000";
        if (mOutputStream != null)
            mOutputStream.write(command.getBytes());
        else
            throw new IOException("Null Output Stream");

        message.setText("Retrieving Data...");
        receiveData();
    }

    private void clearArray() throws IOException {
            String command = "3000";
        if (mOutputStream != null)
            mOutputStream.write(command.getBytes());
        else
            throw new IOException("Null Output Stream");

        message.setText("Memory Cleared!");
        readTags.clear();
        storedStringIndex = 0;
    }

    private void flushStream() throws IOException {
        System.out.println("FlushStream called...");
        mOutputStream.flush();
        mInputStream.reset();
    }

    private void receiveData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for '[' as the delimeter character
        final byte terminator = 38; //ASCII code for '&' acting as our terminator character
        try {
            flushStream();
        } catch (IOException e){}

        stopWorker = false;
        readBufferPosition = 0;
        final byte[] readBuffer = new byte[1024];
        final Thread workThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];

                            mInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter || b == terminator) {
                                    if (b == terminator)
                                        createItems(readTags);

                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "UTF-8");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            message.setText(data);
                                            readTags.add(storedStringIndex++, data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workThread.start();
    }

    private void createItems(ArrayList<String> epcCodes) {
        // Create a listview, show the read item entries, allow the user to edit the fields, submit
        // items into database

        // Display a loading screen while creating the fragment??
        System.out.println("Generating Items...");
        ((MainActivity)getActivity()).serialNumbers = new ArrayList<>(epcCodes);

        readTags.clear();
        storedStringIndex = 0;

        // Generate fragment for creating and editing the read items.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new EpcCreateItem());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
}