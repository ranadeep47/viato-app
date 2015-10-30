package in.viato.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.orhanobut.logger.Logger;

import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.ui.fragments.AddressEditFragment;
import in.viato.app.ui.fragments.AddressListFragment;

/**
 * Created by saiteja on 20/09/15.
 */
public class AddressListActivity extends AbstractActivity {

    public static final String ARG_ADDRESS_ID = "addressId";
    public static final String ARG_ADDRESS_INDEX = "addressIndex";
    public static final String ARG_ADDRESSES_SIZE = "addressListSize";
    public static final String ARG_ADDRESS = "address";

    private int selectedAddressId;
    private int addressListSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);

        Intent intent = getIntent();
        if(intent != null){
            selectedAddressId = intent.getIntExtra(ARG_ADDRESS_ID, -1);
            addressListSize = intent.getIntExtra(ARG_ADDRESSES_SIZE, -1);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        loadFragment(R.id.frame_content, AddressListFragment.newInstance(selectedAddressId), AddressListFragment.TAG, false, AddressListFragment.TAG);
        if(selectedAddressId < 0){
            loadFragment(R.id.frame_content, AddressEditFragment.newInstance(selectedAddressId, AddressEditFragment.CREATE_ADDRESS, null, addressListSize), AddressEditFragment.TAG, false, AddressEditFragment.TAG);
        } else {
            loadFragment(R.id.frame_content, AddressListFragment.newInstance(selectedAddressId), AddressListFragment.TAG, false, AddressListFragment.TAG);
        }
    }
}
