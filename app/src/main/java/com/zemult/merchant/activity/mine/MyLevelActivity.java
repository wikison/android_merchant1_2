package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.MyLevelTaskAdapter;
import com.zemult.merchant.aip.mine.UserLevelInfoRequest;
import com.zemult.merchant.aip.mine.UserTaskListRequest;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_UserLevelList;
import com.zemult.merchant.model.apimodel.APIM_UserTaskList;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FixedListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/8/3.
 */
public class MyLevelActivity extends MBaseActivity {


    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.head_iv)
    ImageView headIv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.level_tv)
    TextView levelTv;
    @Bind(R.id.nextEXP_tv)
    TextView nextEXPTv;
    ImageManager imageManager;
    String head = "", name = "";
    int level, nextEXP, experience, lackexp;

    UserTaskListRequest userTaskListRequest;//用户的等级任务列表

    UserLevelInfoRequest userLevelInfoRequest;//用户的等级信息
    List<M_Task> topdatas = new ArrayList<M_Task>();
    List<M_Task> buttomdatas = new ArrayList<M_Task>();
    List<M_Task> alldatas = new ArrayList<M_Task>();
    @Bind(R.id.task_lv)
    FixedListView taskLv;
    MyLevelTaskAdapter myLevelTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylevel);
        ButterKnife.bind(this);
        lhTvTitle.setText("我的等级");
        lhBtnRightiamge.setVisibility(View.VISIBLE);
        lhBtnRightiamge.setBackgroundResource(R.mipmap.paihang_icon);
        imageManager = new ImageManager(this);
        head = getIntent().getStringExtra("head")==null?"":getIntent().getStringExtra("head");
        imageManager.loadCircleHasBorderImage(head, headIv, this.getResources().getColor(R.color.divider_dc), 1);
        nameTv.setText(getIntent().getStringExtra("name"));
        showPd();

        userLevelInfo(); //获取用户等级信息

        userTaskList(2);//网络获取上面的长度

    }

    //用户的等级任务列表
    private void userTaskList(final int type) {
        if (userTaskListRequest != null) {
            userTaskListRequest.cancel();
        }
        UserTaskListRequest.Input input = new UserTaskListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.type = type;
        input.page = 1;
        input.rows = Constants.ROWS;     //每页显示的页数
        input.convertJosn();
        userTaskListRequest = new UserTaskListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                ToastUtil.showMessage("网络故障");
            }

            @Override
            public void onResponse(Object response) {
                Log.i("aaa", "请求的结果是" + ((APIM_UserTaskList) response).status);

                if (((APIM_UserTaskList) response).status == 1) {

                    if (type == 2) {
                        topdatas = ((APIM_UserTaskList) response).taskList;
                        M_Task bean = new M_Task();
                        bean.setViewType(1);
                        topdatas.add(0,bean);


                        // topdatas.add(0,new M_Task().setViewType(1));

                        userTaskList(1);//网络获取下面的长度
                        Log.i("aaa", "上面长度" + topdatas.size());
                    }
                    if (type == 1) {
                        buttomdatas = ((APIM_UserTaskList) response).taskList;
                       // buttomdatas.add(0,);
                        M_Task bean = new M_Task();
                        bean.setViewType(2);
                        buttomdatas.add(0,bean);
                        Log.i("aaa", "下面长度" + buttomdatas.size());
                        alldatas.addAll(topdatas);
                        alldatas.addAll(buttomdatas);
                        Log.i("aaa", "总长度为" + alldatas.size());
                        myLevelTaskAdapter = new MyLevelTaskAdapter(MyLevelActivity.this, topdatas, alldatas);
                        taskLv.setAdapter(myLevelTaskAdapter);
                        dismissPd();
                       // setListViewHeightBasedOnChildren(taskLv);
                    }
                } else {
                    ToastUtils.show(MyLevelActivity.this, ((APIM_UserTaskList) response).info);
                }
            }
        });
        sendJsonRequest(userTaskListRequest);
    }


    // //获取用户自身等级信息
    private void userLevelInfo() {

        if (userLevelInfoRequest != null) {
            userLevelInfoRequest.cancel();
        }

        UserLevelInfoRequest.Input input = new UserLevelInfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }

        input.convertJosn();
        userLevelInfoRequest = new UserLevelInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserLevelList) response).status == 1) {
                    level = ((APIM_UserLevelList) response).level;
                    levelTv.setText("" + level);
                    nextEXP = ((APIM_UserLevelList) response).nextEXP;
                    experience = ((APIM_UserLevelList) response).experience;
                    lackexp = nextEXP - experience;
                    nextEXPTv.setText("距离下级Lv." + (level + 1) + "还差" + lackexp + "经验值");

                } else {
                    ToastUtils.show(MyLevelActivity.this, ((APIM_UserLevelList) response).info);
                }

            }
        });
        sendJsonRequest(userLevelInfoRequest);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_btn_rightiamge})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.lh_btn_rightiamge:
                startActivity(new Intent(MyLevelActivity.this, LevelRankingActivity.class));
                break;
        }
    }


    /**
     * 设置listview高度的方法
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}





