package com.zemult.merchant.activity.mine;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.AlbumAdpater;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.view.MeasuredGridView;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Pic;
import com.zemult.merchant.mvp.presenter.PicPresenter;
import com.zemult.merchant.mvp.view.IPicView;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.imagepicker.Bimp;
import com.zemult.merchant.util.imagepicker.ChoosePicRec;
import com.zemult.merchant.util.imagepicker.ImageAlbumActivity;
import com.zemult.merchant.util.oss.OssHelper;
import com.zemult.merchant.util.oss.OssService;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;

/**
 * 我的相册 商户相册
 *
 * @author djy
 * @time 2016/11/24 16:03
 */
public class AlbumActivity extends BaseActivity implements AlbumAdpater.ChoosePicCallBack, IPicView {
    public static final String INTENT_MERCHANTID = "merchant_id";
    public static final String INTENT_USERID = "user_id";
    public static final String INTENT_JUSTLOOK = "justlook";

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.gvImgs)
    MeasuredGridView gvImgs;
    @Bind(R.id.ll_del)
    LinearLayout llDel;

    private AlbumAdpater mAdpater;
    protected String tackPhotoName = "", coverPic = "";
    private List<String> photos = new ArrayList<>();
    private PicPresenter picPresenter;
    private int merchantId, userId, merchantJustlook;
    private int callbackcount = 0;
    private List<String> ossImgnameList = new ArrayList<String>();
    private OssService ossService;
    private enum FROM{
        MERCHANT, MERCHANT_JUSTLOOK, ME, OTHERUSER
    }
    private FROM from;

    protected Handler chooseImgHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case Constants.CHOOSE_PHOTOS:
                        photos.clear();
                        // 取消广播监听
                        AlbumActivity.this.unregisterReceiver(choosePicRec);
                        photos.addAll((List<String>) msg.obj);
                        uploadImg();
                        break;
                    case Constants.PHOTO_COMPASS_SUCCESS:
                        // 拍照并 压缩照片成功
                        try {
                            photos.clear();
                            photos.add(msg.obj.toString());
                            uploadImg();
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

    private void uploadImg() {
        if (!photos.isEmpty()) {
            ossImgnameList.clear();
            ossService = OssHelper.initOSS(this);
            for (int i = 0; i < photos.size(); i++) {
                if (SlashHelper.userManager().getUserinfo() != null) {
                    String ossImgname = "app/android_" + SlashHelper.userManager().getUserId() + System.currentTimeMillis() + ".jpg";
                    ossService.asyncPutImage(ossImgname, photos.get(i));
                    ossImgnameList.add(Constants.OSSENDPOINT + ossImgname);
                    Log.d(getClass().getName(), ossImgname);
                }
            }
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_album);
        //注册上传图片广播
        registerReceiver(new String[]{Constants.BROCAST_OSS_UPLOADIMAGE});
    }

    @Override
    public void init() {
        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, -1);
        merchantJustlook = getIntent().getIntExtra(INTENT_JUSTLOOK, -1);
        userId = getIntent().getIntExtra(INTENT_USERID, -1);
        picPresenter = new PicPresenter(listJsonRequest, this);

        if (merchantId != -1){
            if(merchantJustlook != -1)
                from = FROM.MERCHANT_JUSTLOOK;
            else
                from = FROM.MERCHANT;

            lhTvTitle.setText("商家相册");
        }else if (userId != -1){
            if(userId == SlashHelper.userManager().getUserId()){
                from = FROM.ME;
                lhTvTitle.setText("我的相册");
            }else {
                from = FROM.OTHERUSER;
                lhTvTitle.setText("TA的相册");
            }
        }
        if(from == FROM.MERCHANT || from == FROM.ME){
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText("编辑");
        }

        List<M_Pic> pics = new ArrayList<>();
        // 商家相册和个人相册 第一项都为拍照选照片
        if(from == FROM.MERCHANT || from == FROM.ME)
            pics.add(new M_Pic());

        mAdpater = new AlbumAdpater(this, pics);
        gvImgs.setAdapter(mAdpater);
        mAdpater.setChoosePicCallBack(this);

        if (merchantId != -1)
            picPresenter.merchant_picList(merchantId);
        else
            picPresenter.user_picList(userId);
        if(from == FROM.OTHERUSER || from == FROM.MERCHANT_JUSTLOOK)
            mAdpater.unshowAdd(true);
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
                    callbackcount = 0;
                    String pics = "";
                    for (int i = 0; i < ossImgnameList.size(); i++) {
                        pics += ossImgnameList.get(i);
                        if (i != ossImgnameList.size() - 1)
                            pics += ",";
                    }
                    if (merchantId != -1)
                        picPresenter.merchant_pic_add(merchantId, pics);
                    else
                        picPresenter.user_pic_add(userId, pics);
                }
            } else {
                ToastUtils.show(this, intent.getStringExtra("info"));
            }

        }

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right, R.id.ll_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_right:
                if ("编辑".equals(tvRight.getText().toString())) {
                    tvRight.setText("完成");
                    mAdpater.showCheckbox(true);
                    llDel.setVisibility(View.VISIBLE);
                } else {
                    mAdpater.clearCheck(); //清除选中状态
                    tvRight.setText("编辑");
                    mAdpater.showCheckbox(false);
                    llDel.setVisibility(View.GONE);
                }
                break;
            case R.id.ll_del:
                if(mAdpater.getData().size() == 1)
                    ToastUtil.showMessage("没有选中的图片");
                else {
                    String picIds = "";
                    for (int i = 1; i < mAdpater.getData().size(); i++){
                        if(mAdpater.getData().get(i).isSelected())
                            picIds += mAdpater.getData().get(i).picId + ",";
                    }
                    if(picIds.equals(""))
                        ToastUtil.showMessage("没有选中的图片");
                    else {
                        if(picIds.endsWith(","))
                            picIds = picIds.substring(0, picIds.length() -1);
                        if (merchantId != -1)
                            picPresenter.merchant_pic_del(merchantId, picIds);
                        else
                            picPresenter.user_pic_del(userId, picIds);
                    }
                }
                break;
        }
    }

    @Override
    public void takePic() {
        compareState();
        tackPhotoName = AppUtils.getStringToday() + ".jpg";
        AppUtils.tackPic(this, tackPhotoName, Constants.TACKPHOTO);
    }

    @Override
    public void choosePic() {
        compareState();
        int num = Constants.ABLUM_NUMS - mAdpater.getData().size() - 1;
        if (num >= Constants.DEFAULT_IMAGE_MAX_SIZE)
            num = Constants.DEFAULT_IMAGE_MAX_SIZE;
        chooseImgs(this, num, choosePicRec);
    }

    @Override
    public void picDetail(int pos, List<String> pics, List<Integer> picIds) {
        compareState();
        if (from == FROM.MERCHANT)
            AppUtils.toImageDetial(AlbumActivity.this, pos, pics, picIds , true ,true, false,merchantId, userId);
        else if(from == FROM.ME)
            AppUtils.toImageDetial(AlbumActivity.this, pos, pics, picIds , true ,false, false,merchantId, userId);
        else
            AppUtils.toImageDetial(AlbumActivity.this, pos, pics, null , false ,false, false,merchantId, userId);
    }

    private void compareState(){
        mAdpater.clearCheck(); //清除选中状态
        tvRight.setText("编辑");
        mAdpater.showCheckbox(false);
        llDel.setVisibility(View.GONE);
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
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // 拍照
            if (requestCode == Constants.TACKPHOTO)
                AppUtils.tackPickResult(tackPhotoName, chooseImgHandler);
            if(resultCode == RESULT_OK && requestCode == Constants.IMAGEDITAL){
                if (merchantId != -1)
                picPresenter.merchant_picList(merchantId);
                else
                picPresenter.user_picList(userId);

                coverPic = data.getStringExtra("coverPic");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showProgressDialog() {
        showPd();
    }

    @Override
    public void hideProgressDialog() {
        dismissPd();
    }

    @Override
    public void showError(String error) {
        ToastUtil.showMessage(error);
    }

    @Override
    public void setPicList(List<M_Pic> list) {
        if (from == FROM.MERCHANT || from == FROM.ME){
            if (list.isEmpty()) {
                list = new ArrayList<>();
            }
            list.add(0, new M_Pic());
        }

        mAdpater.setData(list);
    }

    @Override
    public void addPicSuccess() {
        if (merchantId != -1)
            picPresenter.merchant_picList(merchantId);
        else
            picPresenter.user_picList(userId);

    }

    @Override
    public void delPicSuccess() {
        if (merchantId != -1)
            picPresenter.merchant_picList(merchantId);
        else
            picPresenter.user_picList(userId);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("coverPic", coverPic);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

}
