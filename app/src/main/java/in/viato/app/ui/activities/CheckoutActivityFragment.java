package in.viato.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.viato.app.CustomLinearLayoutManager;
import in.viato.app.R;
import in.viato.app.dummy.DummyBooks;
import in.viato.app.ui.adapters.CheckoutListAdapter;
import in.viato.app.ui.fragments.AbstractFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class CheckoutActivityFragment extends AbstractFragment {
    @Bind(R.id.checkout_list) RecyclerView checkoutListRV;

    private AbstractActivity mActivity;


    public static CheckoutActivityFragment newInstance(String param1, String param2) {
        CheckoutActivityFragment fragment = new CheckoutActivityFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mActivity = (AbstractActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        ButterKnife.bind(this, view);

        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.checkout_toolbar);
        mToolbar.setTitle(R.string.title_activity_checkout);
        mToolbar.setTitleTextAppearance(getActivity(), R.style.Viato_ActionBar_Title);

        mActivity.setSupportActionBar(mToolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkoutListRV.setHasFixedSize(true);

        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(getContext(), 1, true);
        checkoutListRV.setLayoutManager(linearLayoutManager);

        CheckoutListAdapter checkoutListAdapter = new CheckoutListAdapter(DummyBooks.get(getContext()).getBooks());
        checkoutListRV.setAdapter(checkoutListAdapter);

        return view;
    }

    @OnClick(R.id.card_view_address)
    public void onAddressClicked() {
        getActivity().startActivity(new Intent(getActivity(), AddressListActivity.class));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_book_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
