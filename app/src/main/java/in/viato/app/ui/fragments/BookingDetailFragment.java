package in.viato.app.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
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

import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.http.models.response.Booking;
import in.viato.app.http.models.response.Rental;
import in.viato.app.model.Notification;
import in.viato.app.ui.widgets.BetterViewAnimator;
import in.viato.app.ui.widgets.DividerItemDecoration;
import in.viato.app.ui.widgets.MyHorizantalLlm;
import in.viato.app.utils.AppConstants;
import retrofit.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BookingDetailFragment extends AbstractFragment {
    public static final String TAG = BookingDetailFragment.class.getSimpleName();

    private static final String ARG_ORDER_ID = "param1";

    public static final int REQUEST_EXTEND = 0;
    public static final int REQUEST_RETURN = 1;

    private String orderId;
    private Booking booking;

    private int OrderStatusInt = 0;

    @Bind(R.id.main_container) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.main_animator) BetterViewAnimator mAnimator;
    @Bind(R.id.bookings_empty) LinearLayout mEmptyContainer;
    @Bind(R.id.booking_details) NestedScrollView mBookingDetails;

    @Bind(R.id.order_id) TextView orderIdTV;
//    @Bind(R.id.order_status) TextView status;
    @Bind(R.id.date) TextView date;
    @Bind(R.id.date_desc) TextView dateDesc;
    @Bind(R.id.order_value) TextView orderValue;

    @Bind(R.id.btn_cancel_order) Button cancelOrder;
    @Bind(R.id.btn_extend_order) Button extendBooking;
    @Bind(R.id.btn_return_order) Button returnOrder;
    @Bind(R.id.btn_review_order) Button reviewOrder;
    @Bind(R.id.btns_order_delivered) LinearLayout layoutDeliveredActions;

    @Bind(R.id.order_items_rv) RecyclerView orderItemsList;

    /**
     * @param orderId Order id.
     * @return A new instance of fragment BookingDetailFragment.
     */
    public static BookingDetailFragment newInstance(String orderId) {
        BookingDetailFragment fragment = new BookingDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            orderId = getArguments().getString(ARG_ORDER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_description, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViatoAPI.getBookingDetail(orderId)
                .subscribe(new Subscriber<Response<Booking>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        Snackbar.make(mCoordinatorLayout, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(Response<Booking> bookingResponse) {
                        if (bookingResponse.isSuccess()) {
                            booking = bookingResponse.body();
                            if (booking == null) {
                                mAnimator.setDisplayedChildView(mEmptyContainer);
                            } else {
                                setDetails();
                                mAnimator.setDisplayedChildView(mBookingDetails);
                            }
                        } else {
                            Snackbar.make(mCoordinatorLayout, bookingResponse.message(), Snackbar.LENGTH_LONG).show();
                            Logger.e(bookingResponse.message());
                        }
                    }
                });
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

    public void setDetails() {
        orderIdTV.setText("Order id: " + booking.getOrder_id());

//        switch (OrderStatusInt){
//            case AppConstants.ORDER_STATUS.ORDER_PLACED:
//                status.setText(R.string.order_placed);
//                dateDesc.setText(R.string.order_delivery_date);
//                break;
//
//            case AppConstants.ORDER_STATUS.ORDER_CANCELED:
//                status.setText(R.string.order_canceled);
//                dateDesc.setText(R.string.order_canceled_date);
//                break;
//
//            case AppConstants.ORDER_STATUS.ORDER_DELIVERED:
//                status.setText(R.string.order_delivered);
//                dateDesc.setText(R.string.order_return_date);
//                break;
//
//            case AppConstants.ORDER_STATUS.ORDER_RETURNED:
//                status.setText(R.string.order_returned);
//                dateDesc.setText(R.string.order_returned_date);
//                break;
//
//        }

        date.setText("12-12-15");

        List<Rental> rentalList = booking.getRentals();
        RentalsAdapter adapter = new RentalsAdapter(rentalList);
        orderItemsList.setAdapter(adapter);
        orderItemsList.setLayoutManager(new MyHorizantalLlm(getContext(), LinearLayoutManager.VERTICAL, false));
        orderItemsList.addItemDecoration(new DividerItemDecoration(getContext(), null));
        orderItemsList.hasFixedSize();
    }

    public class RentalsAdapter extends RecyclerView.Adapter<RentalsAdapter.ViewHolder> {
        private List<Rental> mRentals;

        public RentalsAdapter(List<Rental> rentals) {
            this.mRentals = rentals;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.notification_heading) TextView mTitle;
            @Bind(R.id.notification_body) TextView mBody;
            @Bind(R.id.notification_date) TextView mDate;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_notification, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
//            final Notification notification = mNotifications.get(position);
//
//            holder.mTitle.setText(notification.getTitle());
//            holder.mBody.setText(notification.getBody());
//            holder.mDate.setText(notification.getDate());
        }

        @Override
        public int getItemCount() {
            return mRentals.size();
        }
    }
}
