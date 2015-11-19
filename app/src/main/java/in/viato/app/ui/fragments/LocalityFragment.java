package in.viato.app.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Places;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.http.models.Locality;
import in.viato.app.ui.adapters.PlaceAutocompleteAdapter;
import retrofit.HttpException;
import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalityFragment extends AbstractFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = LocalityFragment.class.getSimpleName();

    private String placeId;
    private String placeName;

    public static final String EXTRA_PLACE_ID = "in.viato.app.placeId";
    public static final String EXTRA_PLACE_NAME = "in.viato.app.placeName";

    private PlaceAutocompleteAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;

    @Bind(R.id.locality)
    AutoCompleteTextView locality;

    @Bind(R.id.use_location)
    TextView useLocation;

    public LocalityFragment() {
        // Required empty public constructor
    }

    public static LocalityFragment newInstance() {
        return new LocalityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_locality, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        mAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, null, null);
        locality.setAdapter(mAdapter);
        locality.setOnItemClickListener(mAutocompleteClickListener);

        useLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                    return;
                }

                getLocality();
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard(locality);
        getActivity().finish();
        return true;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            placeId = item.getPlaceId();
            placeName = (String)item.getPrimaryText(null);
            setResult(placeId, placeName);
        }
    };

    public void setResult(String placeId, String placeName) {
        hideKeyboard(locality);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_PLACE_ID, placeId);
        intent.putExtra(EXTRA_PLACE_NAME, placeName);

        Logger.d("set result: " + placeId + ", " + placeName);

        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void getLocality() {
        final ProgressDialog dialog = showProgressDialog("Fetching locality...");
        Location mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation == null) {
            Logger.d("locality null");
            dialog.dismiss();
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
                        dialog.dismiss();
                        if (e instanceof HttpException) {
                            Logger.e("Error Code : %d", ((HttpException) e).code());
                            Logger.e(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Locality locality) {
                        dialog.dismiss();
                        placeId = locality.getPlaceId();
                        placeName = locality.getName();
                        setResult(placeId, placeName);
                    }
                });
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
}
