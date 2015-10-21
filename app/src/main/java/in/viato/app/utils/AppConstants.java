package in.viato.app.utils;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by ranadeep on 12/09/15.
 */
public class AppConstants {

    public static final java.lang.String PLAY_STORE_MARKET_LINK = "https://play.google.com/store/apps/details?id=red.yelo";
    public static final String REFERRER_FORMAT = "referrer=%s";
    public static final String REFERRER_VALUE = "utm_source=%s&utm_campaign=%s&utm_medium=%s&utm_content=%s";
    public static final String POST_SHARE = "post_share";
    public static final String APP_VIRALITY = "app_virality";
    public static final String ANDROID_APP = "android_app";
    public static final String CHECK_THIS_OUT = "check_this_out";

    public static final String SMS_VERIFY_FORMAT = "VIATO OTP Code : ";

    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String ERROR = "error";

    public static final String FBPERMISSIONS[] = new String[]{
            "email", "user_about_me"
    };

    public static final String MIME_MULTIPART = "multipart/form-data";
    public static final String MIME_OCTET = "application/octet-stream";

    //VERIFICATION
    public static final String ALREADY_REGISTERED = "already_registered";
    public static final String NOT_REGISTERED = "not_registered";

    public static interface FragmentTags {
        public static final String HOME_SCREEN = "home_screen";

        //dialogs
        public static final String DIALOG_TAKE_PICTURE = "dialog_take_picture";
    }

    public static interface DurationUnit {
        public static final int HOUR = 0;
        public static final int DAY = 1;
        public static final int WEEK = 2;
        public static final int MONTH = 3;

    }

    public enum UserInfo {

        INSTANCE;

        private String mAccessToken;
        private String mEmail;
        private String mMobileNumber;
        private String mId;

        private String mDeviceId;
        private String mName;
        private int mAppVersion;

        private UserInfo() {
            reset();
        }

        public void reset() {
            mAccessToken = "";
            mEmail = "";
            mId = "";
            mName = "";
            mMobileNumber = "";
            mAppVersion = MiscUtils.getPackageInfo().versionCode;
            mDeviceId = MiscUtils.getDeviceId();
        }

        public String getName(){ return mName;}

        public void setName(final String name){
            mName = name;
        }

        public int getAppVersion() { return mAppVersion; }

        public void setAppVersion(int appVersion) {
            mAppVersion = appVersion;
        }

        public String getMobileNumber() {
            return mMobileNumber;
        }

        public void setMobileNumber(final String mobileNumber) {
            mMobileNumber = mobileNumber == null ? "" : mobileNumber;
        }

        public String getAccessToken() {
            return mAccessToken;
        }

        public void setAuthToken(final String authToken) {
            mAccessToken = authToken == null ? "" : authToken;
        }

        public String getEmail() {
            return mEmail;
        }

        public void setEmail(final String email) {
            mEmail = email == null ? "" : email;
        }

        public String getId() {
            return mId;
        }

        public void setId(final String id) {
            mId = id == null ? "" : id;
        }

        public String getDeviceId() {
            return mDeviceId;
        }

        public void setDeviceId(final String deviceId) {
            mDeviceId = deviceId;
        }

    }

    /**
     * Singleton to hold the current network state. Broadcast receiver for network state will be
     * used to keep this updated
     */
    public enum DeviceInfo {

        INSTANCE;

        private final Location defaultLocation = new Location(LocationManager.PASSIVE_PROVIDER);

        private boolean mIsNetworkConnected;
//        private int mCurrentNetworkType;
//        private Location mLatestLocation;

        private DeviceInfo() {
            reset();
        }

        public void reset() {

            mIsNetworkConnected = false;
//            mCurrentNetworkType = ConnectivityManager.TYPE_DUMMY;
//            mLatestLocation = defaultLocation;
        }

        public boolean isNetworkConnected() {
            return mIsNetworkConnected;
        }

        public void setNetworkConnected(final boolean isNetworkConnected) {
            mIsNetworkConnected = isNetworkConnected;
        }

//        public int getCurrentNetworkType() {
//            return mCurrentNetworkType;
//        }
//
//        public void setCurrentNetworkType(final int currentNetworkType) {
//            mCurrentNetworkType = currentNetworkType;
//        }
//
//        public Location getLatestLocation() {
//            return mLatestLocation;
//        }
//
//        public void setLatestLocation(final Location latestLocation) {
//            if (latestLocation == null) {
//                mLatestLocation = defaultLocation;
//            }
//            mLatestLocation = latestLocation;
//        }

    }

    public static interface Keys {
        public static final String UP_NAVIGATION_TAG = "up_navigation_tag";
    }

    public static interface ORDER_STATUS {
        public static final int ORDER_PLACED = 0;
        public static final int ORDER_CANCELED = 1;
        public static final int ORDER_DELIVERED = 2;
        public static final int ORDER_EXTENDED = 3;
        public static final int ORDER_RETURNED = 4;
    }
}
