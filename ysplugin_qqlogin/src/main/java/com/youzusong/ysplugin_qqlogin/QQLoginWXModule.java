package com.youzusong.ysplugin_qqlogin;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.utils.WXLogUtils;
import com.taobao.weex.utils.WXResourceUtils;

public class QQLoginWXModule  extends WXSDKEngine.DestroyableModule{

    @JSMethod(uiThread = true)
    public void login(JSONObject options, JSCallback jsCallback) {
        String id = options.getString("id");

        JSONObject result = new JSONObject();
        result.put("name", "youzusong");
        result.put("plugin", "QQLogin");
        result.put("id", id);
        jsCallback.invokeAndKeepAlive(result);
    }

    @Override
    public void destroy() {

    }

}
