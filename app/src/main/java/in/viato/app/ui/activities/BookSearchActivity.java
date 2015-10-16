package in.viato.app.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.http.models.response.SearchResultItem;
import in.viato.app.ui.widgets.BetterViewAnimator;
import rx.Subscriber;


/**
 * Created by ranadeep on 23/09/15.
 */
public class BookSearchActivity extends AbstractActivity {

    public static final String TAG = BookSearchActivity.class.getSimpleName();

    private View mResultsView;
    private SearchView mSearchView;

    private BetterViewAnimator container;
    private CardView scanButton;
    private RecyclerView list;

    private static final int REQUEST_SCAN_BARCODE = 1;
    private SearchResultAdapter mAdapter;

    public static final String ARG_SEARCH_ACTION = "search_action";
    public static final String ARG_ACTION_TO_ADD = "to_add";

    private Boolean isSearchToAdd = false;

    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);

        mResultsView = ((ViewStub) findViewById(R.id.stub_import)).inflate();
        container = (BetterViewAnimator) mResultsView;
        scanButton = (CardView) mResultsView.findViewById(R.id.button_scan_barcode);
        list = (RecyclerView) mResultsView.findViewById(R.id.search_results_list);
        Log.d(TAG, "onCreate");

        Intent intent = getIntent();
        if (intent != null){
            handleIntent(getIntent());
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
        Log.d(TAG, "onPostCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        /*final int textViewID = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text",null, null);
        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) mSearchView.findViewById(textViewID);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, "#FFFFFF"); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {}*/

        mSearchView.setIconifiedByDefault(true);
        mSearchView.setFocusable(true);
        mSearchView.setIconified(false);
        mSearchView.requestFocusFromTouch();
        Log.d(TAG, "onCreateOptionsMenu");

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter.clear();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_barcode:
                scanBarcode();
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SCAN_BARCODE && resultCode == RESULT_OK){
            query = data.getStringExtra("isbn");
//            performQuery(isbn);
            Log.d(TAG, "onActivityResult");
            mSearchView.setIconified(false);
            mSearchView.setQuery(query, true);
            Logger.d("ISBN received : %s", query);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            isSearchToAdd = intent.getBooleanExtra(ARG_ACTION_TO_ADD, false);
            Logger.d("Query is %s", query);
            Log.d(TAG, "handleIntent");
            performQuery(query);
        }
    }

    private void scanBarcode(){
        startActivityForResult(new Intent(this, BarcodeScannerActivity.class), REQUEST_SCAN_BARCODE);
    }


    private void performQuery(String query){
        Log.d(TAG, "performQuery");
        container.setDisplayedChildId(R.id.search_books_loading);
        int len = query.length();
        Logger.d(String.valueOf(len));
        if(len == 0) {
            mAdapter.clear();
            return;
        } else if (len < 3 && len > 0){
            return;
        }
        mViatoAPI
                .search(query)
                .subscribe(new Subscriber<List<SearchResultItem>>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("Query completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d(e.getMessage());
                        container.setDisplayedChildId(R.id.search_books_error);
                    }

                    @Override
                    public void onNext(List<SearchResultItem> searchResultItems) {
                        if (searchResultItems != null && searchResultItems.size() != 0) {
                            container.setDisplayedChildId(R.id.search_results_list);
                            mAdapter.setItems(searchResultItems);
                        } else container.setDisplayedChildId(R.id.search_books_empty);
                    }
                });
    }

    public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ResultItemHolder>{

        private List<SearchResultItem> results = new ArrayList<>();
        private Boolean toAdd;

        public SearchResultAdapter(Boolean toAdd) {
            this.toAdd = toAdd;
        }

        public void setItems(List<SearchResultItem> items){
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
            final SearchResultItem result = results.get(position);
            holder.title.setText(result.getTitle());
            holder.author.setText(result.getAuthor());
            holder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = getIntent();
                    data.putExtra("id", result.getBookId());
                    setResult(RESULT_OK, data);
                    finish();
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), BookDetailActivity.class);
                    intent.putExtra(BookDetailActivity.ARG_BOOK_ID, result.getBookId());
                    startActivity(intent);
                }
            });
            holder.mLinearLayout.setVisibility(isSearchToAdd ? View.VISIBLE : View.GONE);
            Picasso.with(holder.itemView.getContext())
                    .load(result.getCover())
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
            @Bind(R.id.btn_action) Button mButton;
            @Bind(R.id.btn_action_wrapper) LinearLayout mLinearLayout;

            public ResultItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
