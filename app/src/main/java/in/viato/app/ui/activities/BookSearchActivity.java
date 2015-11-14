package in.viato.app.ui.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.ui.widgets.BetterViewAnimator;
import in.viato.app.ui.widgets.MyVerticalLlm;
import in.viato.app.utils.SharedPrefHelper;
import retrofit.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by ranadeep on 23/09/15.
 */
public class BookSearchActivity extends AbstractActivity {

    public static final String TAG = BookSearchActivity.class.getSimpleName();
    public static final String ARG_SEARCH_ACTION = "search_action";
    public static final String ARG_ACTION_TO_ADD = "to_add";

    private static final int REQUEST_SCAN_BARCODE = 1;

    private View mResultsView;
    private SearchView mSearchView;
    private BetterViewAnimator container;
    private CardView scanButton;
    private RecyclerView list;
    private SearchResultAdapter mAdapter;
    private RecyclerView suggestionsList;
    private LinearLayout suggestionsContainer;

    private Boolean isSearchToAdd = false;

    private String query = "";
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);

        mActivity = this;

        mResultsView = ((ViewStub) findViewById(R.id.stub_import)).inflate();
        container = (BetterViewAnimator) mResultsView;
        scanButton = (CardView) mResultsView.findViewById(R.id.button_scan_barcode);
        list = (RecyclerView) mResultsView.findViewById(R.id.search_results_list);
        suggestionsList = (RecyclerView) mResultsView.findViewById(R.id.suggestions_list);

        Intent intent = getIntent();
        if (intent != null){
            handleIntent(intent);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mAdapter = new SearchResultAdapter(isSearchToAdd);
        list.setAdapter(mAdapter);
        list.setLayoutManager(new LinearLayoutManager(this));

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                showRecentSuggestions();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                hideSuggestions();
                onBackPressed();
                return true;
            }
        });
        menuItem.expandActionView();

        /*final int textViewID = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text",null, null);
        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) mSearchView.findViewById(textViewID);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, "#FFFFFF"); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {}*/
//        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    showRecentSuggestions();
//                } else {
//                    hideSuggestions();
//                }
//            }
//        });
//        showRecentSuggestions();
        mSearchView.setFocusable(true);
//        mSearchView.setIconifiedByDefault(true);
//        mSearchView.setIconified(false);
        mSearchView.requestFocusFromTouch();
        mSearchView.setQuery(query, true);
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter.clear();
                return false;
            }
        });

        observeQueryChange().subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(final String s) {
                if (s.isEmpty()) {
                    showRecentSuggestions();
                    return;
                }

                if (s.equals(query)) {
                    return;
                }

                mViatoAPI.getSuggestions(s)
                        .subscribe(new Subscriber<Response<List<String>>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Response<List<String>> bookItems) {
                                if (bookItems.isSuccess()) {
                                    List<String> suggestions = bookItems.body();
                                    SuggestionsAdapter adapter = (SuggestionsAdapter) suggestionsList.getAdapter();
                                    if (adapter == null) {
                                        adapter = new SuggestionsAdapter(suggestions, false);
                                        suggestionsList.setLayoutManager(new MyVerticalLlm(mActivity, LinearLayoutManager.VERTICAL, false));
                                        suggestionsList.setAdapter(adapter);
                                    } else {
                                        adapter.replaceAll(suggestions, false);
                                    }
                                    container.setDisplayedChildView(suggestionsList);
                                } else {
                                    try {
                                        Logger.e(bookItems.errorBody().string());
                                        Toast.makeText(mActivity, bookItems.errorBody().string(), Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_barcode:
                scanBarcode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SCAN_BARCODE && resultCode == RESULT_OK){
            query = data.getStringExtra("isbn");
            mSearchView.setIconified(false);
            mSearchView.setQuery(query, true);
            performQuery(query);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViatoApplication.get().sendScreenView(getString(R.string.book_search_activity));
//        Analytics.with(this).screen("screen", getString(R.string.book_search_activity));
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            isSearchToAdd = intent.getBooleanExtra(ARG_ACTION_TO_ADD, false);
            query = intent.getStringExtra(SearchManager.QUERY);
            if(query == null || query.equals("")) return;
            performQuery(query);
        }
    }

    private void scanBarcode(){
        startActivityForResult(new Intent(this, BarcodeScannerActivity.class), REQUEST_SCAN_BARCODE);
    }


    private void performQuery(final String Query1){
        mViatoApp.sendEvent("search", "submit_query",Query1);
        container.setDisplayedChildId(R.id.search_books_loading);
        int len = Query1.length();
        if(len == 0 && mAdapter != null) {
            mAdapter.clear();
            return;
        }

        query = Query1;
        updateRecentSearch(Query1);

        mViatoAPI
                .search(Query1)
                .subscribe(new Subscriber<List<BookItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        container.setDisplayedChildId(R.id.search_books_error);
                    }

                    @Override
                    public void onNext(List<BookItem> searchResultItems) {
                        if (searchResultItems != null && searchResultItems.size() != 0) {
                            container.setDisplayedChildId(R.id.search_results_list);
                            mAdapter.setItems(searchResultItems);
                        } else {
                            mViatoApp.sendEvent("search", "not_found", Query1);
                            container.setDisplayedChildId(R.id.search_books_empty);
                        }
                    }
                });
    }

    public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ResultItemHolder>{

        private List<BookItem> results = new ArrayList<>();
        private Boolean toAdd;

        public SearchResultAdapter(Boolean toAdd) {
            this.toAdd = toAdd;
        }

        public void setItems(List<BookItem> items){
            this.results = items;
            notifyDataSetChanged();
        }

        public void clear(){
            this.results.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return results.size();
        }

        @Override
        public void onBindViewHolder(ResultItemHolder holder, final int position) {
            final BookItem result = results.get(position);
            holder.title.setText(result.getTitle());
            holder.author.setText(result.getAuthors());
            holder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = getIntent();
                    data.putExtra("book", result); //Pass the BookItem as a result to the calling Activity
                    setResult(RESULT_OK, data);
                    finish();
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), BookDetailActivity.class);
                    intent.putExtra(BookDetailActivity.ARG_BOOK_ID, result.getCatalogueId());

                    Product product = new Product()
                            .setId(result.getCatalogueId())
                            .setName(result.getTitle())
                            .setPosition(position)
                            .setCustomMetric(getResources().getInteger(R.integer.name), getItemCount());
                    HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                            .addImpression(product, "Search Results");
                    Tracker t = mViatoApp.getGoogleAnalyticsTracker();
                    t.setScreenName(getString(R.string.book_search_activity));
                    t.send(builder.build());

                    mViatoApp.sendEventWithCustomDimension("book", "clicked", result.getTitle(), getResources().getInteger(R.integer.source), "search_results");

                    startActivity(intent);
                }
            });
            holder.mLinearLayout.setVisibility(toAdd ? View.VISIBLE : View.GONE);
            Picasso.with(holder.itemView.getContext())
                    .load(result.getThumbs().get(0))
                    .into(holder.cover);
        }

        @Override
        public ResultItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.holder_search_result, parent, false);
            return new ResultItemHolder(v);
        }

        public class ResultItemHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.search_result_cover) ImageView cover;
            @Bind(R.id.search_result_title) TextView title;
            @Bind(R.id.search_result_author) TextView author;
            @Bind(R.id.btn_action) ImageButton mButton;
            @Bind(R.id.btn_action_wrapper) LinearLayout mLinearLayout;

            public ResultItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {
        private List<String> suggestions;
        private Context mContext;
        private Boolean mIsRecent;

        public SuggestionsAdapter(List<String> suggestions, boolean isRecent) {
            this.suggestions = suggestions;
            this.mIsRecent = isRecent;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.title) TextView mTitle;
            @Bind(R.id.icon) ImageView mIcon;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public SuggestionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.holder_search_suggestions, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SuggestionsAdapter.ViewHolder holder, int position) {
            final String suggestion = suggestions.get(position);
            holder.mTitle.setText(suggestion);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    query = suggestion;
                    mSearchView.setQuery(suggestion, true);
                }
            });
            int drawableId = R.drawable.ic_search_black;
            if (mIsRecent) {
                drawableId = R.drawable.ic_history_black;
            }
            Picasso.with(mContext)
                    .load(drawableId)
                    .into(holder.mIcon);
        }

        @Override
        public int getItemCount() {
            return suggestions.size();
        }

        public void clearAll() {
            this.suggestions.clear();
            notifyDataSetChanged();
        }

        public void replaceAll(List<String> suggestions, Boolean isRecent) {
            this.suggestions = suggestions;
            this.mIsRecent = isRecent;
            notifyDataSetChanged();
        }
    }

    public void showRecentSuggestions() {
        String suggestionsString = SharedPrefHelper.getString(R.string.pref_suggestions, null);
        if (suggestionsString == null) {
            return;
        }
        ArrayList<String> suggestionsArray = new ArrayList<String>(Arrays.asList(suggestionsString.split(",")));
        suggestionsList.setLayoutManager(new MyVerticalLlm(this, LinearLayoutManager.VERTICAL, false));
        suggestionsList.setAdapter(new SuggestionsAdapter(suggestionsArray, true));
        container.setDisplayedChildView(suggestionsList);
    }

    public void hideSuggestions() {
        SuggestionsAdapter adapter = (SuggestionsAdapter) suggestionsList.getAdapter();
        if (adapter != null) {
            adapter.clearAll();
        }
    }

    @Override
    public void onBackPressed() {
        if(suggestionsList.getAdapter() != null) {
            if (suggestionsList.getAdapter().getItemCount() > 0) {
                hideSuggestions();
                return;
            }
        }
        super.onBackPressed();
    }

    public Observable<String> observeQueryChange() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {

                mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(final String newText) {
                        subscriber.onNext(newText);
                        return true;
                    }
                });
            }
        })
        .debounce(400, TimeUnit.MILLISECONDS, Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    public void updateRecentSearch(String query) {
        int suggestionsCount = 5;
        String suggestionsString = SharedPrefHelper.getString(R.string.pref_suggestions);
        Logger.d(suggestionsString);
        String[] temp =  new String[suggestionsCount + 1];
        String[] suggestionsArray;
        suggestionsArray = suggestionsString.split(",");
        List<String> suggestionsList = new ArrayList<>(Arrays.asList(suggestionsArray));
        suggestionsList.removeAll(Collections.singleton(null));

        int index = suggestionsArray.length;

        if (suggestionsList.contains(query)){
            index = suggestionsList.indexOf(query);
            Logger.d("index " + index);
            if (index + 1 != suggestionsArray.length) {
                System.arraycopy(suggestionsArray, index + 1, temp, index + 1, suggestionsArray.length - index - 1);
            }
        }

        System.arraycopy(suggestionsArray, 0, temp, 1, index);
        temp[0] = query;

        suggestionsList = new ArrayList<String>(Arrays.asList(temp)).subList(0, suggestionsCount);
        suggestionsList.removeAll(Collections.singleton(null));
        suggestionsArray = suggestionsList.toArray(suggestionsArray);

        suggestionsString = TextUtils.join(",", suggestionsArray);
        Logger.d(suggestionsString);
        SharedPrefHelper.set(R.string.pref_suggestions, suggestionsString);
    }
}
