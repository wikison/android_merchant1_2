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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Wikison on 2016/8/4.
 */
public class PublishTaskRewardTypeActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lv)
    ListView lv;

    String requestType;
    List<RewardType> mDatas = new ArrayList<>();
    CommonAdapter commonAdapter;
    double bonuseMoney;
    int bonuseNum;
    int isHand =0; //红包分配是否发布人手动分配(0:否,1:是)(红包奖励必填)
    private Context mContext;
    private int merchantId;
    private String merchantName;
    private int state = 0; //0普通红包 1拼手气红包

    private double voucherMoney;
    private int voucherNum;
    private double voucherMinMoney;
    private int isUnion;
    private String voucherNote = "";
    private String voucherEndTime = "";

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

        lhTvTitle.setText("选择奖励方式");

        bonuseMoney = getIntent().getDoubleExtra("bonuse_money", 0);
        bonuseNum = getIntent().getIntExtra("bonuse_num", 0);
        state = getIntent().getIntExtra("bonuse_type", -1);
        isHand = getIntent().getIntExtra("is_hand", -1);

        voucherMoney = getIntent().getDoubleExtra("voucher_money", 0);
        voucherNum = getIntent().getIntExtra("voucher_num", 0);
        voucherMinMoney = getIntent().getIntExtra("voucher_min_money", -1);
        isUnion = getIntent().getIntExtra("is_union", 0);
        voucherEndTime = getIntent().getStringExtra("voucher_end_time");
        voucherNote = getIntent().getStringExtra("voucher_note");

        merchantId = getIntent().getIntExtra("merchant_id", -1);
        requestType = getIntent().getStringExtra("requesttype");

        String typeName[] = {"无", "红包", "代金券"};
        for (int i = 0; i < typeName.length; i++) {
            mDatas.add(new RewardType(typeName[i], i));
        }
    }


    private void initView() {
        lv.setAdapter(commonAdapter = new CommonAdapter<RewardType>(mContext, R.layout.item_task_type, mDatas) {
            @Override
            public void convert(CommonViewHolder holder, RewardType publishType, int position) {
                holder.setText(R.id.tv, publishType.typeName);
            }

        });
    }

    private void initListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RewardType p = mDatas.get(position);
                if (requestType.equals(Constants.BROCAST_PUBLISH_TASK_COUPON)) {
                    if (p.tag == 1) {
                        //跳转到红包界面
                        Intent intent = new Intent(mContext, PublishTaskBonusesActivity.class);
                        intent.putExtra("cash_type", p.tag);
                        intent.putExtra("reward_type_name", p.typeName);
                        intent.putExtra("bonuse_type", state);
                        intent.putExtra("bonuse_money", bonuseMoney);
                        intent.putExtra("bonuse_num", bonuseNum);
                        intent.putExtra("is_hand", isHand);
                        intent.putExtra("requesttype", Constants.BROCAST_PUBLISH_TASK_COUPON);
                        startActivityForResult(intent, 1);

                    } else if (p.tag == 2) {
                        //跳转到代金券设置界面
                        Intent intent = new Intent(mContext, PublishTaskRewardActivity.class);
                        intent.putExtra("cash_type", p.tag);
                        intent.putExtra("reward_type_name", p.typeName);
                        intent.putExtra("voucher_money", voucherMoney);
                        intent.putExtra("voucher_num", voucherNum);
                        intent.putExtra("voucher_min_money", voucherMinMoney);
                        intent.putExtra("voucher_end_time", voucherEndTime);
                        intent.putExtra("is_union", isUnion);
                        intent.putExtra("voucher_note", voucherNote);
                        intent.putExtra("requesttype", Constants.BROCAST_PUBLISH_TASK_COUPON);
                        startActivityForResult(intent, 2);
                    } else {
                        Intent intent = new Intent(Constants.BROCAST_PUBLISH_TASK_COUPON);
                        intent.putExtra("cash_type", p.tag);
                        intent.putExtra("cash_type_name", p.typeName);
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

    class RewardType {

        String typeName;
        int tag = -1;

        RewardType(String typeName, int tag) {
            this.typeName = typeName;
            this.tag = tag;
        }
    }

}