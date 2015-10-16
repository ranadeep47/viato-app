package in.viato.app.ui.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.databinding.FragmentEditAddressBinding;
import in.viato.app.dummy.Addresses;
import in.viato.app.model.Address;
import in.viato.app.ui.activities.AbstractActivity;

public class EditAddressFragment extends AbstractFragment {

    public static final String TAG = EditAddressFragment.class.getSimpleName();
    public static final String ARG_SELECTED_ADDRESS = "selectedAddress";

    @NotEmpty @Bind(R.id.editText_address) EditText address;
    @NotEmpty @Bind(R.id.editText_label) EditText label;
    @NotEmpty @Bind(R.id.editText_locality) EditText locality;

    private Boolean isValid = false;
    private int mSelectedAddress;
    private Address addressItem;
    private List<Address> addresses;
    private Validator mValidator;

    public static EditAddressFragment newInstance (int selectedAddressId){
        EditAddressFragment fragment = new EditAddressFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_ADDRESS, selectedAddressId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mSelectedAddress = args.getInt(ARG_SELECTED_ADDRESS, -1);
        addresses = (new Addresses()).get();
        setValidation(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        FragmentEditAddressBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_address, container, false);

        if(mSelectedAddress < 0) {
            addressItem = new Address();
        } else {
            addressItem = addresses.get(mSelectedAddress);
        }

        binding.setAddress(addressItem);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AbstractActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_address, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_done:
                mValidator.validate();
                return true;
            default:
                return false;
        }
    }

    public void setValidation(Context context) {
        mValidator = new Validator(context);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                isValid = true;

                Intent intent = new Intent();
                intent.putExtra(ARG_SELECTED_ADDRESS, mSelectedAddress); //addressItem
                if (getParentFragment() != null) {
                    getParentFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                } else {
                    onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }
                onBackPressed();
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError error : errors) {
                    View view = error.getView();
                    String message = error.getCollatedErrorMessage(getContext());

                    if (view instanceof EditText) {
                        ((EditText) view).setError(message);
                    } else {
                        Snackbar.make(view, "message", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mValidator = null;
    }

    public void sendResult(){
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(AddressListFragment.ARG_SELECTED_ADDR, mSelectedAddress);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}

