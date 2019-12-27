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
            Toast.makeText(mWXSDKInstance.getContext(), "Run Module", Toast.LENGTH_SHORT).show();

            String clientId = options.getString("clientId");

            // 设定选项值
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(clientId)
                    .requestEmail()
                    .requestId()
                    .requestProfile()
                    .build();

            // 获取client
            GoogleSignInClient signInClient = GoogleSignIn.getClient(mContext, gso);

            // 获取intent
            Intent signInIntent = signInClient.getSignInIntent();

            // 开启登录视窗
            ((Activity) mContext).startActivityForResult(signInIntent, RC_SIGN_IN);

        } catch (Exception ex) {
            JSONObject result = new JSONObject();
            result.put("method", "GoogleLoginWXModule.login");
            result.put("msg", ex.getMessage());
            mJsCallback.invokeAndKeepAlive(result);
        }
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

                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personFamilyName = account.getFamilyName();
                String personEmail = account.getEmail();
                String personId = account.getId();

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
