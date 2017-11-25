package sd.group3.uams;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by ericjames on 11/19/17.
 */

public class InventorySearchResults extends Fragment {
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        mListView = view.findViewById(R.id.search_inventory_list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
                // Set serialNum to make query for item information
                System.out.println("SearchActivity ItemClickListener");
                ((MainActivity)getActivity()).itemId = ((MainActivity)getActivity()).tempIds.get(position);

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
