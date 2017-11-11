package sd.group3.uams;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by ericjames on 11/6/17.
 */

public class SearchActivity extends Fragment {
    private ListView mListView;
    private ArrayList<String> itemNames = new ArrayList<String>();
    private ArrayList<String> itemDescriptions = new ArrayList<String>();
    private ArrayList<String> itemImages = new ArrayList<String>();
    private ArrayList<Integer> serialNums = new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        mListView = view.findViewById(R.id.inventory_list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
                // Set serialNum to make query for item information
                ((MainActivity)getActivity()).serialNum = serialNums.get(position);

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.content_frame, new ItemInfo());
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return view;
    }
}
