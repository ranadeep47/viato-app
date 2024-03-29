package in.viato.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.http.models.response.Booking;
import in.viato.app.http.models.response.Rental;
import in.viato.app.ui.activities.HomeActivity;
import in.viato.app.ui.adapters.TitleAdapter;
import in.viato.app.ui.widgets.BetterViewAnimator;
import in.viato.app.ui.widgets.MyVerticalLlm;
import in.viato.app.utils.MiscUtils;
import in.viato.app.utils.RxUtils;
import retrofit.HttpException;
import retrofit.Response;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class BookingsFragment extends AbstractFragment {

    public static final String TAG = BookingsFragment.class.getSimpleName();

    public static final String ARG_BOOKING_ID = "bookingId";

    @Bind(R.id.main_container) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.main_animator) BetterViewAnimator mAnimator;
    @Bind(R.id.progress_bar) ProgressBar mProgressBar;
    @Bind(R.id.bookings_list) RecyclerView mBookingsList;
    @Bind(R.id.no_connection) LinearLayout mNoConnection;
    @Bind(R.id.bookings_empty) LinearLayout mEmptyContainer;

    private List<Booking> mBookings;

    private CompositeSubscription mSubs = new CompositeSubscription();

    public static BookingsFragment newInstance() {
        return new BookingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent =  getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if (intent != null) {
            if(intent.hasExtra(ARG_BOOKING_ID)) {
                String id = intent.getStringExtra(ARG_BOOKING_ID);
                loadFragment(R.id.frame_content, BookingDetailFragment.newInstance(id),
                        BookingDetailFragment.TAG, true, BookingDetailFragment.TAG);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookings, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mViatoApp.sendScreenView(getString(R.string.booking_list_fragment));
//        Analytics.with(getContext()).screen("screen", getString(R.string.booking_list_fragment));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSubs.add(mViatoAPI.getBookings()
                .subscribe(new Subscriber<Response<List<Booking>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            handleNetworkException((HttpException) e);
                        }
                    }

                    @Override
                    public void onNext(Response<List<Booking>> listResponse) {
                        if (listResponse.isSuccess()) {
                            mBookings = listResponse.body();
                            if (mBookings.isEmpty()) {
                                mAnimator.setDisplayedChildView(mEmptyContainer);
                            } else {
                                setupRentHistory();
                                mAnimator.setDisplayedChildView(mBookingsList);
                            }
                        } else {
                            mAnimator.setDisplayedChildView(mNoConnection);
                            try {
                                Snackbar.make(mCoordinatorLayout, listResponse.errorBody().string(), Snackbar.LENGTH_LONG).show();
                                Logger.e(listResponse.errorBody().string());
                            } catch (IOException e) {
                                Logger.e(e, "error");
                            }
                        }
                    }
                }));
    }

    @Override
    public void onDestroy() {
        RxUtils.unsubscribeIfNotNull(mSubs);
        super.onDestroy();
    }

    public void setupRentHistory() {
        mBookingsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBookingsList.setHasFixedSize(true);
        mBookingsList.setAdapter(new OrdersListAdapter(mBookings));
    }

    public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {
        private List<Booking> mBookings = new ArrayList<>();

        public OrdersListAdapter(List<Booking> orders) {
            this.mBookings = orders;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.booking_id) TextView orderId;
            @Bind(R.id.date_placed) TextView datePlaced;
            @Bind(R.id.books) RecyclerView books;
            @Bind(R.id.status) TextView status;
            @Bind(R.id.view_details) Button viewDetails;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.holder_booking_item, parent, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Booking booking = mBookings.get(position);
            final List<Rental> rentals =  booking.getRentals();

            holder.orderId.setText(booking.getOrder_id());
            holder.datePlaced.setText(MiscUtils.getFormattedDate(booking.getBooked_at()));
            holder.status.setText(booking.getStatus());
            holder.books.setLayoutManager(new MyVerticalLlm(getContext(), LinearLayoutManager.VERTICAL, false));
            holder.books.setAdapter(new TitleAdapter(rentals));
            holder.viewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                loadFragment(R.id.frame_content, BookingDetailFragment.newInstance(booking.get_id()),
                        BookingDetailFragment.TAG, true, BookingDetailFragment.TAG);
                    ViatoApplication.get().sendEvent("bookings","view_details", "");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mBookings.size();
        }
    }

    @OnClick(R.id.btn_empty_action)
    public void showTrending() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.putExtra(HomeActivity.EXTRA_SELECT_TAB, HomeActivity.TAB_TRENDING);
        startActivity(intent);
        getActivity().finish();
    }
}
