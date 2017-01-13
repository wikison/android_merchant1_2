package com.zemult.merchant.activity.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.UserLabelDel_1_2Request;
import com.zemult.merchant.aip.mine.UserLabelListHistory_1_2Request;
import com.zemult.merchant.aip.mine.UserLabelList_1_2Request;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Label;
import com.zemult.merchant.model.apimodel.APIM_UserTagList_1_2;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FNRadioGroup;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.taobao.av.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class LabelHomeActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener{

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    TextView tv_seeMore;

    ImageView ivNodatalabel;
    ImageView ivNodatalist;


    FNRadioGroup wordWrapView;
    TextView tv_toplab;
    int colorArry[] = new int[]{Color.GREEN, Color.RED, Color.GRAY, Color.BLUE, Color.YELLOW, Color.BLACK};
    UserLabelList_1_2Request userLabelList_1_2Request;
    UserLabelListHistory_1_2Request userLabelListHistory_1_2Request;
    UserLabelDel_1_2Request userLabelDel_1_2Request;
    private int page = 1;
    List<M_Label> labelHistoryList= new ArrayList<M_Label>();;
    List<M_Label> labelList= new ArrayList<M_Label>();;
    CommonAdapter commonAdapter;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_commonlist);
        registerReceiver(new String[]{Constants.BROCAST_CLOSE_ACTIVITY_FORLABEL});

    }

    @Override
    protected void onResume() {
        super.onResume();
        userLabelList_1_2Request();
        user_labelList_history_1_2Request(false);
    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_CLOSE_ACTIVITY_FORLABEL.equals(intent.getAction())) {
           finish();
        }
    }

    @Override
    public void init() {
        lhTvTitle.setText("标签互动");
        lhBtnRightiamge.setVisibility(View.VISIBLE);
        lhBtnRightiamge.setBackgroundResource(R.mipmap.biaoqian_label_icon);
        View head_lablehome = LayoutInflater.from(this).inflate(R.layout.head_lablehome, null);
        wordWrapView = (FNRadioGroup) head_lablehome.findViewById(R.id.view_wordwrap);
        ivNodatalabel= (ImageView) head_lablehome.findViewById(R.id.iv_nodatalabel);
        ivNodatalist= (ImageView) head_lablehome.findViewById(R.id.iv_nodatalist);
        tv_toplab= (TextView) head_lablehome.findViewById(R.id.tv_toplab);
        tv_seeMore= (TextView) head_lablehome.findViewById(R.id.tv_seeMore);
        smoothListView.setAdapter(null);
        smoothListView.addHeaderView(head_lablehome);
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        tv_seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LabelHomeActivity.this,LabelListActivity.class);
                intent.putExtra("titleName","大家眼中的我");
                startActivity(intent);
            }
        });
    }

    //我的被别人贴的标签的记录列表
    private void user_labelList_history_1_2Request(boolean isfirstLoad) {
        if (userLabelListHistory_1_2Request != null) {
            userLabelListHistory_1_2Request.cancel();
        }


        UserLabelListHistory_1_2Request.Input input = new UserLabelListHistory_1_2Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.rows = Constants.ROWS;
        if (isfirstLoad) {
            input.page = 1;
        } else {
            input.page = page;
        }

        input.convertJosn();
        userLabelListHistory_1_2Request = new UserLabelListHistory_1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserTagList_1_2) response).status == 1) {
                    if (page == 1) {
                        labelHistoryList = ((APIM_UserTagList_1_2) response).labelHistoryList;
                        if(null==labelHistoryList||labelHistoryList.size()==0){
                            ivNodatalist.setVisibility(View.VISIBLE);
                        }
                        else{
                            ivNodatalist.setVisibility(View.GONE);
                        }
                        smoothListView.setAdapter(commonAdapter = new CommonAdapter<M_Label>(LabelHomeActivity.this, R.layout.item_lablehome, labelHistoryList) {
                            @Override
                            public void convert(CommonViewHolder holder, final M_Label mlabel, int position) {


                                GradientDrawable drawable = new GradientDrawable();
                                drawable.setShape(GradientDrawable.RECTANGLE); // 画框
                                drawable.setStroke(2, colorArry[position % 6]); // 边框粗细及颜色
                                drawable.setCornerRadii(new float[]{25,
                                        25, 25, 25, 25, 25, 25, 25});
                                drawable.setColor(0x22FFFFFF);  // 边框内部颜色
                                holder.setText(R.id.tv_friendname, mlabel.fromUserName);
                                holder.setTextColor(R.id.tv_labname,colorArry[position % 6]);
                                holder.setText(R.id.tv_tietime, DateTimeUtil.getTimeGapInfo(mlabel.createTime) );
                                holder.setText(R.id.tv_labdescription, mlabel.note);
//                                if(!StringUtil.isEmpty(mlabel.fromUserName)&&mlabel.fromUserName.length()>6){
//                                    holder.setText(R.id.tv_fromwho, "获得标签");//来自"+mlabel.fromUserName.substring(0,6)+"...的
//                                }
//                                else{
//                                    holder.setText(R.id.tv_fromwho, "获得来自"+mlabel.fromUserName+"的标签");
//                                }
                                holder.setText(R.id.tv_fromwho, "获得标签");
                                if(!StringUtil.isEmpty(mlabel.labelName)&&mlabel.labelName.length()>10){
                                    holder.setText(R.id.tv_labname, mlabel.labelName.substring(0,10)+"...",drawable);
                                }
                                else{
                                    holder.setText(R.id.tv_labname, mlabel.labelName,drawable);
                                }
                                if(!StringUtils.isEmpty(mlabel.fromUserHead)){
                                    holder.setCircleImage(R.id.iv_friendhead,mlabel.fromUserHead);
                                }
                                else{
                                    holder.setImageResource(R.id.iv_friendhead,R.mipmap.user_icon);
                                }
                                holder.setOnclickListener(R.id.btn_giveback, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent =new Intent(LabelHomeActivity.this,LabelListActivity.class);
                                        intent.putExtra("titleName","我的标签");
                                        intent.putExtra("fromUserId",mlabel.fromUserId);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    } else {
                        labelHistoryList.addAll(((APIM_UserTagList_1_2) response).labelHistoryList);
                        commonAdapter.notifyDataSetChanged();
                    }
                    if (((APIM_UserTagList_1_2) response).maxpage <= page) {
                        smoothListView.setLoadMoreEnable(false);
                    } else {
                        smoothListView.setLoadMoreEnable(true);
                        page++;
                    }

                } else {
                    ToastUtils.show(LabelHomeActivity.this, ((APIM_UserTagList_1_2) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();


            }
        });
        sendJsonRequest(userLabelListHistory_1_2Request);
    }



    //大家眼中的我的标签列表
    private void userLabelList_1_2Request() {
        if (userLabelList_1_2Request != null) {
            userLabelList_1_2Request.cancel();
        }
        UserLabelList_1_2Request.Input input = new UserLabelList_1_2Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
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
                    ToastUtils.show(LabelHomeActivity.this, ((APIM_UserTagList_1_2) response).info);
                }
            }
        });
        sendJsonRequest(userLabelList_1_2Request);
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
                    ToastUtils.show(LabelHomeActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userLabelDel_1_2Request);
    }

    void initLable() {
//        ExplosionField explosionField = new ExplosionField(this);
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
            if(i<10){
                wordWrapView.addView(LableBtn);
            }
            else{
                break;
            }
        }
//        explosionField.addListener(wordWrapView,false);
    }

    public void removeLable(View viewLable) {
        wordWrapView.removeView(viewLable);
        user_label_del_1_2(Integer.parseInt(viewLable.getTag().toString()));

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_btn_rightiamge})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.lh_btn_rightiamge:
                Intent intent =new Intent(LabelHomeActivity.this,MyLabelActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onRefresh() {
        user_labelList_history_1_2Request(true);
    }

    @Override
    public void onLoadMore() {
        user_labelList_history_1_2Request(false);
    }
}
