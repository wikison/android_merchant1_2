package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.MerchantEditinfoRequest;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

/**
 * 我是商家--商户管理
 *
 * @author djy
 * @time 2016/8/4 14:27
 */
public class MerchantManageActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.tv_fullname)
    TextView tvFullname;
    @Bind(R.id.tv_classify)
    TextView tvClassify;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.iv3)
    ImageView iv3;
    @Bind(R.id.iv2)
    ImageView iv2;
    @Bind(R.id.iv1)
    ImageView iv1;
    @Bind(R.id.ll_pic)
    LinearLayout llPic;
    @Bind(R.id.tv_account)
    TextView tvAccount;
    @Bind(R.id.tv_tel)
    TextView tvTel;

    private Context mContext;
    private static final int REQ_ALBUM = 0x110;
    public static final String INTENT_ID = "merchantId";
    private int merchantId;

    private M_Merchant mMerchant;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_business_manage);
    }

    @Override
    public void init() {
        mContext = this;
        merchantId = getIntent().getIntExtra(INTENT_ID, -1);
        lhTvTitle.setText("商家管理");

        merchant_getinfo();
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.ll_pic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll_pic:
                Intent intent2 = new Intent(mContext, AlbumActivity.class);
                intent2.putExtra(AlbumActivity.INTENT_MERCHANTID, merchantId);
                startActivityForResult(intent2, REQ_ALBUM);
                break;
        }
    }

    /**
     * 获取商户(场景)信息
     */
    private MerchantInfoRequest merchantGetinfoRequest;

    private void merchant_getinfo() {
        showPd();
        if (merchantGetinfoRequest != null) {
            merchantGetinfoRequest.cancel();
        }

        MerchantInfoRequest.Input input = new MerchantInfoRequest.Input();
        input.merchantId = merchantId;

        input.convertJosn();
        merchantGetinfoRequest = new MerchantInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetinfo) response).status == 1) {
                    mMerchant = ((APIM_MerchantGetinfo) response).merchant;
                    tvFullname.setText(mMerchant.name);
                    tvClassify.setText(mMerchant.industryName);
                    tvAddress.setText(mMerchant.address);
                    if(!TextUtils.isEmpty(mMerchant.tel))
                        tvTel.setText(mMerchant.tel.substring(0,3) + "****" + mMerchant.tel.substring(mMerchant.tel.length()-4,mMerchant.tel.length()));

                    if(!TextUtils.isEmpty(mMerchant.bankCard))
                        tvAccount.setText(mMerchant.bankCard.substring(0,3) + "****" + mMerchant.bankCard.substring(mMerchant.bankCard.length()-4,mMerchant.bankCard.length()));

                    if (!TextUtils.isEmpty(mMerchant.pics)) {
                        if (mMerchant.pics.contains(",")) {
                            String[] photos = mMerchant.pics.split(",");
                            String[] photosarray = new String[3];

                            if (photos.length > 3) {
                                for (int i = 0; i < 3; i++) {
                                    photosarray[i] = photos[i];
                                }
                            } else {
                                for (int i = 0; i < photos.length; i++) {
                                    photosarray[i] = photos[i];
                                }
                            }
                            switch (photosarray.length) {
                                case 2:
                                    iv1.setVisibility(View.VISIBLE);
                                    imageManager.loadUrlImage(photosarray[0], iv1);
                                    iv2.setVisibility(View.VISIBLE);
                                    imageManager.loadUrlImage(photosarray[1], iv2);
                                    break;
                                case 3:
                                    iv1.setVisibility(View.VISIBLE);
                                    imageManager.loadUrlImage(photosarray[0], iv1);
                                    iv2.setVisibility(View.VISIBLE);
                                    imageManager.loadUrlImage(photosarray[1], iv2);
                                    iv3.setVisibility(View.VISIBLE);
                                    imageManager.loadUrlImage(photosarray[2], iv3);
                                    break;
                            }
                        } else {
                            iv1.setVisibility(View.VISIBLE);
                            imageManager.loadUrlImage(mMerchant.pics, iv1);
                        }
                    }
                } else {
                    ToastUtil.showMessage(((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantGetinfoRequest);
    }
    /**
     * 修改商家(场景)信息
     */
    private MerchantEditinfoRequest editinfoRequest;
    private void merchant_editinfo(String coverPic) {
        showPd();
        if (editinfoRequest != null) {
            editinfoRequest.cancel();
        }

        MerchantEditinfoRequest.Input input = new MerchantEditinfoRequest.Input();
        input.merchantId = merchantId;
        input.pic = coverPic;

        input.convertJosn();
        editinfoRequest = new MerchantEditinfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                } else {
                    ToastUtil.showMessage(((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(editinfoRequest);
    }

    /**
     * ==================================处理刷新请求=================================================
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && requestCode == REQ_ALBUM) {
                merchant_getinfo();
                String coverPic = data.getStringExtra("coverPic");
                if(!StringUtils.isBlank(coverPic))
                    merchant_editinfo(coverPic);

            }
    }

}
