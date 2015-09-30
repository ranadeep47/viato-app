package in.viato.app.ui.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.viato.app.model.Review;
import in.viato.app.ui.adapters.ReviewRVAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowCaseReview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowCaseReview extends ShowcaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowCaseReview.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowCaseReview newInstance(String param1, String param2) {
        ShowCaseReview fragment = new ShowCaseReview();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected ReviewRVAdapter getAdapter() {
        return new ReviewRVAdapter(Review.get());
    }

    @Override
    protected String getTitle() {
        return "Reviews";
    }
}
