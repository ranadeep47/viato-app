package in.viato.app.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.viato.app.R;
import in.viato.app.ui.activities.BookDetailActivity;
import in.viato.app.ui.fragments.BookDetailFragment;

/**
 * Created by saiteja on 24/09/15.
 */
public class RelatedBooksRVAdapter
        extends RecyclerView.Adapter<RelatedBooksRVAdapter.ViewHolder> {

    private List<String> mValues;
    private int mLayoutId;
    private Context mContext;
    private int id = 1;
    private Boolean mClickable;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.book_poster);
        }
    }

    public RelatedBooksRVAdapter(int layoutId, List<String> items, Boolean clickable) {
        mValues = items;
        mLayoutId = layoutId;
        mClickable = clickable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mClickable){
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    intent.putExtra(BookDetailFragment.EXTRA_ID, id);

                    context.startActivity(intent);
                }
            });
        }

        Picasso.with(mContext)
                .load(mValues.get(position))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
