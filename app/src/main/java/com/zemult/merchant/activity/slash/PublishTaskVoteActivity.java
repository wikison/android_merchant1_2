package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.VoteItemView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2016/8/4.
 * 投票设置界面
 */
public class PublishTaskVoteActivity extends BaseActivity {
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
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.iv_type_icon)
    ImageView ivTypeIcon;
    @Bind(R.id.tv_vote_title)
    TextView tvVoteTitle;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_edit_vote)
    TextView tvEditVote;
    @Bind(R.id.ll_vote_main)
    LinearLayout llVoteMain;
    @Bind(R.id.iv_add_note)
    ImageView ivAdd;
    @Bind(R.id.tv_add_vote)
    TextView tvAddNote;
    @Bind(R.id.rl_add_vote)
    RelativeLayout rlAddVote;

    Context mContext;
    String title;
    int validCount = 0;
    String requestType;
    int taskType;
    String taskName;
    String voteNote;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_publish_task_vote);

    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        title = getIntent().getStringExtra("title");
        requestType = getIntent().getStringExtra("requesttype");
        taskType = getIntent().getIntExtra("publish_type", 2);
        taskName = getIntent().getStringExtra("publish_type_name");
        voteNote = getIntent().getStringExtra("vote_note");

        mContext = this;

        if (StringUtils.isBlank(voteNote)) {
            for (int i = 0; i < 2; i++) {
                VoteItemView v = new VoteItemView(this);
                v.setTag(i);
                v.setIconVisibility(View.INVISIBLE);
                v.setItemVisibility(View.VISIBLE);
                llVoteMain.addView(v);
            }

            for (int i = 2; i < 10; i++) {
                VoteItemView v = new VoteItemView(this);
                v.setTag(i);
                v.setIconVisibility(View.VISIBLE);
                v.setItemVisibility(View.GONE);
                llVoteMain.addView(v);
            }
        } else {
            String[] strVotes = voteNote.split("\\|=\\|");
            for (int i = 0; i < strVotes.length; i++) {
                VoteItemView v = new VoteItemView(this);
                v.setTag(i);
                if (i < 2) {
                    v.setItemString(strVotes[i]);
                    v.setIconVisibility(View.INVISIBLE);
                    v.setItemVisibility(View.VISIBLE);
                } else {
                    v.setItemString(strVotes[i]);
                    v.setIconVisibility(View.VISIBLE);
                    v.setItemVisibility(View.VISIBLE);
                }
                llVoteMain.addView(v);

            }

            for (int i = strVotes.length; i < 10; i++) {
                VoteItemView v = new VoteItemView(this);
                v.setTag(i);
                v.setIconVisibility(View.VISIBLE);
                v.setItemVisibility(View.GONE);
                llVoteMain.addView(v);
            }
        }


    }

    private void initView() {
        tvRight.setVisibility(View.VISIBLE);
        lhTvTitle.setText("投票设置");
        tvRight.setText("完成");
        tvTitle.setText(title);

    }

    private void initListener() {

    }

    private void addVoteItem() {
        for (int i = 0; i < llVoteMain.getChildCount(); i++) {
            VoteItemView v = (VoteItemView) llVoteMain.getChildAt(i);
            if (v.getVisibility() == View.GONE) {
                v.setItemVisibility(View.VISIBLE);
                v.setFocus();
                break;
            }
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rl_add_vote, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;

            case R.id.rl_add_vote:
                addVoteItem();
                break;

            case R.id.tv_right:
                validCount = 0;
                if (isValid()) {
                    if (requestType.equals(Constants.BROCAST_PUBLISH_TASK_VOTE)) {
                        Intent intent = new Intent(Constants.BROCAST_PUBLISH_TASK_VOTE);
                        intent.putExtra("vote_note", getVoteNote());
                        intent.putExtra("vote_title", title);
                        intent.putExtra("publish_type", taskType);
                        intent.putExtra("publish_type_name", taskName);
                        setResult(RESULT_OK, intent);
                        sendBroadcast(intent);
                        finish();
                    }
                } else {
                    ToastUtil.showMessage("至少设置两个投票选项");
                }
                break;
        }
    }

    private boolean isValid() {
        boolean result;
        for (int i = 0; i < llVoteMain.getChildCount(); i++) {
            VoteItemView v = (VoteItemView) llVoteMain.getChildAt(i);
            if (!v.isBlank()) {
                validCount++;
            }
        }
        result = validCount >= 2;
        return result;
    }

    private String getVoteNote() {
        String result = "";
        for (int i = 0; i < llVoteMain.getChildCount(); i++) {
            VoteItemView v = (VoteItemView) llVoteMain.getChildAt(i);
            if (!v.isBlank()) {
                result = (result.equals("") ? result + v.getItemString() : result + "|=|" + v.getItemString());
            }
        }
        return result;
    }

}
