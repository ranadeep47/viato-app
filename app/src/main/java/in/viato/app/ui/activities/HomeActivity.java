package in.viato.app.ui.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import in.viato.app.BuildConfig;
import in.viato.app.R;
import in.viato.app.http.models.ForceUpdate;
import in.viato.app.http.models.response.Serviceability;
import in.viato.app.service.RegistrationIntentService;
import in.viato.app.ui.fragments.CategoryBooksFragment;
import in.viato.app.ui.fragments.HomeFragment;
import in.viato.app.utils.MiscUtils;
import in.viato.app.utils.SharedPrefHelper;
import jp.wasabeef.picasso.transformations.ColorFilterTransformation;
import retrofit.Response;
import rx.Subscriber;

/**
 * Created by ranadeep on 19/09/15.
 */
public class HomeActivity extends AbstractNavDrawerActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private AbstractActivity mActivity;
    private ViewPager mViewPager;
    private TabLayout mTabs;
    private EditText searchBar;

    private Location mLastLocation;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    @Bind(R.id.stub_cover_image) ViewStub stubCoverImage;
    @Bind(R.id.main_container) CoordinatorLayout mMainContainer;

    public static final int TAB_CATEGORIES = '0';
    public static final int TAB_TRENDING = '1';

    public static final String EXTRA_SELECT_TAB = "select_tab";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_tablayout);

        mActivity = this;

        if(checkPlayServices()) {
            buildGoogleApiClient();
        }

        String access_token = SharedPrefHelper.getString(R.string.pref_access_token);
        if(access_token.length() == 0) {
            startActivity(new Intent(this, RegistrationActivity.class));
            finish();
            return;
        } else {
            // TODO: 30/10/15 replace 1 with user id
            String userId = SharedPrefHelper.getString(R.string.pref_user_id);
            String email = SharedPrefHelper.getString(R.string.pref_email);

//            Analytics.with(this).identify(userId, new Traits().putEmail(email).putPhone(phone), null);
            Crashlytics.setUserIdentifier(userId);
            Crashlytics.setUserEmail(email);
        }

        mViewPager = (ViewPager)((ViewStub) findViewById(R.id.stub_viewpager_my_books)).inflate();
        mTabs = (TabLayout)((ViewStub) findViewById(R.id.stub_tabs_my_books)).inflate();

        handleIntent(getIntent());
        checkForceUpdate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupViewPager();
        showCoverImage();
        Picasso.with(this)
                .load("https://c2.staticflickr.com/6/5790/21790653402_0cf3b8c65e.jpg")
                .transform(new ColorFilterTransformation(R.color.primary_dark))
                .into(coverImage);

//        http://192.168.1.101:8080/image/cover

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                startActivity(new Intent(this, BookSearchActivity.class));
                mViatoApp.trackEvent(getString(R.string.home_activity),
                        "search", "clicked", "icon", "", "home_menu");
                return true;
            case R.id.menu_cart:
                startActivity(new Intent(this, CheckoutActivity.class));
                mViatoApp.trackEvent(getString(R.string.home_activity),
                        "cart", "clicked", "icon", "", "home_menu");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    static class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void clearAll() {
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }

    public void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(HomeFragment.newInstance(), "Genres");
        adapter.addFrag(CategoryBooksFragment.newInstance("trending", getResources().getString(R.string.trending)),
                getResources().getString(R.string.trending)
        );

        mViewPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mViewPager);

        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String screenName = (String) mViewPager.getAdapter().getPageTitle(position);
                mViatoApp.trackScreenView(screenName + " Fragment");
//                Analytics.with(mActivity).screen("screen", screenName + " Fragment");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(listener);
        listener.onPageSelected(0);
    }
    protected void showCoverImage(){
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        View coverContainer = stubCoverImage.inflate();
        coverImage = (ImageView) coverContainer.findViewById(R.id.cover_image);
        searchBar = (EditText) coverContainer.findViewById(R.id.search_bar);

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = v.getText().toString();
                    Intent intent = new Intent(mActivity, BookSearchActivity.class);
                    intent.putExtra(SearchManager.QUERY, query);
                    intent.setAction("android.intent.action.SEARCH");
                    startActivity(intent);
                    mViatoApp.trackEvent(getString(R.string.home_activity),
                            "search", "submit", "query", query, getString(R.string.home_activity));
                    searchBar.setText("");
                    return true;
                }
                return false;
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setSearchBarDrawables();
            }
        });

        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (searchBar.getRight() - searchBar.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        setSearchBarDrawables();
                        if (searchBar.getText().length() > 0) {
                            //clear search bar
                            searchBar.setText("");
                        } else {
                            //start barcode activity
                            startActivity(new Intent(mActivity, BarcodeScannerActivity.class));
                            mViatoApp.trackEvent(getString(R.string.home_activity),
                                    "barcode_scanner", "clicked", "icon", "", "searchbar_home");
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void handleIntent(Intent intent) {
        if(intent == null){ return; }
        if(intent.hasExtra(EXTRA_SELECT_TAB)){
            int index = intent.getIntExtra(EXTRA_SELECT_TAB, 0);
            mViewPager.setCurrentItem(index);
        }
    }

    public void setSearchBarDrawables() {
        if(searchBar.getText().toString().length() > 0) {
            searchBar.setCompoundDrawablesWithIntrinsicBounds(mActivity.getResources().getDrawable(R.drawable.ic_search_black),
                    null,
                    mActivity.getResources().getDrawable(R.drawable.ic_close_black),
                    null);
        } else {
            searchBar.setCompoundDrawablesWithIntrinsicBounds(mActivity.getResources().getDrawable(R.drawable.ic_search_black),
                    null,
                    mActivity.getResources().getDrawable(R.drawable.ic_barcode_black),
                    null);

        }
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return getResources().getInteger(R.integer.nav_item_home);
    }

    public void checkServiceability() {
        Boolean checkedAvailable =  SharedPrefHelper.getBoolean(R.string.pref_show_unavailable);
        if (checkedAvailable) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

//        Log.d( "qwe3", mLastLocation + "");

        if (mLastLocation == null) {
            mViatoAPI.getServiceLocations().subscribe(new Subscriber<Response<List<String>>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Response<List<String>> listResponse) {
                    if (listResponse.isSuccess()) {
                        List<String> body = listResponse.body();
                        String places = TextUtils.join(", ", body);
                        new AlertDialog.Builder(mActivity)
                                .setTitle("Sorry")
                                .setMessage("We currently serve following localities:\n" + places)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPrefHelper.set(R.string.pref_show_unavailable, true);
                                    }
                                })
                                .create()
                                .show();
                    }
                }
            });
            return;
        }

        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();

        mViatoAPI.getServiceability(String.valueOf(latitude), String.valueOf(longitude))
                .subscribe(new Subscriber<Response<Serviceability>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response<Serviceability> serviceabilityResponse) {
                        if (serviceabilityResponse.isSuccess()) {
                            Serviceability serviceability = serviceabilityResponse.body();
                            Boolean aBoolean = serviceability.getIs_supported();
                            String places = serviceability.getSupported_localities();
                            if (!aBoolean) {
                                new AlertDialog.Builder(mActivity)
                                        .setTitle("Sorry")
                                        .setMessage("We currently serve following localities:\n" + places)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SharedPrefHelper.set(R.string.pref_show_unavailable, true);
                                            }
                                        })
                                        .create()
                                        .show();
                            } else {
                                SharedPrefHelper.set(R.string.pref_show_unavailable, true);
                            }
                        } else {
                            try {
                                Log.e("HomeActivity", serviceabilityResponse.errorBody().string());
                            } catch (IOException e) {
                                com.orhanobut.logger.Logger.e(e, "error");
                            }
                        }
                    }
                });
        return;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkServiceability();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
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

        mGoogleApiClient = null;
    }

    public void checkForceUpdate() {
        String build = BuildConfig.BUILD_TYPE.toUpperCase();
        int versionCode = MiscUtils.getPackageInfo().versionCode;
        mViatoAPI.checkForceUpdate(build, versionCode)
                .subscribe(new Subscriber<Response<ForceUpdate>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response<ForceUpdate> forceUpdateResponse) {
                        if (forceUpdateResponse.isSuccess()) {
                            ForceUpdate body = forceUpdateResponse.body();
                            if (body.getUpdate()){
                                showUpdateDialog(body);
                            }
                        } else {
                            try {
                                com.orhanobut.logger.Logger.e(forceUpdateResponse.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void showUpdateDialog(final ForceUpdate body) {
        new AlertDialog.Builder(mActivity)
                .setTitle("Please update")
                .setMessage(body.getMessage())
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=in.viato.app"));
                        startActivity(browserIntent);
                        showUpdateDialog(body);
                    }
                })
                .create()
                .show();
    }
}


