package in.viato.app.ui.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import in.viato.app.R;

/**
 * Created by ranadeep on 18/09/15.
 */
public class LoginConfirmFragment extends AbstractFragment {
    
    public static final String TAG = "LoginConfirmFragment";

    @Bind(R.id.email) AutoCompleteTextView mEmail;
    @Bind(R.id.smscodeLabel) TextInputLayout mSMSLabel;
    @Bind(R.id.smscode) EditText mSMSCode;

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

        String[] emails = getEmails();
        ArrayAdapter<String> emailsAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                emails);
        mEmail.setThreshold(1);
        mEmail.setAdapter(emailsAdapter);
        mEmail.setText(emails[0]);
        //TODO setup email validator and change error label accordingly
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    private String[] getEmails(){
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
}

//TODO Store values in shared prefs from the results of verification call
//TODO Auto verify sms code
//TODO Upload all the user accounts
//TODO Show welcome toast to existing users