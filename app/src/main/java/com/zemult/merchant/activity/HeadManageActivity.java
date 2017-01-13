package com.zemult.merchant.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserEditinfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.PermissionTest;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.imagepicker.Bimp;
import com.zemult.merchant.util.imagepicker.ChoosePicRec;
import com.zemult.merchant.util.imagepicker.ImageAlbumActivity;
import com.zemult.merchant.util.oss.OssHelper;
import com.zemult.merchant.util.oss.OssService;
import com.zemult.merchant.view.common.MMAlert;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class HeadManageActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_bg)
    ImageView ivBg;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;

    private Context mContext;
    private String headString = "", tackPhotoName = "";

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_head_manage);
    }

    @Override
    public void init() {
        lhTvTitle.setText("更换头像");
        mContext = this;
        registerReceiver(new String[]{Constants.BROCAST_OSS_UPLOADIMAGE});
        llRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.gengduo_icon);
        imageManager.loadUrlImage(SlashHelper.userManager().getUserinfo().getHead(), ivBg);
        headString = SlashHelper.userManager().getUserinfo().getHead();
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.ll_right, R.id.iv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll_right:
            case R.id.iv_right:
                MMAlert.addPhotoDialog(mContext, new MMAlert.AddPhotoCallback() {
                    @Override
                    public void onTakePic() {
                        PermissionTest pt = new PermissionTest();
                        if (pt.isCameraPermission()) {
                            takePic();
                        } else {
                            ToastUtil.showMessage("需要允许使用相机权限");
                        }
                    }

                    @Override
                    public void onChoosePoc() {
                        choosePic();
                    }
                });
                break;
        }
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
                headString = Constants.OSSENDPOINT + intent.getStringExtra("object");
                //修改本地IM的头像
                YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                imKit.getContactService().clearContactInfoCache(SlashHelper.userManager().getUserId() + "", imKit.getIMCore().getAppKey());
                user_editinfo();
            } else {
                ToastUtils.show(this, intent.getStringExtra("info"));
            }
        }
    }


    /**
     * 拍照
     */
    public void takePic() {
        tackPhotoName = AppUtils.getStringToday() + ".jpg";
        AppUtils.tackPic(this, AppUtils.getStringToday() + ".jpg", Constants.TACKPHOTO);
    }

    /**
     * 选择本地图片
     */
    public void choosePic() {
        chooseImgs(this, 1, choosePicRec);
    }

    /**
     * 从相册选择多张图片,注意: 传入的activity需要解绑ChoosePicRec
     *
     * @param activity
     * @param maxNum   最大可挑选的数量
     */
    public void chooseImgs(Activity activity, int maxNum, ChoosePicRec choosePicRec) {
        try {
            Bimp.clear();
            IntentFilter intentFilter = new IntentFilter("CHOOSEIMG");
            Intent intent = new Intent(activity, ImageAlbumActivity.class);
            activity.registerReceiver(choosePicRec, intentFilter);
            intent.putExtra("maxSize", maxNum);
            intent.putExtra("chooseHead", maxNum);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // 拍照
            if (resultCode == RESULT_OK && requestCode == Constants.TACKPHOTO)
                AppUtils.tackPickResult(tackPhotoName, chooseImgHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    UserEditinfoRequest userEditinfoRequest;

    //修改用户资料信息
    private void user_editinfo() {
        showPd();
        if (userEditinfoRequest != null) {
            userEditinfoRequest.cancel();
        }
        UserEditinfoRequest.Input input = new UserEditinfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
            input.name = SlashHelper.userManager().getUserinfo().getName();
            input.head = headString;
            input.account = SlashHelper.userManager().getUserinfo().getAccount();
            input.sex = SlashHelper.userManager().getUserinfo().getSex();
            input.company = SlashHelper.userManager().getUserinfo().getCompany();
            input.position = SlashHelper.userManager().getUserinfo().getPosition();
            input.isOpen = SlashHelper.userManager().getUserinfo().getIsOpen();
            input.province = SlashHelper.userManager().getUserinfo().getProvince();
            input.city = SlashHelper.userManager().getUserinfo().getCity();
            input.note = SlashHelper.userManager().getUserinfo().getNote();
            input.convertJosn();
        }

        userEditinfoRequest = new UserEditinfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    SlashHelper.userManager().getUserinfo().setHead(headString);
                    imageManager.loadUrlImage(headString, ivBg);

                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userEditinfoRequest);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }



    protected Handler chooseImgHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case Constants.CHOOSE_PHOTOS:
                        // 取消广播监听
                        HeadManageActivity.this.unregisterReceiver(choosePicRec);
                        uploadImg(((List<String>) msg.obj).get(0));
                        break;
                    case Constants.PHOTO_COMPASS_SUCCESS:
                        // 拍照并 压缩照片成功
                        try {
                            uploadImg(msg.obj.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    protected ChoosePicRec choosePicRec = new ChoosePicRec(chooseImgHandler);

    private void uploadImg(String path) {
        showUncanclePd();
        OssService ossService = OssHelper.initOSS(mContext);
        if (SlashHelper.userManager().getUserinfo() != null) {
            String ossImgname = "app/android_" + SlashHelper.userManager().getUserId() + System.currentTimeMillis() + ".jpg";
            ossService.asyncPutImage(ossImgname, path);
            Log.d(getClass().getName(), ossImgname);
        }
        dismissPd();
    }

}



