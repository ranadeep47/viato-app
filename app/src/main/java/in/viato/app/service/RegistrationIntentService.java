package in.viato.app.service;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.orhanobut.logger.Logger;

import in.viato.app.R;
import in.viato.app.utils.SharedPrefHelper;

/**
 * Created by saiteja on 02/11/15.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            SharedPrefHelper.set(R.string.pref_gcm_reg_token, token);

        } catch (Exception e) {
            Logger.d(TAG, "Failed to complete token refresh", e);
        }
    }

    public RegistrationIntentService() {
        super(TAG);
    }
}
