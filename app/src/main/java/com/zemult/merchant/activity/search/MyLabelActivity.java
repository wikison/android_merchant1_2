package com.zemult.merchant.activity.search;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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
import com.zemult.merchant.aip.mine.UserTagDel_1_2Request;
import com.zemult.merchant.aip.mine.UserTagListHistory_1_2Request;
import com.zemult.merchant.aip.mine.UserTagList_1_2Request;
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
import com.zemult.merchant.view.explosion.ExplosionField;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class MyLabelActivity extends BaseActivity  implements SmoothListView.ISmoothListViewListener{

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    TextView tv_toplab,tv_goTask;
    TextView tv_seeMore;
    ImageView ivNodatalabel;
    ImageView ivNodatalist;

    FNRadioGroup wordWrapView;
    UserTagList_1_2Request userTagList_1_2Request;
    UserTagListHistory_1_2Request userTagListHistory_1_2Request;
    UserTagDel_1_2Request userTagDel_1_2Request;
    private int page = 1;
    List<M_Label> labelHistoryList= new ArrayList<M_Label>();;
    List<M_Label> tagList= new ArrayList<M_Label>();
    CommonAdapter commonAdapter;
    int colorArry[] = new int[]{Color.GREEN, Color.RED, Color.GRAY, Color.BLUE, Color.YELLOW, Color.BLACK};

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_commonlist);
    }

    @Override
    public void init() {
        lhTvTitle.setText("我的标签包");
        View head_lablehome = LayoutInflater.from(this).inflate(R.layout.head_lablehome, null);
        wordWrapView = (FNRadioGroup) head_lablehome.findViewById(R.id.view_wordwrap);
        ivNodatalabel= (ImageView) head_lablehome.findViewById(R.id.iv_nodatalabel);
        ivNodatalist= (ImageView) head_lablehome.findViewById(R.id.iv_nodatalist);
        tv_toplab= (TextView) head_lablehome.findViewById(R.id.tv_toplab);
        tv_goTask= (TextView) head_lablehome.findViewById(R.id.tv_goTask);
        tv_seeMore= (TextView) head_lablehome.findViewById(R.id.tv_seeMore);
        tv_goTask.setVisibility(View.VISIBLE);
        tv_goTask.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        tv_toplab.setText("点击标签选择好友贴标签");
        smoothListView.setAdapter(null);
        smoothListView.addHeaderView(head_lablehome);
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        tv_goTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent=new Intent(Constants.BROCAST_CLOSE_ACTIVITY_FORLABEL);
                sendBroadcast(intent);
            }
        });
        tv_seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MyLabelActivity.this,LabelListActivity.class);
                intent.putExtra("titleName","我的标签");
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        user_tagList_1_2();
        user_tagList_history_1_2(false);
    }

    //我给别人贴标签的记录列表
    private void user_tagList_history_1_2(boolean isfirstLoad) {
        if (userTagListHistory_1_2Request != null) {
            userTagListHistory_1_2Request.cancel();
        }


        UserTagListHistory_1_2Request.Input input = new UserTagListHistory_1_2Request.Input();
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
        userTagListHistory_1_2Request = new UserTagListHistory_1_2Request(input, new ResponseListener() {
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
                        smoothListView.setAdapter(commonAdapter = new CommonAdapter<M_Label>(MyLabelActivity.this, R.layout.item_mylable, labelHistoryList) {
                            @Override
                            public void convert(CommonViewHolder holder, final M_Label mlabel, int position) {
                                    GradientDrawable drawable = new GradientDrawable();
                                    drawable.setShape(GradientDrawable.RECTANGLE); // 画框
                                    drawable.setStroke(2, colorArry[position % 6]); // 边框粗细及颜色
                                    drawable.setCornerRadii(new float[]{25,
                                            25, 25, 25, 25, 25, 25, 25});
                                    drawable.setColor(0x22FFFFFF);  // 边框内部颜色
                                    holder.setTextColor(R.id.tv_labname,colorArry[position % 6]);
                                    holder.setText(R.id.tv_labname, mlabel.labelName,drawable);

                                    holder.setText(R.id.tv_tietime, DateTimeUtil.getTimeGapInfo(mlabel.createTime) );
                                    holder.setText(R.id.tv_labdescription, mlabel.note);
                                    holder.setText(R.id.tv_fromwho, "我给    "+mlabel.toUserName+"贴了一个标签");
                                    holder.setCircleImage(R.id.iv_friendhead,mlabel.fromUserHead);
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
                    ToastUtils.show(MyLabelActivity.this, ((APIM_UserTagList_1_2) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();


            }
        });
        sendJsonRequest(userTagListHistory_1_2Request);
    }



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
                    ToastUtils.show(MyLabelActivity.this, ((APIM_UserTagList_1_2) response).info);
                }
            }
        });
        sendJsonRequest(userTagList_1_2Request);
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
                    ToastUtils.show(MyLabelActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userTagDel_1_2Request);
    }

    void initLable() {
        ExplosionField explosionField = new ExplosionField(this);
        wordWrapView.setChildMargin(10, 10, 10, 10);
        wordWrapView.removeAllViews();

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
            if(i<10){
                wordWrapView.addView(LableBtn);
            }
            else{
                break;
            }
        }
        explosionField.addListener(wordWrapView,true);
    }

    public void removeLable(View viewLable) {
        wordWrapView.removeView(viewLable);
        user_tag_del_1_2(Integer.parseInt(viewLable.getTag().toString()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        user_tagList_history_1_2(true);
    }

    @Override
    public void onLoadMore() {
        user_tagList_history_1_2(false);
    }
}
