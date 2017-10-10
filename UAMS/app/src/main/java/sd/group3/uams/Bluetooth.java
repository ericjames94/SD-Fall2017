package sd.group3.uams;

//Libraries needed to utilize Fragments
import android.support.v4.app.Fragment;

//Libraries needed for Bluetooth functionality
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

//Misc Libraries for desired functionality
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Receive Data");
    }
}
