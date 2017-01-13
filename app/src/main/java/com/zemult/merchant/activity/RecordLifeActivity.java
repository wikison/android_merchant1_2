package com.zemult.merchant.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.discover.ManagerNews_addRequest;
import com.zemult.merchant.app.BaseImageChooseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.imagepicker.ImagePickerAdapter;
import com.zemult.merchant.util.oss.OssHelper;
import com.zemult.merchant.util.oss.OssService;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;


public class RecordLifeActivity extends BaseImageChooseActivity implements
        ImagePickerAdapter.ChooseImageAdapterCallBack {
    protected List<String> ossImgnameList = new ArrayList<String>();
    String content;
    int num = 100, callbackcount = 0;
    TextView editnum, tv;
    ManagerNews_addRequest manager_news_addrequest;
    int industryId;
    String industryName;
    private EditText etContent;
    private OssService ossService;

    protected void beforeInit() {
    }

    @Override
    protected void initView() {
        setTitleText("心情小记");
        setTitleLeftButton("");
        setTitleRightButton("发布");
        industryId = getIntent().getIntExtra("industryId", 0);
        industryName = getIntent().getStringExtra("industryName");

        View appView = getLayoutInflater().inflate(
                R.layout.activity_recordlife, null);
        appMainView.addView(appView, layoutParams);
        etContent = (EditText) appView.findViewById(R.id.etContent);
        editnum = (TextView) appView.findViewById(R.id.editnum);
        tv = (TextView) appView.findViewById(R.id.tv);
        if (!TextUtils.isEmpty(industryName))
            tv.setText(String.format("来自%sde角色", industryName));

        EditFilter.WordFilter(etContent, 500, editnum);
    }

    @Override
    protected void initData() {
        //注册上传图片广播
        registerReceiver(new String[]{Constants.BROCAST_OSS_UPLOADIMAGE});
    }


    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_OSS_UPLOADIMAGE.equals(intent.getAction())) {
            if (intent.getStringExtra("status").equals("ok")) {
                ++callbackcount;
                if (ossImgnameList.size() == callbackcount) {
                    manager_news_add();
                }
            } else {
                ToastUtils.show(this, intent.getStringExtra("info"));
            }

        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.top_right_btn:
                uploadImage();
                break;
            default:
                break;
        }
    }

    /**
     * 上传图片
     */
    private void uploadImage() {
        content = etContent.getText().toString().trim();
        if ((content == null || "".equals(content)) && (photos.isEmpty())) {
            Toast.makeText(this, "不能发布空的内容", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.length() < 10) {
            ToastUtil.showMessage("文字内容不得少于10个字");
            return;
        }
        showPd();
        if (photos.size() > 0) {
            //有图片
            ossService = OssHelper.initOSS(this);
            for (int i = 0; i < photos.size(); i++) {
                if (SlashHelper.userManager().getUserinfo() != null) {
                    String ossImgname = "app/android_" + SlashHelper.userManager().getUserId() + System.currentTimeMillis() + ".jpg";
                    ossService.asyncPutImage(ossImgname, photos.get(i));
                    ossImgnameList.add(Constants.OSSENDPOINT + ossImgname);
                    Log.d(getClass().getName(), ossImgname);
                }

            }
        } else {
            //没图片
            manager_news_add();
        }
    }

    //用户(经营人)发布方案
    void manager_news_add() {
        if (manager_news_addrequest != null) {
            manager_news_addrequest.cancel();
        }

        ManagerNews_addRequest.Input input = new ManagerNews_addRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.industryId = industryId;
        input.note = content;
        for (int i = 0; i < ossImgnameList.size(); i++) {
            if (i != ossImgnameList.size() - 1) {
                input.pic += ossImgnameList.get(i) + ",";
            } else {
                input.pic += ossImgnameList.get(i);
            }
        }
        input.convertJosn();

        ossImgnameList.clear();
        callbackcount = 0;
        manager_news_addrequest = new ManagerNews_addRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getName(), error.toString());
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("发布成功");
                    Intent intent = new Intent(Constants.BROCAST_FRESHSLASH);
                    sendBroadcast(intent);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(manager_news_addrequest);
    }


}
