package com.zemult.merchant.activity.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.common.CommonHotSearchList;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_HotWord;
import com.zemult.merchant.model.apimodel.APIM_HotList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.FixedGridView;
import com.zemult.merchant.view.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
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
    List<M_HotWord> hotWordList = new ArrayList<>();
    CommonAdapter hotWordAdapter;
    CommonHotSearchList commonHotSearchList;
    List<String> listHotTags = new ArrayList<>();
    List<String> listRecentTags = new ArrayList<>();
    List<String> listRecentTagsTemp = new ArrayList<>();
    String strRecentTags = "";

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
    }

    private void initView() {
        if (!StringUtils.isBlank(strRecentTags)) {
            llRecentSearch.setVisibility(View.VISIBLE);
            tclRecentSearch.setTags(listRecentTags);
        } else {
            listRecentTags.clear();
            llRecentSearch.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        mSearchView.setSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearch(String text) {
                goToSearch(text);
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
    }

    private void goToSearch(String key) {
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

        Intent intent = new Intent(mActivity, SearchActivity.class);
        intent.putExtra(SearchActivity.INTENT_KEY, key);
        intent.putExtra("requesttype", Constants.BROCAST_SEARCH_RECENT_WORD);
        startActivity(intent);
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

}
