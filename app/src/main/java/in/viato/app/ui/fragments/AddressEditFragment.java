package in.viato.app.ui.fragments;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.http.models.Address;
import in.viato.app.http.models.Locality;
import in.viato.app.ui.adapters.PlaceAutocompleteAdapter;
import rx.Subscriber;

public class AddressEditFragment extends AbstractFragment
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public static final String TAG = AddressEditFragment.class.getSimpleName();

    public static final String ARG_SELECTED_ADDRESS = "selectedAddress";
    public static final String ARG_ADDRESS = "address";

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(8.06, 77.5), new LatLng(37.4, 75.4));

    public static final String ARG_ACTION = "action";
    public static final int CREATE_ADDRESS = 0;

    public static final int EDIT_ADDRESS = 1;

    protected GoogleApiClient mGoogleApiClient;
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
    AutoCompleteTextView locality;

    private int mAction;
    private int mSelectedAddress;
    private String placeId;
    private Address mAddress;
    private Validator mValidator;

    public static AddressEditFragment newInstance (int selectedAddressId, int action, Address address){
        AddressEditFragment fragment = new AddressEditFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_ADDRESS, selectedAddressId);
        args.putInt(ARG_ACTION, action);
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

        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_edit, container, false);

        setValidation(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        flat.setText(mAddress.getFlat());
        street.setText(mAddress.getStreet());
        label.setText(mAddress.getLabel());

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    locality.setText(placeLikelihood.getPlace().getName());
                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
                likelyPlaces.release();
            }
        });
        if(mAddress.getLocality() == null) {
            //Todo: get from fused location api
            placeId = "";
        } else {
            locality.setText(mAddress.getLocality().getName());
            placeId = mAddress.getLocality().getPlaceId();
        }

        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, BOUNDS_INDIA, null);
        locality.setOnItemClickListener(mAutocompleteClickListener);
        locality.setAdapter(mAdapter);
        locality.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mValidator.validate();
                }
                return false;
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

    public void setValidation(AddressEditFragment that) {
        mValidator = new Validator(that);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                mAddress.setLabel(label.getText().toString());
                mAddress.setFlat(flat.getText().toString());
                mAddress.setStreet(street.getText().toString());
                mAddress.setLocality(new Locality(placeId, locality.getText().toString()));

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
    public void onDestroy() {
        super.onDestroy();

        mValidator = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /*
                     Retrieve the place ID of the selected item from the Adapter.
                     The adapter stores each Place suggestion in a AutocompletePrediction from which we
                     read the place ID and title.
                      */
                    final AutocompletePrediction item = mAdapter.getItem(position);
                    final String placeId = item.getPlaceId();
                    final CharSequence primaryText = item.getPrimaryText(null);


                    Log.i(TAG, "Autocomplete item selected: " + primaryText);

                    /*
                     Issue a request to the Places Geo Data API to retrieve a Place object with additional
                     details about the place.
                      */
        //            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
        //                    .getPlaceById(mGoogleApiClient, placeId);
        //            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
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
                        Logger.d("error in creating error");
                    }

                    @Override
                    public void onNext(Address address) {
                        Toast.makeText(getContext(), "New Address Added", Toast.LENGTH_SHORT).show();
                        setResult(getActivity().RESULT_OK, address);
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

                    }

                    @Override
                    public void onNext(Address address) {
                        Toast.makeText(getContext(), "Address Updated", Toast.LENGTH_SHORT).show();
                        setResult(getActivity().RESULT_OK, address);
                    }
                });
    }

    public void setResult(int resultCode, Address address) {
        Intent intent = new Intent();
        intent.putExtra(ARG_ADDRESS, address);
        getActivity().setResult(resultCode, intent);
        getActivity().finish();
    }

//    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
//            = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()) {
//                // Request did not complete successfully
//                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
//                places.release();
//                return;
//            }
//            // Get the Place object from the buffer.
//            final Place place = places.get(0);
//
//            // Format details of the place for display and show it in a TextView.
//            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
//                    place.getId(), place.getAddress(), place.getPhoneNumber(),
//                    place.getWebsiteUri()));
//
//            // Display the third party attributions if set.
//            final CharSequence thirdPartyAttribution = places.getAttributions();
//            if (thirdPartyAttribution == null) {
//                mPlaceDetailsAttribution.setVisibility(View.GONE);
//            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
//            }
//
//            Log.i(TAG, "Place details received: " + place.getName());
//
//            places.release();
//        }
//    };

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}

