package in.viato.app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
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
import in.viato.app.ui.adapters.PlaceAutocompleteAdapter;
import retrofit.HttpException;
import rx.Subscriber;

public class AddressEditFragment extends AbstractFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = AddressEditFragment.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    public static final String ARG_SELECTED_ADDRESS = "selectedAddress";
    public static final String ARG_ADDRESS = "address";
    public static final String ARG_LENGTH = "listSize";

    public static final String ARG_ACTION = "action";

    public static final int CREATE_ADDRESS = 0;
    public static final int EDIT_ADDRESS = 1;

    private PlaceAutocompleteAdapter mAdapter;

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
    AutoCompleteTextView localityTV;

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
        View view = inflater.inflate(R.layout.fragment_address_edit, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        flat.setText(mAddress.getFlat());
        street.setText(mAddress.getStreet());
        label.setText(mAddress.getLabel());
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
            if (checkPlayServices()) {
                buildGoogleApiClient();
                getLocality();
            }
        } else {
            placeId = mAddress.getLocality().getPlaceId();
            placeName = mAddress.getLocality().getName();
        }

        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, null, null);
        localityTV.setAdapter(mAdapter);
        localityTV.setText(placeName);
        localityTV.setOnItemClickListener(mAutocompleteClickListener);
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
        super.onDestroy();

        mValidator = null;
        mGoogleApiClient = null;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final AutocompletePrediction item = mAdapter.getItem(position);
                    placeId = item.getPlaceId();
                    placeName = (String)item.getPrimaryText(null);
                }
            };

    public void createAddress(Address address) {
        address.setId("");
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
                        Toast.makeText(getContext(), "New Address Added", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Address Updated", Toast.LENGTH_SHORT).show();
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

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
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
        getLocality();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

