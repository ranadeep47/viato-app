package in.viato.app;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;

import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import in.viato.app.http.clients.viato.ViatoAPI;
import in.viato.app.receivers.NetworkStateReceiver;
import in.viato.app.utils.AppConstants;
import in.viato.app.utils.MiscUtils;
import in.viato.app.utils.SharedPrefHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by ranadeep on 10/09/15.
 */
public class ViatoApplication extends Application implements NetworkStateReceiver.NetworkStateReceiverListener{

    private final String mTAG = "ViatoApplication";

    private static ViatoApplication instance;

    private RefWatcher mRefWatcher;
    private NetworkStateReceiver mNetworkStateReceiver;

    private ViatoAPI mViatoAPI;

    public static Typeface roboto;
    public static Typeface robotoCondensed;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = (ViatoApplication) getApplicationContext();
        mRefWatcher = LeakCanary.install(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        roboto = Typeface.createFromAsset(getAssets(), "fonts/roboto/Roboto-Black.ttf");
        robotoCondensed = Typeface.createFromAsset(getAssets(), "fonts/roboto/RobotoCondensed-Bold.ttf");

        Logger.init(mTAG);

        setupNetworkChangeReceiver();
        //saveCurrentAppVersionIntoPreferences();
        //Initialise API only after all the apt prefs have been read into memory
        //initAPI();
    }

    public static ViatoApplication get(){
        return instance;
    }

    private void initAPI() {
        //Initialise APIs with access_tokens from sprefs.
        mViatoAPI = new ViatoAPI();
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
     * Save the current app version info into preferences. This is purely for
     * future use where we might need to use these values on an app update
     */
    private void saveCurrentAppVersionIntoPreferences() {
        PackageInfo info = MiscUtils.getPackageInfo();
        SharedPrefHelper
                .set(R.string.pref_last_version_code, info.versionCode);
        SharedPrefHelper
                .set(R.string.pref_last_version_name, info.versionName);
    }
}
