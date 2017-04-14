package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Pic;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.model.apimodel.APIM_PicList;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.HeaderMerchantDetailView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class MerchantAdminActivity extends BaseActivity {
    @Bind(R.id.btn_exit)
    Button btnExit;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_head)
    LinearLayout llHead;

    private int merchantId;
    private Context mContext;
    private Activity mActivity;
    private M_Merchant merchantInfo;
    private HeaderMerchantDetailView headerMerchantDetailView;

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

        // 设置其他头部
        headerMerchantDetailView = new HeaderMerchantDetailView(mActivity);
        headerMerchantDetailView.fillView(new M_Merchant(), llHead);
        headerMerchantDetailView.unShowLvTop(true);
        headerMerchantDetailView.setImageOnClick(new HeaderMerchantDetailView.ImageOnClick() {
            @Override
            public void imageOnclick(M_Pic pic) {
                merchant_pic_noteList(pic);
            }
        });

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
                    headerMerchantDetailView.dealWithTheView(merchantInfo);
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
                    headerMerchantDetailView.dealWithTheView(merchantInfo, ((APIM_PicList) response).picList);
                } else {
                    ToastUtil.showMessage(((APIM_PicList) response).info);
                    headerMerchantDetailView.dealWithTheView(merchantInfo, null);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantPicListRequest);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
