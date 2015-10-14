package in.viato.app.ui.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.databinding.POrderItemSmallBinding;
import in.viato.app.dummy.Bookings;
import in.viato.app.model.Booking;
import in.viato.app.ui.activities.AbstractActivity;
import in.viato.app.ui.widgets.BetterViewAnimator;
import in.viato.app.ui.widgets.CustomAlertDialogBuilder;
import in.viato.app.ui.widgets.DividerItemDecoration;
import in.viato.app.ui.widgets.MyHorizantalLlm;
import in.viato.app.utils.AppConstants;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class OrderDescription extends AbstractFragment {
    public static final String TAG = OrderDescription.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";

    public static final int REQUEST_EXTEND = 0;
    public static final int REQUEST_RETURN = 1;

    private static final String DIALOG_MODIFY_ORDER = "dialog_modify_order";

    private int mParam1;

    private int OrderStatusInt = 0;

    private AbstractActivity mActivity;
    private FragmentManager mFragmentManager;

    @Bind(R.id.order_id) TextView orderId;
    @Bind(R.id.order_status) TextView status;
    @Bind(R.id.date) TextView date;
    @Bind(R.id.date_desc) TextView dateDesc;
    @Bind(R.id.order_value) TextView orderValue;

    @Bind(R.id.btn_cancel_order) Button cancelOrder;
    @Bind(R.id.btn_extend_order) Button extendBooking;
    @Bind(R.id.btn_return_order) Button returnOrder;
    @Bind(R.id.btn_review_order) Button reviewOrder;
//    @Bind(R.id.btn_review_book) Button reviewBook;
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

        mActivity = (AbstractActivity)getActivity();
        mFragmentManager = mActivity.getSupportFragmentManager();

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

        switch (OrderStatusInt){
            case AppConstants.ORDER_STATUS.ORDER_PLACED:
                status.setText(R.string.order_placed);
                dateDesc.setText(R.string.order_delivery_date);
                break;

            case AppConstants.ORDER_STATUS.ORDER_CANCELED:
                status.setText(R.string.order_canceled);
                dateDesc.setText(R.string.order_canceled_date);
                break;

            case AppConstants.ORDER_STATUS.ORDER_DELIVERED:
                status.setText(R.string.order_delivered);
                dateDesc.setText(R.string.order_return_date);
                break;

            case AppConstants.ORDER_STATUS.ORDER_RETURNED:
                status.setText(R.string.order_returned);
                dateDesc.setText(R.string.order_returned_date);
                break;

        }

        date.setText("12-12-15");
        orderId.setText("Order #12");

        orderItemsList.setAdapter(new OrderItemsRV(new Bookings().get()));
        orderItemsList.setLayoutManager(new MyHorizantalLlm(getContext(), LinearLayoutManager.VERTICAL, false));
        orderItemsList.addItemDecoration(new DividerItemDecoration(getContext(), null));
        orderItemsList.hasFixedSize();
    }

    public class OrderItemsRV extends RecyclerView.Adapter<OrderItemsRV.ViewHolder> {
        private List<Booking> mBookings;

        public OrderItemsRV(List<Booking> bookings) {
            super();
            this.mBookings = bookings;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final POrderItemSmallBinding mBinding;

            public ViewHolder(POrderItemSmallBinding binding) {
                super(binding.getRoot());
                this.mBinding = binding;
            }

            public void bindConnection(Booking booking ) {
                mBinding.setBooking(booking);
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
            final Booking Booking = mBookings.get(position);

            holder.mBinding.btnExtendOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmRequest(REQUEST_RETURN, "4567");
                }
            });

            holder.mBinding.btnReturnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmRequest(REQUEST_EXTEND, "2345");
                    Booking.price.set(1234567);
                    Toast.makeText(getContext(), "You are slow", Toast.LENGTH_SHORT).show();
                }
            });

            holder.bindConnection(Booking);
        }

        @Override
        public int getItemCount() {
            return mBookings.size();
        }
    }

    public void confirmRequest(int mAction, String id){
        String title = "";

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_modify_order, null);

        final BetterViewAnimator mContainer = (BetterViewAnimator) view.findViewById(R.id.container_animator);
        final RelativeLayout mConfirmReturn = (RelativeLayout) view.findViewById(R.id.confirm_return);
        final RelativeLayout mConfirmExtend = (RelativeLayout) view.findViewById(R.id.confirm_extend);
        final LinearLayout mBar = (LinearLayout) view.findViewById(R.id.progress_bar);
        final LinearLayout mSuccess = (LinearLayout) view.findViewById(R.id.success);

        switch(mAction) {
            case REQUEST_EXTEND:
                title = "Want to extend return period?";
                mContainer.setDisplayedChildView(mConfirmExtend);
                break;
            case REQUEST_RETURN:
                title = "Want to return the book?";
                mContainer.setDisplayedChildView(mConfirmExtend);
                break;
            default:
                break;
        }

        new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(title)
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        mContainer.setDisplayedChildView(mBar);
                        askServer();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();

    }

    public void askServer(){
        final ProgressDialog ringProgressDialog = ProgressDialog.show(getContext(), "Please wait ...", "Confirming availability", true);
        Observable.timer(5000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        ringProgressDialog.dismiss();
                        showSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    public void showSuccess() {
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen).show();
    }
}
