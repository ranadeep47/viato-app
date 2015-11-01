package in.viato.app.ui.fragments;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.login.HttpClient;
import in.viato.app.http.models.request.Account;
import in.viato.app.receivers.SMSReceiver;
import in.viato.app.ui.activities.HomeActivity;
import in.viato.app.utils.AppConstants;
import in.viato.app.utils.SharedPrefHelper;
import retrofit.HttpException;
import retrofit.Response;
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
        return new LoginConfirmFragment();
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

        mSMSCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.getText().length() == 4) {
                    mViatoApp.trackEvent(getString(R.string.login_fragment), "login", "manual_verify", "otp");
                    verifyOTP();
                    return false;
                }
                return false;
            }
        });

        String[] emails = getEmails();
        ArrayAdapter<String> emailsAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                emails);
        mEmail.setThreshold(3);
        mEmail.setAdapter(emailsAdapter);
        mEmail.setText(emails[0]);
        mEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitEmail();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        ViatoApplication.get().trackScreenView(getString(R.string.login_confirm_fragment));
//        Analytics.with(getContext()).screen("screen", getString(R.string.login_confirm_fragment));

        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        getActivity().registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(smsReceiver);
    }

    private String[] getEmails() {
        ArrayList<String> emails = new ArrayList<>();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        AccountManager accountManager = AccountManager.get(getActivity());
        android.accounts.Account[] accounts = accountManager.getAccountsByType("com.google");
        for (android.accounts.Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                emails.add(possibleEmail);
            }
        }

        return emails.toArray(new String[emails.size()]);
    }

    private List<Account> getAccounts() {
        AccountManager accountManager = AccountManager.get(getActivity());
        android.accounts.Account[] accounts = accountManager.getAccounts();
        List<Account> accountList = new ArrayList<>();
        for (android.accounts.Account account : accounts){
            accountList.add(new Account(account.name, account.type));
        }
        return accountList;
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
                            if (e instanceof HttpException) {
//                                handleNetworkException(e);
                                if (((HttpException) e).code() == 502) {
                                    //handle network error
                                } else if (((HttpException) e).code() == 400) {
                                    //handle error message from server
                                } else if (((HttpException) e).code() == 404) {
                                    //
                                }
                            }
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            Logger.e(e.getMessage());
                        }

                        @Override
                        public void onNext(String token) {
                            mToken = token;
                            otpInputContainer.setVisibility(View.GONE);
                            otpsuccessContainer.setVisibility(View.VISIBLE);
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
        mViatoApp.trackEvent(getString(R.string.login_fragment), "login", "auto_verify", "otp");
        verifyOTP();
    }

    @OnClick(R.id.btn_resend_otp)
    public void resendOTP() {
        String mobile_number = SharedPrefHelper.getString(R.string.pref_mobile_number);
        String device_id = SharedPrefHelper.getString(R.string.pref_device_id);

        mViatoApp.trackEvent(getString(R.string.login_fragment), "login", "resend", "otp");

        //Todo: Handle error condition
        mHttpClient.login(mobile_number, device_id)
                .subscribe(new Subscriber<Response<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        if (stringResponse.isSuccess()) {
                            Toast.makeText(getContext(), stringResponse.body(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), stringResponse.message(), Toast.LENGTH_SHORT).show();
                            Logger.e(stringResponse.message());
                        }
                    }
                });
    }

    @OnClick(R.id.btn_submit)
    public void submitEmail() {
        final String email = mEmail.getText().toString();
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError(null);
            mHttpClient.finishLogin(email, mToken, getAccounts())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String access_token) {
                            mViatoApp.trackEvent(getString(R.string.login_fragment), "login", "submit", "email");

                            AppConstants.UserInfo.INSTANCE.setAuthToken(access_token);
                            AppConstants.UserInfo.INSTANCE.setEmail(email);

                            SharedPrefHelper.set(R.string.pref_access_token, access_token);
                            SharedPrefHelper.set(R.string.pref_email, email);

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
