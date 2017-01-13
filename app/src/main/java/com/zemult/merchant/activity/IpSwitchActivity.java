package com.zemult.merchant.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.AppApplication;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 测试地址切换 test
 */
public class IpSwitchActivity extends MAppCompatActivity {


    @Bind(R.id.urlTv)
    TextView urlTv;
    @Bind(R.id.ip_switch_sp)
    Spinner ipSwitchSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipswitch);
        ButterKnife.bind(this);
//        String url = getSharedPreferences("IpSwitchActivity", MODE_APPEND)
//                .getString("url", null);
//        if(null==url){
//            getSharedPreferences("IpSwitchActivity", MODE_APPEND).edit().putString("url", Urls.BASIC_URL)
//                    .commit();
//        }
//        String text = url == null ? "当前url："
//                + Urls.BASIC_URL : "当前url：" + url;
        urlTv.setText(Urls.BASIC_URL);
    }

    public void switchIp(View view) {
        String url = ipSwitchSp.getSelectedItem().toString();
//        getSharedPreferences("IpSwitchActivity", MODE_APPEND).edit().putString("url", url)
//                .commit();
        if (url.indexOf("192.168.") != -1) {
            AppApplication.ISDEBUG = true;
        } else {
            AppApplication.ISDEBUG = false;
        }
        Urls.BASIC_URL=url;
        reboot(url);
    }

    /**
     * 切换soap地址
     *
     * @param url
     */
    public void reboot(String url) {
        ToastUtil.showMessage("成功切换服务器url:" + url);
        this.finish();
    }

}
