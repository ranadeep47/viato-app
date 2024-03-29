package in.viato.app.ui.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.http.models.Address;
import in.viato.app.http.models.Locality;
import in.viato.app.ui.activities.AddressListActivity;
import in.viato.app.ui.activities.LocalityActivity;
import in.viato.app.ui.adapters.PlaceAutocompleteAdapter;
import retrofit.HttpException;
import rx.Subscriber;

public class AddressEditFragment extends AbstractFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = AddressEditFragment.class.getSimpleName();

    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;

    public static final String ARG_SELECTED_ADDRESS = "selectedAddress";
    public static final String ARG_ADDRESS = "address";
    public static final String ARG_LENGTH = "listSize";
    public static final String ARG_ACTION = "action";

    public static final int CREATE_ADDRESS = 0;
    public static final int EDIT_ADDRESS = 1;

    public static final int LOCALITY_REQUEST_CODE = 0;

    @Bind(R.id.editText_label)
    @NotEmpty
    EditText label;

    @Bind(R.id.editText_flat)
    @NotEmpty
    EditText flat;

    @Bind(R.id.editText_street)
    @NotEmpty
    EditText street;

    @Bind(R.id.editText_locality)
    @NotEmpty
    TextView localityTV;

    @Bind(R.id.scrollView) ScrollView mScrollView;

    private int mAction;
    private int mSelectedAddress;
    private int mListLength;
    private String placeId;
    private String placeName;
    private Address mAddress;
    private Validator mValidator;

    public static AddressEditFragment newInstance (int selectedAddressId, int action, Address address, int listSize){
        AddressEditFragment fragment = new AddressEditFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_SELECTED_ADDRESS, selectedAddressId);
        args.putInt(ARG_ACTION, action);
        args.putInt(ARG_LENGTH, listSize);

        if(address == null){
            address = new Address();
        }

        args.putParcelable(ARG_ADDRESS, address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        mSelectedAddress = args.getInt(ARG_SELECTED_ADDRESS, -1);
        mAction = args.getInt(ARG_ACTION, 0);
        mAddress = args.getParcelable(ARG_ADDRESS);
        mListLength = args.getInt(ARG_LENGTH);

        setHasOptionsMenu(true);

        mValidator = new Validator(this);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                mAddress.setLabel(label.getText().toString());
                mAddress.setFlat(flat.getText().toString());
                mAddress.setStreet(street.getText().toString());
                mAddress.setLocality(new Locality(placeId, localityTV.getText().toString()));

                switch (mAction) {
                    case EDIT_ADDRESS:
                        updateAddress(mAddress, mAddress.getId());
                        break;
                    case CREATE_ADDRESS:
                        createAddress(mAddress);
                        break;
                }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_address_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        flat.setText(mAddress.getFlat());
        street.setText(mAddress.getStreet());
        label.setText(mAddress.getLabel() == null ? "Home" : mAddress.getLabel());
        label.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mValidator.validate();
                }
                return false;
            }
        });

        if(mAddress.getLocality() == null) {
            placeId = null;
            placeName = null;
        } else {
            placeId = mAddress.getLocality().getPlaceId();
            placeName = mAddress.getLocality().getName();
        }

        final AddressEditFragment thisFragment = this;

        localityTV.setText(placeName);
        localityTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocalityActivity.class);
                startActivityForResult(intent, LOCALITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
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
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        mValidator = null;
        mGoogleApiClient = null;

        super.onDestroy();
    }

    public void createAddress(Address address) {
        mViatoAPI.createAddress(address)
                .subscribe(new Subscriber<Address>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        Snackbar.make(mScrollView, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(Address address) {
                        Toast.makeText(getContext(), "Address added", Toast.LENGTH_SHORT).show();
                        mSelectedAddress = mListLength;
                        setResult(Activity.RESULT_OK, address);
                    }
                });
    }


    public void updateAddress(Address address, String id) {
        mViatoAPI.updateAddress(id, address)
                .subscribe(new Subscriber<Address>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        Snackbar.make(mScrollView, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(Address address) {
                        setResult(Activity.RESULT_OK, address);
                        Toast.makeText(getContext(), "Address updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setResult(int resultCode, Address address) {
        Intent intent = new Intent();
        intent.putExtra(ARG_ADDRESS, address);
        intent.putExtra(AddressListActivity.ARG_ADDRESS_INDEX, mSelectedAddress);
        getActivity().setResult(resultCode, intent);
        getActivity().finish();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        mGoogleApiClient.connect();
    }

    public void getLocality() {
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation == null) {
            return;
        }

        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();

        mViatoAPI.getLocality(String.valueOf(latitude), String.valueOf(longitude))
                .subscribe(new Subscriber<Locality>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            Logger.e("Error Code : %d", ((HttpException) e).code());
                            Logger.e(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Locality locality) {
                        setLocality(locality);
                    }
                });
    }

    public void setLocality(Locality locality) {
        if (locality != null){
            placeId = locality.getPlaceId();
            placeName = locality.getName();
            localityTV.setText(placeName);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (placeId == null) {
            getLocality();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.d("received codes: " + requestCode + ", " + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == LOCALITY_REQUEST_CODE) {
            String localPlaceId = data.getStringExtra(LocalityFragment.EXTRA_PLACE_ID);
            String localPlaceName = data.getStringExtra(LocalityFragment.EXTRA_PLACE_NAME);
            if (localPlaceName.isEmpty()){
                return;
            }
            Logger.d("received result: " + localPlaceId + ", " + localPlaceName);
            placeName = localPlaceName;
            placeId = localPlaceId;
            localityTV.setText(placeName);
        }
    }
}
