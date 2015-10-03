package in.viato.app.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.R;
import in.viato.app.model.OrderInShort;
import in.viato.app.ui.adapters.RelatedBooksRVAdapter;
import in.viato.app.ui.widgets.MyHorizantalLlm;
import in.viato.app.utils.ui.LinearLayoutManager;

public class PastOrdersFragment extends AbstractFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = PastOrdersFragment.class.getSimpleName();

    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;

    private List<OrderInShort> mOrders = new ArrayList<OrderInShort>();

    public static PastOrdersFragment newInstance(String param1, String param2) {
        PastOrdersFragment fragment = new PastOrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PastOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        for (int i = 0; i < 2; i++) {
            ArrayList<String> links = new ArrayList<String>();
            for (int j = 0; j < 5; j++) {
                links.add("http://i.imgur.com/DvpvklR.png");
            }

            OrderInShort order = new OrderInShort();
            order.setCovers(links);
            order.setOrderId(String.valueOf(i));
            order.setOrderStatus("Completed");
            order.setOrderValue(String.valueOf(i * 5));
            order.setUpdatedOn("1-Aug-15");
            mOrders.add(order);
        }
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(new OrdersListAdapter(mOrders, getContext()));
        recyclerView.setHasFixedSize(true);
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {
//
//        @Bind(R.id.order_id) TextView orderId;
//        @Bind(R.id.order_status) TextView orderStatus;
//        @Bind(R.id.last_update) TextView lastUpdated;
//        @Bind(R.id.order_value) TextView orderValue;
//        @Bind(R.id.book_covers) RecyclerView bookCoversList;

        private TextView orderId;
        private TextView orderStatus;
        private TextView lastUpdated;
        private TextView orderValue;
        private RecyclerView bookCoversList;


        public OrdersViewHolder(View itemView) {
            super(itemView);

            orderId = (TextView)itemView.findViewById(R.id.order_id);
            orderStatus = (TextView)itemView.findViewById(R.id.order_status);
            lastUpdated = (TextView)itemView.findViewById(R.id.last_update);
            orderValue = (TextView)itemView.findViewById(R.id.order_value);
            bookCoversList = (RecyclerView) itemView.findViewById(R.id.book_covers);

        }
    }

    public class OrdersListAdapter extends RecyclerView.Adapter<OrdersViewHolder> {

        private Context mContext;
        private List<OrderInShort> mOrders = new ArrayList<OrderInShort>();

        public OrdersListAdapter(List<OrderInShort> orders, Context context) {
            mOrders = orders;
            mContext = context;
        }

        @Override
        public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            mContext = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_past_orders, parent, false);
            return new OrdersViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final OrdersViewHolder holder, int position) {
            final OrderInShort order = mOrders.get(position);

            holder.orderId.setText("Order id: #" + order.getOrderId());
            holder.orderStatus.setText("Status: " + order.getOrderStatus());
            holder.orderValue.setText("Rs. " + order.getOrderValue());
            holder.lastUpdated.setText(order.getUpdatedOn());
            holder.bookCoversList.setAdapter(new RelatedBooksRVAdapter(R.layout.p_book_thumbnail_small, order.getCovers()));
            holder.bookCoversList.setLayoutManager(new MyHorizantalLlm(mContext, LinearLayoutManager.HORIZONTAL, false));
            holder.bookCoversList.setHasFixedSize(true);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFragment(R.id.frame_content, OrderDescription.newInstance(1), OrderDescription.TAG, true, OrderDescription.TAG);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mOrders.size();
        }
    }
}
