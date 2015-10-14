package in.viato.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import in.viato.app.R;
import in.viato.app.ui.fragments.AddressListFragment;
import in.viato.app.ui.fragments.EditAddressFragment;

/**
 * Created by saiteja on 20/09/15.
 */
public class AddressListActivity extends AbstractActivity {

    public static final String ARG_ADDRESS_ID = "addressId";
    private int selectedAddressId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);

        Intent intent = getIntent();
        if(intent != null){
            selectedAddressId = intent.getIntExtra(ARG_ADDRESS_ID, 0);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(selectedAddressId == 0){
            loadFragment(R.id.frame_content, EditAddressFragment.newInstance(selectedAddressId), EditAddressFragment.TAG, false, EditAddressFragment.TAG);
        } else {
            loadFragment(R.id.frame_content, AddressListFragment.newInstance(selectedAddressId), AddressListFragment.TAG, false, AddressListFragment.TAG);
        }
    }
}
