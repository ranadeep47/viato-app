package in.viato.app.ui.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.databinding.PNotificationBinding;
import in.viato.app.dummy.Notifications;
import in.viato.app.model.Notification;
import in.viato.app.ui.widgets.DividerItemDecoration;

/**
 * Created by saiteja on 05/10/15.
 */
public class NotificationsActivity extends AbstractNavDrawerActivity {

    @Bind(R.id.frame_content) FrameLayout mFrameLayout;

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
            private final PNotificationBinding mBinding;

            public ViewHolder(PNotificationBinding binding) {
                super(binding.getRoot());
                this.mBinding = binding;
            }

            public void bindConnection(Notification notification ) {
                mBinding.setNotification(notification);
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PNotificationBinding binding = DataBindingUtil
                    .inflate(LayoutInflater.from(parent.getContext()),
                            R.layout.p_notification,
                            parent,
                            false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Notification notification = mNotifications.get(position);
            holder.bindConnection(notification);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logger.d("Clicked");
                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Intent intent = new Intent();
                    intent.setClassName("in.viato.app", "in.viato.app.ui.activities.HomeActivity");
                    intent.putExtra(HomeActivity.EXTRA_SETECT_TAB, HomeActivity.TAB_TRENDING);
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(mActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle("My notification")
                                    .setContentText("Hello World!")
                                    .setAutoCancel(true)
                                    .setSound(soundUri)
                                    .setContentIntent(resultPendingIntent);
                    int mNotificationId = 001;
                    NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(mNotificationId, mBuilder.build());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mNotifications.size();
        }
    }


    public void setupNotificationsRV() {
        RecyclerView mRecyclerView = new RecyclerView(this);
        mRecyclerView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        mRecyclerView.setHasFixedSize(true);
        mFrameLayout.addView(mRecyclerView);


        mRecyclerView.setAdapter(new NotificationsAdapter(mNotifications));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));
    }
}
