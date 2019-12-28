package com.youzusong.ysplugin_qqlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.utils.WXLogUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class QQLoginWXModule  extends WXSDKEngine.DestroyableModule{

    private Context mContext;
    private Tencent mTencent;
    private JSCallback mJsCallback;

    @JSMethod(uiThread = true)
    public void login(JSONObject options, JSCallback jsCallback) {
        mContext = mWXSDKInstance.getContext();
        if (!(mContext instanceof Activity)) {
            return;
        }

        mJsCallback = jsCallback;

        try{
            Toast.makeText(mWXSDKInstance.getContext(), "Run Module QQ", Toast.LENGTH_SHORT).show();

            String appId = options.getString("appId");

            mTencent = Tencent.createInstance(appId, mContext);
            mTencent.login((Activity) mContext, "all", new QQLoginLisenter());

        }catch (Exception ex){
            JSONObject result = new JSONObject();
            result.put("method", "QQLoginWXModule.login");
            result.put("msg", ex.getMessage());
            mJsCallback.invokeAndKeepAlive(result);
        }
    }

    @JSMethod(uiThread = true)
    public void test(JSONObject options, JSCallback jsCallback) {

        String appId = options.getString("appId");

        Toast.makeText(mWXSDKInstance.getContext(), "test " + appId, Toast.LENGTH_SHORT).show();

        JSONObject result = new JSONObject();
        result.put("appId", appId);
        result.put("success", "true");
        jsCallback.invokeAndKeepAlive(result);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        JSONObject result = new JSONObject();
        result.put("method", "QQLoginWXModule.onActivityResult");
        result.put("msg", "requestCode:" + requestCode + ", resultCode:" + resultCode);
        mJsCallback.invokeAndKeepAlive(result);

        // 判断是否为QQ登录行为
        if (requestCode == Constants.REQUEST_LOGIN) {
            // 判断是否QQ登录成功
            if (resultCode == Constants.UI_NONE) {
                //Tencent.onActivityResultData(requestCode, resultCode, data, new QQLoginListener(mContext, mTencent, mCbUserinfo));
                Tencent.handleResultData(data, new QQLoginLisenter());
            }
        }
    }

    private class QQLoginLisenter implements IUiListener {

        QQLoginLisenter() {
        }

        @Override
        public void onComplete(Object o) {
            Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();

            try {
                org.json.JSONObject obj = (org.json.JSONObject) o;
                String openid = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expiresIn = obj.getString("expires_in");

                mTencent.setOpenId(openid);
                mTencent.setAccessToken(accessToken, expiresIn);

                UserInfo userInfo = new UserInfo(mContext, mTencent.getQQToken());
                userInfo.getUserInfo(new QQUserInfoListener());

            } catch (Exception ex) {
                JSONObject result = new JSONObject();
                result.put("method", "QQLoginLisenter.onComplete");
                result.put("msg", ex.getMessage());
                mJsCallback.invokeAndKeepAlive(result);
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(mContext, "登录取消", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
        }
    }

    private class QQUserInfoListener implements IUiListener{

        @Override
        public void onComplete(Object o) {
            try {
                org.json.JSONObject obj = (org.json.JSONObject) o;
                String nickname = obj.getString("nickname");

                JSONObject result = new JSONObject();
                result.put("nickname", nickname);
                mJsCallback.invokeAndKeepAlive(result);

            } catch (Exception ex) {
                JSONObject result = new JSONObject();
                result.put("method", "QQUserInfoListener.onComplete");
                result.put("msg", ex.getMessage());
                mJsCallback.invokeAndKeepAlive(result);
            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(UiError uiError) {

        }
    }

}
