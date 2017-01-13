package com.zemult.merchant.activity.slash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.MySlashMerchantActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.slash.ManagerJoinmerchantRequest;
import com.zemult.merchant.aip.slash.MerchantGetrecruitroleListRequest;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetrecruitroleList;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

import static com.zemult.merchant.config.Constants.ROWS;


//0133.斜杠角色
public class SlashRoleActivity extends MAppCompatActivity {

    public static final String TAG = SlashRoleActivity.class.getSimpleName();
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.detailgo_lv)        //详情
            LinearLayout detailgoLv;
    @Bind(R.id.suggest_tv)
    TextView suggestTv;
    @Bind(R.id.role_lv)
    ListView roleLv;
    @Bind(R.id.suggest_rl)         //提出建议
            RelativeLayout suggestRl;
    @Bind(R.id.head_iv)
    ImageView head_iv;
    @Bind(R.id.name_tv)
    TextView name_tv;
    @Bind(R.id.tv_jumpaction)
    TextView tvjumpaction;
    CommonAdapter commonAdapter;
    List<M_Industry> industryList;
    int merchantId, page = 1, status;
    String name, head;
    ManagerJoinmerchantRequest managerJoinmerchantRequest;
    MerchantGetrecruitroleListRequest merchantGetrecruitroleListRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash_role);
        ButterKnife.bind(this);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("斜杠角色");
        merchantId = getIntent().getIntExtra("merchantId", 0);
        name = getIntent().getStringExtra("name") == null ? "" : getIntent().getStringExtra("name");
        head = getIntent().getStringExtra("head") == null ? "" : getIntent().getStringExtra("head");
        status = getIntent().getIntExtra("status", 0);
        if (!TextUtils.isEmpty(head)) {
            imageManager.loadUrlImage(head, head_iv);
        }

        name_tv.setText(name);
        merchant_getrecruitroleList();
        if (status == 1) {// merchantstatus  场景状态(1:待上线 2:已上线)
            tvjumpaction.setText("企业入驻");
        } else {
            tvjumpaction.setText("企业详情");
        }


    }


    private void merchant_getrecruitroleList() {
        if (merchantGetrecruitroleListRequest != null) {
            merchantGetrecruitroleListRequest.cancel();
        }

        MerchantGetrecruitroleListRequest.Input input = new MerchantGetrecruitroleListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.operateUserId = SlashHelper.userManager().getUserId();
        }
        input.merchantId = merchantId;
        input.page = page;
        input.rows = ROWS;

        input.convertJosn();
        merchantGetrecruitroleListRequest = new MerchantGetrecruitroleListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetrecruitroleList) response).status == 1) {
                    industryList = ((APIM_MerchantGetrecruitroleList) response).industryList;

                } else {
                    ToastUtils.show(SlashRoleActivity.this, ((APIM_MerchantGetrecruitroleList) response).info);
                }
                roleLv.setAdapter(commonAdapter = new CommonAdapter<M_Industry>(SlashRoleActivity.this, R.layout.slashrole_item
                        , industryList) {
                    @Override
                    public void convert(CommonViewHolder holder, final M_Industry m_industry, final int position) {

                        holder.setImage(R.id.rohead_iv, m_industry.icon);

                        holder.setText(R.id.roname_tv, m_industry.industryName);
                        holder.setText(R.id.ronumber_tv, m_industry.personNum + "");
                        holder.setButtonDisable(R.id.btn_apply, m_industry.status, status);
                        holder.setOnclickListener(R.id.btn_apply, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ("已申请".equals(((Button) v).getText().toString())) {
                                    Intent intent = new Intent(SlashRoleActivity.this, MySlashMerchantActivity.class);
                                    intent.putExtra(MySlashMerchantActivity.INTENT_ROLE_ID, m_industry.industryId);
                                    intent.putExtra(MySlashMerchantActivity.INTENT_ROLE_NAME, m_industry.name);
                                    startActivity(intent);
                                } else {
                                    manager_joinmerchant(m_industry.industryId, position, status);
                                }
                            }
                        });
                    }

                });
            }
        });
        sendJsonRequest(merchantGetrecruitroleListRequest);
    }

    //用户申请商户的某个经营角色(--成功跳转角色管理)
    private void manager_joinmerchant(final int industryId, final int position, final int merchantstatus) {//merchantstatus  场景状态(1:待上线 2:已上线)
        showPd();
        if (managerJoinmerchantRequest != null) {
            managerJoinmerchantRequest.cancel();
        }

        final ManagerJoinmerchantRequest.Input input = new ManagerJoinmerchantRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.industryId = industryId;
        input.merchantId = merchantId;

        input.convertJosn();
        managerJoinmerchantRequest = new ManagerJoinmerchantRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
//                    if(((CommonResult) response).flag==0){//是否有该角色 (0：否,1：有)-- flag=0表示用户没有该角色,让用户去参与这个角色
//                        IntentUtil.intStart_activity(SlashRoleActivity.this,JoinRolesActivity.class,
//                                new Pair<String, Integer>("merchantId",merchantId),
//                                new Pair<String, Integer>("industryId",industryId));
//                    }else{
                    industryList.get(position).status = 1;
                    commonAdapter.NotifyDataSetChanged(industryList);
                    if (merchantstatus == 2) {//merchantstatus  场景状态(1:待上线 2:已上线)
                        IntentUtil.intStart_activity(SlashRoleActivity.this, MySlashMerchantActivity.class,
                                new Pair<String, Integer>("roleId", industryId));
                    }
                    ToastUtil.showMessage("申请成功");
//                    }

                } else {
                    ToastUtils.show(SlashRoleActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(managerJoinmerchantRequest);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.detailgo_lv, R.id.suggest_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.detailgo_lv:
                if (status == 1) {// merchantstatus  场景状态(1:待上线 2:已上线)
//                    Intent it2 = new Intent(SlashRoleActivity.this, MerchantEditActivity.class);
                    Intent it2 = new Intent(SlashRoleActivity.this, MerchantEnterActivity.class);
                    it2.putExtra("merchantId", merchantId);
                    it2.putExtra("intentFrom", TAG);
                    startActivity(it2);
                } else {
                    Intent intent = new Intent(SlashRoleActivity.this, ShopDetailActivity.class);
                    intent.putExtra("merchantId", merchantId);
                    startActivity(intent);
                }
                break;
            case R.id.suggest_rl:
                Intent it2 = new Intent(SlashRoleActivity.this, NewroleActivity.class);
                startActivity(it2);
                break;

        }
    }

}
