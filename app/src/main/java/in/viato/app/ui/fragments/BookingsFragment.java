package in.viato.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.http.models.response.Booking;
import in.viato.app.http.models.response.Rental;
import in.viato.app.ui.activities.HomeActivity;
import in.viato.app.ui.adapters.RelatedBooksRVAdapter;
import in.viato.app.ui.widgets.BetterViewAnimator;
import in.viato.app.ui.widgets.DividerItemDecoration;
import in.viato.app.ui.widgets.MyHorizantalLlm;
import retrofit.Response;
import rx.Subscriber;

public class BookingsFragment extends AbstractFragment {

    public static final String TAG = BookingsFragment.class.getSimpleName();

    @Bind(R.id.main_container) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.main_animator) BetterViewAnimator mAnimator;
    @Bind(R.id.progress_bar) ProgressBar mProgressBar;
    @Bind(R.id.bookings_list) RecyclerView mBookingsList;
    @Bind(R.id.no_conection) LinearLayout mNoConnection;
    @Bind(R.id.bookings_empty) LinearLayout mEmptyContainer;

    private List<Booking> mBookings;

    public static BookingsFragment newInstance() {
        return new BookingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*recyclerView = (RecyclerView) LayoutInflater
                .from(getContext())
                .inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        return recyclerView;*/
        return inflater.inflate(R.layout.fragment_bookings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViatoAPI.getBookings()
                .subscribe(new Subscriber<Response<List<Booking>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage() + " due to " + e.getCause());
                    }

                    @Override
                    public void onNext(Response<List<Booking>> listResponse) {
                        if (listResponse.isSuccess()) {
                            mBookings = listResponse.body();
                            if (mBookings.size() == 0) {
                                mAnimator.setDisplayedChildView(mEmptyContainer);
                            } else {
                                setupRentHistory();
                                mAnimator.setDisplayedChildView(mBookingsList);
                            }
                        } else {
                            try {
                                mAnimator.setDisplayedChildView(mNoConnection);
                                Snackbar.make(mCoordinatorLayout, listResponse.errorBody().string(), Snackbar.LENGTH_LONG).show();
                                Logger.e(listResponse.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBookings = null;
    }

    public void setupRentHistory() {
        mBookingsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBookingsList.addItemDecoration(new DividerItemDecoration(getContext(), null));
        mBookingsList.setHasFixedSize(true);
        mBookingsList.setAdapter(new OrdersListAdapter(mBookings));
    }

    public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {
        private List<Booking> mBookings = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat("d MMM y");
        Calendar cal = Calendar.getInstance(); // creates calendar

        public OrdersListAdapter(List<Booking> orders) {
            this.mBookings = orders;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.book_covers) RecyclerView bookCovers;
            @Bind(R.id.status) TextView status;
            @Bind(R.id.date) TextView date;
            @Bind(R.id.amount) TextView amount;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_booking_item, parent, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Booking booking = mBookings.get(position);
            final List<String> covers = new ArrayList<>();
            for (Rental rental : booking.getRentals()) {
                String cover  = rental.getItem().getThumbs().get(0);
                covers.add(cover);
            }

            cal.setTime(booking.getBooked_at()); // sets calendar time/date
            holder.date.setText(dateFormat.format(cal.getTime()));
            holder.status.setText(booking.getStatus());
            holder.amount.setText("Rs. " + ((int) booking.getPayment().getTotal_payable()) + "");

            LinearLayoutManager layoutManager = new MyHorizantalLlm(getContext(), LinearLayoutManager.HORIZONTAL, false);
            RelatedBooksRVAdapter adapter = new RelatedBooksRVAdapter(R.layout.holder_thumbnail_small, covers, true);

            holder.bookCovers.setAdapter(adapter);
            holder.bookCovers.setLayoutManager(layoutManager);
            holder.bookCovers.hasFixedSize();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFragment(R.id.frame_content, BookingDetailFragment.newInstance(booking.get_id()), BookingDetailFragment.TAG, true, BookingDetailFragment.TAG);
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
        intent.putExtra(HomeActivity.EXTRA_SETECT_TAB, HomeActivity.TAB_TRENDING);
        startActivity(intent);
        getActivity().finish();
    }
}
