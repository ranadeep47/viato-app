package in.viato.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.http.models.old.Category;
import in.viato.app.http.models.response.CategoryItem;
import in.viato.app.ui.activities.CategoryBooksActivity;
import jp.wasabeef.picasso.transformations.ColorFilterTransformation;
import rx.Subscriber;

/**
 * Created by ranadeep on 19/09/15.
 */
public class HomeFragment extends AbstractFragment {

    public static final String TAG = "HomeFragment";

    private static final String STATE_POSITION_INDEX = "state_position_index";
    private static final String STATE_POSITION_OFFSET = "state_position_offset";

    @Bind(R.id.categories_list) RecyclerView list;

    private LinearLayoutManager layoutManager;
    private CategoryListAdapter adapter;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new CategoryListAdapter();
        layoutManager = new LinearLayoutManager(getActivity());

        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        mViatoAPI.getCategories().subscribe(new Subscriber<List<CategoryItem>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Logger.e(e.getMessage());
                showError("Check your internet connection.");
            }

            @Override
            public void onNext(List<CategoryItem> categories) {
                adapter.setCategories(categories);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onBackPressed() {
        //Disable back button, home fragment !
        return true;
    }

    private class CategoryListAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

        private final LayoutInflater inflater;
        private List<CategoryItem> categories = Collections.emptyList();

        public CategoryListAdapter() {
            inflater = LayoutInflater.from(getActivity());
        }

        public void setCategories(List<CategoryItem> categories){
            this.categories = categories;
            notifyDataSetChanged();
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.holder_category, parent, false);
            CategoryViewHolder categoryHolder = new CategoryViewHolder(view);
            //TODO preloadSizeProvider crap
            return categoryHolder;
        }

        @Override
        public void onBindViewHolder(final CategoryViewHolder holder, int position) {
            final CategoryItem category = categories.get(position);
            //TODO load cateogry into imageView
            holder.titleView.setText(category.getTitle());
            Picasso
                    .with(getActivity())
                    .load(category.getImages().getCover())
                    .placeholder(R.drawable.placeholder)
                    .transform(new ColorFilterTransformation(R.color.black))
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .error(R.drawable.placeholder)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getActivity())
                                    .load(category.getImages().getCover())
                                    .transform(new ColorFilterTransformation(R.color.black))
                                    .into(holder.imageView);
                        }
                    });

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO start an intent based on category information
//                    Toast.makeText(getActivity(), category.getTitle(), Toast.LENGTH_SHORT).show();
//                    loadFragment(R.id.frame_content, CategoryBooksFragment.newInstance(category.getId()), CategoryBooksFragment.TAG, true, CategoryBooksFragment.TAG);
                    Intent intent = new Intent(getContext(), CategoryBooksActivity.class);
                    intent.putExtra(CategoryBooksActivity.ARG_CATEGORY_ID, category.get_id());
                    intent.putExtra(CategoryBooksActivity.ARG_CATEGORY_NAME, category.getTitle());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        @Override
        public void onViewRecycled(CategoryViewHolder holder) {
            super.onViewRecycled(holder);
            //TODO requestManager.clear(holder.imageView)
        }
    }

    private static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final ImageView imageView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.category_title);
            imageView = (ImageView) itemView.findViewById(R.id.category_image);
        }
    }


}
