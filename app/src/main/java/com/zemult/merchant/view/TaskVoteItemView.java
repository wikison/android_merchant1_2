package com.zemult.merchant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.util.ToastUtil;

public class TaskVoteItemView extends RelativeLayout {
    TextView tv_votetext;
    CheckBox cb_vote;

    private Context mContext;

    public TaskVoteItemView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_task_vote_item, this);
        initViews();
        initListener();
    }

    public TaskVoteItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initViews() {
        tv_votetext = (TextView) findViewById(R.id.tv_votetext);
        cb_vote = (CheckBox) findViewById(R.id.cb_vote);
    }

    public void setItemTag(String itemTage) {
        cb_vote.setTag(itemTage);
    }


    private void initListener() {
        cb_vote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                for(int i=0;i< ((LinearLayout)  v.getParent()).getChildCount();i++){
//                    if(v.getTag().toString().equals(((LinearLayout)  v.getParent()).getTag().toString())){
//                        ((CheckBox)  ((LinearLayout)  v.getParent()).getChildAt(i)).setChecked(true);
//                    }
//                    else{
//                        ((CheckBox)  ((LinearLayout)  v.getParent()).getChildAt(i)).setChecked(false);
//                    }
//                }
                ToastUtil.showMessage(v.getTag().toString());
            }
        });
    }
}
