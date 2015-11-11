package in.viato.app.ui.fragments;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.login.HttpClient;
import in.viato.app.http.models.request.Account;
import in.viato.app.http.models.response.LoginResponse;
import in.viato.app.receivers.SMSReceiver;
import in.viato.app.ui.activities.HomeActivity;
import in.viato.app.utils.AppConstants;
import in.viato.app.utils.SharedPrefHelper;
import retrofit.HttpException;
import retrofit.Response;
import rx.Subscriber;

/**
 * Created by ranadeep on 18/09/15.
 */
public class LoginConfirmFragment extends AbstractFragment implements SMSReceiver.OnSmsReceivedListener {

    public static final String TAG = LoginConfirmFragment.class.getSimpleName();

    @Bind(R.id.email) AutoCompleteTextView mEmail;
    @Bind(R.id.smscode) EditText mSMSCode;
    @Bind(R.id.otp_input_container) LinearLayout otpInputContainer;
    @Bind(R.id.otp_success_container) LinearLayout mOtpSuccessContainer;
    @Bind(R.id.btn_resend_otp) Button mOtpButton;
    @Bind(R.id.main_container) CoordinatorLayout mContainer;

    private static final int OTP_DISPLAY_DELAY = 60 * 1000;

    private Handler mHandler = new Handler();

    private SMSReceiver smsReceiver;
    private String mToken;

    private final HttpClient mHttpClient = ViatoApplication.get().getHttpClient();

    private Boolean isOtpVerified = false;
    private Boolean isEmailVerified = false;
    private Boolean isFinishClicked = false;

    public static LoginConfirmFragment newInstance() {
        return new LoginConfirmFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViatoApplication.get().sendScreenView(getString(R.string.login_confirm_fragment));
        return inflater.inflate(R.layout.fragment_login_confirm, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        smsReceiver = new SMSReceiver();

        mSMSCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.getText().length() == 4) {
                    mViatoApp.sendEvent("login", "manual_verify", "otp");
                    verifyOTP();
                    return false;
                }
                return false;
            }
        });

        String[] emails = getEmails();
        if (emails.length > 0) {
            ArrayAdapter<String> emailsAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    emails);
            mEmail.setThreshold(3);
            mEmail.setAdapter(emailsAdapter);
            mEmail.setText(emails[0]);
        }

        mEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    finishLogin();
                    return true;
                }
                return false;
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
            if(mOtpButton != null){
                mOtpButton.setVisibility(View.VISIBLE);
            }
            }
        }, OTP_DISPLAY_DELAY);
    }

    @Override
    public void onResume() {
        super.onResume();

//        Analytics.with(getContext()).screen("screen", getString(R.string.login_confirm_fragment));

        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        smsReceiver.addListener(this);
        getActivity().registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(smsReceiver);
        smsReceiver.removeListener(this);

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
        final ProgressDialog dialog = showProgressDialog("Signing in...");
        String otp = mSMSCode.getText().toString();
        if (otp.matches(getString(R.string.regex_otp))) {
            mSMSCode.setError(null);
            mHttpClient.verifyOTP(otp, SharedPrefHelper.getString(R.string.pref_mobile_number))
                    .subscribe(new Subscriber<Response<String>>() {
                        @Override
                        public void onCompleted() {

                        }

                        //Todo: Handle error condition
                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            if (e instanceof HttpException) {
                                handleNetworkException(((HttpException) e));
                            }
                        }

                        @Override
                        public void onNext(Response<String> response) {
                            dialog.dismiss();
                            if (response.isSuccess()) {
                                isOtpVerified = true;
                                mToken = response.body();
                                otpInputContainer.setVisibility(View.GONE);
                                mOtpSuccessContainer.setVisibility(View.VISIBLE);

                                if(isFinishClicked){
                                    finishLogin();
                                }
                            } else {
                                try {
                                    Toast.makeText(getContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                                    Logger.e(response.errorBody().string());
                                } catch (IOException e) {
                                    Logger.e(e, "error");
                                }
                            }
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
        mViatoApp.sendEvent("login", "auto_verify", "otp");
        verifyOTP();
    }

    @OnClick(R.id.btn_resend_otp)
    public void resendOTP() {
        String mobile_number = SharedPrefHelper.getString(R.string.pref_mobile_number);
        String device_id = SharedPrefHelper.getString(R.string.pref_device_id);

        mViatoApp.sendEvent("login", "resend", "otp");

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
                            try {
                                Toast.makeText(getContext(), stringResponse.errorBody().string(), Toast.LENGTH_SHORT).show();
                                Logger.e(stringResponse.errorBody().string());
                            } catch (IOException e) {
                                Logger.e(e, "error");
                            }
                        }
                    }
                });
    }

    @OnClick(R.id.btn_submit)
    public void finishLogin() {
        isFinishClicked = true;

        if(!isOtpVerified) {
            verifyOTP();
            return;
        }
        if (!isEmailVerified) {
            verifyEmail();
            return;
        }
        if (isOtpVerified && isEmailVerified) {
            mViatoApp.sendEvent("login", "finish", "login");
            startActivity(new Intent(getActivity(), HomeActivity.class));
            getActivity().finish();
        }
    }

//    public Boolean validateEmail(String email) {
//        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
//        Matcher matcher = pattern.matcher(email);
//        return matcher.matches();
//    }

    public void verifyEmail() {
        final String email = mEmail.getText().toString();
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError(null);
            mHttpClient.finishLogin(email, mToken, getAccounts())
                    .subscribe(new Subscriber<Response<LoginResponse>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Response<LoginResponse> loginResponse) {
                            if(loginResponse.isSuccess()) {
                                isEmailVerified = true;

                                LoginResponse response = loginResponse.body();

                                mViatoApp.sendEvent("login", "submit", "email");

                                String access_token = response.getAccess_token();
                                String user_id = response.getUser_id();

                                AppConstants.UserInfo.INSTANCE.setAuthToken(access_token);
                                AppConstants.UserInfo.INSTANCE.setId(user_id);
                                AppConstants.UserInfo.INSTANCE.setEmail(email);

                                SharedPrefHelper.set(R.string.pref_access_token, access_token);
                                SharedPrefHelper.set(R.string.pref_user_id, user_id);
                                SharedPrefHelper.set(R.string.pref_email, email);

                                if(isFinishClicked){
                                    finishLogin();
                                }
                            } else {
                                try {
                                    Toast.makeText(getContext(), loginResponse.errorBody().string(), Toast.LENGTH_LONG).show();
                                    Logger.e(loginResponse.errorBody().string());
                                } catch (IOException e) {
                                    Logger.e(e, "error");
                                }

                            }
                        }
                    });
        } else {
            mEmail.setError(getString(R.string.error_email_input));
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        smsReceiver = null;
    }
}

//TODO Store values in shared prefs from the results of verification call
//TODO Auto verify sms code
//TODO Upload all the user accounts
//TODO Show welcome toast to existing users
