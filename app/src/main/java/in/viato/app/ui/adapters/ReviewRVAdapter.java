package in.viato.app.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.models.Review;

/**
 * Created by saiteja on 24/09/15.
 */
public class ReviewRVAdapter extends RecyclerView.Adapter<ReviewRVAdapter.ViewHolder> {

    private List<Review> mValues;
    private int id = 1;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView reviewerName;
        public final TextView reviewedDate;
        public final ImageView reviewerImage;
        public final TextView reviewText;
        public final RatingBar ratingBar;


        public ViewHolder(View view) {
            super(view);
            reviewerName = (TextView) view.findViewById(R.id.reviewer_name);
            reviewedDate = (TextView) view.findViewById(R.id.review_date);
            reviewerImage = (ImageView) view.findViewById(R.id.reviewer_img);
            reviewText = (TextView) view.findViewById(R.id.review_text);
            ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        }
    }

    public ReviewRVAdapter(List<Review> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Review review = mValues.get(position);

        holder.reviewerName.setText(review.getUserName());
        holder.reviewedDate.setText(review.getDate());
        holder.ratingBar.setRating(review.getRating());
        String reviewText = review.getReviewText();
        if(reviewText.length() > 0) {
            holder.reviewText.setText(reviewText);
            holder.reviewText.setVisibility(View.VISIBLE);
        }
        Picasso.with(holder.reviewerImage.getContext())
                .load(review.getDpLink())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.reviewerImage);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}