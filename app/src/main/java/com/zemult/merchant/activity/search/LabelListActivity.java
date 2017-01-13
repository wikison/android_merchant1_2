package com.zemult.merchant.activity.search;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserLabelDel_1_2Request;
import com.zemult.merchant.aip.mine.UserLabelList_1_2Request;
import com.zemult.merchant.aip.mine.UserTagDel_1_2Request;
import com.zemult.merchant.aip.mine.UserTagList_1_2Request;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Label;
import com.zemult.merchant.model.apimodel.APIM_UserTagList_1_2;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FNRadioGroup;
import com.zemult.merchant.view.explosion.ExplosionField;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class LabelListActivity extends BaseActivity{

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_toplab2)
    TextView tvToplab2;

    ImageView ivNodatalabel;
    ImageView ivNodatalist;

    UserTagDel_1_2Request userTagDel_1_2Request;
    FNRadioGroup wordWrapView;
    TextView tv_toplab;
    int colorArry[] = new int[]{Color.GREEN, Color.RED, Color.GRAY, Color.BLUE, Color.YELLOW, Color.BLACK};
    UserLabelList_1_2Request userLabelList_1_2Request;
    UserTagList_1_2Request  userTagList_1_2Request;
    List<M_Label> tagList= new ArrayList<M_Label>();
    List<M_Label> labelList= new ArrayList<M_Label>();;
    String titleName;
    int fromUserId,taId;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_labellist);
        titleName=getIntent().getStringExtra("titleName");
        fromUserId=getIntent().getIntExtra("fromUserId",0);
        taId=getIntent().getIntExtra("taId",0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if("我的标签".equals(titleName)){
            user_tagList_1_2();
            tv_toplab.setText("点击标签选择好友贴标签");
        }
        if("大家眼中的我".equals(titleName)){
            userLabelList_1_2Request();
            tv_toplab.setText("大家眼中的我");
        }
        if("TA的标签".equals(titleName)){
            if(taId==SlashHelper.userManager().getUserId()){
                tvToplab2.setVisibility(View.INVISIBLE);
            }
            userLabelList_1_2Request();
            tvToplab2.setText("给TA贴一个");
            tvToplab2.setBackgroundResource(R.drawable.common_selector_btn);
            tvToplab2.setTextColor(Color.WHITE);
            tv_toplab.setText("大家眼中的TA");
            tvToplab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(LabelListActivity.this,LabelListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("titleName","我的标签");
                    intent.putExtra("fromUserId",taId);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void init() {
        lhTvTitle.setText(titleName);
        wordWrapView =(FNRadioGroup) findViewById(R.id.view_wordwrap);
        ivNodatalabel= (ImageView) findViewById(R.id.iv_nodatalabel);
        ivNodatalist= (ImageView) findViewById(R.id.iv_nodatalist);
        tv_toplab= (TextView) findViewById(R.id.tv_toplab);



    }


    public void removeLable(View viewLable) {
        wordWrapView.removeView(viewLable);
        if("我的标签".equals(titleName)){
            user_tag_del_1_2(Integer.parseInt(viewLable.getTag().toString()));
        }
        if("大家眼中的我".equals(titleName)){
            user_label_del_1_2(Integer.parseInt(viewLable.getTag().toString()));
        }


    }

    //大家眼中的我的标签列表
    private void userLabelList_1_2Request() {
        if (userLabelList_1_2Request != null) {
            userLabelList_1_2Request.cancel();
        }
        UserLabelList_1_2Request.Input input = new UserLabelList_1_2Request.Input();
        if(taId!=0){
            input.userId =taId;
        }
        else{
            if (SlashHelper.userManager().getUserinfo() != null) {
                input.userId = SlashHelper.userManager().getUserId();
            }
        }

        input.rows =100;
        input.page = 1;

        input.convertJosn();
        userLabelList_1_2Request = new UserLabelList_1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserTagList_1_2) response).status == 1) {
                    labelList = ((APIM_UserTagList_1_2) response).labelList;
                    if(null==labelList||labelList.size()==0){
                        ivNodatalabel.setVisibility(View.VISIBLE);
                    }
                    else{
                        ivNodatalabel.setVisibility(View.GONE);
                        initLable();
                    }
                } else {
                    ToastUtils.show(LabelListActivity.this, ((APIM_UserTagList_1_2) response).info);
                }
            }
        });
        sendJsonRequest(userLabelList_1_2Request);
    }
    UserLabelDel_1_2Request userLabelDel_1_2Request;



    //我的标签包列表
    private void user_tagList_1_2() {
        if (userTagList_1_2Request != null) {
            userTagList_1_2Request.cancel();
        }
        UserTagList_1_2Request.Input input = new UserTagList_1_2Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.rows =100;
        input.page = 1;

        input.convertJosn();
        userTagList_1_2Request = new UserTagList_1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserTagList_1_2) response).status == 1) {
                    tagList = ((APIM_UserTagList_1_2) response).tagList;
                    if(null==tagList||tagList.size()==0){
                        ivNodatalabel.setVisibility(View.VISIBLE);
                    }
                    else{
                        ivNodatalabel.setVisibility(View.GONE);
                        initLable();
                    }

                } else {
                    ToastUtils.show(LabelListActivity.this, ((APIM_UserTagList_1_2) response).info);
                }
            }
        });
        sendJsonRequest(userTagList_1_2Request);
    }



    //删除标签--大家眼中的我
    private void user_label_del_1_2(int labelId) {
        if (userLabelDel_1_2Request != null) {
            userLabelDel_1_2Request.cancel();
        }

        UserLabelDel_1_2Request.Input input = new UserLabelDel_1_2Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.labelId=labelId;
        input.convertJosn();
        userLabelDel_1_2Request = new UserLabelDel_1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("删除成功");
                } else {
                    ToastUtils.show(LabelListActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userLabelDel_1_2Request);
    }



    //删除标签--我的标签包
    private void user_tag_del_1_2(int tagId) {
        if (userTagDel_1_2Request != null) {
            userTagDel_1_2Request.cancel();
        }
        UserTagDel_1_2Request.Input input = new UserTagDel_1_2Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.tagId=tagId;
        input.convertJosn();
        userTagDel_1_2Request = new UserTagDel_1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("删除成功");
                } else {
                    ToastUtils.show(LabelListActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userTagDel_1_2Request);
    }

    void initLable() {
        ExplosionField explosionField = new ExplosionField(this);
        wordWrapView.setChildMargin(10, 10, 10, 10);
        wordWrapView.removeAllViews();
        for (int i = 0; i < labelList.size(); i++) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE); // 画框
            drawable.setStroke(2, colorArry[i % 6]); // 边框粗细及颜色
            drawable.setCornerRadii(new float[]{25,
                    25, 25, 25, 25, 25, 25, 25});
            drawable.setColor(0x22FFFFFF);  // 边框内部颜色
            RadioButton LableBtn = new RadioButton(this);
            LableBtn.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
            LableBtn.setTextSize(12);
            LableBtn.setPadding(12, 8, 12, 8);
            LableBtn.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            LableBtn.setTextColor(colorArry[i % 6]);
            LableBtn.setTag(labelList.get(i).labelId);
            LableBtn.setText(labelList.get(i).labelName);
            wordWrapView.addView(LableBtn);
        }
        for (int i = 0; i < tagList.size(); i++) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE); // 画框
            drawable.setStroke(2, colorArry[i % 6]); // 边框粗细及颜色
            drawable.setCornerRadii(new float[]{25,
                    25, 25, 25, 25, 25, 25, 25});
            drawable.setColor(0x22FFFFFF);  // 边框内部颜色
            RadioButton LableBtn = new RadioButton(this);
            LableBtn.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
            LableBtn.setTextSize(12);
            LableBtn.setPadding(12, 8, 12, 8);
            LableBtn.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            LableBtn.setTextColor(colorArry[i % 6]);
            LableBtn.setTag(tagList.get(i).tagId);
            LableBtn.setText(tagList.get(i).tagName);
            wordWrapView.addView(LableBtn);
        }

        if("我的标签".equals(titleName)){
            explosionField.setFriendId(fromUserId);
            explosionField.addListener(wordWrapView,true);
        }
        if("大家眼中的我".equals(titleName)){
            explosionField.addListener(wordWrapView,false);
        }

    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_btn_rightiamge})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
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
