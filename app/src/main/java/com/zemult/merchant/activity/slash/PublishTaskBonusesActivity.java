package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.ToastUtil;
import com.taobao.av.util.StringUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Wikison on 2016/8/8.
 * 发红包页面
 */
public class PublishTaskBonusesActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.tv_bonuses_money)
    TextView tvBonusesMoney;
    @Bind(R.id.tv_bonuses_num)
    TextView tvBonusesNum;
    @Bind(R.id.tv_hint)
    TextView tvHint;
    @Bind(R.id.tv_change_bonuses_type)
    TextView tvChangeBonusesType;
    @Bind(R.id.cb_hand)
    CheckBox cbHand;
    @Bind(R.id.ll_bonuses_hand)
    LinearLayout llBonusesHand;
    @Bind(R.id.tv_bonuses_all)
    TextView tvBonusesAll;
    @Bind(R.id.btn_publish_bonuses)
    RoundTextView btnPublishBonuses;
    @Bind(R.id.et_bonuses_money)
    EditText etBonusesMoney;
    @Bind(R.id.et_bonuses_number)
    EditText etBonusesNumber;
    @Bind(R.id.iv_pin)
    ImageView ivPin;
    boolean isEnabled = false;
    double bonuseMoneyAll, bonusesOne;
    int bonusesNum;
    boolean isBonuses = true, isBonusesNum = true;
    int isHand = 0; //红包分配是否发布人手动分配(0:否,1:是)(红包奖励必填)
    private int state = 0; //0普通红包 1拼手气红包
    private String requestType = "";
    private Context mContext;
    private String strBonuses, strBonusesNum;
    private int cashType;
    private String reWardName;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_publish_task_bonuses);
    }

    @Override
    public void init() {
        initData();
        initListener();

    }

    private void initData() {
        mContext = this;
        requestType = getIntent().getStringExtra("requesttype");
        cashType = getIntent().getIntExtra("cash_type", -1);
        reWardName = getIntent().getStringExtra("reward_type_name");
        bonuseMoneyAll = getIntent().getDoubleExtra("bonuse_money", 0);
        bonusesNum = getIntent().getIntExtra("bonuse_num", 0);
        state = getIntent().getIntExtra("bonuse_type", -1);
        isHand = getIntent().getIntExtra("is_hand", 0);

        if (state == 0) {
            state = 1;
            changeView();
            if (bonusesNum > 0) {
                etBonusesMoney.setText(bonuseMoneyAll / bonusesNum + "");
                etBonusesNumber.setText(bonusesNum + "");
                tvBonusesAll.setText("￥" + String.format("%.2f", bonuseMoneyAll));
                checkBonuses();
            }
        } else if (state == 1) {
            state = 0;
            changeView();
            if (bonusesNum > 0) {
                etBonusesMoney.setText(bonuseMoneyAll + "");
                etBonusesNumber.setText(bonusesNum + "");
                tvBonusesAll.setText("￥" + String.format("%.2f", bonuseMoneyAll));
                checkBonuses();
            }
        }
        cbHand.setChecked(isHand == 1 ? true : false);

        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText(getResources().getString(R.string.publish_bonuses));
    }

    private void initListener() {
        etBonusesMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etBonusesMoney.setText(s);
                        etBonusesMoney.setSelection(s.length());
                        isBonuses = false;
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etBonusesMoney.setText(s);
                    etBonusesMoney.setSelection(2);
                    isBonuses = false;
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etBonusesMoney.setText(s.subSequence(0, 1));
                        etBonusesMoney.setSelection(1);
                        isBonuses = false;
                        return;
                    }
                }

                if (s.toString().length() >= 1 && !s.toString().endsWith(".") && Double.valueOf(s.toString()) > 0) {
                    if (state == 0) {
                        if (Double.valueOf(s.toString()) > 200) {
                            ToastUtil.showMessage("单个红包最大200");
                            isBonuses = false;
                            checkBonuses();
                            return;
                        }
                    }
                    if (state == 1) {
                        if (Double.valueOf(s.toString()) > 20000) {
                            ToastUtil.showMessage("红包总额不超过20000");
                            isBonuses = false;
                            checkBonuses();
                            return;
                        }
                    }
                    isBonuses = true;

                } else {
                    isBonuses = false;
                }
                checkBonuses();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etBonusesNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 1) {
                    if (s.toString().substring(0).equals("0")) {
                        etBonusesNumber.setText("");
                        isBonusesNum = false;
                        checkBonuses();
                        return;
                    } else if (Integer.valueOf(s.toString()) > 100) {
                        isBonusesNum = false;
                        ToastUtil.showMessage("红包个数最大设置100个");
                        checkBonuses();
                        return;
                    }
                    isBonusesNum = true;

                } else {
                    isBonusesNum = false;
                }
                checkBonuses();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_change_bonuses_type, R.id.btn_publish_bonuses})
    public void viewClick(View v) {
        switch (v.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;

            case R.id.tv_change_bonuses_type:
                changeView();
                break;

            case R.id.btn_publish_bonuses:
                if (isEnabled) {
                    isHand = cbHand.isChecked() ? 1 : 0;
                    if (requestType.equals(Constants.BROCAST_PUBLISH_TASK_BONUSES)) {
                        Intent intent = new Intent(Constants.BROCAST_PUBLISH_TASK_BONUSES);
                        intent.putExtra("bonuse_type", state);
                        intent.putExtra("bonuse_money", bonuseMoneyAll);
                        intent.putExtra("bonuse_num", bonusesNum);
                        intent.putExtra("is_hand", isHand);
                        setResult(RESULT_OK, intent);
                        sendBroadcast(intent);
                        finish();

                    }

                }
                break;
        }
    }

    private void changeView() {
        if (state == 0) {
            state = 1;
            tvChangeBonusesType.setText(getResources().getString(R.string.bonuses_money_change2));
            tvBonusesMoney.setText(getResources().getString(R.string.bonuses_money_all));
            ivPin.setVisibility(View.VISIBLE);
            tvHint.setText(getResources().getString(R.string.bonuses_money_hint2));
            tvChangeBonusesType.setClickable(true);
            etBonusesMoney.setText("");
            etBonusesNumber.setText("");
            tvBonusesAll.setText("￥0.00");
            btnPublishBonuses.getDelegate().setBackgroundColor(getResources().getColor(R.color.btn_press_enable));
            isEnabled = false;
            return;
        }
        if (state == 1) {
            state = 0;
            tvChangeBonusesType.setText(getResources().getString(R.string.bonuses_money_change1));
            tvBonusesMoney.setText(getResources().getString(R.string.bonuses_money_one));
            ivPin.setVisibility(View.INVISIBLE);
            tvHint.setText(getResources().getString(R.string.bonuses_money_hint1));
            tvChangeBonusesType.setClickable(true);
            etBonusesMoney.setText("");
            etBonusesNumber.setText("");
            tvBonusesAll.setText("￥0.00");
            btnPublishBonuses.getDelegate().setBackgroundColor(getResources().getColor(R.color.btn_press_enable));
            isEnabled = false;
            return;
        }
    }

    private void getEditString() {
        strBonuses = StringUtil.isEmpty(etBonusesMoney.getText().toString())?"0":etBonusesMoney.getText().toString();
        strBonusesNum =StringUtil.isEmpty(etBonusesNumber.getText().toString())?"0":etBonusesNumber.getText().toString();
    }

    private void checkBonuses() {
        if (isBonuses == true && isBonusesNum == true) {
            getEditString();
            if (state == 0) {
                bonusesOne = Double.valueOf(strBonuses);
                bonusesNum = Integer.valueOf(strBonusesNum);
                bonuseMoneyAll = bonusesOne * bonusesNum;
                if (bonuseMoneyAll > 20000) {
                    ToastUtil.showMessage("红包总额不超过20000");
                    isBonusesNum = false;
                    isBonuses = false;
                    tvBonusesAll.setText("￥0.00");
                    btnPublishBonuses.getDelegate().setBackgroundColor(getResources().getColor(R.color.btn_press_enable));
                    isEnabled = false;

                    return;

                }
            } else if (state == 1) {
                bonuseMoneyAll = Double.valueOf(strBonuses);
                bonusesNum = Integer.valueOf(strBonusesNum);
                bonusesOne = bonuseMoneyAll / bonusesNum;
                if (bonusesOne < 0.01) {
                    ToastUtil.showMessage("单个红包最小0.01");
                    isBonusesNum = false;
                    isBonuses = false;
                    tvBonusesAll.setText("￥0.00");
                    btnPublishBonuses.getDelegate().setBackgroundColor(getResources().getColor(R.color.btn_press_enable));
                    isEnabled = false;
                    return;
                }
            }
            tvBonusesAll.setText("￥" + String.format("%.2f", bonuseMoneyAll));
            btnPublishBonuses.getDelegate().setBackgroundColor(getResources().getColor(R.color.bg_head_red));
            isEnabled = true;
        }
        if (isBonuses == false || isBonusesNum == false) {
            tvBonusesAll.setText("￥0.00");
            btnPublishBonuses.getDelegate().setBackgroundColor(getResources().getColor(R.color.btn_press_enable));
            isEnabled = false;
        }

    }
}
