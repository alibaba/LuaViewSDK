/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle.asynctask;

import android.os.AsyncTask;

/**
 * 简单任务封装
 *
 * @author song
 */
public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    public void executeInPool(Params... values) {
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, values);
    }

    public void executeSerial(Params...values){
        super.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, values);
    }

}