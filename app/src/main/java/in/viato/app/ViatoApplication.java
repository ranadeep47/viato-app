package in.viato.app;

import android.app.Application;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;

import com.crashlytics.android.Crashlytics;
import in.viato.app.http.clients.login.HttpClient;
import in.viato.app.utils.AppConstants;
import in.viato.app.utils.AppConstants.UserInfo;

import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.orhanobut.logger.LogLevel;
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

    public static Typeface montserrat;

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

        montserrat = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");

        if (BuildConfig.DEBUG) {
            Logger.init(mTAG);
        } else {
            Logger.init(mTAG).setLogLevel(LogLevel.NONE);
        }

        setupNetworkChangeReceiver();
        saveCurrentAppDetailsIntoPreferences();
        readUserInfoFromSharedPref();
        //Initialise API only after all the apt prefs have been read into memory
        initAPI();
        initializeFabric();

        //fabric initiate
        String email = SharedPrefHelper.getString(R.string.pref_email);
        String user_id = SharedPrefHelper.getString(R.string.pref_user_id);

        if ((!email.isEmpty()) && (!user_id.isEmpty())) {
            Crashlytics.setUserIdentifier(user_id);
            Crashlytics.setUserEmail(email);
        }
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
        UserInfo.INSTANCE.setAuthToken(SharedPrefHelper.getString(R.string.pref_access_token));
        UserInfo.INSTANCE.setId(SharedPrefHelper.getString(R.string.pref_user_id));
        UserInfo.INSTANCE.setEmail(SharedPrefHelper.getString(R.string.pref_email));
        UserInfo.INSTANCE.setName(SharedPrefHelper.getString(R.string.pref_name));
        UserInfo.INSTANCE.setMobileNumber(SharedPrefHelper.getString(R.string.pref_mobile_number));
        UserInfo.INSTANCE.setDeviceId(SharedPrefHelper.getString(R.string.pref_device_id));
        UserInfo.INSTANCE.setAppVersion(SharedPrefHelper.getInt(R.string.pref_last_version_code));
        UserInfo.INSTANCE.setAppToken(SharedPrefHelper.getString(R.string.pref_gcm_reg_token));
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
    public void sendScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();
        t.setScreenName(screenName);
        t.send(new HitBuilders.ScreenViewBuilder().build());
        Logger.d("Screen View recorded: " + screenName);
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
        }
    }


    public void sendEvent(String category, String action, String label, long value,
                                 HitBuilders.EventBuilder eventBuilder) {
            Tracker t = getGoogleAnalyticsTracker();
            t.send(eventBuilder
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(value)
                    .build());

            Logger.d("Event recorded: \n" +
                    "\tCategory: " + category +
                    "\tAction: " + action +
                    "\tLabel: " + label +
                    "\tValue: " + value);
    }

    public void sendEvent(String category, String action, String label) {
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        sendEvent(category, action, label, 0, eventBuilder);
    }

    /***
     * Tracking event
     *
     * * @param screenName same of the screen event has occurred
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */

    public void sendEvent(String category, String action, String label, Long value) {
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        sendEvent(category, action, label, value, eventBuilder);
    }

    public void sendEventWithCustomDimension(String category, String action, String label,
                                             int dimensionIndex, String dimensionValue) {
        // Create a new HitBuilder, populate it with the custom dimension, and send it along
        // to the rest of the event building process.
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder.setCustomDimension(dimensionIndex, dimensionValue);
        sendEvent(category, action, label, 0, eventBuilder);

        Logger.d("Custom Dimension Attached:\n" +
                "\tindex: " + dimensionIndex +
                "\tvalue: " + dimensionValue);
    }

    public void sendEventWithCustomDimension(String category, String action, String label, Long value,
                                                    int dimensionIndex, String dimensionValue) {
        // Create a new HitBuilder, populate it with the custom dimension, and send it along
        // to the rest of the event building process.
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder.setCustomDimension(dimensionIndex, dimensionValue);
        sendEvent(category, action, label, value, eventBuilder);

        Logger.d("Custom Dimension Attached:\n" +
                "\tindex: " + dimensionIndex +
                "\tvalue: " + dimensionValue);
    }

    public void initializeFabric () {
        // Set up Crashlytics, disabled for debug builds
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());
    }
}
