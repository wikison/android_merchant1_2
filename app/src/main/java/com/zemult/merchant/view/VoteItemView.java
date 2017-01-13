package com.zemult.merchant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zemult.merchant.R;

import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2016/8/4.
 */
public class VoteItemView extends RelativeLayout {
    ImageView ivDelete;
    EditText etVote;

    private Context mContext;

    public VoteItemView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_vote_item, this);
        initViews();
        initListener();
    }

    public VoteItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initViews() {
        ivDelete = (ImageView) findViewById(R.id.iv_delete);
        etVote = (EditText) findViewById(R.id.et_vote);
    }

    public boolean isBlank() {
        String strItem = etVote.getText().toString();
        return StringUtils.isBlank(strItem);

    }

    public void setIconVisibility(int visibility) {
        ivDelete.setVisibility(visibility);
    }

    public void setItemVisibility(int visibility) {
        this.setVisibility(visibility);
    }

    public String getItemString() {
        return etVote.getText().toString();
    }

    public void setItemString(String strVoteItem) {
        etVote.setText(strVoteItem);
    }

    public void setFocus() {
        etVote.requestFocus();
    }

    private void initListener() {
        ivDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etVote.setText("");
                VoteItemView.this.setVisibility(View.GONE);
            }
        });
    }
}
