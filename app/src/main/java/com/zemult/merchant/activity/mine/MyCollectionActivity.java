package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.MerchantDetailActivity;
import com.zemult.merchant.activity.slash.SearchDetailActivity;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.adapter.minefragment.MyCollectionAdapter;
import com.zemult.merchant.adapter.slashfrgment.HomeChildNewAdapter;
import com.zemult.merchant.aip.mine.UserDeleteFavoriteRequest;
import com.zemult.merchant.aip.mine.UserFavoriteListRequest;
import com.zemult.merchant.aip.slash.UserFavoriteMerchantDelRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.model.apimodel.APIM_ManagerSearchnewsList;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 056 我的收藏
 */
public class MyCollectionActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {

    public static final String TAG = "MyCollectionActivity";
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    private Context mContext;
    private Activity mActivity;
    private int page = 1;
    private UserFavoriteListRequest userFavoriteListRequest; // 获取用户的方案列表(角色/场景/时间倒排序)
    private UserFavoriteMerchantDelRequest userFavoriteMerchantDelRequest;//删除收藏

    private HomeChildNewAdapter mAdapter; // 主页数据



    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_collection);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        showPd();
        user_favorite_list(false);

        smoothListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                CommonDialog.showDialogListener(MyCollectionActivity.this, null, "取消", "确定", "删除此条收藏", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPd();
                        int merchantId = ((M_Merchant) parent.getAdapter().getItem(position)).merchantId;
                        userDeleteFavorite(merchantId, position);
                        CommonDialog.DismissProgressDialog();
                    }
                });
                return true;
            }
        });
    }

    private void userDeleteFavorite(int merchantId, final int position) {
        if (userFavoriteMerchantDelRequest != null) {
            userFavoriteMerchantDelRequest.cancel();
        }
        UserFavoriteMerchantDelRequest.Input input = new UserFavoriteMerchantDelRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.merchantId = merchantId;
        input.convertJosn();
        userFavoriteMerchantDelRequest = new UserFavoriteMerchantDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("删除成功");
                    mAdapter.delOneRecord(position - 1);
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }

            }
        });
        sendJsonRequest(userFavoriteMerchantDelRequest);
    }

    private void initData() {
        mContext = this;
        mActivity = this;
    }

    private void initView() {
        lhTvTitle.setText("我的收藏");
        mAdapter = new HomeChildNewAdapter(mContext, new ArrayList<M_Merchant>());
        mAdapter.unshowTop();
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, MerchantDetailActivity.class);
                intent.putExtra(MerchantDetailActivity.MERCHANT_ID, mAdapter.getItem(position-1).merchantId);
                startActivity(intent);
            }
        });
//        searchEtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH
//                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                    hideSoftInputView();
//                    showPd();
//                    user_favorite_list(false);
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    /**
     * 我的收藏--搜索用户收藏列表(条件:名称--用户名/方案内容)
     */
    private void user_favorite_list(final boolean isLoadMore) {
        if (userFavoriteListRequest != null) {
            userFavoriteListRequest.cancel();
        }
        UserFavoriteListRequest.Input input = new UserFavoriteListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.center = (String) SPUtils.get(mContext, Constants.SP_CENTER, Constants.CENTER);
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();

        userFavoriteListRequest = new UserFavoriteListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    fillAdapter(((APIM_MerchantList) response).merchantList,
                            ((APIM_MerchantList) response).maxpage,
                            isLoadMore);

                } else {
                    ToastUtils.show(MyCollectionActivity.this, ((APIM_MerchantList) response).info);
                }
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }
        });
        sendJsonRequest(userFavoriteListRequest);
    }

    // 填充数据  List<M_News>  List<M_News>
    private void fillAdapter(final List<M_Merchant> list, int maxpage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            smoothListView.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
        } else {
            smoothListView.setVisibility(View.VISIBLE);
            rlNoData.setVisibility(View.GONE);

            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(list, isLoadMore);
        }
    }

    @Override
    public void onRefresh() {
        user_favorite_list(false);
    }

    @Override
    public void onLoadMore() {
        user_favorite_list(true);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
                onBackPressed();
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
