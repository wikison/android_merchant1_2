package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.aip.mine.MerchantPicListRequest;
import com.zemult.merchant.aip.mine.MerchantPicNoteListRequest;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.aip.slash.UserAddSaleUserRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Pic;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.model.apimodel.APIM_PicList;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FNRadioGroup;
import com.zemult.merchant.view.HeaderMerchantDetailView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class BindMerchantActivity extends BaseActivity {
    public static int MODIFY_TAG = 111;
    public static int MODIFY_POSITION = 555;

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_head)
    LinearLayout llHead;
    @Bind(R.id.tv_service_position)
    TextView tvServicePosition;
    @Bind(R.id.rl_service_position)
    RelativeLayout rlServicePosition;
    @Bind(R.id.tv_text_service)
    TextView tvTextService;
    @Bind(R.id.rg_ta_service)
    FNRadioGroup rgTaService;
    @Bind(R.id.tv_service)
    TextView tvService;
    @Bind(R.id.rl_service)
    RelativeLayout rlService;
    @Bind(R.id.btn_bind)
    Button btnBind;

    private HeaderMerchantDetailView headerMerchantDetailView;
    private Context mContext;
    private Activity mActivity;
    private M_Merchant merchantInfo;
    private int merchantId;
    private String positionName, tags;

    @Override
    public void setContentView() {
        merchantId = getIntent().getIntExtra(MerchantDetailActivity.MERCHANT_ID, -1);
        mContext = this;
        mActivity = this;
        setContentView(R.layout.activity_bind_merchant);
    }

    @Override
    public void init() {
        lhTvTitle.setText("绑定商户");

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

    /**
     * 商家详情
     */
    private MerchantInfoRequest request;
    private void merchant_info() {
        showPd();
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

    /**
     * 用户申请成为商家的营销经理
     */
    UserAddSaleUserRequest userAddSaleUserRequest;

    private void user_add_saleuser() {
        showPd();
        if (userAddSaleUserRequest != null) {
            userAddSaleUserRequest.cancel();
        }
        UserAddSaleUserRequest.Input input = new UserAddSaleUserRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.position = positionName;
        input.tags = tags;

        input.convertJosn();
        userAddSaleUserRequest = new UserAddSaleUserRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    Intent intent = new Intent(Constants.BROCAST_BE_SERVER_MANAGER_SUCCESS);
                    sendBroadcast(intent);

                    finish();
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userAddSaleUserRequest);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MODIFY_TAG) {
                tags = data.getStringExtra("tags");
                initTags();
            } else if (requestCode == MODIFY_POSITION) {
                positionName = data.getStringExtra("position_name");
                tvServicePosition.setText(positionName);
            }
        }
    }

    private void initTags() {
        rgTaService.setChildMargin(0, 24, 24, 0);
        rgTaService.removeAllViews();
        if (!StringUtils.isBlank(tags)) {
            List<String> tagList = new ArrayList<String>(Arrays.asList(tags.split(",")));
            int iShowSize = tagList.size();
            if (iShowSize > 0) {
                rgTaService.setVisibility(View.VISIBLE);

                for (int i = 0; i < iShowSize; i++) {
                    GradientDrawable drawable = new GradientDrawable();
                    drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE); // 画框
                    drawable.setCornerRadii(new float[]{50,
                            50, 50, 50, 50, 50, 50, 50});
                    drawable.setColor(0xffe8e8e8);  // 边框内部颜色
                    RadioButton rbTag = new RadioButton(mContext);
                    rbTag.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
                    rbTag.setTextSize(12);
                    rbTag.setPadding(22, 8, 22, 8);
                    rbTag.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rbTag.setTextColor(0xff464646);
                    rbTag.setText(tagList.get(i).toString());

                    rgTaService.addView(rbTag);
                }
            } else {
                rgTaService.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bind, R.id.rl_service_position, R.id.rl_service})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.rl_service:
                intent = new Intent(mActivity, TabManageActivity.class);
                intent.putExtra(TabManageActivity.TAG, merchantId);
                intent.putExtra(TabManageActivity.NAME, merchantInfo.name);
                intent.putExtra(TabManageActivity.COMEFROM, 1);
                startActivityForResult(intent, MODIFY_TAG);
                break;
            case R.id.rl_service_position:
                intent = new Intent(mActivity, PositionSetActivity.class);
                intent.putExtra("position_name", "");
                startActivityForResult(intent, MODIFY_POSITION);
                break;
            case R.id.btn_bind:
                if(StringUtils.isBlank(tags)){
                    ToastUtil.showMessage("请选择管家服务");
                    return;
                }
                user_add_saleuser();
                break;
        }
    }
}
