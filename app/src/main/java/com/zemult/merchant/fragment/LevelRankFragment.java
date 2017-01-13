package com.zemult.merchant.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.RanklistAdapter;
import com.zemult.merchant.aip.mine.UserLevelListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.model.apimodel.APIM_UserFansList;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/7/23.
 */
public class



LevelRankFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {
    @Bind(R.id.rank_tv)
    TextView rankTv;
    @Bind(R.id.rank_lv)
    SmoothListView rankLv;
    @Bind(R.id.myhead_iv)
    ImageView myheadIv;
    @Bind(R.id.myname_tv)
    TextView mynameTv;
    @Bind(R.id.others_tv)
    TextView othersTv;
    @Bind(R.id.tv_state)
    TextView tvState;
    private int type;//标识网络获取方法
    int page = 1;
    private boolean hasLoaded;
    private UserLevelListRequest userLevelListRequest;
    private List<M_Fan> datas = new ArrayList<M_Fan>();
    private RanklistAdapter ranklistAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPd();
        type = getArguments().getInt("data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_levelrank, null, false);
        ButterKnife.bind(this, view);
        if (type == 0) {
            rankTv.setText("用户总排行");
        } else if (type == 1) {
            rankTv.setText("好友排行");
        }


//        ranklistAdapter = new RanklistAdapter(getActivity(), datas);
//        rankLv.setAdapter(ranklistAdapter);
        rankLv.setRefreshEnable(true);
        rankLv.setLoadMoreEnable(false);
        rankLv.setSmoothListViewListener(this);

        return view;

    }

    private void getNetworkData(boolean isLoadMore) {

        userLevelList(isLoadMore);

    }

    private void userLevelList(final boolean isLoadMore) {
        if (userLevelListRequest != null) {
            userLevelListRequest.cancel();
        }
        UserLevelListRequest.Input input = new UserLevelListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        //input.userId = 72;
        input.type = type;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        userLevelListRequest = new UserLevelListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                rankLv.stopRefresh();
                rankLv.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserFansList) response).status == 1) {

                    Log.i("sunjian", "11111" + ((APIM_UserFansList) response).status);

                    if (null == ranklistAdapter) {
                        ranklistAdapter = new RanklistAdapter(getActivity(), datas);
                        rankLv.setAdapter(ranklistAdapter);
                    }

                    fillAdapter(((APIM_UserFansList) response).userList,
                            ((APIM_UserFansList) response).maxpage,
                            isLoadMore);
                    fillmyself(((APIM_UserFansList) response).head, ((APIM_UserFansList) response).name, ((APIM_UserFansList) response).level, ((APIM_UserFansList) response).experience, ((APIM_UserFansList) response).place);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_UserFansList) response).info);
                }
                rankLv.stopRefresh();
                rankLv.stopLoadMore();
                if (type == 0) {
                    rankLv.setLoadMoreEnable(false);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userLevelListRequest);
    }

    private void fillmyself(String head, String name, int level, int experience, int place) {

        ImageManager imageManager = new ImageManager(getActivity());
        imageManager.loadCircleHasBorderImage(head, myheadIv, getResources().getColor(R.color.gainsboro), 1);

        mynameTv.setText(name);

        othersTv.setText("排名" + place + "     " + "等级" + level + "     " + "经验值" + experience);
    }


    /**
     * 设置数据
     */
    private void fillAdapter(final List<M_Fan> list, int maxpage, boolean isLoadMore) {
        if (list != null && !list.isEmpty()) {
            rankLv.setLoadMoreEnable(page < maxpage);
            ranklistAdapter.setData(list, isLoadMore);
        }
    }


    @Override
    protected void lazyLoad() {


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {

        getNetworkData(false);

    }

    @Override
    public void onLoadMore() {
          getNetworkData(true);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i("abcd", "123" + hidden);

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        if (isVisibleToUser) {
            //fragment可见时加载数据

            getNetworkData(false);
        } else {
            //不可见时不执行操作
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

}
