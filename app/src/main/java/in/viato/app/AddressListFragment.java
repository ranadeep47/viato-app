package in.viato.app;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.model.Address;
import in.viato.app.ui.adapters.AddressAdapter;
import in.viato.app.ui.fragments.EditAddressFragment;

public class AddressListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Bind(R.id.toolbar_addressList)
    Toolbar toolbar;

    @Bind(R.id.listView_addressList)
    ListView listView;

    @Bind(R.id.coordinatorLayout_addressList)
    CoordinatorLayout mCoordinatorLayout;

    private AppCompatActivity mActivity;

    // TODO: Rename and change types and number of parameters
    public static AddressListFragment newInstance(String param1, String param2) {
        AddressListFragment fragment = new AddressListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    public AddressListFragment() {
//        // Required empty public constructor
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        mActivity = ((AppCompatActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_address_list, container, false);

        ButterKnife.bind(this, v);

        toolbar.setTitle("Select Address");
        mActivity.setSupportActionBar(toolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address("1-5-144, Siricilla Road", "Kamareddy - 503111", "House"));
        addresses.add(new Address("108, B Wing, Building No. 32, Sahyadri CHS, MHADA", "Chandivali, Mumbai - 400072", "Home"));
        addresses.add(new Address("602, Toppr, Powai Plaza, Hirandani Gardens", "Powai, Mumbai - 400076", "Work"));

        listView.setAdapter(new AddressAdapter(mActivity, addresses));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mActivity, String.valueOf(position), Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_address_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_edit:
                FragmentManager fm = mActivity.getSupportFragmentManager();
                android.support.v4.app.Fragment fragment = new EditAddressFragment();
                fm.beginTransaction()
                        .replace(R.id.frame_content, fragment)
                        .addToBackStack("EditAddressFragment")
                        .commit();
                return true;

            case R.id.action_delete:
                Snackbar.make(mCoordinatorLayout, "Deleted", Snackbar.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
