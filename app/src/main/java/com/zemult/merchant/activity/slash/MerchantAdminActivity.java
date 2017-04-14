package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.MerchantPicListRequest;
import com.zemult.merchant.aip.mine.MerchantPicNoteListRequest;
import com.zemult.merchant.aip.mine.UserSaleMerchantDelRequest;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Ad;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Pic;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.model.apimodel.APIM_PicList;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.HeaderAdViewView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class MerchantAdminActivity extends BaseActivity {

    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.tv_num)
    TextView tvNum;
    @Bind(R.id.btn_exit)
    Button btnExit;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.ll_ad_container)
    LinearLayout llAdContainer;

    private int merchantId;
    private Context mContext;
    private Activity mActivity;
    private M_Merchant merchantInfo;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_merchant_admin);
    }

    @Override
    public void init() {
        merchantId = getIntent().getIntExtra(MerchantDetailActivity.MERCHANT_ID, -1);
        mContext = this;
        mActivity = this;
        lhTvTitle.setText("商户管理");

        merchant_info();
    }

    private MerchantInfoRequest request;

    /**
     * 商家详情
     */
    private void merchant_info() {
        if (request != null) {
            request.cancel();
        }
        MerchantInfoRequest.Input input = new MerchantInfoRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.convertJosn();
        request = new MerchantInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetinfo) response).status == 1) {
                    merchantInfo = ((APIM_MerchantGetinfo) response).merchant;
                    // 名字
                    if (!TextUtils.isEmpty(merchantInfo.name))
                        tvName.setText(merchantInfo.name);
                    // 详细地址
                    if (!TextUtils.isEmpty(merchantInfo.address))
                        tvAddress.setText(merchantInfo.address);
                    tvNum.setText(merchantInfo.payNum + "人找人服务");
                    tvMoney.setText((int) (merchantInfo.perMoney) + "");
                    merchant_picList();
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(request);
    }

    /**
     * 获取商家详情的图片列表(非证件照)
     */
    private MerchantPicListRequest merchantPicListRequest;

    private void merchant_picList() {
        showPd();
        if (merchantPicListRequest != null) {
            merchantPicListRequest.cancel();
        }
        MerchantPicListRequest.Input input = new MerchantPicListRequest.Input();
        input.merchantId = merchantId;
        input.page = 1;
        input.rows = 5;
        input.convertJosn();

        merchantPicListRequest = new MerchantPicListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_PicList) response).status == 1) {
                    dealWithTheView(merchantInfo, ((APIM_PicList) response).picList);
                } else {
                    ToastUtil.showMessage(((APIM_PicList) response).info);
                    dealWithTheView(merchantInfo, null);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantPicListRequest);
    }

    private void dealWithTheView(final M_Merchant merchantInfo, final List<M_Pic> picList) {
        if (picList == null || picList.isEmpty()) {
            ivCover.setVisibility(View.VISIBLE);
            llAdContainer.setVisibility(View.GONE);
            // 封面
            if (!StringUtils.isBlank(merchantInfo.pic))
                imageManager.loadUrlImageWithDefaultImg(merchantInfo.pic, ivCover, "@450h", R.mipmap.merchant_default_cover);
            else
                ivCover.setImageResource(R.mipmap.merchant_default_cover);
        } else {
            ivCover.setVisibility(View.GONE);
            llAdContainer.setVisibility(View.VISIBLE);
            // 设置广告数据 加入到smoothListView的headerView
            List<M_Ad> advertList = new ArrayList<>();

            for(M_Pic pic : picList){
                M_Ad ad = new M_Ad();
                ad.setImg(pic.picPath);
                ad.setNote(pic.note);
                advertList.add(ad);
            }
            HeaderAdViewView headerAdViewView = new HeaderAdViewView(mActivity, DensityUtil.dip2px(mContext, 200));
            headerAdViewView.setShowType(3);
            headerAdViewView.setRotate(false);
            headerAdViewView.setShowTitle(true);
            headerAdViewView.fillView(advertList, llAdContainer);

            headerAdViewView.setImageOnClick(new HeaderAdViewView.ImageOnClick() {
                @Override
                public void imageOnclick(int postion) {
                    merchant_pic_noteList(picList.get(postion));
                }
            });
        }
    }

    /**
     * 获取商家详情的图片对应的描述列表
     */
    private MerchantPicNoteListRequest picNoteListRequest;

    private void merchant_pic_noteList(final M_Pic pic) {
        showPd();
        if (picNoteListRequest != null) {
            picNoteListRequest.cancel();
        }
        MerchantPicNoteListRequest.Input input = new MerchantPicNoteListRequest.Input();
        input.picId = pic.picId;
        input.convertJosn();

        picNoteListRequest = new MerchantPicNoteListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_PicList) response).status == 1) {
                    List<M_Pic> noteList = ((APIM_PicList) response).noteList;
                    List<String> pics = new ArrayList<>();
                    List<String> notes = new ArrayList<>();
                    if (noteList == null) {
                        noteList = new ArrayList<>();
                    }
                    for (M_Pic pic : noteList) {
                        pics.add(StringUtils.isBlank(pic.picPath) ? "" : pic.picPath);
                        notes.add(StringUtils.isBlank(pic.note) ? "" : pic.note);
                    }
                    AppUtils.toImageDetial(mActivity, 0, pics, notes);
                } else {
                    ToastUtil.showMessage(((APIM_PicList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(picNoteListRequest);
    }

    private UserSaleMerchantDelRequest userSaleMerchantDelRequest;
    public void userSaleMerchantDel() {
        showUncanclePd();
        if (userSaleMerchantDelRequest != null) {
            userSaleMerchantDelRequest.cancel();
        }
        UserSaleMerchantDelRequest.Input input = new UserSaleMerchantDelRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.merchantId = merchantId;

        input.convertJosn();
        userSaleMerchantDelRequest = new UserSaleMerchantDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(mContext, "退出成功");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userSaleMerchantDelRequest);
    }

    @OnClick({R.id.btn_exit, R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_exit:
                userSaleMerchantDel();
                break;
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }
}
