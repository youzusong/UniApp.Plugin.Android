package com.you.uniplugin_google_login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;

public class GoogleLoginWXModule extends WXSDKEngine.DestroyableModule {

    private static final Integer RC_SIGN_IN = 20001;

    private Context mContext;
    private JSCallback mJsCallback;

    @JSMethod(uiThread = true)
    public void login(JSONObject options, JSCallback jsCallback) {
        mContext = mWXSDKInstance.getContext();
        mJsCallback = jsCallback;

        if (!(mContext instanceof Activity)) {
            return;
        }

        try {
            Toast.makeText(mWXSDKInstance.getContext(), "Run Module GG", Toast.LENGTH_SHORT).show();

            String clientId = options.getString("clientId");

            tracking("获取clientid：" + clientId, jsCallback);

            tracking("设定选项值", jsCallback);

            // 设定选项值
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(clientId)
                    .requestEmail()
                    .requestId()
                    .requestProfile()
                    .build();


            tracking("获取client", jsCallback);

            // 获取client
            GoogleSignInClient signInClient = GoogleSignIn.getClient(mContext, gso);


            tracking("获取intent", jsCallback);

            // 获取intent
            Intent signInIntent = signInClient.getSignInIntent();

            tracking("开启登录视窗", jsCallback);

            // 开启登录视窗
            ((Activity) mContext).startActivityForResult(signInIntent, RC_SIGN_IN);

        } catch (Exception ex) {
            tracking("GoogleLoginWXModule.login - error:" + ex.getMessage(), jsCallback);
        }
    }

    private void tracking(String msg, JSCallback jsCallback) {
        JSONObject result = new JSONObject();
        result.put("msg", msg);
        jsCallback.invokeAndKeepAlive(result);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        JSONObject result = new JSONObject();
        result.put("method", "GoogleLoginWXModule.onActivityResult");
        result.put("msg", "requestCode:" + requestCode + ", resultCode:" + resultCode);
        mJsCallback.invokeAndKeepAlive(result);

        // 判断是否Googl登录行为
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(mContext, "GG登录成功", Toast.LENGTH_SHORT).show();

                JSONObject obj = new JSONObject();
                obj.put("displayName", account.getDisplayName());
                obj.put("givenName", account.getGivenName());
                obj.put("familyName", account.getFamilyName());
                obj.put("email", account.getEmail());
                obj.put("id", account.getId());
                mJsCallback.invokeAndKeepAlive(obj);

            } catch (Exception ex) {
                Toast.makeText(mContext, "GG登录失败", Toast.LENGTH_LONG).show();
                JSONObject obj = new JSONObject();
                obj.put("method", "GoogleLoginWXModule.onActivityResult");
                obj.put("msg", ex.getMessage());
                mJsCallback.invokeAndKeepAlive(obj);
            }
        }
    }
}
