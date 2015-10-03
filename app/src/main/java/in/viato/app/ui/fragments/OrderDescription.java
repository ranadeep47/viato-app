package in.viato.app.ui.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import in.viato.app.BR;
import in.viato.app.R;
import in.viato.app.databinding.POrderItemSmallBinding;
import in.viato.app.dummy.DummyBooks;
import in.viato.app.model.Book;
import in.viato.app.ui.widgets.DividerItemDecoration;
import in.viato.app.ui.widgets.MyHorizantalLlm;
import in.viato.app.utils.ui.LinearLayoutManager;


public class OrderDescription extends AbstractFragment {
    public static final String TAG = OrderDescription.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int mParam1;

    private Boolean order_placed =  true;
    private Boolean order_delivered = false;
    private Boolean order_canceled = false;
    private Boolean order_extended = false;
    private Boolean order_returned = false;

    @Bind(R.id.order_id) TextView orderId;
    @Bind(R.id.order_status) TextView status;
    @Bind(R.id.date) TextView date;
    @Bind(R.id.date_desc) TextView dateDesc;
    @Bind(R.id.order_value) TextView orderValue;

    @Bind(R.id.btn_cancel_order) Button cancelOrder;
    @Bind(R.id.btn_extend_order) Button extendOrder;
    @Bind(R.id.btn_return_order) Button returnOrder;
    @Bind(R.id.btn_review_order) Button reviewOrder;
    @Bind(R.id.btns_order_delivered) LinearLayout layoutDeliveredActions; //layout contains extend and return buttons

    @Bind(R.id.order_items_rv) RecyclerView orderItemsList;

    /**
     * @param orderId Order id.
     * @return A new instance of fragment OrderDescription.
     */
    public static OrderDescription newInstance(int orderId) {
        OrderDescription fragment = new OrderDescription();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    public OrderDescription() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_description, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(order_placed) {
            status.setText(R.string.order_placed);
            dateDesc.setText(R.string.order_delivery_date);
        } else if(order_canceled) {
            status.setText(R.string.order_canceled);
            dateDesc.setText(R.string.order_canceled_date);
        } else if(order_delivered) {
            status.setText(R.string.order_delivered);
            dateDesc.setText(R.string.order_return_date);
        } else if (order_returned) {
            status.setText(R.string.order_returned);
            dateDesc.setText(R.string.order_returned_date);
        }

        date.setText("12-12-15");
        orderId.setText("Order #12");
        orderItemsList.setAdapter(new OrderItemsRV(DummyBooks.get(getContext()).getBooks()));
        orderItemsList.setLayoutManager(new MyHorizantalLlm(getContext(), LinearLayoutManager.VERTICAL, false));
        orderItemsList.addItemDecoration(new DividerItemDecoration(getContext(), null));
        orderItemsList.hasFixedSize();
    }

    public class OrderItemsRV extends RecyclerView.Adapter<OrderItemsRV.ViewHolder> {
        private List<Book> mBooks;

        public OrderItemsRV(List<Book> books) {
            super();
            this.mBooks = books;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final POrderItemSmallBinding mBinding;

            public ViewHolder(POrderItemSmallBinding binding) {
                super(binding.getRoot());

                this.mBinding = binding;
            }

            public void bindConnection(Book book ) {
                mBinding.setBook(book);
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            POrderItemSmallBinding binding = DataBindingUtil
                    .inflate(LayoutInflater.from(parent.getContext()),
                            R.layout.p_order_item_small,
                            parent,
                            false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Book book = mBooks.get(position);
            holder.bindConnection(book);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }
    }
}
