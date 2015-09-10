package in.viato.app.ui.activties;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.receivers.NetworkStateReceiver;
import in.viato.app.ui.fragments.AbstractFragment;
import in.viato.app.ui.fragments.FragmentTransition;
import in.viato.app.utils.RxUtils;
import rx.subscriptions.CompositeSubscription;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ranadeep on 11/09/15.
 */
public class AbstractActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{

    private final static String TAG = "AbstractActivity";

    private ProgressDialog mProgressDialog;
    private static boolean mMainActivityIsOpen;

    protected CompositeSubscription mRxSubs;

    protected NetworkStateReceiver mNetworkReceiver;

    @Bind(R.id.retry) Button retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.bind(this);
        mNetworkReceiver = new NetworkStateReceiver(this);
        mNetworkReceiver.addListener(this);
    }

    @Override
    public void onBackPressed() {
    /* Get the reference to the current master fragment and check if that will handle
        onBackPressed. If yes, do nothing. Else, let the Activity handle it. */
        final AbstractFragment masterFragment = getCurrentMasterFragment();

        boolean handled = false;
        if (masterFragment != null && masterFragment.isResumed()) {
            handled = masterFragment.onBackPressed();
        }

        if (!handled) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ViatoApplication.getNetworkStateReceiver().addListener(this);
        mRxSubs = RxUtils.getNewCompositeSubIfUnsubscribed(mRxSubs);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViatoApplication.getNetworkStateReceiver().removeListener(this);
        RxUtils.unsubscribeIfNotNull(mRxSubs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        //Fetch the current primary fragment. If that will handle the Menu click,
        // pass it to that one
        final AbstractFragment currentMainFragment = (AbstractFragment)
                getFragmentManager()
                        .findFragmentById(R.id.frame_content);

        boolean handled = false;
        if (currentMainFragment != null) {
            handled = currentMainFragment.onOptionsItemSelected(item);
        }

        if (!handled) {
            // To provide Up navigation
            if (item.getItemId() == android.R.id.home) {
                doUpNavigation();
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }

        }

        return handled;


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void doUpNavigation() {
        final Intent upIntent = NavUtils.getParentActivityIntent(this);

        if (upIntent == null) {

            NavUtils.navigateUpFromSameTask(this);

        } else {
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                // This activity is NOT part of this app's task, so create a
                // new
                // task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                                // Navigate up to the closest parent
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(this, upIntent);
            }
        }

    }

    @Override
    public void onNetworkAvailable() {

    }

    @Override
    public void onNetworkUnavailable() {
        showNoInternet();
    }


    public void showNoInternet(){
        FrameLayout overlay = (FrameLayout) findViewById(R.id.frame_overlay);
        overlay.setVisibility(View.VISIBLE);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_no_internet);
        layout.setVisibility(View.VISIBLE);

        //Bind Retry click event
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
    }

    public static boolean mainActivityIsOpen() {
        return mMainActivityIsOpen;
    }

    public static void setMainActivityIsOpen(boolean mainActivityIsOpen) {
        mMainActivityIsOpen = mainActivityIsOpen;
    }

    public ProgressDialog showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgress(0);
        return mProgressDialog;
    }

    public AbstractFragment getCurrentMasterFragment() {

        return (AbstractFragment) getFragmentManager()
                .findFragmentById(R.id.frame_content);

    }

    public void loadFragment(final int containerResId,
                             final AbstractFragment fragment, final String tag,
                             final boolean addToBackStack, final String backStackTag) {

        loadFragment(containerResId, fragment, tag, addToBackStack, backStackTag, false);
    }

    public void loadFragment(final int containerResId,
                             final AbstractFragment fragment, final String tag,
                             final boolean addToBackStack, final String backStackTag,
                             final boolean customAnimate) {

        loadFragment(containerResId, fragment, tag, addToBackStack, backStackTag, customAnimate, false);
    }

    public void loadFragment(final int containerResId,
                             final AbstractFragment fragment, final String tag,
                             final boolean addToBackStack, final String backStackTag,
                             final boolean customAnimate, final boolean remove) {


        final FragmentManager fragmentManager = getFragmentManager();

        if (remove) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().remove(fragment).commit();
            fragmentManager.executePendingTransactions();
        }
        final FragmentTransaction transaction = fragmentManager
                .beginTransaction();

        if (customAnimate) {
            final FragmentTransition fragmentTransition = fragment.getClass()
                    .getAnnotation(
                            FragmentTransition.class);
            if (fragmentTransition != null) {

                transaction
                        .setCustomAnimations(fragmentTransition.enterAnimation(), fragmentTransition
                                .exitAnimation(), fragmentTransition
                                .popEnterAnimation(), fragmentTransition
                                .popExitAnimation());

            }
        }


        transaction.replace(containerResId, fragment, tag);

        if (addToBackStack) {
            transaction.addToBackStack(backStackTag);
        }
        transaction.commit();

    }
}
