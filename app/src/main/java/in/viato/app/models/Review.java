package in.viato.app.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiteja on 24/09/15.
 */
public class Review {
    private String mUserName;
    private String mDate;
    private String mReviewText;
    private String mDpLink;
    private float mRating;

    private static List<Review> mReviewList;

    public Review(String userName, String date, float rating, String reviewText, String dpLink) {
        mUserName = userName;
        mDate = date;
        mRating = rating;
        mReviewText = reviewText;
        mDpLink = dpLink;
    }

    public static List<Review> get() {
        if(mReviewList == null) {
            List<Review> reviews = new ArrayList<Review>();
            for (int i = 0; i < 3; i++) {
                Review review = new Review("Saiteja", "24-09-2015", 4,"Lorem Epsum", "http://i.imgur.com/DvpvklR.png");
                reviews.add(review);
            }
            mReviewList = reviews;
            return mReviewList;
        } else {
            return mReviewList;
        }
    }

    public Review getItem(int pos){
        return mReviewList.get(pos);
    }

    public String getDpLink() {
        return mDpLink;
    }

    public String getReviewText() {
        return mReviewText;
    }

    public float getRating() {
        return mRating;
    }

    public String getDate() {
        return mDate;
    }

    public String getUserName() {
        return mUserName;
    }


}
