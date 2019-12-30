package com.youzusong.uniplugin_oauth;

import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;

public class YsOauthWXModule extends WXSDKEngine.DestroyableModule {

    @Override
    public void destroy() {

    }

    @JSMethod(uiThread = true)
    public void login(JSONObject options, JSCallback jscbSuccess, JSCallback jscbError) {
        Toast.makeText(mWXSDKInstance.getContext(), "Run Module", Toast.LENGTH_SHORT).show();

        String provider = options.getString("provider");

        if ("google".equals(provider)) {
            JSONObject result = new JSONObject();
            result.put("provider", provider);
            result.put("name", "test");
            jscbSuccess.invokeAndKeepAlive(result);
        } else {
            JSONObject result = new JSONObject();
            result.put("code", 1001);
            result.put("msg", "provider不合法");
            jscbError.invokeAndKeepAlive(result);
        }

    }


}
