/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.taobao.luaview.receiver.ConnectionStateChangeBroadcastReceiver;

/**
 * Network Util
 *
 * @author song
 */
public class NetworkUtil {
    private static final String TAG = "NetworkUtil";

    //ConnectionChangeListener
    private static ConnectionStateChangeBroadcastReceiver sConnectionBroadcastReceiver;

    /**
     * 网络类型
     * GPRS 2G(2.5) General Packet Radia Service 114kbps
     * EDGE 2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
     * CDMA 2G 电信 Code Division Multiple Access 码分多址
     * IDEN 2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
     * 1xRTT 2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
     * UMTS 3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
     * EVDO_0 3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
     * EVDO_A 3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
     * HSPA 3G (分HSDPA,HSUPA) High Speed Packet Access
     * EVDO_B 3G EV-DO Rev.B 14.7Mbps 下行 3.5G
     * EHRPD 3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
     * HSPAP 3G HSPAP 比 HSDPA 快些
     * HSUPA 3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
     * HSDPA 3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
     * LTE 4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
     */
    public enum NetworkType {
        NETWORK_NONE("none"), NETWORK_2G("2g"), NETWORK_3G("3g"), NETWORK_4G("4g"), NETWORK_WIFI("wifi"), NETWORK_UNKNOWN("unknown");

        String type;

        NetworkType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    /**
     * 注册ConnectionChangeListener
     *
     * @param context
     */
    public static synchronized void registerConnectionChangeListener(Context context, ConnectionStateChangeBroadcastReceiver.OnConnectionChangeListener listener) {
        if (context != null) {
            if (sConnectionBroadcastReceiver == null) {
                sConnectionBroadcastReceiver = new ConnectionStateChangeBroadcastReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SHUTDOWN);
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                try {
                    context.getApplicationContext().registerReceiver(sConnectionBroadcastReceiver, filter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sConnectionBroadcastReceiver.addOnConnectionChangeListener(listener);
        }
    }

    /**
     * 反注册ConnectionChangeListener
     *
     * @param context
     */
    public static synchronized void unregisterConnectionChangeListener(Context context, ConnectionStateChangeBroadcastReceiver.OnConnectionChangeListener listener) {
        if (context != null && sConnectionBroadcastReceiver != null) {
            try {
                sConnectionBroadcastReceiver.removeOnConnectionChangeListener(listener);
                if (sConnectionBroadcastReceiver.getListenerSize() == 0) {
                    context.getApplicationContext().unregisterReceiver(sConnectionBroadcastReceiver);
                    sConnectionBroadcastReceiver = null;
                }
            } catch (Exception e) {//保护
                e.printStackTrace();
            }
        }
    }


    /**
     * 得到当前网络类型字符串
     *
     * @return
     */
    public static synchronized String getCurrentTypeStr(Context context) {
        final NetworkType type = getCurrentType(context);
        return type.getType();
    }

    /**
     * 得到当前网络类型
     *
     * @return
     */
    public static NetworkType getCurrentType(Context context) {
        if (!isNetWorkAvailable(context)) {
            return NetworkType.NETWORK_NONE;
        }
        if (isWifi(context)) {
            return NetworkType.NETWORK_WIFI;
        }
        TelephonyManager tm = null;
        try {
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }

        if (tm == null) {
            return NetworkType.NETWORK_NONE;
        }

        int type = tm.getNetworkType();
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NetworkType.NETWORK_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NetworkType.NETWORK_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NetworkType.NETWORK_4G;
            default:
                return NetworkType.NETWORK_UNKNOWN;
        }
    }

    /**
     * 网络是否可用
     *
     * @return
     * @date 2012-11-20下午1:55:23
     */
    public static boolean isNetWorkAvailable(Context context) {
        boolean isAvailable = false;
        try {
            ConnectivityManager cm = getConnectivityManager(context);
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null) {
                    isAvailable = info.isAvailable();
                }
            }
        } catch (Exception e) {
        }

        return isAvailable;
    }

    /**
     * 网络是否有效，可传输数据(注意Available仅仅是打开，有网络，不一定连接成功)，
     *
     * @return
     * @date 2014-1-13
     */
    public static boolean isNetworkConnected(Context context) {
        boolean isConnected = false;
        try {
            ConnectivityManager cm = getConnectivityManager(context);
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null) {
                    isConnected = info.isAvailable() && info.isConnected();
                }
            }
        } catch (Exception e) {
        }
        return isConnected;
    }

    /**
     * 判断是否是wifi网络
     *
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = getConnectivityManager(context);
        if (cm != null) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.getTypeName().equals("WIFI")) {
                return true;
            }
        }
        return false;
    }

    //----------------------------private methods--------------------------
    private static ConnectivityManager getConnectivityManager(Context context) {
        ConnectivityManager cm = null;
        try {
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e) {
        }
        return cm;
    }
}
