package in.viato.app.ui.activties;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.viato.app.CustomLinearLayoutManager;
import in.viato.app.R;
import in.viato.app.dummy.DummyBooks;
import in.viato.app.ui.adapters.CheckoutListAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class CheckoutActivityFragment extends Fragment {
    @Bind(R.id.checkout_list) RecyclerView checkoutListRV;

    private static AbstractActivity sActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sActivity = ((AbstractActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        ButterKnife.bind(this, view);

        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.checkout_toolbar);
        mToolbar.setTitle(R.string.title_activity_checkout);
        mToolbar.setTitleTextAppearance(getActivity(), R.style.Viato_ActionBar_Title);

        sActivity.setSupportActionBar(mToolbar);
        sActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);


        checkoutListRV.setHasFixedSize(true);

        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(getContext(), 1, true);
        checkoutListRV.setLayoutManager(linearLayoutManager);

        CheckoutListAdapter checkoutListAdapter = new CheckoutListAdapter(getContext(), DummyBooks.get(getContext()).getBooks());
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
}
