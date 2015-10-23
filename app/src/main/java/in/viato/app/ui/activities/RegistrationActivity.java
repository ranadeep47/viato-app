package in.viato.app.ui.activities;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import in.viato.app.R;
import in.viato.app.ui.fragments.LoginConfirmFragment;
import in.viato.app.ui.fragments.LoginFragment;
import in.viato.app.utils.AppConstants;
import in.viato.app.utils.SharedPrefHelper;

/**
 * Created by ranadeep on 15/09/15.
 */
public class RegistrationActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState); //NOTE should be placed after requestWindowFeature
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bare);


        LoginFragment loginFragment = LoginFragment.newInstance();
        loadFragment(R.id.frame_content, loginFragment, LoginFragment.TAG, false, LoginFragment.TAG);
//        loadFragment(R.id.frame_content, LoginConfirmFragment.newInstance(), LoginConfirmFragment.TAG, false, LoginConfirmFragment.TAG);

        //Prevent keybaord from automatically popping up when there is only EditText in LoginFragment
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
