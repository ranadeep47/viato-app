package in.viato.app.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.http.models.Address;
import in.viato.app.http.models.request.BookingBody;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.http.models.response.Cart;
import in.viato.app.ui.activities.AddressListActivity;
import in.viato.app.ui.activities.HomeActivity;
import in.viato.app.ui.activities.SuccessActivity;
import in.viato.app.ui.widgets.BetterViewAnimator;
import in.viato.app.ui.widgets.DividerItemDecoration;
import in.viato.app.ui.widgets.MyVerticalLlm;
import rx.Subscriber;

/**
 * A placeholder fragment containing a simple view.
 */
public class CheckoutFragment extends AbstractFragment {

    public static final String TAG = CheckoutFragment.class.getSimpleName();

    private static final int REQUEST_ADDRESS = 0;

    private int mSelectedAddress = -1;
    private List<Address> addresses;
    private List<BookItem> items;
    private int total = 0;

    private String deliveryDate;
    private String returnDate;

    @Bind(R.id.checkout_list) RecyclerView checkoutListRV;
    @Bind(R.id.lv_addressList) LinearLayout mAddressWrapper;
    @Bind(R.id.checkout_animator) BetterViewAnimator mViewContainer;
    @Bind(R.id.main_container) CoordinatorLayout mLayout;
    @Bind(R.id.total) TextView totalTV;
    @Bind(R.id.delivery_date) TextView deliveryDateTV;
    @Bind(R.id.return_date) TextView returnDateTV;

    @Bind(R.id.tv_address_flat) TextView mAddressFlat;
    @Bind(R.id.tv_address_street) TextView mAddressStreet;
    @Bind(R.id.tv_address_locality) TextView mAddressLocality;
    @Bind(R.id.tv_address_label) TextView mAddressLabel;

    public static CheckoutFragment newInstance() {
        return new CheckoutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViatoAPI.getCart()
                .subscribe(new Subscriber<Cart>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("Failed to fetch cart " + getString(R.string.due_to) + e.getMessage());
                    }

                    @Override
                    public void onNext(Cart cart) {
                        items = cart.getCart();
                        addresses = cart.getAddresses();
                        setupBooksList();
                        setupAddress();
                        setTotal();
                        setDates();
                        mViewContainer.setDisplayedChildId(R.id.checkout_container);
                    }
                });
    }

    @OnClick(R.id.card_view_address)
    public void onAddressClicked() {
        Intent intent = new Intent(getActivity(), AddressListActivity.class);
        intent.putExtra(AddressListActivity.ARG_ADDRESS_ID, mSelectedAddress);
        startActivityForResult(intent, REQUEST_ADDRESS);
    }

    @OnClick(R.id.place_order)
    public void placeOrder(final View v) {
        mViatoAPI.placeOrder(new BookingBody(addresses.get(mSelectedAddress).getId()))
        .subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onNext(String s) {
                Intent intent = new Intent(getContext(), SuccessActivity.class);
                intent.putExtra(SuccessActivity.ARG_ORDER_ID, s);
                intent.putExtra(SuccessActivity.ARG_DELIVERY_DATE, deliveryDate);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @OnClick(R.id.btn_empty_action)
    public void goToTrending() {
        startActivity(new Intent(getContext(), HomeActivity.class));
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_ADDRESS) {
            if(data != null){
                if (data.getParcelableExtra(AddressListActivity.ARG_ADDRESS) == null) {
                    Logger.e("No address found");
                    return;
                } else {
                    Logger.e("Address found");
                }

                Address address = data.getParcelableExtra(AddressListActivity.ARG_ADDRESS);
                mSelectedAddress = data.getIntExtra(AddressListActivity.ARG_ADDRESS_INDEX, mSelectedAddress);
                Logger.e(address.getFlat());
                String flat = address.getFlat();
                String street = address.getStreet();
                String label = address.getLabel();
                String locality = address.getLocality().getName();

                mAddressFlat.setText(flat);
                mAddressStreet.setText(street);
                mAddressLocality.setText(locality);
                mAddressLabel.setText(label);
            } else {
                Logger.e("empty data");
            }
        }
    }
    
    public void setupBooksList() {
        checkoutListRV.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new MyVerticalLlm(getContext(), LinearLayoutManager.VERTICAL, false);
        checkoutListRV.setLayoutManager(linearLayoutManager);

        final CheckoutListAdapter adapter = new CheckoutListAdapter(items);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
//                super.onChanged();

                Logger.d("" + adapter.getItemCount());

                if (adapter.getItemCount() == 0) {
                    totalTV.setText(total + "");
                    mViewContainer.setDisplayedChildId(R.id.checkout_empty);
                }
                setTotal();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                setTotal();
                if (positionStart == 0 && itemCount == 1) {
                    mViewContainer.setDisplayedChildId(R.id.checkout_container);
                }
            }
        });

        checkoutListRV.addItemDecoration(new DividerItemDecoration(getContext(), null));
        checkoutListRV.setAdapter(adapter);
    }
    
    public void setupAddress() {
        View addAddress = mAddressWrapper.findViewById(R.id.add_address);
        View alreadyAddress = mAddressWrapper.findViewById(R.id.already_address);

        if(addresses.size() == 0) {
            alreadyAddress.setVisibility(View.GONE);
            addAddress.setVisibility(View.VISIBLE);
        } else {
            Address address = null;

            for (int i = 0; i < addresses.size(); i++) {
                if (addresses.get(i).getIs_default()){
                    address = addresses.get(i);
                    mSelectedAddress = i;
                }
            }

            if(address == null) {
                alreadyAddress.setVisibility(View.GONE);
                addAddress.setVisibility(View.VISIBLE);
                return;
            }

            mAddressLabel.setText(address.getLabel());
            mAddressFlat.setText(address.getFlat());
            mAddressStreet.setText(address.getStreet());
            mAddressLocality.setText(address.getLocality().getName());

            addAddress.setVisibility(View.GONE);
            alreadyAddress.setVisibility(View.VISIBLE);
        }
    }

    public class CheckoutListAdapter extends RecyclerView.Adapter<CheckoutListAdapter.ViewHolder> {
        private List<BookItem> sBookList;
        private Context mContext;

        public CheckoutListAdapter(List<BookItem> bookList) {
            super();
            this.sBookList = bookList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.cover) ImageView mCover;
            @Bind(R.id.title) TextView mTitle;
            @Bind(R.id.author) TextView mAuthor;
            @Bind(R.id.price) TextView mPrice;
            @Bind(R.id.remove) ImageButton mRemove;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(mContext).inflate(R.layout.holder_checkout_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            BookItem book = sBookList.get(position);
            holder.mTitle.setText(book.getTitle());
            holder.mAuthor.setText(book.getAuthors());
            holder.mPrice.setText("Rs. " + ((int)book.getPricing().getRent()));
            Picasso.with(mContext)
                    .load(book.getCover())
                    .into(holder.mCover);
            holder.mRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final BookItem removed = sBookList.get(position);
                    mViatoAPI.removeFromCart(removed.get_id())
                            .subscribe(new Subscriber<String>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Logger.d(e.getMessage());
                                }

                                @Override
                                public void onNext(String s) {
                                    sBookList.remove(position);
                                    notifyDataSetChanged();
                                    Snackbar.make(v, "Item removed", Snackbar.LENGTH_LONG).show();
                                }
                            });
                }
            });
        }

        @Override
        public int getItemCount() {
            return sBookList.size();
        }
    }

    public void setTotal() {
        int total = 0;
        for (BookItem item : items){
            total += (int) item.getPricing().getRent();
        }
        totalTV.setText("Rs. " + total);
    }

    public void setDates() {
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date

        cal.add(Calendar.HOUR_OF_DAY, 48); // adds 48 hours
        deliveryDate = dateFormat.format(cal.getTime());
        deliveryDateTV.setText(deliveryDate);

        int period = items.get(0).getPricing().getPeriod();
        cal.add(Calendar.DAY_OF_YEAR, period); // adds 48 hours
        returnDate = dateFormat.format(cal.getTime());
        returnDateTV.setText(returnDate);
    }
}