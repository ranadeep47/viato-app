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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.segment.analytics.Analytics;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.http.models.Address;
import in.viato.app.http.models.request.RentalBody;
import in.viato.app.http.models.response.Booking;
import in.viato.app.http.models.response.Rental;
import in.viato.app.ui.widgets.BetterViewAnimator;
import in.viato.app.ui.widgets.DividerItemDecoration;
import in.viato.app.ui.widgets.MyHorizantalLlm;
import in.viato.app.ui.widgets.MyVerticalLlm;
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

    @Bind(R.id.main_container) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.main_animator) BetterViewAnimator mAnimator;
    @Bind(R.id.bookings_empty) LinearLayout mEmptyContainer;
    @Bind(R.id.booking_details) NestedScrollView mBookingDetails;
    @Bind(R.id.progress_bar) ProgressBar mProgressBar;

    @Bind(R.id.order_id) TextView mOrderId;
    @Bind(R.id.placed_on) TextView mPlacedOn;
    @Bind(R.id.order_items_rv) RecyclerView mOrderItemsList;
    @Bind(R.id.order_value) TextView mOrderValue;
    @Bind(R.id.payment_status) TextView mPaymentStatus;
    @Bind(R.id.label) TextView mAddressLabel;
    @Bind(R.id.flat) TextView mAddressFlat;
    @Bind(R.id.street) TextView mAddressStreet;
    @Bind(R.id.locality) TextView mAddressLocality;
//    @Bind(R.id.btn_cancel_order) Button mCancelOrder;
//    @Bind(R.id.btn_review_order) Button mReviewOrder;


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
        getBookingDetail();
    }

    @Override
    public void onResume() {
        super.onResume();

//        mViatoApp.trackScreenView(getString(R.string.booking_detail_fragment));
        Analytics.with(getContext()).screen("screen", getString(R.string.booking_detail_fragment));
    }

    public void getBookingDetail() {
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

    public void setDetails() {
        mOrderId.setText("#" + booking.getOrder_id());

        DateFormat dateFormat = new SimpleDateFormat("d MMM y");
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(booking.getBooked_at()); // sets calendar time/date
        mPlacedOn.setText(dateFormat.format(cal.getTime()));

        List<Rental> rentalList = booking.getRentals();

        RentalsAdapter adapter = new RentalsAdapter(rentalList);
        mOrderItemsList.setAdapter(adapter);
        mOrderItemsList.setLayoutManager(new MyVerticalLlm(getContext(), LinearLayoutManager.VERTICAL, false));
        mOrderItemsList.addItemDecoration(new DividerItemDecoration(getContext(), null));
        mOrderItemsList.hasFixedSize();

        mOrderValue.setText("Rs. " + getOrderTotal());

        setupDeliveryAddress();
    }

    public class RentalsAdapter extends RecyclerView.Adapter<RentalsAdapter.ViewHolder> {
        private List<Rental> mRentals;

        DateFormat dateFormat = new SimpleDateFormat("d MMM y");
        Calendar cal = Calendar.getInstance(); // creates calendar

        public RentalsAdapter(List<Rental> rentals) {
            this.mRentals = rentals;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.cover) ImageView mCover;
            @Bind(R.id.title) TextView mTitle;
            @Bind(R.id.sub_title) TextView mSubtitle;
            @Bind(R.id.price) TextView mPrice;
            @Bind(R.id.btn_extend) Button mBtnExtend;
            @Bind(R.id.btn_return) Button mBtnReturn;
//            @Bind(R.id.not_delivered) LinearLayout mNotDelivered;
            @Bind(R.id.delivery_estimate) TextView mDeliveryEstimate;
//            @Bind(R.id.delivered) LinearLayout mDelivered;
            @Bind(R.id.delivered_date) TextView mDeliveredDate;
//            @Bind(R.id.return_) LinearLayout mReturn;
            @Bind(R.id.return_date) TextView mReturnDate;
            @Bind(R.id.price_extend) TextView mPriceExtended;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_order_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Rental rental = mRentals.get(position);

            Picasso.with(getContext())
                    .load(rental.getItem().getThumbs().get(0))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.mCover);

            holder.mTitle.setText(rental.getItem().getTitle());
            holder.mSubtitle.setText(rental.getItem().getAuthors());
            holder.mPrice.setText("Rs. " + (int)rental.getItem().getPricing().getRent() + "");
            holder.mBtnExtend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logger.d(rental.get_id());
                    new android.app.AlertDialog.Builder(getContext())
                            .setTitle("Extend rental?")
                            .setMessage("You can extend the rental period for x days for Rs. y")
                            .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    extendRental(rental.get_id());
                                }
                            })
                            .setNegativeButton(R.string.disagree, null)
                            .create()
                            .show();
                }
            });

            holder.mBtnReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new android.app.AlertDialog.Builder(getContext())
                            .setTitle("Return rental?")
                            .setMessage("return  message")
                            .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    returnRental(rental.get_id());
                                }
                            })
                            .setNegativeButton(R.string.disagree, null)
                            .create()
                            .show();
                }
            });


            if (rental.getExpected_delivery_at() != null) {
                cal.setTime(rental.getExpected_delivery_at());
                Logger.d("confirmed. delivery on " + dateFormat.format(cal.getTime()));
                holder.mDeliveryEstimate.setVisibility(View.VISIBLE);
                holder.mDeliveryEstimate.setText("Estimated Delivery: " + dateFormat.format(cal.getTime()));
            } else {
                Logger.d("not confirmed");
//                holder.mDeliveryEstimate.setVisibility(View.GONE);
            }

            if(rental.getDelivered_at() != null) {
                Logger.d("delivery on %s", rental.getDelivered_at().toString());
                holder.mDeliveryEstimate.setVisibility(View.GONE);
                holder.mBtnExtend.setVisibility(View.VISIBLE);
                holder.mBtnReturn.setVisibility(View.VISIBLE);
                holder.mDeliveredDate.setVisibility(View.VISIBLE);
                holder.mReturnDate.setVisibility(View.VISIBLE);

                cal.setTime(rental.getDelivered_at());
                holder.mDeliveredDate.setText("Delivered on: " + dateFormat.format(cal.getTime()));
                cal.setTime(rental.getExpires_at());
                holder.mReturnDate.setText("Return on: " + dateFormat.format(cal.getTime()));
            } else {
                Logger.d("not delivered");
//                holder.mBtnExtend.setVisibility(View.GONE);
//                holder.mReturnDate.setVisibility(View.GONE);
//                holder.mDeliveredDate.setVisibility(View.GONE);
//                holder.mReturn.setVisibility(View.GONE);
            }

            if (rental.getExtended_at() != null) {
                Logger.d("extended on %s", rental.getExtended_at().toString());

                cal.setTime(rental.getExtended_at());
                holder.mPriceExtended.setText("+ Rs. " + (int) rental.getExtension_payment().getTotal_payable());
                holder.mBtnExtend.setVisibility(View.GONE);
            } else {
                Logger.d("not extended");
//                holder.mBtnExtend.setVisibility(View.VISIBLE);
            }

            if (rental.getPickup_requested_at() != null) {
                Logger.d("pickup requested on %s", rental.getPickup_requested_at().toString());
                holder.mBtnExtend.setVisibility(View.GONE);
                holder.mBtnReturn.setVisibility(View.GONE);
            } else {
                Logger.d("pickup not requested");
            }

            if(rental.getExpires_at().after(new Date())) {
                holder.mBtnReturn.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return mRentals.size();
        }
    }

    public int getOrderTotal() {
        List<Rental> rentalList = booking.getRentals();
        int total = (int)booking.getBooking_payment().getTotal_payable();
        for (Rental rental : rentalList) {
            if (rental.getExtension_payment() != null) {
                total =  total + (int)rental.getExtension_payment().getTotal_payable();
            }
        }
        return total;
    }

    public void setupDeliveryAddress() {
        Address deliveryAddress = booking.getDelivery_address();

        String input = deliveryAddress.getLabel();
        mAddressLabel.setText("(" + Character.toUpperCase(input.charAt(0)) + input.substring(1) + ")");
        mAddressFlat.setText(deliveryAddress.getFlat());
        mAddressStreet.setText(deliveryAddress.getStreet());
        mAddressLocality.setText(deliveryAddress.getLocality().getName());
    }

    public void extendRental(String id) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(getContext(), "Please wait ...", "Extending your rental period", true);
        ringProgressDialog.setCancelable(false);

        mViatoAPI.extendRental(new RentalBody(id))
                .subscribe(new Subscriber<Response<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        ringProgressDialog.dismiss();
                        if (stringResponse.isSuccess()) {
                            Snackbar.make(mCoordinatorLayout, stringResponse.message(), Snackbar.LENGTH_SHORT).show();
                            mAnimator.setDisplayedChildView(mProgressBar);
                            getBookingDetail();
                        } else {
                            Snackbar.make(mCoordinatorLayout, stringResponse.message(), Snackbar.LENGTH_SHORT).show();
                            Logger.e(stringResponse.message());
                        }
                    }
                });
    }

    public void returnRental(String id) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(getContext(), "Please wait ...", "Requesting return", true);
        ringProgressDialog.setCancelable(false);

        mViatoAPI.returnRental(new RentalBody(id))
                .subscribe(new Subscriber<Response<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        ringProgressDialog.dismiss();
                        if (stringResponse.isSuccess()) {
                            Snackbar.make(mCoordinatorLayout, stringResponse.message(), Snackbar.LENGTH_SHORT).show();
                            mAnimator.setDisplayedChildView(mProgressBar);
                            getBookingDetail();
                        } else {
                            Snackbar.make(mCoordinatorLayout, stringResponse.message(), Snackbar.LENGTH_SHORT).show();
                            Logger.e(stringResponse.message());
                        }
                    }
                });
    }

//    public void confirmRequest(int mAction, String id){
//        String title = "";
//
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_modify_order, null);
//
//        final BetterViewAnimator mContainer = (BetterViewAnimator) view.findViewById(R.id.container_animator);
//        final RelativeLayout mConfirmReturn = (RelativeLayout) view.findViewById(R.id.confirm_return);
//        final RelativeLayout mConfirmExtend = (RelativeLayout) view.findViewById(R.id.confirm_extend);
//        final LinearLayout mBar = (LinearLayout) view.findViewById(R.id.progress_bar);
//        final LinearLayout mSuccess = (LinearLayout) view.findViewById(R.id.success);
//
//        switch(mAction) {
//            case REQUEST_EXTEND:
//                title = "Want to extend return period?";
//                mContainer.setDisplayedChildView(mConfirmExtend);
//                break;
//            case REQUEST_RETURN:
//                title = "Want to return the book?";
//                mContainer.setDisplayedChildView(mConfirmExtend);
//                break;
//            default:
//                break;
//        }
//
//        new AlertDialog.Builder(getActivity())
//                .setView(view)
//                .setTitle(title)
//                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(final DialogInterface dialog, int which) {
//                        mContainer.setDisplayedChildView(mBar);
//                        askServer();
//                    }
//                })
//                .setNegativeButton(R.string.cancel, null)
//                .create()
//                .show();
//
//    }
//
//    public void askServer(){
//        final ProgressDialog ringProgressDialog = ProgressDialog.show(getContext(), "Please wait ...", "Confirming availability", true);
//        Observable.timer(5000, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber() {
//                    @Override
//                    public void onCompleted() {
//                        ringProgressDialog.dismiss();
//                        showSuccess();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//
//                    }
//                });
//    }
//
//    public void showSuccess() {
//        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
//        new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen).show();
//    }
}
