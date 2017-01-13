package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.ClassifyActivity;
import com.zemult.merchant.activity.mine.MerchantFullNameActivity;
import com.zemult.merchant.activity.mine.MyAreaActivity;
import com.zemult.merchant.activity.mine.TrueNameActivity;
import com.zemult.merchant.activity.mine.TrueNameResultActivity;
import com.zemult.merchant.activity.mine.WithdrawChooseActivity;
import com.zemult.merchant.aip.slash.MerchantAddentityExistRequest;
import com.zemult.merchant.aip.slash.MerchantAddentityNewRequest;
import com.zemult.merchant.aip.slash.MerchantEditentityNewRequest;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.aip.slash.MerchantInfoRequest2;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

/**
 * 商家入驻
 */
public class MerchantEnterActivity extends MAppCompatActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_full_name)
    ImageView ivFullName;
    @Bind(R.id.tv_fullname)
    EditText tvFullname;
    @Bind(R.id.iv_name_right)
    ImageView ivNameRight;
    @Bind(R.id.rl_full_name)
    RelativeLayout rlFullName;
    @Bind(R.id.iv_classify)
    ImageView ivClassify;
    @Bind(R.id.tv_classify)
    TextView tvClassify;
    @Bind(R.id.iv_class)
    ImageView ivClass;
    @Bind(R.id.rl_chooseclassify)
    RelativeLayout rlChooseclassify;
    @Bind(R.id.area_iv)
    ImageView areaIv;
    @Bind(R.id.tv_area)
    TextView tvArea;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.rl_choosearea)
    RelativeLayout rlChoosearea;
    @Bind(R.id.iv_address)
    ImageView ivAddress;
    @Bind(R.id.et_address)
    EditText etAddress;
    @Bind(R.id.ll_three)
    LinearLayout llThree;
    @Bind(R.id.naap)
    ImageView naap;
    @Bind(R.id.rl_certification)
    RelativeLayout rlCertification;
    @Bind(R.id.cer)
    ImageView cer;
    @Bind(R.id.rl_uploadimage)
    RelativeLayout rlUploadimage;
    @Bind(R.id.btn_commit)
    Button btnCommit;
    @Bind(R.id.et_yongjin)
    EditText etYongjin;
    @Bind(R.id.et_tel)
    EditText etTel;
    @Bind(R.id.iv_certification)
    ImageView ivCertification;
    @Bind(R.id.tv_uploadimage)
    TextView tvUploadimage;
    @Bind(R.id.iv_uploadimage)
    ImageView ivUploadimage;
    @Bind(R.id.tv_check_note)
    TextView tvCheckNote;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.ll_protocol)
    LinearLayout llProtocol;
    @Bind(R.id.cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.tv_protocol)
    TextView tvProtocol;

    public static final String INTENT_MERCHANTID = "merchantId";
    private static final int REQ_CHOOSE_WITHDRAW = 0x110;

    // exist
    private int merchantId;
    private MerchantAddentityNewRequest merchantAddentityNewRequest;
    private String name, city, area, address, province, west, east, IDphotos, tel, aliAccount, bankCard, bankName, bankUser;
    private int industryId, moneyType = -1;
    private double commissionDiscount;
    private M_Merchant merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_add);
        ButterKnife.bind(this);

        lhTvTitle.setText("商家入驻");

        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, -1);

        String[] center = Constants.CENTER.split(",");
        east = center[0];
        west = center[1];

        // 实体商家入驻
        if (merchantId == -1) {
            Intent intent = new Intent(MerchantEnterActivity.this, MerchantFullNameActivity.class);
            startActivityForResult(intent, 1);
        } else {
            initViewEdit();
            showPd();
            my_merchant_getinfo();
        }
        registerReceiver(new String[]{Constants.BROCAST_OSS_UPLOADIMAGE, Constants.BROCAST_EDITMERCHANT});
    }

    private void initViewNew() {
        llThree.setVisibility(View.VISIBLE);
//        tvClassify.setText("选择行业");
//        tvArea.setText("选择地区");
//        etAddress.setText("");
//        etAccount.setText("");
//
//        industryId = 0;
//        area = "";
//        IDphotos = "";

        llProtocol.setVisibility(View.VISIBLE);
        ivAdd.setVisibility(View.VISIBLE);
        ivClass.setVisibility(View.VISIBLE);
        rlChooseclassify.setClickable(true);
        rlChoosearea.setClickable(true);
        etAddress.setEnabled(true);
        tvFullname.setEnabled(true);
        EditFilter.CashFilter(etYongjin, 100);
    }

    private void initViewExist() {
        llThree.setVisibility(View.VISIBLE);
        ivAdd.setVisibility(View.INVISIBLE);
        ivClass.setVisibility(View.INVISIBLE);
        rlChooseclassify.setClickable(false);
        rlChoosearea.setClickable(false);
        etAddress.setEnabled(false);
        EditFilter.CashFilter(etYongjin, 100);
    }

    // 审核不通过时
    private void initViewEdit() {
        rlFullName.setClickable(false);
        llThree.setVisibility(View.VISIBLE);
        ivNameRight.setVisibility(View.INVISIBLE);
        tvCheckNote.setVisibility(View.VISIBLE);
        ivCertification.setVisibility(View.VISIBLE);
        ivUploadimage.setVisibility(View.VISIBLE);
        tvUploadimage.setVisibility(View.VISIBLE);
        btnCommit.setVisibility(View.VISIBLE);

        rlCertification.setClickable(true);
        rlUploadimage.setClickable(true);
        etTel.setEnabled(true);
        etYongjin.setEnabled(true);

        tvArea.setTextColor(getResources().getColor(R.color.font_black_333));
        tvClassify.setTextColor(getResources().getColor(R.color.font_black_333));
        etAddress.setTextColor(getResources().getColor(R.color.font_black_333));
        EditFilter.CashFilter(etYongjin, 100);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rl_chooseclassify, R.id.rl_choosearea, R.id.rl_uploadimage, R.id.rl_full_name, R.id.btn_commit, R.id.rl_certification, R.id.tv_protocol, R.id.ll_tixian})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.rl_chooseclassify:
                Intent classintent = new Intent(MerchantEnterActivity.this, ClassifyActivity.class);
                classintent.putExtra("requesttype", Constants.BROCAST_EDITMERCHANT);
                startActivityForResult(classintent, 2);
                break;

            case R.id.rl_choosearea:
                IntentUtil.start_activity(MerchantEnterActivity.this, MyAreaActivity.class, new Pair<String, String>("requesttype", Constants.BROCAST_EDITMERCHANT));
                break;
            case R.id.rl_full_name:
                Intent intent = new Intent(MerchantEnterActivity.this, MerchantFullNameActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.rl_certification:
                // 实名认证 0否 1是
                if (SlashHelper.userManager().getUserinfo().isConfirm == 1)
                    IntentUtil.start_activity(MerchantEnterActivity.this, TrueNameResultActivity.class);
                else
                    IntentUtil.start_activity(MerchantEnterActivity.this, TrueNameActivity.class);
                break;
            case R.id.rl_uploadimage:
                IntentUtil.start_activity(MerchantEnterActivity.this, UploadCredentialsActivity.class);
                break;
            case R.id.tv_protocol:
                IntentUtil.start_activity(MerchantEnterActivity.this, BaseWebViewActivity.class, new Pair<String, String>("titlename", getString(R.string.app_name) + "平台商家入驻协议"), new Pair<String, String>("url", Constants.PROTOCOL_MERCHANT));
                break;
            case R.id.ll_tixian:
                Intent intent1 = new Intent(MerchantEnterActivity.this, WithdrawChooseActivity.class);
                intent1.putExtra("aliAccount", aliAccount);
                intent1.putExtra("bankCard", bankCard);
                intent1.putExtra("bankName", bankName);
                intent1.putExtra("bankUser", bankUser);
                startActivityForResult(intent1, REQ_CHOOSE_WITHDRAW);
                break;
            case R.id.btn_commit:
                name = tvFullname.getText().toString().toString();
                address = etAddress.getText().toString().toString();
                tel = etTel.getText().toString().toString();

                if (TextUtils.isEmpty(name) && "商家名称".equals(name)) {
                    ToastUtil.showMessage("请填写商家名称");
                    return;
                }
                if (industryId == 0) {
                    ToastUtil.showMessage("请选择分类");
                    return;
                }
                if (TextUtils.isEmpty(area)) {
                    ToastUtil.showMessage("请选择地区");
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    ToastUtil.showMessage("请填写上详细地址");
                    return;
                }
                // 实名认证 0否 1是
                if (SlashHelper.userManager().getUserinfo().isConfirm == 0) {
                    ToastUtil.showMessage("请实名认证");
                    return;
                }
                if (TextUtils.isEmpty(IDphotos)) {
                    ToastUtil.showMessage("请上传证件照片");
                    return;
                }
                if (TextUtils.isEmpty(etYongjin.getText().toString())) {
                    ToastUtil.showMessage("请填写佣金比例");
                    return;
                }
                commissionDiscount = Double.parseDouble(etYongjin.getText().toString());
                if (commissionDiscount > 100) {
                    ToastUtil.showMessage("佣金比例需设置在0~100内");
                    return;
                }
                if (TextUtils.isEmpty(etTel.getText().toString())) {
                    ToastUtil.showMessage("请填写联系电话");
                    return;
                }
//                if(!StringMatchUtils.isMobileNO(etTel.getText().toString())) {
//                    ToastUtil.showMessage("电话格式不正确");
//                    return;
//                }
                if (moneyType == -1) {
                    ToastUtil.showMessage("请选择提现方式");
                    return;
                }
                if (!cbAgree.isChecked()) {
                    ToastUtil.showMessage("请勾选同意平台入驻协议");
                    return;
                }

                if (merchant.reviewstatus == 1) {
                    if (merchant.fromType == 0)
                        merchant_addentity_exist();
                    else
                        merchant_editentity_new();
                } else {
                    if (ivClass.getVisibility() == View.VISIBLE)
                        merchant_addentity_new();
                    else
                        merchant_addentity_exist();
                }
                break;
        }
    }

    //实体商户入驻--添加全新的商户时
    private void merchant_addentity_new() {
        if (merchantAddentityNewRequest != null) {
            merchantAddentityNewRequest.cancel();
        }
        MerchantAddentityNewRequest.Input input = new MerchantAddentityNewRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
            input.name = name;
            input.industryId = industryId;
            input.city = city;
            input.area = area;
            input.address = address;
            input.province = province;
            input.east = east;//"119.971736,31.829737"
            input.west = west;
            input.IDphotos = IDphotos;
            input.tel = tel;
            input.commissionDiscount = commissionDiscount;

            input.convertJosn();
        }

        merchantAddentityNewRequest = new MerchantAddentityNewRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("创建成功");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(merchantAddentityNewRequest);
    }

    //实体商家入驻--选择已经存在的商户时
    private MerchantAddentityExistRequest merchantAddentityExistRequest;

    private void merchant_addentity_exist() {
        if (merchantAddentityExistRequest != null) {
            merchantAddentityExistRequest.cancel();
        }
        final MerchantAddentityExistRequest.Input input = new MerchantAddentityExistRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.IDphotos = IDphotos;
        input.shortName = "";
        input.tel = tel;
        input.commissionDiscount = commissionDiscount;
        input.convertJosn();
        merchantAddentityExistRequest = new MerchantAddentityExistRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("添加成功");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(merchantAddentityExistRequest);
    }

    private MerchantEditentityNewRequest editentityNewRequest;

    //实体商户入驻--添加全新的商户时
    private void merchant_editentity_new() {
        if (editentityNewRequest != null) {
            editentityNewRequest.cancel();
        }
        MerchantEditentityNewRequest.Input input = new MerchantEditentityNewRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.merchantId = merchantId;
            input.userId = SlashHelper.userManager().getUserId();
            input.name = name;
            input.industryId = industryId;
            input.city = city;
            input.area = area;
            input.address = address;
            input.province = province;
            input.east = east;//"119.971736,31.829737"
            input.west = west;
            input.IDphotos = IDphotos;
            input.tel = tel;
            input.commissionDiscount = commissionDiscount;

            input.convertJosn();
        }

        editentityNewRequest = new MerchantEditentityNewRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("重新入驻请求已提交");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(editentityNewRequest);
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
                    merchant = ((APIM_MerchantGetinfo) response).merchant;

                    initViewExist();

                    tvFullname.setText(merchant.name);
                    tvClassify.setText(merchant.industryName);
                    tvArea.setText(merchant.provinceName + " " + merchant.cityName + " " + merchant.areaName);
                    etAddress.setText(merchant.address);
                    etTel.setText(merchant.tel);

                    industryId = merchant.industryId;
                    area = merchant.area;
                } else {
                    ToastUtil.showMessage(((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantGetinfoRequest);
    }

    /**
     * 获取自己的 商家(场景)详情(请求)
     */
    private MerchantInfoRequest2 myMerchantGetinfoRequest;

    private void my_merchant_getinfo() {
        showPd();
        if (myMerchantGetinfoRequest != null) {
            myMerchantGetinfoRequest.cancel();
        }

        MerchantInfoRequest2.Input input = new MerchantInfoRequest2.Input();
        input.merchantId = merchantId;
        input.userId = SlashHelper.userManager().getUserId();

        input.convertJosn();
        myMerchantGetinfoRequest = new MerchantInfoRequest2(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetinfo) response).status == 1) {
                    merchant = ((APIM_MerchantGetinfo) response).merchant;

                    tvFullname.setText(merchant.name);
                    tvClassify.setText(merchant.industryName);
                    tvArea.setText(merchant.provinceName + " " + merchant.cityName + " " + merchant.areaName);
                    etAddress.setText(merchant.address);
                    etTel.setText(merchant.tel);
                    tvCheckNote.setText(merchant.checkNote);
                    etYongjin.setText(merchant.commissionDiscount + "");

                    IDphotos = merchant.IDphotos;
                    industryId = merchant.industryId;
                    area = merchant.area;
                    moneyType = merchant.moneyType;
                    bankCard = merchant.bankCard;
                    bankUser = merchant.bankUser;
                    bankName = merchant.bankName;

                    // 来源类型(0:系统录入 1:用户提交)
                    if (merchant.fromType == 0) {
                        ivAdd.setVisibility(View.INVISIBLE);
                        ivClass.setVisibility(View.INVISIBLE);
                        rlChooseclassify.setClickable(false);
                        rlChoosearea.setClickable(false);
                        etAddress.setEnabled(false);
                        tvFullname.setEnabled(false);
                    } else {
                        ivAdd.setVisibility(View.VISIBLE);
                        ivClass.setVisibility(View.VISIBLE);
                        rlChooseclassify.setClickable(true);
                        rlChoosearea.setClickable(true);
                        etAddress.setEnabled(true);
                        tvFullname.setEnabled(true);
                    }
                } else {
                    ToastUtil.showMessage(((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(myMerchantGetinfoRequest);
    }

    /**
     * ==================================处理刷新请求=================================================
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            merchantId = data.getIntExtra("merchantId", -1);
            if (merchantId != -1) {
                showPd();
                merchant_getinfo();
            } else {
//                tvFullname.setText(merchantName);
                initViewNew();
            }
        }
        else if (requestCode == 2 && resultCode == RESULT_OK) {
            industryId = data.getIntExtra("industryId", 0);
            tvClassify.setText(data.getStringExtra("industryName"));
        }
        else if (requestCode == 2 && resultCode == 2) {
            finish();
        }
        else if(requestCode == REQ_CHOOSE_WITHDRAW && resultCode == RESULT_OK){
            moneyType = data.getIntExtra("moneyType", -1);
            aliAccount = data.getStringExtra("aliAccount");
            bankCard = data.getStringExtra("bankCard");
            bankName = data.getStringExtra("bankName");
            bankUser = data.getStringExtra("bankUser");
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
                IDphotos = Constants.OSSENDPOINT + intent.getStringExtra("object");
            } else {
                ToastUtil.showMessage(intent.getStringExtra("info"));
            }
        }
        if (Constants.BROCAST_EDITMERCHANT.equals(intent.getAction())) {
            city = intent.getStringExtra("city");
            province = intent.getStringExtra("province");
            area = intent.getStringExtra("area");
            tvArea.setText(intent.getStringExtra("provinceName") + " " + intent.getStringExtra("cityName") + " " + intent.getStringExtra("areaName"));
        }
    }
}
