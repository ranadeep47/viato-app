package in.viato.app.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.dummy.Addresses;
import in.viato.app.model.Address;
import in.viato.app.ui.activities.AbstractActivity;

public class AddressListFragment extends AbstractFragment {

    public static final String TAG = AddressListFragment.class.getSimpleName();

    public static final String ARG_SELECTED_ADDR = "selected_address";

    private static List<Address> addresses;

    private AbstractActivity mActivity;
    private ListView listView;
    private int mSelectedAddress;

    @Bind(R.id.coordinatorLayout_addressList) CoordinatorLayout mCoordinatorLayout;

    public static AddressListFragment newInstance(int mSelectedAddress) {
        AddressListFragment fragment = new AddressListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_ADDR, mSelectedAddress);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSelectedAddress = getArguments().getInt(ARG_SELECTED_ADDR, 1);
        }

        setHasOptionsMenu(true);

        mActivity = ((AbstractActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        listView = (ListView) view.findViewById(R.id.listView);

        View footerView =  ((LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.p_add_address, null, false);
        listView.addFooterView(footerView);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addresses = (new Addresses()).get();
        listView.setAdapter(new AddressAdapter(addresses));
    }

    @OnClick(R.id.btn_add_address)
    public void addNewAddress() {
        editAddress(0);
    }

    public void editAddress(int addressId) {
        FragmentManager fm = mActivity.getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = EditAddressFragment.newInstance(addressId);
        fm.beginTransaction()
                .replace(R.id.frame_content, fragment)
                .addToBackStack("EditAddressFragment")
                .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_address_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit:
                editAddress(mSelectedAddress);
                return true;

            case R.id.action_delete:
                Snackbar.make(mCoordinatorLayout, "Deleted", Snackbar.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class AddressAdapter extends BaseAdapter {
        List<Address> addresses;

        @Bind(R.id.radio_select_address) RadioButton radioButton;
        @Bind(R.id.tv_label_addressItem) TextView textViewLabel;
        @Bind(R.id.tv_street_addressItem) TextView textViewAddress;
        @Bind(R.id.tv_locality_addressItem) TextView textViewLocality;

        public AddressAdapter(List<Address> addressList) {
            this.addresses = addressList;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_address_list_item, null);
            }

            ButterKnife.bind(this, convertView);

            Address address = getItem(position);
            textViewLabel.setText(address.mLabel.get());
            textViewAddress.setText(address.mAddress.get());
            textViewLocality.setText(address.mLocality.get());

            if(position == mSelectedAddress) {
                radioButton.setChecked(true);
            }else {
                radioButton.setChecked(false);
            }
            radioButton.setTag(position);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedAddress = (Integer) v.getTag();
                    notifyDataSetChanged();
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    radioButton.performClick();
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return addresses.size();
        }

        @Override
        public Address getItem(int position) {
            return addresses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
