package in.viato.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.ViatoApplication;

/**
 * Created by ranadeep on 11/09/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    private ConnectivityManager mManager;
    private List<NetworkStateReceiverListener> mListeners;
    private boolean mConnected;

    public NetworkStateReceiver() {
        super();
    }

    public NetworkStateReceiver(Context context) {
        mListeners = new ArrayList<NetworkStateReceiverListener>();
        mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkStateChanged();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null)
            return;
        mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (checkStateChanged()) notifyStateToAll();
    }

    private boolean checkStateChanged() {
        boolean prev = mConnected;
        NetworkInfo activeNetwork = mManager.getActiveNetworkInfo();
        mConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return prev != mConnected;
    }

    private void notifyStateToAll() {
        for (NetworkStateReceiverListener listener : mListeners) {
            notifyState(listener);
        }
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if (listener != null) {
            if (mConnected) listener.onNetworkAvailable();
            else listener.onNetworkUnavailable();
        }
    }

    public void addListener(NetworkStateReceiverListener l) {
        mListeners.add(l);
        notifyState(l);
    }

    public void removeListener(NetworkStateReceiverListener l) {
        mListeners.remove(l);
    }

    public interface NetworkStateReceiverListener {
        public void onNetworkAvailable();
        public void onNetworkUnavailable();
    }

}