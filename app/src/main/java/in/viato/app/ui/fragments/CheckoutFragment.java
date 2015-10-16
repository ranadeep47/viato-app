package in.viato.app.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.databinding.FragmentCheckoutBinding;
import in.viato.app.databinding.PAddressItemBinding;
import in.viato.app.dummy.Addresses;
import in.viato.app.dummy.Books;
import in.viato.app.model.Address;
import in.viato.app.ui.activities.AbstractActivity;
import in.viato.app.ui.activities.AddressListActivity;
import in.viato.app.ui.activities.SuccessActivity;
import in.viato.app.ui.adapters.CheckoutListAdapter;
import in.viato.app.ui.widgets.BetterViewAnimator;
import in.viato.app.ui.widgets.DividerItemDecoration;
import in.viato.app.ui.widgets.MyHorizantalLlm;

/**
 * A placeholder fragment containing a simple view.
 */
public class CheckoutFragment extends AbstractFragment {

    public static final String TAG = CheckoutFragment.class.getSimpleName();

    private static final int REQUEST_ADDRESS = 0;

    private int mSelectedAddress = 1;
    private List<Address> addresses;
    private FragmentCheckoutBinding mBinding;

    @Bind(R.id.checkout_list) RecyclerView checkoutListRV;
    @Bind(R.id.lv_addressList) BetterViewAnimator container;

    public static CheckoutFragment newInstance() {
        CheckoutFragment fragment = new CheckoutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addresses = (new Addresses()).get();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupBooksList();
        setupAddress();
    }

    @OnClick(R.id.card_view_address)
    public void onAddressClicked() {
        Intent intent = new Intent(getActivity(), AddressListActivity.class);
        intent.putExtra(AddressListActivity.ARG_ADDRESS_ID, mSelectedAddress);
        Bundle bundle = new Bundle();
        getActivity().startActivityForResult(intent, REQUEST_ADDRESS);
    }

    @OnClick(R.id.place_order)
    public void placeOrder() {
        getActivity().startActivity(new Intent(getContext(), SuccessActivity.class));
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_ADDRESS) {

        }
    }
    
    public void setupBooksList() {
        checkoutListRV.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new MyHorizantalLlm(getContext(), LinearLayoutManager.VERTICAL, false);
        checkoutListRV.setLayoutManager(linearLayoutManager);

        CheckoutListAdapter checkoutListAdapter = new CheckoutListAdapter((new Books()).get());
        checkoutListRV.addItemDecoration(new DividerItemDecoration(getContext(), null));
        checkoutListRV.setAdapter(checkoutListAdapter);
    }
    
    public void setupAddress() {
        if(mSelectedAddress < 0) {
            container.setDisplayedChildId(R.id.add_address);
            mBinding.setAddress(new Address());
        } else {
            container.setDisplayedChildId(R.id.already_aaddress);
            mBinding.setAddress(addresses.get(mSelectedAddress));
        }
    }
}

