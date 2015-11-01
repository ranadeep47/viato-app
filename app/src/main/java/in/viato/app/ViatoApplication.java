package in.viato.app;

import android.app.Application;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;

import com.crashlytics.android.Crashlytics;
import in.viato.app.http.clients.login.HttpClient;
import in.viato.app.utils.AppConstants;
import in.viato.app.utils.AppConstants.UserInfo;

import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import in.viato.app.http.clients.viato.ViatoAPI;
import in.viato.app.receivers.NetworkStateReceiver;
import in.viato.app.utils.MiscUtils;
import in.viato.app.utils.SharedPrefHelper;
import io.fabric.sdk.android.Fabric;


/**
 * Created by ranadeep on 10/09/15.
 */
public class ViatoApplication extends Application implements NetworkStateReceiver.NetworkStateReceiverListener{

    private final String mTAG = ViatoApplication.class.getSimpleName();

    private static ViatoApplication instance;

    private RefWatcher mRefWatcher;
    private NetworkStateReceiver mNetworkStateReceiver;

    private ViatoAPI mViatoAPI;
    private HttpClient mHttpClient;

    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = (ViatoApplication) getApplicationContext();
        mRefWatcher = LeakCanary.install(this);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, MiscUtils.calculateDiskCacheSize(this.getCacheDir())));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(BuildConfig.DEBUG);
        built.setLoggingEnabled(false);
        Picasso.setSingletonInstance(built);

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build());

        Logger.init(mTAG);

        setupNetworkChangeReceiver();
        saveCurrentAppDetailsIntoPreferences();
        readUserInfoFromSharedPref();
        //Initialise API only after all the apt prefs have been read into memory
        initAPI();

        initializeFabric();
    }

    public static ViatoApplication get(){
        return instance;
    }

    private void initAPI() {
        //Initialise APIs with access_tokens from sprefs.
        mViatoAPI = new ViatoAPI();
    }

    public HttpClient getHttpClient() {
        if(mHttpClient == null){
            mHttpClient = new HttpClient();
        }
        return mHttpClient;
    }

    public static RefWatcher getRefWatcher(){
        return instance.mRefWatcher;
    }

    public static NetworkStateReceiver getNetworkStateReceiver(){
        return instance.mNetworkStateReceiver;
    }

    public ViatoAPI getViatoAPI(){
        return mViatoAPI;
    }

    public void setupNetworkChangeReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mNetworkStateReceiver = new NetworkStateReceiver(this);
        mNetworkStateReceiver.addListener(this);
        registerReceiver(mNetworkStateReceiver, intentFilter);
    }

    @Override
    public void onNetworkAvailable() {
        AppConstants.DeviceInfo.INSTANCE.setNetworkConnected(true);
    }

    @Override
    public void onNetworkUnavailable() {
        AppConstants.DeviceInfo.INSTANCE.setNetworkConnected(false);
    }

    /**
     * Reads the previously fetched auth token from Shared Preferencesand stores
     * it in the Singleton for in memory access
     */
    private void readUserInfoFromSharedPref() {
        UserInfo.INSTANCE.setAuthToken(SharedPrefHelper
                .getString(R.string.pref_access_token));
        UserInfo.INSTANCE.setId(SharedPrefHelper
                .getString(R.string.pref_user_id));
        UserInfo.INSTANCE.setEmail(SharedPrefHelper
                .getString(R.string.pref_email));
        UserInfo.INSTANCE.setName(SharedPrefHelper
                .getString(R.string.pref_name));
        UserInfo.INSTANCE.setMobileNumber(SharedPrefHelper.getString(R.string.pref_mobile_number));
        UserInfo.INSTANCE.setDeviceId(SharedPrefHelper.getString(R.string.pref_device_id));
        UserInfo.INSTANCE.setAppVersion(SharedPrefHelper.getInt(R.string.pref_last_version_code));
    }

    /**
     * Save the current app version info into preferences. This is purely for
     * future use where we might need to use these values on an app update
     */
    private void saveCurrentAppDetailsIntoPreferences() {
        PackageInfo info = MiscUtils.getPackageInfo();
        SharedPrefHelper.set(R.string.pref_last_version_code, info.versionCode);
        SharedPrefHelper.set(R.string.pref_last_version_name, info.versionName);
        SharedPrefHelper.set(R.string.pref_device_id, MiscUtils.getDeviceId());
    }

    /*
     *Gets the default {@link Tracker} for this {@link Application}.
     *@return tracker
    */
    synchronized public Tracker getGoogleAnalyticsTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(BuildConfig.DEBUG);
            mTracker = analytics.newTracker(R.xml.app_tracker);
        }
        return mTracker;
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();

        t.setScreenName(null);
    }

    /***
     * Tracking exception
     *
     * @param screenName same of the screen exception has occurred
     * @param e exception to be tracked
     */
    public void trackException(String screenName, Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();
            t.setScreenName(screenName);
            t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(
                                    new StandardExceptionParser(this, null)
                                            .getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build()
            );
            GoogleAnalytics.getInstance(this).dispatchLocalHits();
            t.setScreenName(null);
        }
    }

    /***
     * Tracking event
     *
     * * @param screenName same of the screen event has occurred
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */

    public void trackEvent(String screenName, String category, String action, String label) {
        trackEvent(screenName, category, action, label, "");
    }

    public void trackEvent(String screenName, String category, String action, String label, String extra_key) {
        Tracker t = getGoogleAnalyticsTracker();

        t.setScreenName(screenName);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setCustomDimension(getResources().getInteger(R.integer.extra_key), extra_key)
                .build());
        GoogleAnalytics.getInstance(this).dispatchLocalHits();
        t.setScreenName(null);
    }

    public void trackEvent(String screenName, String category, String action, String label, String extra_key, String source) {
        Tracker t = getGoogleAnalyticsTracker();
        t.setScreenName(screenName);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setCustomDimension(getResources().getInteger(R.integer.extra_key), extra_key)
                .setCustomDimension(getResources().getInteger(R.integer.source), source)
                .build());
        GoogleAnalytics.getInstance(this).dispatchLocalHits();
        t.setScreenName(null);
    }

    public void initializeFabric () {
        // Set up Crashlytics, disabled for debug builds
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());
    }
}
