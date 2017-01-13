package com.zemult.merchant.activity.slash;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonGetindustryinfoRequest;
import com.zemult.merchant.aip.slash.ManagerAddmanagerRequest;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_CommonGetindustryinfo;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class JoinRolesActivity extends MBaseActivity {

    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;

    @Bind(R.id.joinrole_iv)
    ImageView joinroleIv;
    @Bind(R.id.joinrole_name)
    TextView joinroleName;
    @Bind(R.id.joinrole_intro1_tv)
    TextView joinroleIntro1Tv;
    @Bind(R.id.joinrole_intro2_tv)
    TextView joinroleIntro2Tv;
    @Bind(R.id.joinrole_intro3_tv)
    TextView joinroleIntro3Tv;
    @Bind(R.id.joinrole_go_tv)
    TextView joinroleGoTv;
    @Bind(R.id.joinrole_check)
    CheckBox joinroleCheck;
    @Bind(R.id.joinrole_rule_tv)
    TextView joinroleRuleTv;
    @Bind(R.id.joinrole_commit)
    Button joinroleCommit;

    ManagerAddmanagerRequest managerAddmanagerRequest;
    CommonGetindustryinfoRequest commonGetindustryinfoRequest;
    String noteClause;

    int industryId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_roles);

        ButterKnife.bind(this);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("参与角色");
        industryId=getIntent().getIntExtra("industryId",0);
        joinroleCommit.setOnClickListener(this);
        joinroleRuleTv.setOnClickListener(this);
        joinroleGoTv.setOnClickListener(this);

        commonGetindustryinfoRequest();
    }

    //用户 成为某经营人角色(参与角色)
    private void manager_addmanager() {
        if (managerAddmanagerRequest != null) {
            managerAddmanagerRequest.cancel();
        }

        ManagerAddmanagerRequest.Input input = new ManagerAddmanagerRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.industryId = industryId;

        input.convertJosn();
        managerAddmanagerRequest = new ManagerAddmanagerRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(JoinRolesActivity.this,"参与成功，请申请您需要的角色");
                    finish();
                } else {
                    ToastUtils.show(JoinRolesActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(managerAddmanagerRequest);
    }

    //获取单个行业角色详情
    private void commonGetindustryinfoRequest() {
        if (commonGetindustryinfoRequest != null) {
            commonGetindustryinfoRequest.cancel();
        }

        CommonGetindustryinfoRequest.Input input = new CommonGetindustryinfoRequest.Input();
        input.industryId = industryId;

        input.convertJosn();
        commonGetindustryinfoRequest = new CommonGetindustryinfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_CommonGetindustryinfo) response).status == 1) {
                    imageManager.loadUrlImage(((APIM_CommonGetindustryinfo) response).industry.pic,joinroleIv);
                    joinroleName.setText(((APIM_CommonGetindustryinfo) response).industry.name);
                    joinroleIntro1Tv.setText(((APIM_CommonGetindustryinfo) response).industry.createNote1);
                    joinroleIntro2Tv.setText(((APIM_CommonGetindustryinfo) response).industry.createNote2);
                    joinroleIntro3Tv.setText(((APIM_CommonGetindustryinfo) response).industry.createNote3);
                    noteClause=((APIM_CommonGetindustryinfo) response).industry.noteClause;

                } else {
                    ToastUtils.show(JoinRolesActivity.this, ((APIM_CommonGetindustryinfo) response).info);
                }
            }
        });
        sendJsonRequest(commonGetindustryinfoRequest);
    }



    @OnClick({R.id.ll_back, R.id.lh_btn_back,R.id.joinrole_go_tv, R.id.joinrole_rule_tv, R.id.joinrole_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                finish();
                break;
            case R.id.joinrole_go_tv:
                break;
            case R.id.joinrole_rule_tv:
                IntentUtil.start_activity(JoinRolesActivity.this,BaseWebViewActivity.class,
                        new Pair<String, String>("titlename","条款"),new Pair<String, String>("titlename","条款"));
                break;
            case R.id.joinrole_commit:
                manager_addmanager();
//                if(joinroleCheck.isChecked()){
//                }
//                else{
//                    ToastUtil.showMessage("请勾选同意按钮");
//                }

                break;
        }
    }
}
