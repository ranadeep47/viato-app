package in.viato.app.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.dummy.Notifications;
import in.viato.app.model.Notification;
import in.viato.app.ui.widgets.DividerItemDecoration;

/**
 * Created by saiteja on 05/10/15.
 */
public class NotificationsActivity extends AbstractNavDrawerActivity {

    @Bind(R.id.frame_content)
    FrameLayout mFrameLayout;

    private List<Notification> mNotifications;
    private NotificationsActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        mActivity = this;
        mNotifications = new Notifications().get();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNotificationsRV();
    }

    public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
        private List<Notification> mNotifications;

        public NotificationsAdapter(List<Notification> notifications) {
            this.mNotifications = notifications;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.notification_heading) TextView mTitle;
            @Bind(R.id.notification_body) TextView mBody;
            @Bind(R.id.notification_date) TextView mDate;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_notification, parent, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Notification notification = mNotifications.get(position);

            holder.mTitle.setText(notification.getTitle());
            holder.mBody.setText(notification.getBody());
            holder.mDate.setText(notification.getDate());
        }

//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//            final Notification notification = mNotifications.get(position);
//            holder.bindConnection(notification);
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Logger.d("Clicked");
//                    open activity
//                }
//            });
//        }

        @Override
        public int getItemCount() {
            return mNotifications.size();
        }
    }


    public void setupNotificationsRV() {
        RecyclerView mRecyclerView = (RecyclerView) getLayoutInflater().inflate(R.layout.recycler_view, mFrameLayout, false);
        mRecyclerView.setHasFixedSize(true);
        mFrameLayout.addView(mRecyclerView);

        mRecyclerView.setAdapter(new NotificationsAdapter(mNotifications));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        mViatoApp.trackScreenView(getString(R.string.notification_activity));
//        Analytics.with(this).screen("screen", getString(R.string.notification_activity));

    }

    @Override
    protected int getSelfNavDrawerItem() {
        return getResources().getInteger(R.integer.nav_item_notifications);
    }
}
