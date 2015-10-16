package in.viato.app.ui.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.login.HttpClient;
import in.viato.app.receivers.SMSReceiver;
import in.viato.app.ui.activities.HomeActivity;
import in.viato.app.utils.SharedPrefHelper;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by ranadeep on 18/09/15.
 */
public class LoginConfirmFragment extends AbstractFragment implements SMSReceiver.OnSmsReceivedListener {

    public static final String TAG = LoginConfirmFragment.class.getSimpleName();

    @Bind(R.id.email) AutoCompleteTextView mEmail;
    @Bind(R.id.smscode) EditText mSMSCode;
    @Bind(R.id.otp_input_container) LinearLayout otpInputContainer;
    @Bind(R.id.otp_success_container) LinearLayout otpsuccessContainer;

    private SMSReceiver smsReceiver;
    private String mToken;

    private final HttpClient mHttpClient = ViatoApplication.get().getHttpClient();

    public static LoginConfirmFragment newInstance() {

        Bundle args = new Bundle();

        LoginConfirmFragment fragment = new LoginConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_confirm, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        smsReceiver = new SMSReceiver();
        smsReceiver.addListener(this);

//        mBtnVerifyOtp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                verifyOTP();
//            }
//        });

        String[] emails = getEmails();
        ArrayAdapter<String> emailsAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                emails);
//        mEmail.setThreshold(1);
        mEmail.setAdapter(emailsAdapter);
        mEmail.setText(emails[0]);
        //TODO setup email validator and change error label accordingly
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        getActivity().registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(smsReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    private String[] getEmails() {
        ArrayList<String> emails = new ArrayList<>();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        AccountManager accountManager = AccountManager.get(getActivity());
        Account[] accounts = accountManager.getAccountsByType("com.google");
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                emails.add(possibleEmail);
            }
        }

        return emails.toArray(new String[emails.size()]);
    }

    public void verifyOTP() {
        String otp = mSMSCode.getText().toString();
        if (otp.matches(getString(R.string.regex_otp))) {

            mSMSCode.setError(null);
            mHttpClient.verifyOTP(otp, SharedPrefHelper.getString(R.string.pref_mobile_number))
                    .subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        //Todo: Handle error condition
                        @Override
                        public void onError(Throwable e) {
//                        if (e instanceof Retrofit.RetrofitError) {
//                            if (((RetrofitError) e).isNetworkError()) {
//                                //handle network error
//                            } else {
//                                //handle error message from server
//                            }
//                        }
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            Logger.e(e.getMessage());
                        }

                        @Override
                        public void onNext(String token) {
                            mToken = token;
                            otpInputContainer.setVisibility(View.GONE);
                            otpsuccessContainer.setVisibility(View.VISIBLE);
                            Logger.d(token);
                        }
                    });
        } else {
            mSMSCode.setError(getString(R.string.error_otp_input));
            return;
        }
    }

    @Override
    public void onSmsReceived(String otp) {
        mSMSCode.setText(otp);
        verifyOTP();
    }

    @OnClick(R.id.btn_resend_otp)
    public void resendOTP() {
        String mobile_number = SharedPrefHelper.getString(R.string.pref_mobile_number);
        String device_id = SharedPrefHelper.getString(R.string.pref_device_id);

        //Todo: Handle error condition
        mHttpClient.login(mobile_number, device_id, new ArrayList<String>())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.d(throwable.getMessage() + " due to " + throwable.getCause());
                        mSMSCode.setError(throwable.getMessage());
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @OnClick(R.id.btn_submit)
    public void submitEmail() {
        String email = mEmail.getText().toString();
        Logger.d(email + "  " + mToken);
        if(mToken == null){
            verifyOTP();
            return;
        }
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError(null);
            mHttpClient.submitEmail(email, mToken)
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
//                            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                            SharedPrefHelper.set(R.string.pref_access_token, s);
                            startActivity(new Intent(getActivity(), HomeActivity.class));
                            getActivity().finish();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            mEmail.setError(getString(R.string.error_email_input));
            return;
        }
    }

//    public Boolean validateEmail(String email) {
//        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
//        Matcher matcher = pattern.matcher(email);
//        return matcher.matches();
//    }
}

//TODO Store values in shared prefs from the results of verification call
//TODO Auto verify sms code
//TODO Upload all the user accounts
//TODO Show welcome toast to existing users