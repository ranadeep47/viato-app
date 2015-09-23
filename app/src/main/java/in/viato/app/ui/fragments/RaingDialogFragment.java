package in.viato.app.ui.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RaingDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RaingDialogFragment extends DialogFragment {

    private static final String ARG_RATING = "rating";
    private static final String ARG_REVIEW = "review";

    public static final String EXTRA_RATING = "in.viato.app.rating";
    public static final String EXTRA_REVIEW = "in.viato.app.review";

    private float rating;
    private String review;

    @Bind(R.id.rating)
    RatingBar mRatingBar;

    @Bind(R.id.review)
    EditText mReview;

    public static RaingDialogFragment newInstance(float rating, String review) {
        RaingDialogFragment fragment = new RaingDialogFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_RATING, rating);
        args.putString(ARG_REVIEW, review);
        fragment.setArguments(args);
        return fragment;
    }

    public RaingDialogFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_raing_dialog, null);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            rating = getArguments().getFloat(ARG_RATING);
            review = getArguments().getString(ARG_REVIEW);

            mRatingBar.setRating(rating);
            mReview.setText(review);
        }

        return new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.rating_dialog_title)
                .setPositiveButton(R.string.submit,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                float rating = mRatingBar.getRating();
                                String review =String.valueOf(mReview.getText());
                                sendResult(Activity.RESULT_OK, rating, review);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, float rating, String review) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_RATING, rating);
        intent.putExtra(EXTRA_REVIEW, review);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
