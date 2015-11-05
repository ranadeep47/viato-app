package in.viato.app.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.http.models.Address;
import in.viato.app.ui.activities.AddressListActivity;
import in.viato.app.ui.widgets.BetterViewAnimator;
import rx.Subscriber;

public class AddressListFragment extends AbstractFragment {

    public static final String TAG = AddressListFragment.class.getSimpleName();

    public static final String ARG_SELECTED_ADDR = "selected_address";

    private List<Address> mAddresses;

    private RecyclerView mAddressList;
    private AddressListAdapter mAdapter;

    private int mSelectedAddress;

    @Bind(R.id.coordinatorLayout_addressList) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.addresses_animator) BetterViewAnimator mAnimator;
    @Bind(R.id.address_container) LinearLayout addressContainer;

    public static AddressListFragment newInstance(int mSelectedAddress) {
        AddressListFragment fragment = new AddressListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putInt(ARG_SELECTED_ADDR, mSelectedAddress);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSelectedAddress = getArguments().getInt(ARG_SELECTED_ADDR, -1);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_list, container, false);
        mAddressList = (RecyclerView) view.findViewById(R.id.address_list);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViatoAPI.getAddress()
                .subscribe(new Subscriber<List<Address>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Address> addresses) {
                        mAddresses = addresses;
                        getActivity().invalidateOptionsMenu();
                        setupAddresses();
                        mAnimator.setDisplayedChildView(addressContainer);
                    }
                });
    }

    @OnClick(R.id.add_address)
    public void addNewAddress() {
        AddressEditFragment fragment = AddressEditFragment.newInstance(
                mSelectedAddress,
                AddressEditFragment.CREATE_ADDRESS,
                null,
                mAddresses.size());
        loadFragment(R.id.frame_content, fragment, AddressEditFragment.TAG, true, AddressEditFragment.TAG);
    }

    public void editAddress(Address address) {
        AddressEditFragment fragment = AddressEditFragment.newInstance(
                mSelectedAddress,
                AddressEditFragment.EDIT_ADDRESS,
                address,
                mAddresses.size());
        loadFragment(R.id.frame_content, fragment, AddressEditFragment.TAG, true, AddressEditFragment.TAG);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_address_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit:
                editAddress(mAddresses.get(mSelectedAddress));
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Selected Address?")
                        .setMessage("Deleted address cannot be recovered. But you can create new address")
                        .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAddress(mSelectedAddress);
                            }
                        })
                        .setNegativeButton(R.string.disagree, null)
                        .create()
                        .show();
                return true;
            case android.R.id.home:
                setResult();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {
        private List<Address> addresses;

        public AddressListAdapter(List<Address> addressList) {
            this.addresses = addressList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.radio_select_address) RadioButton radioButton;
            @Bind(R.id.tv_address_label) TextView textViewLabel;
            @Bind(R.id.tv_address_flat) TextView textViewFlat;
            @Bind(R.id.tv_address_street) TextView textViewStreet;
            @Bind(R.id.tv_address_locality) TextView textViewLocality;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.holder_address_list_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Address address = addresses.get(position);
//            position == mSelectedAddress
            holder.radioButton.setChecked(mSelectedAddress == position);
            holder.radioButton.setTag(position);
            holder.textViewLabel.setText(address.getLabel());
            holder.textViewFlat.setText(address.getFlat());
            holder.textViewStreet.setText(address.getStreet());
            holder.textViewLocality.setText(address.getLocality().getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) holder.radioButton.getTag();
                    if (mSelectedAddress != position) {
                        mSelectedAddress = position;
                        notifyDataSetChanged();
                    }
                }
            });

            holder.radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    if (mSelectedAddress != position) {
                        mSelectedAddress = position;
                        Logger.d(mSelectedAddress + " " + position);
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return addresses.size();
        }
    }

    public void deleteAddress(int index) {
        mViatoAPI.deleteAddress(mAddresses.get(index).getId())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("Error deleting address" + getString(R.string.due_to) + e.getMessage());
                        Toast.makeText(getContext(), "Error deleting address. Please try again", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(String s) {
                        mAddresses.remove(mSelectedAddress);
                        mSelectedAddress = mAddresses.size() - 1;
                        mAdapter.notifyItemRemoved(mSelectedAddress);
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(mCoordinatorLayout, s, Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(mAddresses == null){

        } else if (mAddresses.size() == 0){
            for (int i = 0; i < menu.size(); i++) {
                if(menu.getItem(i).getItemId() != android.R.id.home) {
                    menu.getItem(i).setVisible(false);
                }
            }
        }

        super.onPrepareOptionsMenu(menu);
    }

    public void setupAddresses() {
        mAdapter = new AddressListAdapter(mAddresses);
        mAddressList.setAdapter(mAdapter);
        mAddressList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (mAdapter.getItemCount() == 0) {
                    getActivity().invalidateOptionsMenu();
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (positionStart == 0 && itemCount == 1) {
                    getActivity().invalidateOptionsMenu();
                }
            }
        });
    }

    public void setResult(){
        Intent intent = new Intent();
        intent.putExtra(AddressListActivity.ARG_ADDRESS_INDEX, mSelectedAddress);
        if (mSelectedAddress > -1) {
            intent.putExtra(AddressListActivity.ARG_ADDRESS, mAddresses.get(mSelectedAddress));
        }
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public boolean onBackPressed() {
        setResult();
        return super.onBackPressed();
    }
}
