package in.viato.app.ui.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.http.models.response.BookItem;

/**
 * Created by saiteja on 15/09/15.
 */
public class CheckoutListAdapter extends RecyclerView.Adapter<CheckoutListAdapter.ViewHolder> {
    private List<BookItem> sBookList;
    private Context mContext;

    public CheckoutListAdapter(List<BookItem> bookList) {
        super();
        this.sBookList = bookList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
        holder.mPrice.setText("Rs. " + book.getPricing().getRent());
        Picasso.with(mContext)
                .load(book.getCover())
                .into(holder.mCover);
        holder.mRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BookItem removed = sBookList.remove(position);
//                notifyItemRemoved(position);
                notifyDataSetChanged();

                Snackbar.make(v, "removed", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return sBookList.size();
    }
}