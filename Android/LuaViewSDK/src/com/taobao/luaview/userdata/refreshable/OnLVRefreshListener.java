package com.taobao.luaview.userdata.refreshable;

/**
 * 刷新回调
 *
 * @author song
 * @date 16/7/20
 * 主要功能描述
 * 修改描述
 * 下午6:05 song XXX
 */
public interface OnLVRefreshListener {

    /**
     * 刷新接口，可以接收一个参数
     * @param param
     */
    void onRefresh(Object param);
}
