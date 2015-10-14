package in.viato.app.ui.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.R;
import in.viato.app.databinding.FragmentPastOrdersBinding;
import in.viato.app.dummy.Orders;
import in.viato.app.model.OrderInShort;
import in.viato.app.ui.activities.AbstractActivity;
import in.viato.app.ui.widgets.DividerItemDecoration;

public class PastOrdersFragment extends AbstractFragment {

    public static final String TAG = PastOrdersFragment.class.getSimpleName();

    RecyclerView recyclerView;

    private List<OrderInShort> mOrders = new ArrayList<>();

    public static PastOrdersFragment newInstance() {
        return new PastOrdersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrders = new Orders().get();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recyclerView =  new RecyclerView(getActivity().getApplicationContext());
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), null));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new OrdersListAdapter(mOrders));
    }

    public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {
        private List<OrderInShort> mOrders = new ArrayList<>();

        public OrdersListAdapter(List<OrderInShort> orders) {
            mOrders = orders;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final FragmentPastOrdersBinding mBinding;

            public ViewHolder(FragmentPastOrdersBinding binding) {
                super(binding.getRoot());
                this.mBinding = binding;
            }

            public void bindConnection(OrderInShort order ) {
                mBinding.setOrder(order);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FragmentPastOrdersBinding binding = DataBindingUtil
                    .inflate(LayoutInflater.from(parent.getContext()),
                            R.layout.fragment_past_orders,
                            parent,
                            false);
            View v = binding.getRoot();
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFragment(R.id.frame_content, OrderDescription.newInstance(1), OrderDescription.TAG, true, OrderDescription.TAG);
                }
            });
            return new ViewHolder(binding);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final OrderInShort order = mOrders.get(position);
            holder.bindConnection(order);
        }

        @Override
        public int getItemCount() {
            return mOrders.size();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOrders = null;
    }
}
