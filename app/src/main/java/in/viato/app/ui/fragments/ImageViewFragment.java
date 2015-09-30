package in.viato.app.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import butterknife.Bind;
import in.viato.app.R;

/**
 * Created by ranadeep on 15/09/15.
 */
public class ImageViewFragment extends AbstractFragment {

    public static final String ARG_DRAWABLE = "ARG_DRAWABLE";
    public static final String ARG_TITLE = "ARG_TITLE";
    public static final String ARG_SUBTITLE = "ARG_SUBTITLE";

    @Bind(R.id.fragment_image) ImageView mImage;
    @Bind(R.id.image_title) TextView mImageTitle;
    @Bind(R.id.image_subtitle) TextView mImageSubtitle;


    public static ImageViewFragment newInstance(int drawable, String title, String subtitle) {
        Bundle args = new Bundle();
        args.putInt(ARG_DRAWABLE, drawable);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_SUBTITLE, subtitle);
        ImageViewFragment fragment = new ImageViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_image_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ButterKnife bindings done
        Bundle args = getArguments();
        mImage.setImageResource(args.getInt(ARG_DRAWABLE));
        mImageTitle.setText(args.getString(ARG_TITLE));
        mImageSubtitle.setText(args.getString(ARG_SUBTITLE));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
