package com.zemult.merchant.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.MerchantEnter2Activity;
import com.zemult.merchant.app.view.ProgressWebView;
import com.zemult.merchant.config.Constants;


/**
 * 使用条款和隐私策略
 */
public class BaseWebViewActivity extends MBaseActivity {

    String url, titlename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra("url");
        titlename = getIntent().getStringExtra("titlename") == null ? "" : getIntent().getStringExtra("titlename");
        View appView = getLayoutInflater().inflate(
                R.layout.activity_clause_tactics, null);
        ProgressWebView wView = (ProgressWebView) appView.findViewById(R.id.webvRules);
        WebSettings wSet = wView.getSettings();
        wSet.setJavaScriptEnabled(true);
        wView.setWebViewClient(new MyWebViewClient());
        wView.loadUrl(url);
        appMainView.addView(appView, layoutParams);
        setTitleLeftButton("");
        setTitleText(titlename);
    }


    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String info = "";
            //此处根据需求处理逻辑...
            if (url.startsWith(Constants.SCHEME_PREFIX)) {
                info = url.substring(Constants.SCHEME_PREFIX.length());
                if (info.equalsIgnoreCase("merchantAdd")) {
                    if (!noLogin(BaseWebViewActivity.this)) {
                        Intent intent = new Intent();
                        intent.setClass(BaseWebViewActivity.this, MerchantEnter2Activity.class);
                        startActivity(intent);
                    }

                }

            } else {
                view.loadUrl(url);

            }
            return true;
        }

    }

}
