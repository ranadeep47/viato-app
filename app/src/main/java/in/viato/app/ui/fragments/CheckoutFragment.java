package in.viato.app.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
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
import in.viato.app.utils.MiscUtils;
import in.viato.app.utils.RxUtils;
import retrofit.Response;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

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

    private CoordinatorLayout grandParent;

    @Bind(R.id.checkout_list) RecyclerView checkoutListRV;
    @Bind(R.id.lv_addressList) LinearLayout mAddressWrapper;
    @Bind(R.id.checkout_animator) BetterViewAnimator mViewContainer;
    @Bind(R.id.main_container) CoordinatorLayout mLayout;
    @Bind(R.id.total) TextView totalTV;
    @Bind(R.id.delivery_date) TextView deliveryDateTV;
    @Bind(R.id.return_date) TextView returnDateTV;
    @Bind(R.id.return_period) TextView returnPeriod;
    @Bind(R.id.add_address) View addAddress;
    @Bind(R.id.already_address) View alreadyAddress;

    @Bind(R.id.tv_address_flat) TextView mAddressFlat;
    @Bind(R.id.tv_address_street) TextView mAddressStreet;
    @Bind(R.id.tv_address_locality) TextView mAddressLocality;
    @Bind(R.id.tv_address_label) TextView mAddressLabel;

    private CompositeSubscription mSubs = new CompositeSubscription();

    public static CheckoutFragment newInstance() {
        return new CheckoutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        grandParent = (CoordinatorLayout) ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);

        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchCart();
    }

    @Override
    public void onResume() {
        super.onResume();

        ViatoApplication.get().sendScreenView(getString(R.string.checkout_fragment));
//        Analytics.with(getContext()).screen("screen", getString(R.string.checkout_fragment));
    }

    @Override
    public void onDestroy() {
        RxUtils.unsubscribeIfNotNull(mSubs);
        super.onDestroy();
    }

    @OnClick(R.id.card_view_address)
    public void onAddressClicked() {
        Intent intent = new Intent(getActivity(), AddressListActivity.class);
        intent.putExtra(AddressListActivity.ARG_ADDRESS_ID, mSelectedAddress);
        intent.putExtra(AddressListActivity.ARG_ADDRESSES_SIZE, addresses.size());
        startActivityForResult(intent, REQUEST_ADDRESS);
    }

    @OnClick(R.id.place_order)
    public void placeOrder(final View v) {
        if(addresses.size() == 0 || mSelectedAddress == -1) {
            Toast.makeText(getContext(), "Add your address.", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = showProgressDialog("Placing your order...");
        mViatoAPI.placeOrder(new BookingBody(addresses.get(mSelectedAddress).getId()))
            .subscribe(new Subscriber<Response<String>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Response<String> stringResponse) {
                    progressDialog.dismiss();
                    if (stringResponse.isSuccess()) {
                        String body = stringResponse.body();
                        String orderId = body;
                        Intent intent = new Intent(getContext(), SuccessActivity.class);
                        intent.putExtra(SuccessActivity.ARG_ORDER_ID, orderId);
                        intent.putExtra(SuccessActivity.ARG_DELIVERY_DATE, deliveryDate);

                        Product product = new Product()
                                .setId(orderId)
                                .setPrice(total)
                                .setQuantity(1);

                        ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD);
                        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                .addProduct(product)
                                .setProductAction(productAction);

                        Tracker t = mViatoApp.getGoogleAnalyticsTracker();
                        t.setScreenName(getString(R.string.book_detail_fragment));
                        t.send(builder.build());

                        mViatoApp.sendEvent("cart", "place", "order");

                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        try {
                            Toast.makeText(getContext(), stringResponse.errorBody().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Logger.e(e, "error");
                        }
                    }
                }
            });
    }


    @OnClick(R.id.btn_empty_action)
    public void goToTrending() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(HomeActivity.EXTRA_SELECT_TAB, HomeActivity.TAB_TRENDING);
        startActivity(intent);
        getActivity().finish();
    }
    
    public void setupBooksList() {

        LinearLayoutManager linearLayoutManager = new MyVerticalLlm(getContext(), LinearLayoutManager.VERTICAL, false);
        checkoutListRV.setLayoutManager(linearLayoutManager);

        final CheckoutListAdapter adapter = new CheckoutListAdapter(items);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
//                super.onChanged();

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

    public void setTotal() {
        int total = 0;
        for (BookItem item : items){
            total += (int) item.getPricing().getRent();
        }
        totalTV.setText("Rs. " + total);
    }

    public void setDates() {
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date

        cal.add(Calendar.HOUR_OF_DAY, 48); // adds 48 hours
        deliveryDate = MiscUtils.getFormattedDate(cal.getTime());
        deliveryDateTV.setText(deliveryDate);

        int period = items.get(0).getPricing().getPeriod();
        cal.add(Calendar.DAY_OF_YEAR, period);
        returnPeriod.setText("Estimated Return (" + period + " days)");
        returnDate = MiscUtils.getFormattedDate(cal.getTime());
        returnDateTV.setText(returnDate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_ADDRESS) {
            Logger.d("Got some data");
            if(data != null){
                mSelectedAddress = data.getIntExtra(AddressListActivity.ARG_ADDRESS_INDEX, mSelectedAddress);
                Address address = data.getParcelableExtra(AddressListActivity.ARG_ADDRESS);

                if (mSelectedAddress == -1) {
                    Logger.d("No address found");
                    addAddress.setVisibility(View.VISIBLE);
                    alreadyAddress.setVisibility(View.GONE);
                    return;
                }

                if(mSelectedAddress == addresses.size()) {
                    addresses.add(address);
                }

                addresses.set(mSelectedAddress, address);

                Logger.d(address.toString());

                String flat = address.getFlat();
                String street = address.getStreet();
                String label = address.getLabel();
                String locality = address.getLocality().getName();

                mAddressFlat.setText(flat);
                mAddressStreet.setText(street);
                mAddressLocality.setText(locality);
                mAddressLabel.setText(Character.toUpperCase(label.charAt(0)) + label.substring(1));

                addAddress.setVisibility(View.GONE);
                alreadyAddress.setVisibility(View.VISIBLE);
            } else {
                Logger.e("empty data");
            }
        }
    }

    @OnClick(R.id.try_again)
    public void tryAgain() {
        fetchCart();
    }

    public void fetchCart() {
        mSubs.add(mViatoAPI.getCart()
                .subscribe(new Subscriber<Cart>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mViewContainer.setDisplayedChildId(R.id.no_connection);
                        Logger.e("Failed to fetch cart " + getString(R.string.due_to) + e.getMessage());
                    }

                    @Override
                    public void onNext(Cart cart) {
                        items = cart.getCart();
                        addresses = cart.getAddresses();
                        if (items.isEmpty()) {
                            //Empty Cart
                            mViewContainer.setDisplayedChildId(R.id.checkout_empty);
                            return;
                        }
                        setupBooksList();
                        setupAddress();
                        setTotal();
                        setDates();
                        mViewContainer.setDisplayedChildId(R.id.checkout_container);
                    }
                }));
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
                    final ProgressDialog dialog = showProgressDialog("Removing from cart...");
                    final BookItem removed = sBookList.get(position);
                    mViatoAPI.removeFromCart(removed.get_id())
                            .subscribe(new Subscriber<String>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getContext(), "Some error occurred. Please try again after some time.", Toast.LENGTH_LONG).show();
                                    Logger.e(e.getMessage());
                                }

                                @Override
                                public void onNext(String s) {
                                    dialog.dismiss();
                                    sBookList.remove(position);
                                    notifyDataSetChanged();

                                    Product product =  new Product()
                                            .setId(removed.get_id())
                                            .setName(removed.getTitle())
                                            .setPrice(removed.getPricing().getRent())
                                            .setQuantity(1);

                                    ProductAction productAction = new ProductAction(ProductAction.ACTION_REMOVE);
                                    HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                            .addProduct(product)
                                            .setProductAction(productAction);

                                    Tracker t = mViatoApp.getGoogleAnalyticsTracker();
                                    t.setScreenName(mContext.getString(R.string.book_detail_fragment));
                                    t.send(builder.build());

                                    mViatoApp.sendEvent("cart", "remove", removed.getTitle());

                                    Toast.makeText(getContext(), "Removed", Toast.LENGTH_SHORT).show();
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
}
