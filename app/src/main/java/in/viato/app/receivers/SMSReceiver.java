package in.viato.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.viato.app.utils.AppConstants;

/**
 * Created by ranadeep on 11/09/15.
 */
public class SMSReceiver extends BroadcastReceiver {

    /**
     * SmsManager to get the sms in the receiver
     */
    private Context mContext;

    private List<OnSmsReceivedListener> mListeners;

    private static final String TAG = SMSReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        mContext = context;

        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String mMessage = currentMessage.getDisplayMessageBody();
                    if(!((mMessage.toLowerCase()).contains(AppConstants.SMS_VERIFY_FORMAT.toLowerCase()))) {
                        return;
                    }

                    Matcher m = Pattern.compile("(\\d{4})").matcher(mMessage);
                    if ( m.find() ) {
                        String otp = m.group();
                        if (mListeners != null) {
                            for(OnSmsReceivedListener aListener : mListeners) {
                                aListener.onSmsReceived(otp);
                            }
                        }
                        return;
                    }
                }
            }
        } catch(Exception e) {
            Logger.e("Exception smsReceiver : " + e.getMessage());
        }
    }

    public interface OnSmsReceivedListener {
        public void onSmsReceived(String otp);
    }

    public void addListener(OnSmsReceivedListener l) {
        if (mListeners == null){
            mListeners = new ArrayList<OnSmsReceivedListener>();
        }
        mListeners.add(l);
    }
}
