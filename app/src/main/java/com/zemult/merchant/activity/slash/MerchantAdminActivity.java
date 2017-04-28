package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.adapter.slashfrgment.HomePeopleAdapter;
import com.zemult.merchant.aip.mine.MerchantPicListRequest;
import com.zemult.merchant.aip.mine.MerchantPicNoteListRequest;
import com.zemult.merchant.aip.mine.UserSaleMerchantDelRequest;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.aip.slash.MerchantSaleuserListRequest;
import com.zemult.merchant.aip.slash.User2SaleMerchantEditRequest;
import com.zemult.merchant.aip.slash.User2SaleUserInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Pic;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.model.apimodel.APIM_PicList;
import com.zemult.merchant.model.apimodel.APIM_SearchUsersList;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FNRadioGroup;
import com.zemult.merchant.view.HeaderMerchantDetailView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
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
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.tv_service_position)
    TextView tvServicePosition;
    @Bind(R.id.rl_service_position)
    RelativeLayout rlServicePosition;
    @Bind(R.id.rg_ta_service)
    FNRadioGroup rgTaService;
    @Bind(R.id.tv_service)
    TextView tvService;
    @Bind(R.id.rl_service)
    RelativeLayout rlService;

    public static int MODIFY_TAG = 111;
    public static int MODIFY_POSITION = 555;


    private int merchantId;
    private Context mContext;
    private Activity mActivity;
    private M_Merchant merchantInfo, merchant;
    private HeaderMerchantDetailView headerMerchantDetailView;

    MerchantInfoRequest request;
    User2SaleUserInfoRequest user2SaleUserInfoRequest;
    UserSaleMerchantDelRequest userSaleMerchantDelRequest;
    MerchantPicNoteListRequest picNoteListRequest;
    MerchantPicListRequest merchantPicListRequest;
    MerchantSaleuserListRequest allRequest;
    User2SaleMerchantEditRequest user2SaleMerchantEditRequest;

    String strPosition = "";

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_merchant_admin);
    }

    @Override
    public void init() {
        merchantId = getIntent().getIntExtra(MerchantDetailActivity.MERCHANT_ID, -1);
        merchant = (M_Merchant) getIntent().getSerializableExtra(UserDetailActivity.MERCHANT_INFO);
        mContext = this;
        mActivity = this;
        lhTvTitle.setText("商户管理");

        setSaleMerchantInfo(merchant);

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
        merchant2_saleuserList();
    }

    private void setSaleMerchantInfo(M_Merchant merchant) {
        if (merchant != null) {
            strPosition = (merchant.position == null ? "" : (merchant.position.equals("无") ? "" : merchant.position));
            tvServicePosition.setText(strPosition);

            initTags(merchant);
        }
    }


    /**
     * 商家详情
     */
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

    private void user_saleUser_info() {
        if (user2SaleUserInfoRequest != null) {
            user2SaleUserInfoRequest.cancel();
        }
        User2SaleUserInfoRequest.Input input = new User2SaleUserInfoRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.saleUserId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantInfo.merchantId;
        input.center = (String) SPUtils.get(mContext, Constants.SP_CENTER, "119.971736,31.829737");
        input.convertJosn();
        user2SaleUserInfoRequest = new User2SaleUserInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetinfo) response).status == 1) {
                    merchant = ((APIM_MerchantGetinfo) response).saleUserInfo;
                    setSaleMerchantInfo(merchant);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(user2SaleUserInfoRequest);
    }

    /**
     * 商家下的服务管家列表-全部          1:去掉熟人和自己,2:排序规则：最近有交易的在前，等级高的在前
     */
    private void merchant2_saleuserList() {
        showPd();
        if (allRequest != null) {
            allRequest.cancel();
        }
        MerchantSaleuserListRequest.Input input = new MerchantSaleuserListRequest.Input();

        input.operateUserId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.page = 1;
        input.rows = 4;
        input.convertJosn();
        allRequest = new MerchantSaleuserListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_SearchUsersList) response).status == 1) {
                    List<M_Userinfo> userList = ((APIM_SearchUsersList) response).userList;
                    String saleUserHeads = "";
                    if (userList != null && !userList.isEmpty()) {
                        for (M_Userinfo userinfo : userList) {
                            saleUserHeads += userinfo.getUserHead() + ",";
                        }
                        // 营销经理们的头像
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        recyclerview.setLayoutManager(linearLayoutManager);
                        //设置适配器
                        HomePeopleAdapter adapter = new HomePeopleAdapter(mContext, saleUserHeads);
                        recyclerview.setAdapter(adapter);

                        recyclerview.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                Intent intent = new Intent(mContext, MerchantDetailActivity.class);
                                intent.putExtra(MerchantDetailActivity.MERCHANT_ID, merchantId);
                                startActivity(intent);
                                return true;
                            }
                        });
                    }
                } else {
                    ToastUtils.show(mContext, ((APIM_SearchUsersList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(allRequest);
    }

    /**
     * 获取商家详情的图片列表(非证件照)
     */
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
                    Intent data = new Intent();
                    data.putExtra("exit_success", 1);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userSaleMerchantDelRequest);
    }

    private void userEditInfo() {
        if (user2SaleMerchantEditRequest != null) {
            user2SaleMerchantEditRequest.cancel();
        }
        showPd();
        User2SaleMerchantEditRequest.Input input = new User2SaleMerchantEditRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.tags = merchant.tags;
        input.state = merchant.state;
        input.position = strPosition.equals("") ? "无" : strPosition;
        input.convertJosn();
        user2SaleMerchantEditRequest = new User2SaleMerchantEditRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    user_saleUser_info();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(user2SaleMerchantEditRequest);
    }

    @OnClick({R.id.btn_exit, R.id.lh_btn_back, R.id.ll_back, R.id.ll_photo, R.id.ll_merchant, R.id.rl_service_position, R.id.rl_service})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_exit:
                userSaleMerchantDel();
                break;
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll_photo:
                if (merchantInfo != null) {
                    List<String> adList = new ArrayList<>();
                    if (StringUtils.isBlank(merchantInfo.pics)) {
                        adList.add(merchantInfo.pic);
                    } else {
                        adList = Arrays.asList(merchantInfo.pics.split(","));
                    }

                    AppUtils.toImageDetial(mActivity, 0, adList, new ArrayList<String>());
                }
                break;
            case R.id.ll_merchant:
                intent = new Intent(mContext, MerchantDetailActivity.class);
                intent.putExtra(MerchantDetailActivity.MERCHANT_ID, merchantId);
                startActivity(intent);
                break;
            case R.id.rl_service:
                intent = new Intent(mActivity, TabManageActivity.class);
                intent.putExtra(TabManageActivity.TAG, merchantInfo.merchantId);
                intent.putExtra(TabManageActivity.NAME, merchantInfo.name);
                intent.putExtra(TabManageActivity.TAGS, merchant.tags);
                intent.putExtra(TabManageActivity.COMEFROM, 2);
                startActivityForResult(intent, MODIFY_TAG);
                break;
            case R.id.rl_service_position:
                intent = new Intent(mActivity, PositionSetActivity.class);
                intent.putExtra("position_name", strPosition);
                startActivityForResult(intent, MODIFY_POSITION);
                break;
        }
    }

    private void initTags(M_Merchant entity) {
        rgTaService.setChildMargin(0, 24, 24, 0);
        rgTaService.removeAllViews();
        if (!StringUtils.isBlank(entity.tags)) {
            List<String> tagList = new ArrayList<String>(Arrays.asList(entity.tags.split(",")));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MODIFY_TAG) {
                user_saleUser_info();
            } else if (requestCode == MODIFY_POSITION) {
                String positionName = data.getStringExtra("position_name");
                if (!strPosition.equals(positionName)) {
                    strPosition = positionName;
                    userEditInfo();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("exit_success", 0);
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }
}
