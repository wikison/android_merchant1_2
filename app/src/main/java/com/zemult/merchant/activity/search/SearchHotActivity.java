package com.zemult.merchant.activity.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.MerchantDetailActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.adapter.slashfrgment.ThinkingAdapter;
import com.zemult.merchant.aip.common.CommonHotSearchList;
import com.zemult.merchant.aip.slash.MerchantFirstpageSearchListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_HotWord;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_HotList;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FixedGridView;
import com.zemult.merchant.view.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import zema.volley.network.ResponseListener;

/**
 * 首页--搜索--关键词
 *
 * @author djy
 * @time 2016/8/3 11:53
 */
public class SearchHotActivity extends BaseActivity {

    private static final int REQ_SEARCH = 0x110;
    @Bind(R.id.a_seach_searchview)
    SearchView mSearchView;
    @Bind(R.id.a_search_grid)
    FixedGridView mGridView;
    @Bind(R.id.ll_nofind)
    LinearLayout llNofind;
    @Bind(R.id.ll_hot_search)
    LinearLayout llHotSearch;
    @Bind(R.id.ll_recent_search)
    LinearLayout llRecentSearch;
    @Bind(R.id.tcl_hot_search)
    TagContainerLayout tclHotSearch;
    @Bind(R.id.tcl_recent_search)
    TagContainerLayout tclRecentSearch;
    @Bind(R.id.tv_delete_history)
    TextView tvDeleteHistory;
    @Bind(R.id.lv)
    ListView lv;

    List<M_HotWord> hotWordList = new ArrayList<>();
    CommonAdapter hotWordAdapter;
    CommonHotSearchList commonHotSearchList;
    List<String> listHotTags = new ArrayList<>();
    List<String> listRecentTags = new ArrayList<>();
    List<String> listRecentTagsTemp = new ArrayList<>();
    String strRecentTags = "";
    private ThinkingAdapter adapter;

    private Context mContext;
    private Activity mActivity;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_search_hot);
    }

    @Override
    public void init() {
        registerReceiver(new String[]{Constants.BROCAST_SEARCH_RECENT_WORD});
        initData();
        initView();
        initListener();
        common_hot_search_list();
    }

    private void initData() {
        mContext = this;
        mActivity = this;
        strRecentTags = SlashHelper.getSettingString("home_search_history", "");
        listRecentTagsTemp = Arrays.asList(strRecentTags.split(",,"));
        listRecentTags.addAll(listRecentTagsTemp);
        adapter = new ThinkingAdapter(mContext, new ArrayList<M_Merchant>());
    }

    private void initView() {
        if (!StringUtils.isBlank(strRecentTags)) {
            llRecentSearch.setVisibility(View.VISIBLE);
            tclRecentSearch.setTags(listRecentTags);
        } else {
            listRecentTags.clear();
            llRecentSearch.setVisibility(View.GONE);
        }
        lv.setAdapter(adapter);
    }

    private void initListener() {
        mSearchView.setSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearch(String text) {
                goToSearch(text);
            }
        });
        mSearchView.setOnThinkingClickListener(new SearchView.OnThinkingClickListener() {
            @Override
            public void onThinkingClick(String text) {
                if(TextUtils.isEmpty(text.trim())){
                    lv.setVisibility(View.GONE);
                    return;
                }
                merchant_firstpage_search_List(text);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchView.setStrSearch(hotWordList.get(position).note);
                goToSearch(hotWordList.get(position).note);
            }
        });

        tclHotSearch.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                mSearchView.setUnThinking(true);
                mSearchView.setStrSearch(text);
                goToSearch(text);
            }

            @Override
            public void onTagLongClick(final int position, String text) {
            }
        });

        tclRecentSearch.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                mSearchView.setUnThinking(true);
                mSearchView.setStrSearch(text);
                goToSearch(text);
            }

            @Override
            public void onTagLongClick(final int position, String text) {
            }
        });

        tvDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listRecentTags.clear();
                tclRecentSearch.removeAllTags();
                llRecentSearch.setVisibility(View.GONE);
                SlashHelper.setSettingString("home_search_history", "");
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeListRecentTags(adapter.getItem(position).name);
                Intent intent = new Intent(mContext, MerchantDetailActivity.class);
                intent.putExtra(MerchantDetailActivity.MERCHANT_ID, adapter.getItem(position).merchantId);
                startActivity(intent);
            }
        });
    }

    private void goToSearch(String key) {
        changeListRecentTags(key);

        Intent intent = new Intent(mActivity, SearchActivity.class);
        intent.putExtra(SearchActivity.INTENT_KEY, key);
        intent.putExtra("requesttype", Constants.BROCAST_SEARCH_RECENT_WORD);
        startActivity(intent);
    }

    private void changeListRecentTags(String key) {
        if (!listRecentTags.contains(key)) {
            if (listRecentTags.size() == Constants.RECENT_SEARCH_ROWS) {
                //元素循环向前移动一位 删除最后一位
                Collections.rotate(listRecentTags, 1);
                listRecentTags.remove(Constants.RECENT_SEARCH_ROWS - 1);
            }
            //向最后一位添加元素
            listRecentTags.add(key);
            SlashHelper.setSettingString("home_search_history", listToString(listRecentTags));
        }

        if (listRecentTags.size() > 0) {
            llRecentSearch.setVisibility(View.VISIBLE);
            tclRecentSearch.setTags(listRecentTags);
        } else {
            llRecentSearch.setVisibility(View.GONE);
        }

        tclRecentSearch.removeAllTags();
        tclRecentSearch.setTags(listRecentTags);
    }


    private MerchantFirstpageSearchListRequest request;

    public void merchant_firstpage_search_List(String key) {
        if (request != null) {
            request.cancel();
        }

        MerchantFirstpageSearchListRequest.Input input = new MerchantFirstpageSearchListRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.industryId = -1;
        input.name = key.trim();
        input.city = Constants.CITYID;
        input.center = Constants.CENTER;
        input.page = 1;
        input.rows = 100;

        input.convertJosn();
        request = new MerchantFirstpageSearchListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    fillAdapter(((APIM_MerchantList) response).merchantList);

                } else {
                    ToastUtil.showMessage(((APIM_MerchantList) response).info);
                }
            }
        });
        sendJsonRequest(request);
    }

    // 填充数据
    private void fillAdapter(List<M_Merchant> list) {
        if (list == null || list.size() == 0) {
            lv.setVisibility(View.GONE);
        } else {
            lv.setVisibility(View.VISIBLE);
            adapter.setData(list);
        }
    }
    private void common_hot_search_list() {
        {
            if (commonHotSearchList != null) {
                commonHotSearchList.cancel();
            }
            showPd();
            CommonHotSearchList.Input input = new CommonHotSearchList.Input();
            input.page = 1;
            input.rows = Constants.HOT_SEARCH_ROWS;

            input.convertJosn();
            commonHotSearchList = new CommonHotSearchList(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissPd();
                    Log.e("VolleyError", error.toString());
                }

                @Override
                public void onResponse(Object response) {
                    dismissPd();
                    if (((APIM_HotList) response).status == 1) {
                        hotWordList = ((APIM_HotList) response).hotList;
                    } else {
                        ToastUtils.show(mActivity, ((APIM_HotList) response).info);
                    }
                    if (hotWordList.size() > 0) {
                        llHotSearch.setVisibility(View.VISIBLE);
                        for (int i = 0; i < hotWordList.size(); i++) {
                            listHotTags.add(hotWordList.get(i).note);
                        }
                        tclHotSearch.setTags(listHotTags);
                    } else {
                        llHotSearch.setVisibility(View.GONE);
                    }

                    mGridView.setAdapter(hotWordAdapter = new CommonAdapter<M_HotWord>(mActivity,
                            R.layout.item_hot_search, hotWordList) {
                        @Override
                        public void convert(CommonViewHolder holder, M_HotWord m_hotWord, int position) {
                            holder.setText(R.id.tv_hot_word, m_hotWord.note);

                        }

                    });

                }
            });
            sendJsonRequest(commonHotSearchList);
        }
    }

    private String listToString(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",,");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        if (Constants.BROCAST_SEARCH_RECENT_WORD.equals(intent.getAction())) {
            listRecentTags.clear();
            strRecentTags = SlashHelper.getSettingString("home_search_history", "");
            listRecentTagsTemp = Arrays.asList(strRecentTags.split(",,"));
            listRecentTags.addAll(listRecentTagsTemp);
            if (listRecentTags.size() > 0) {
                llRecentSearch.setVisibility(View.VISIBLE);
                tclRecentSearch.setTags(listRecentTags);
            } else {
                llRecentSearch.setVisibility(View.GONE);
            }

            tclRecentSearch.removeAllTags();
            tclRecentSearch.setTags(listRecentTags);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchView.setUnThinking(false);
    }
}
