package in.viato.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.orhanobut.logger.Logger;

/**
 * Created by ranadeep on 11/09/15.
 */
public class SMSReceiver extends BroadcastReceiver {

    /**
     * SmsManager to get the sms in the receiver
     */
    private String mMessage;


    private static final String TAG = "SMSReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

//                final Object[] pdusObj = (Object[]) bundle.get("pdus");
//
//                for (int i = 0; i < pdusObj.length; i++) {
//
//                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
//                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
//                    String senderNum = phoneNumber;
//                    mMessage = currentMessage.getDisplayMessageBody();
//                    mMessage = mMessage.replace(AppConstants.SMS_VERIFY_FORMAT,"");
//                    Logger.d(TAG, "SmsReceiver : senderNum: " + senderNum + "; message: " + mMessage);
//
//                    ((YeloApplication) context.getApplicationContext()).getBus().post(new SmsVerification(mMessage));
//                    Logger.d(TAG, "SmsReceiver : senderNum: " + senderNum + "; message: " + mMessage);
//


            } // end for loop
        } // bundle is null
        catch(Exception e) {
            Logger.e(TAG, "Exception smsReceiver" + e);

        }

    }
}
