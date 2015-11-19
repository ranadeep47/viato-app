package in.viato.app.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.ui.activities.BookDetailActivity;

/**
 * Created by ranadeep on 21/09/15.
 */
public class CategoryBooksGridAdapter extends RecyclerView.Adapter<CategoryBooksGridAdapter.CategoryBookItemHolder> {

    private ViatoApplication mViatoApp = ViatoApplication.get();
    private List<BookItem> mBooks = new ArrayList<>();
    private Context mContext;
    private String mCategoryName;

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public void addAll(List<BookItem> books){
        mBooks.addAll(books);
        notifyDataSetChanged();
    }

    public CategoryBooksGridAdapter(String categoryName) {
        mCategoryName = categoryName;
    }

    @Override
    public void onBindViewHolder(CategoryBookItemHolder holder, final int position) {
        final BookItem book = mBooks.get(position);

        Picasso.with(holder.itemView.getContext())
                .load(book.getThumbs().get(0))
                .into(holder.cover);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthors());
        holder.retailPrice.setText("\u20B9 " + (int) book.getPricing().getRent());
        holder.retailPrice.setPaintFlags(holder.retailPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.rentalPrice.setText("\u20B9 " + (int) book.getPricing().getRent());
        holder.itemView.setTag(R.string.book_id, book.getCatalogueId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.ARG_BOOK_ID, book.getCatalogueId());

                Product product = new Product()
                        .setId(book.getCatalogueId())
                        .setName(book.getTitle())
                        .setCategory(mCategoryName)
                        .setPosition(position);
                ProductAction productAction = new ProductAction(ProductAction.ACTION_DETAIL)
                        .setProductActionList("Category List");
                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                        .addProduct(product)
                        .setProductAction(productAction);

                Tracker t = mViatoApp.getGoogleAnalyticsTracker();
                t.setScreenName(mContext.getString(R.string.category_books_fragment));
                t.send(builder.build());

                mViatoApp.sendEventWithCustomDimension("book", "clicked", book.getTitle(), mContext.getResources().getInteger(R.integer.source), mCategoryName);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public CategoryBookItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.holder_category_book_item, parent, false);

        mContext = parent.getContext();

        return new CategoryBookItemHolder(v);
    }

    public static class CategoryBookItemHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.book_cover) ImageView cover;
        @Bind(R.id.book_title) TextView title;
        @Bind(R.id.book_author) TextView author;
        @Bind(R.id.retail_price) TextView retailPrice;
        @Bind(R.id.rental_price) TextView rentalPrice;

        public CategoryBookItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
