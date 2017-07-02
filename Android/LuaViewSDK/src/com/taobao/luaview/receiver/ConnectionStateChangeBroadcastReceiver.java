/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.taobao.luaview.util.NetworkUtil;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 监听网络状态变化
 *
 * @author song
 */
public class ConnectionStateChangeBroadcastReceiver extends BroadcastReceiver {
    private ConcurrentSkipListMap<Integer, WeakReference<OnConnectionChangeListener>> mOnConnectionChangeListeners;

    public interface OnConnectionChangeListener {
        void onConnectionClosed();//所有的连接都断开

        void onMobileConnected();//连接到移动网络

        void onWifiConnected();//连接到wifi
    }

    public ConnectionStateChangeBroadcastReceiver() {
        this.mOnConnectionChangeListeners = new ConcurrentSkipListMap<Integer, WeakReference<OnConnectionChangeListener>>();
    }

    public void addOnConnectionChangeListener(OnConnectionChangeListener listener) {
        final Integer hashCode = listener != null ? listener.hashCode() : null;
        if (hashCode != null) {//有效
            WeakReference<OnConnectionChangeListener> onConnectionChangeListenerWeakReference = mOnConnectionChangeListeners != null ? mOnConnectionChangeListeners.get(hashCode) : null;
            if (onConnectionChangeListenerWeakReference == null || onConnectionChangeListenerWeakReference.get() == null) {//没有加过
                mOnConnectionChangeListeners.put(hashCode, new WeakReference<OnConnectionChangeListener>(listener));
            }
        }
    }

    public void removeOnConnectionChangeListener(OnConnectionChangeListener listener) {
        final Integer hashCode = listener != null ? listener.hashCode() : null;
        if (hashCode != null && mOnConnectionChangeListeners != null) {
            this.mOnConnectionChangeListeners.remove(hashCode);
        }
    }

    /**
     * 获得listener size
     *
     * @return
     */
    public int getListenerSize() {
        return mOnConnectionChangeListeners != null ? mOnConnectionChangeListeners.size() : 0;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (this.mOnConnectionChangeListeners != null) {
            WeakReference<OnConnectionChangeListener> listenerWeakReference = null;
            if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
                for (Integer key : mOnConnectionChangeListeners.keySet()) {
                    listenerWeakReference = mOnConnectionChangeListeners.get(key);
                    if (listenerWeakReference != null && listenerWeakReference.get() != null) {
                        listenerWeakReference.get().onConnectionClosed();
                    }
                }
            } else {
                final NetworkUtil.NetworkType currentNetworkType = NetworkUtil.getCurrentType(context);
                switch (currentNetworkType) {
                    case NETWORK_2G:
                    case NETWORK_3G:
                    case NETWORK_4G: {
                        for (Integer key : mOnConnectionChangeListeners.keySet()) {
                            listenerWeakReference = mOnConnectionChangeListeners.get(key);
                            if (listenerWeakReference != null && listenerWeakReference.get() != null) {
                                listenerWeakReference.get().onMobileConnected();
                            }
                        }
                        break;
                    }
                    case NETWORK_WIFI: {
                        for (Integer key : mOnConnectionChangeListeners.keySet()) {
                            listenerWeakReference = mOnConnectionChangeListeners.get(key);
                            if (listenerWeakReference != null && listenerWeakReference.get() != null) {
                                listenerWeakReference.get().onWifiConnected();
                            }
                        }
                        break;
                    }
                    case NETWORK_NONE: {
                        for (Integer key : mOnConnectionChangeListeners.keySet()) {
                            listenerWeakReference = mOnConnectionChangeListeners.get(key);
                            if (listenerWeakReference != null && listenerWeakReference.get() != null) {
                                listenerWeakReference.get().onConnectionClosed();
                            }
                        }
                        break;
                    }
                    case NETWORK_UNKNOWN:
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
