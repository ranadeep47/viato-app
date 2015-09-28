package in.viato.app.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import butterknife.ButterKnife;
import in.viato.app.ui.activties.AbstractActivity;
import in.viato.app.utils.RxUtils;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ranadeep on 11/09/15.
 */
public abstract class AbstractFragment extends Fragment {

    private static final String TAG = "AbstractFragment";

    protected CompositeSubscription mRxSubs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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

    public boolean onBackPressed() {
        return false;
    }

    public AbstractActivity getHostingActivity() {
        return ((AbstractActivity) super.getActivity());
    }
}
