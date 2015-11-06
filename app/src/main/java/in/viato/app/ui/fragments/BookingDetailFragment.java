package in.viato.app.ui.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.http.models.Address;
import in.viato.app.http.models.request.RentalBody;
import in.viato.app.http.models.response.Booking;
import in.viato.app.http.models.response.Rental;
import in.viato.app.ui.widgets.BetterViewAnimator;
import in.viato.app.ui.widgets.DividerItemDecoration;
import in.viato.app.ui.widgets.MyVerticalLlm;
import retrofit.Response;
import rx.Subscriber;

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
    @Bind(R.id.order_status) TextView mOrderStatus;
    @Bind(R.id.order_items_rv) RecyclerView mOrderItemsList;
    @Bind(R.id.order_value) TextView mOrderValue;
    @Bind(R.id.payment_status) TextView mPaymentStatus;
    @Bind(R.id.label) TextView mAddressLabel;
    @Bind(R.id.flat) TextView mAddressFlat;
    @Bind(R.id.street) TextView mAddressStreet;
    @Bind(R.id.locality) TextView mAddressLocality;


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
        return inflater.inflate(R.layout.fragment_booking_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBookingDetail();
    }

    @Override
    public void onResume() {
        super.onResume();

        mViatoApp.trackScreenView(getString(R.string.booking_detail_fragment));
//        Analytics.with(getContext()).screen("screen", getString(R.string.booking_detail_fragment));
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
                            try {
                                Snackbar.make(mCoordinatorLayout, bookingResponse.errorBody().string(), Snackbar.LENGTH_LONG).show();
                                Logger.e(bookingResponse.errorBody().string());
                            } catch (IOException e) {
                                Logger.e(e, "error");
                            }
                        }
                    }
                });
    }

    public void setDetails() {
        mOrderId.setText(booking.getOrder_id());

        DateFormat dateFormat = new SimpleDateFormat("d MMM y");
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(booking.getBooked_at()); // sets calendar time/date
        mPlacedOn.setText(dateFormat.format(cal.getTime()));
        mOrderStatus.setText(booking.getStatus());

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
            @Bind(R.id.status) TextView mStatus;
            @Bind(R.id.price) TextView mPrice;
            @Bind(R.id.btn_extend) Button mBtnExtend;
            @Bind(R.id.btn_return) Button mBtnReturn;
            @Bind(R.id.delivered_date) TextView mDeliveredDate;
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
            holder.mStatus.setText(rental.getStatus());
            holder.mPrice.setText("Rs. " + (int) rental.getItem().getPricing().getRent() + "");
            holder.mBtnExtend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logger.d(rental.get_id());

                    int period = rental.getItem().getPricing().getPeriod();
                    int price = (int) rental.getItem().getPricing().getRent();

                    new android.app.AlertDialog.Builder(getContext())
                            .setTitle("Extend")
                            .setMessage("You can extend the rental tenure for " + period + " days at Rs. " + price +".")
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
                            .setTitle("Return")
                            .setMessage("Pickup for this book will be initiated. You can order another book now.")
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
                holder.mDeliveredDate.setText("Estimated Delivery: " + dateFormat.format(cal.getTime()));
                holder.mDeliveredDate.setVisibility(View.VISIBLE);
            }

            if(rental.getDelivered_at() != null) {
                holder.mBtnExtend.setVisibility(View.VISIBLE);
                holder.mBtnReturn.setVisibility(View.VISIBLE);

                cal.setTime(rental.getDelivered_at());
                holder.mDeliveredDate.setText("Delivered on: " + dateFormat.format(cal.getTime()));
                cal.setTime(rental.getExpires_at());
                holder.mReturnDate.setText("Return on: " + dateFormat.format(cal.getTime()));

                holder.mDeliveredDate.setVisibility(View.VISIBLE);
                holder.mReturnDate.setVisibility(View.VISIBLE);
            }

            if (rental.getExtended_at() != null) {
                cal.setTime(rental.getExtended_at());
                holder.mPriceExtended.setText("+ Rs. " + (int) rental.getExtension_payment().getTotal_payable());
                holder.mDeliveredDate.setText("Extended on: " + dateFormat.format(cal.getTime()));

                holder.mBtnExtend.setVisibility(View.GONE);
            }

            if (rental.getPickup_requested_at() != null || rental.getExpires_at().before(new Date()) || rental.getStatus() == "CANCELLED") {
                holder.mBtnReturn.setVisibility(View.GONE);
                holder.mBtnExtend.setVisibility(View.GONE);
                holder.mDeliveredDate.setVisibility(View.GONE);
                holder.mReturnDate.setVisibility(View.GONE);
            }

            if (rental.getPickup_done_at() != null) {
                cal.setTime(rental.getPickup_done_at());
                holder.mReturnDate.setText("Returned on: " + dateFormat.format(cal.getTime()));
                holder.mReturnDate.setVisibility(View.VISIBLE);
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

        mAddressLabel.setText(deliveryAddress.getLabel());
        mAddressFlat.setText(deliveryAddress.getFlat());
        mAddressStreet.setText(deliveryAddress.getStreet());
        mAddressLocality.setText(deliveryAddress.getLocality().getName());
    }

    public void extendRental(String id) {
        final ProgressDialog progressDialog = showProgressDialog("Extending rental period...");

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
                        progressDialog.dismiss();
                        if (stringResponse.isSuccess()) {
                            Snackbar.make(mCoordinatorLayout, stringResponse.message(), Snackbar.LENGTH_SHORT).show();
                            mAnimator.setDisplayedChildView(mProgressBar);
                            getBookingDetail();
                        } else {
                            try {
                                Snackbar.make(mCoordinatorLayout, stringResponse.errorBody().string(), Snackbar.LENGTH_SHORT).show();
                                Logger.e(stringResponse.errorBody().string());
                            } catch (IOException e) {
                                Logger.e(e, "error");
                            }
                        }
                    }
                });
    }

    public void returnRental(String id) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Requesting pickup...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

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
                        progressDialog.dismiss();
                        if (stringResponse.isSuccess()) {
                            Snackbar.make(mCoordinatorLayout, stringResponse.message(), Snackbar.LENGTH_SHORT).show();
                            mAnimator.setDisplayedChildView(mProgressBar);
                            getBookingDetail();
                        } else {
                            try {
                                Snackbar.make(mCoordinatorLayout, stringResponse.errorBody().string(), Snackbar.LENGTH_SHORT).show();
                                Logger.e(stringResponse.errorBody().string());
                            } catch (IOException e) {
                                Logger.e(e, "error");
                            }
                        }
                    }
                });
    }

    public void getTransactions() {

    }
}
