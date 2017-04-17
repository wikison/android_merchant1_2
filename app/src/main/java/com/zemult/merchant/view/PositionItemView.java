package com.zemult.merchant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.util.EditFilter;

import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2017/4/17.
 */
public class PositionItemView extends LinearLayout {
    CheckBox cb;
    TextView tvName;
    EditText etName;
    RoundLinearLayout rllName;

    public int getViewTag() {
        return viewTag;
    }

    public void setViewTag(int viewTag) {
        this.viewTag = viewTag;
    }

    int viewTag;

    private ViewPositionClickListener onViewPositionClickListener;

    private Context mContext;

    public PositionItemView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.item_position, this);
        initView();
        initListener();
    }

    private void initView() {
        cb = (CheckBox) findViewById(R.id.cb);
        tvName = (TextView) findViewById(R.id.tv_name);
        etName = (EditText) findViewById(R.id.et_name);
        rllName = (RoundLinearLayout) findViewById(R.id.rll_name);

        EditFilter.WordFilter(etName, 10);
    }

    public PositionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnViewPositionClickListener(ViewPositionClickListener onViewPositionClickListener) {
        this.onViewPositionClickListener = onViewPositionClickListener;
    }

    public boolean isBlank() {
        return StringUtils.isBlank(etName.getText().toString());
    }

    public void setCb(boolean checked) {
        cb.setChecked(checked);
    }

    public boolean getCb() {
        return cb.isChecked();
    }

    public String getTextName() {
        return tvName.getText().toString();
    }

    public void setTextName(String strPositionName) {
        tvName.setText(strPositionName);
    }

    public String getEditName() {
        return etName.getText().toString();
    }

    public void setEditName(String strPositionName) {
        etName.setText(strPositionName);
    }

    public void setEditVisibility(int visibility) {
        rllName.setVisibility(visibility);
    }

    public void setFocus() {
        etName.requestFocus();
    }

    private void initListener() {
        cb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewPositionClickListener != null)
                    onViewPositionClickListener.onPositionManage(viewTag);
            }
        });
    }

    public interface ViewPositionClickListener {
        void onPositionManage(int viewTag);
    }

}
