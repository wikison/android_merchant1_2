package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.aip.mine.UserEditinfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ImageHelper;
import com.zemult.merchant.util.PermissionTest;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.imagepicker.Bimp;
import com.zemult.merchant.util.imagepicker.ChoosePicRec;
import com.zemult.merchant.util.imagepicker.ImageAlbumActivity;
import com.zemult.merchant.util.oss.OssHelper;
import com.zemult.merchant.util.oss.OssService;
import com.zemult.merchant.view.common.MMAlert;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

//1010A成为服务管家1
public class BeManagerFirstActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.btn_next)
    Button btnNext;

    private Context mContext;
    private Activity mActivity;
    private String headString = "", tackPhotoName = "", imageUrl;
    private Uri imageUri;
    private int merchantId;
    private String name = "";

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            compare();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void compare() {
        if (etName.toString().length() > 0
                && !StringUtils.isBlank(headString)) {
            if (etName.getText().toString().length() > 0) {
                btnNext.setEnabled(true);
                btnNext.setBackgroundResource(R.drawable.common_selector_btn);
            }
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackgroundResource(R.drawable.next_bg_btn_select);
        }
    }


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_be_manager_first);
    }

    @Override
    public void init() {
        lhTvTitle.setText("成为服务管家");
        mContext = this;
        mActivity = this;

        merchantId = getIntent().getIntExtra(TabManageActivity.TAG, -1);
        name = getIntent().getStringExtra(TabManageActivity.NAME);

        btnNext.setEnabled(false);
        btnNext.setBackgroundResource(R.drawable.next_bg_btn_select);
        etName.addTextChangedListener(watcher);
        registerReceiver(new String[]{Constants.BROCAST_OSS_UPLOADIMAGE, Constants.BROCAST_BE_SERVER_MANAGER_SUCCESS});
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
                compare();
            } else {
                ToastUtils.show(this, intent.getStringExtra("info"));
            }
        }else if(Constants.BROCAST_BE_SERVER_MANAGER_SUCCESS.equals(intent.getAction())){
            onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_head, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_head:
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
            case R.id.btn_next:
                user_editinfo();
                break;
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
            if (resultCode == RESULT_OK && requestCode == Constants.TACKPHOTO){
                AppUtils.tackPickResult(tackPhotoName, chooseImgHandler);
            }
            // 裁剪
            else if(resultCode == RESULT_OK && requestCode == Constants.PHOTO_CROP){
                if (data != null) {
                    if (imageUri != null) {
                        Bitmap bitmap = decodeUriAsBitmap(imageUri);
                        ivHead.setImageBitmap(bitmap);
                        String path = AppUtils.removeFileHeader(ImageHelper.saveRotateNoCompressBitmap(new File(imageUrl)));
                        if (bitmap != null && bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                        uploadImg(path);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            // 先通过getContentResolver方法获得一个ContentResolver实例，
            // 调用openInputStream(Uri)方法获得uri关联的数据流stream
            // 把上一步获得的数据流解析成为bitmap
            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    protected Handler chooseImgHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case Constants.CHOOSE_PHOTOS:
                        // 取消广播监听
                        mContext.unregisterReceiver(choosePicRec);
                        crop(((List<String>) msg.obj).get(0));
                        break;
                    case Constants.PHOTO_COMPASS_SUCCESS:
                        // 拍照并 压缩照片成功
                        try {
                            crop(msg.obj.toString());
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

    private void crop(String path){
        imageUrl = Constants.SAVE_IMAGE_PATH_IMGS + new SimpleDateFormat("yyMMddHHmmss")
                .format(new Date()) + ".jpg";
        imageUri = Uri.fromFile(new File(imageUrl));

        Intent intent = new Intent();

        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(path.replace("file://", ""))), "image/*");// mUri是已经选择的图片Uri
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 400);// 裁剪框比例
        intent.putExtra("aspectY", 400);
        intent.putExtra("outputX", 400);// 输出图片大小
        intent.putExtra("outputY", 400);
//        intent.putExtra("return-data", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        mActivity.startActivityForResult(intent, Constants.PHOTO_CROP);
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
            input.name = etName.getText().toString();
            input.head = headString;
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
                    SlashHelper.userManager().getUserinfo().setName(etName.getText().toString());

                    Intent intent = new Intent(mActivity, TabManageActivity.class);
                    intent.putExtra(TabManageActivity.TAG, merchantId);
                    intent.putExtra(TabManageActivity.NAME, name);
                    intent.putExtra(TabManageActivity.COMEFROM, 3);
                    startActivity(intent);
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userEditinfoRequest);
    }


}
