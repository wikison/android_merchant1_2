package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2016/8/4.
 */
public class PublishTaskTypeActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lv)
    ListView lv;
    String title;
    String requestType;
    String voteNote;
    List<PublishType> mDatas = new ArrayList<>();
    CommonAdapter commonAdapter;
    double payMoney;
    private Context mContext;
    private boolean isMerchant = false;
    private int merchantId;
    private String merchantName;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_publish_task_type);

    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

    }

    private void initData() {
        mContext = this;
        lhTvTitle.setText("选择任务方式");
        merchantId = getIntent().getIntExtra("merchant_id", -1);
        if (merchantId > 0) {
            isMerchant = true;
            merchantName = getIntent().getStringExtra("merchant_name");
            payMoney = getIntent().getDoubleExtra("pay_money", 0);
        } else {
            isMerchant = false;
        }
        requestType = getIntent().getStringExtra("requesttype");
        title = getIntent().getStringExtra("title");
        voteNote = getIntent().getStringExtra("vote_note");

        int res[] = {R.mipmap.fengxiang_icon, R.mipmap.yuying_icon, R.mipmap.toupiao_icon, R.mipmap.xiaofei_icon};
        String typeName[] = {"分享", "语音", "投票", "买单"};
        if (isMerchant) {
            for (int i = 0; i < res.length; i++) {
                mDatas.add(new PublishType(res[i], typeName[i], i));
            }
        } else {
            for (int i = 0; i < res.length - 1; i++) {
                mDatas.add(new PublishType(res[i], typeName[i], i));
            }
        }


    }

    private void initView() {
        lv.setAdapter(commonAdapter = new CommonAdapter<PublishType>(mContext, R.layout.item_task_type, mDatas) {
            @Override
            public void convert(CommonViewHolder holder, PublishType publishType, int position) {
                holder.setResImage(R.id.iv, publishType.resId);
                holder.setText(R.id.tv, publishType.typeName);
            }

        });
    }

    private void initListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PublishType p = mDatas.get(position);
                if (requestType.equals(Constants.BROCAST_PUBLISH_TASK_TYPE)) {
                    if (p.tag == 2) {
                        //跳转到投票界面
                        if (StringUtils.isBlank(title)) {
                            ToastUtil.showMessage("投票必须先设置任务主题");
                            finish();
                        } else {
                            Intent intent = new Intent(mContext, PublishTaskVoteActivity.class);
                            intent.putExtra("publish_type", p.tag);
                            intent.putExtra("publish_type_name", p.typeName);
                            intent.putExtra("vote_note", voteNote);
                            intent.putExtra("title", title);
                            intent.putExtra("requesttype", Constants.BROCAST_PUBLISH_TASK_TYPE);
                            startActivityForResult(intent, 1);
                        }
                    } else if (p.tag == 3) {
                        //跳转到买单设置界面
                        Intent intent = new Intent(mContext, PublishTaskPayActivity.class);
                        intent.putExtra("publish_type", p.tag);
                        intent.putExtra("publish_type_name", p.typeName);
                        intent.putExtra("merchant_id", merchantId);
                        intent.putExtra("merchant_name", merchantName);
                        intent.putExtra("pay_money", payMoney);
                        intent.putExtra("requesttype", Constants.BROCAST_PUBLISH_TASK_TYPE);
                        startActivityForResult(intent, 2);
                    } else {
                        Intent intent = new Intent(Constants.BROCAST_PUBLISH_TASK_TYPE);
                        intent.putExtra("publish_type", p.tag);
                        intent.putExtra("publish_type_name", p.typeName);
                        setResult(RESULT_OK, intent);
                        sendBroadcast(intent);
                        finish();
                    }

                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            finish();
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

    class PublishType {

        int resId;
        String typeName;
        int tag = -1;

        PublishType(int resId, String typeName, int tag) {
            this.resId = resId;
            this.typeName = typeName;
            this.tag = tag;
        }
    }
}
