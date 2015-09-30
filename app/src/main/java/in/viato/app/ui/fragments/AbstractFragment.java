package in.viato.app.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.content.ClipData;
import android.content.ClipboardManager;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.viato.ViatoAPI;
import in.viato.app.ui.activities.AbstractActivity;
import in.viato.app.utils.AppConstants;
import in.viato.app.utils.RxUtils;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ranadeep on 11/09/15.
 */

public abstract class AbstractFragment extends Fragment{

    private static final String TAG = "AbstractFragment";
    private boolean mIsAttached;

    //Props that need to be inherited. API Services, Composite Subscribers, Listener buses etc..
    protected CompositeSubscription mRxSubs;
    //TODO Add api services here
    protected ViatoAPI mViatoAPI;
    protected int mContainerViewId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //TODO Bind the api services here
        ViatoApplication app = ViatoApplication.get();
        mViatoAPI = app.getViatoAPI();
        mIsAttached = true;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Bind events here
        mRxSubs = RxUtils.getNewCompositeSubIfUnsubscribed(mRxSubs);


    }

    @Override
    public void onPause() {
        super.onPause();
        //Unbid events here
        RxUtils.unsubscribeIfNotNull(mRxSubs);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mIsAttached = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContainerViewId = 0;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: {
                getActivity().finish();

                return true;
            }


            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * Every fragment has to inflate a layout in the onCreateView method. We have added this method to
     * avoid duplicate all the inflate code in every fragment. You only have to return the layout to
     * inflate in this method when extends BaseFragment.
     */
//    protected abstract int getFragmentLayout();

    public boolean isAttached() {
        return mIsAttached;
    }


    /**
     * Helper method to load fragments into layout
     *
     * @param containerResId The container resource Id in the content view into which to load the
     *                       fragment
     * @param fragment       The fragment to load
     * @param tag            The fragment tag
     * @param addToBackStack Whether the transaction should be addded to the backstack
     * @param backStackTag   The tag used for the backstack tag
     */
    public void loadFragment(final int containerResId,
                             final AbstractFragment fragment, final String tag,
                             final boolean addToBackStack, final String backStackTag) {

        if (mIsAttached) {
            ((AbstractActivity) getActivity())
                    .loadFragment(containerResId, fragment, tag, addToBackStack, backStackTag);
        }

    }

    public void onUpNavigate() {
        final Bundle args = getArguments();

        if ((args != null) && args.containsKey(AppConstants.Keys.UP_NAVIGATION_TAG)) {
            getFragmentManager()
                    .popBackStack(args.getString(AppConstants.Keys.UP_NAVIGATION_TAG),
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            getFragmentManager().popBackStack();
        }
    }

    // Copy EditCopy text to the ClipBoard
    public void copyToClipBoard(String message) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Message", message);
        clipboard.setPrimaryClip(clip);
    }

    public void hideKeyboard(EditText editText) {

        if (editText != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } else {

            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    public void showKeyboard(EditText editText) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public boolean onBackPressed() {
        return false;
    }

    protected void showError(String message){
        if(this.getView() != null){
            Snackbar
                    .make(this.getView(), message, Snackbar.LENGTH_LONG)
                            //.setAction(R.string.snackbar_action, myOnClickListener)
                    .show();
        }
        else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
