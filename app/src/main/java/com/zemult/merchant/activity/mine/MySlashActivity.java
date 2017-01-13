package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserIndustryDelRequest;
import com.zemult.merchant.aip.mine.UserIndustryListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.model.apimodel.APIM_UserIndustryList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.swipelistview.SwipeItemLayout;
import com.zemult.merchant.view.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by wikison on 2016/6/14.
 */
public class MySlashActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightImage;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.sml_my_slash)
    SwipeListView mMenuListView;

    List<M_UserRole> listRoles = new ArrayList<M_UserRole>();
    MenuListAdapter menuListAdapter;
    UserIndustryListRequest userIndustryListRequest;
    UserIndustryDelRequest userIndustryDelRequest;
    int page = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_slash);
    }

    @Override
    public void init() {
        initViews();
        initData();
//        initMenus();
//        initListener();
    }

    private void initData() {
        user_IndustryList(true);

    }

    //获取用户列表
    public void user_IndustryList(final boolean isFresh) {
        if (userIndustryListRequest != null) {
            userIndustryListRequest.cancel();
        }

        UserIndustryListRequest.Input input = new UserIndustryListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();

        }
        if (isFresh) {
            input.page = 1;
            listRoles.clear();
        } else {
            input.page = page;
        }

        input.rows = 1000;

        input.convertJosn();
        userIndustryListRequest = new UserIndustryListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserIndustryList) response).status == 1) {
                    listRoles.addAll(((APIM_UserIndustryList) response).industryList);
                    fillAdapter(listRoles,
                            ((APIM_UserIndustryList) response).maxpage);
                } else {
                    ToastUtils.show(MySlashActivity.this, ((APIM_UserIndustryList) response).info);
                }
            }
        });
        sendJsonRequest(userIndustryListRequest);
    }

    private void fillAdapter(List<M_UserRole> list, int maxpage) {
        if (list != null && !list.isEmpty()) {
            menuListAdapter = new MenuListAdapter(this, list);
            mMenuListView.setAdapter(menuListAdapter);
        }
    }

    private void initViews() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText(getResources().getString(R.string.app_name));
        lhBtnRightImage.setVisibility(View.VISIBLE);
        lhBtnRightImage.setBackgroundResource(R.mipmap.add_slash_icon);
        lhBtnRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        mMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                M_UserRole userRole = (M_UserRole) parent.getAdapter().getItem(position);
                Log.i("selectroleid", userRole.industryId + "");
                Intent intent = new Intent(MySlashActivity.this, MySlashMerchantActivity.class);
                intent.putExtra(MySlashMerchantActivity.INTENT_ROLE_ID, userRole.industryId);
                intent.putExtra(MySlashMerchantActivity.INTENT_ROLE_NAME, userRole.name);
                startActivity(intent);
            }
        });


    }

    private void user_industry_del(final M_UserRole userRole) {
        if (userIndustryDelRequest != null) {
            userIndustryDelRequest.cancel();
        }

        UserIndustryDelRequest.Input input = new UserIndustryDelRequest.Input();

        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.industryId = userRole.industryId;

        input.convertJosn();
        userIndustryDelRequest = new UserIndustryDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {

                    ToastUtil.showMessage("删除成功");
                    listRoles.remove(userRole);
                    menuListAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.show(MySlashActivity.this, ((CommonResult) response).info);

                }

            }
        });

        sendJsonRequest(userIndustryDelRequest);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MySlash Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    public class MenuListAdapter extends BaseAdapter {
        private Context context;
        private List<M_UserRole> list;

        public MenuListAdapter(Context context, List<M_UserRole> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i("position -->>", String.valueOf(position));
            Holder holder;
            if (convertView == null) {
                View view = LayoutInflater.from(context).inflate(
                        R.layout.item_my_slash,// 文件名
                        null);
                View menuView = LayoutInflater.from(context).inflate(
                        R.layout.item_swipe_operation, null);
                convertView = new SwipeItemLayout(view, menuView, null, null);
                holder = new Holder();
                holder.tv_slash_name = (TextView) convertView
                        .findViewById(R.id.tv_slash_name);
                holder.iv_slash_head = (ImageView) convertView
                        .findViewById(R.id.iv_slash_head);
                holder.tv_share_btn = (TextView) convertView
                        .findViewById(R.id.share_btn);
                holder.tv_remove_btn = (TextView) convertView
                        .findViewById(R.id.remove_btn);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            M_UserRole role = list.get(position);
            holder.tv_slash_name.setText(role.name);
            imageManager.loadCircleImage(role.icon, holder.iv_slash_head);
            holder.initView(role);
            return convertView;
        }


        private class Holder implements View.OnClickListener {
            TextView tv_slash_name;
            ImageView iv_slash_head;
            TextView tv_share_btn;
            TextView tv_remove_btn;

            M_UserRole model;

            public void initView(M_UserRole model) {
                this.model = model;
                tv_share_btn.setOnClickListener(this);
                tv_remove_btn.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.share_btn:
                        String URL_SHARE_APP = SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_APP, "http://www.54xiegang.com/csdown/index.html");
                        UMImage image = new UMImage(MySlashActivity.this, model.icon);
                        new ShareAction(MySlashActivity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)//SHARE_MEDIA.SINA,SHARE_MEDIA.QZONE,
                                .withText("我在斜杠平台斜杠了" + model.name + "角色！快来参与吧！")
                                .withMedia(image)
                                .withTargetUrl(URL_SHARE_APP)
                                .setCallback(umShareListener)
                                .open();

                        break;
                    case R.id.remove_btn:
                        user_industry_del(model);
                        break;
                    default:
                        break;
                }

            }
        }

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


    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
//                Toast.makeText(MySlashActivity.this,platform + " 收藏成功啦",Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(MySlashActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(MySlashActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(MySlashActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };
}
