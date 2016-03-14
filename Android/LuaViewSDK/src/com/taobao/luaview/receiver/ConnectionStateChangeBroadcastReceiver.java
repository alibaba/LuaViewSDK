package com.taobao.luaview.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.taobao.luaview.util.NetworkUtil;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 监听网络状态变化
 * @author song
 */
public class ConnectionStateChangeBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = ConnectionStateChangeBroadcastReceiver.class.getSimpleName();
    private ConcurrentLinkedQueue<OnConnectionChangeListener> mOnConnectionChangeListeners;

    public interface OnConnectionChangeListener {
        void onConnectionClosed();//所有的连接都断开

        void onMobileConnected();//连接到移动网络

        void onWifiConnected();//连接到wifi
    }

    public ConnectionStateChangeBroadcastReceiver() {
        this.mOnConnectionChangeListeners = new ConcurrentLinkedQueue<OnConnectionChangeListener>();
    }

    public void addOnConnectionChangeListener(OnConnectionChangeListener listener) {
        if (this.mOnConnectionChangeListeners != null && !this.mOnConnectionChangeListeners.contains(listener)) {
            this.mOnConnectionChangeListeners.add(listener);
        }
    }

    public void removeOnConnectionChangeListener(OnConnectionChangeListener listener) {
        if (this.mOnConnectionChangeListeners != null && this.mOnConnectionChangeListeners.contains(listener)) {
            this.mOnConnectionChangeListeners.remove(listener);
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (this.mOnConnectionChangeListeners != null) {
            if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
                for (final OnConnectionChangeListener listener : mOnConnectionChangeListeners) {
                    listener.onConnectionClosed();
                }
            } else {
                final NetworkUtil.NetworkType currentNetworkType = NetworkUtil.getCurrentType(context);
                switch (currentNetworkType) {
                    case NETWORK_2G:
                    case NETWORK_3G:
                    case NETWORK_4G:
                        for (final OnConnectionChangeListener listener : mOnConnectionChangeListeners) {
                            listener.onMobileConnected();
                        }
                        break;
                    case NETWORK_WIFI:
                        for (final OnConnectionChangeListener listener : mOnConnectionChangeListeners) {
                            listener.onWifiConnected();
                        }
                        break;
                    case NETWORK_NONE:
                        for (final OnConnectionChangeListener listener : mOnConnectionChangeListeners) {
                            listener.onConnectionClosed();
                        }
                        break;
                    case NETWORK_UNKNOWN:
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
