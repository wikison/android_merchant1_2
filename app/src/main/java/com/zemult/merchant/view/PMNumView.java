package com.zemult.merchant.view;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.flyco.roundview.RoundRelativeLayout;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.util.EditFilter;

import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2016/9/19.
 */
public class PMNumView extends LinearLayout implements View.OnClickListener, TextWatcher {

    private Context context;
    private RoundTextView decrease;
    private RoundTextView plus;
    private EditText etNum;
    private RoundRelativeLayout rllTaskDescribe;

    private int defaultNum = -1, minNum, maxNum;
    private String showText = "";

    //数字变化监听器
    private NumChangeListener numChangeListener;

    public int getDefaultNum() {
        return defaultNum;
    }

    public void setDefaultNum(int defaultNum) {
        this.defaultNum = defaultNum;
    }

    public int getMinNum() {
        return minNum;
    }

    public void setMinNum(int minNum) {
        this.minNum = minNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public PMNumView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PMNumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public PMNumView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init() {
        View view = View.inflate(context, R.layout.view_num_plus_minus, null);
        decrease = (RoundTextView) view.findViewById(R.id.decrease);
        plus = (RoundTextView) view.findViewById(R.id.plus);
        rllTaskDescribe = (RoundRelativeLayout) view.findViewById(R.id.rll_task_describe);
        etNum = (EditText) view.findViewById(R.id.et_num);
        decrease.setOnClickListener(this);
        plus.setOnClickListener(this);
        etNum.addTextChangedListener(this);
        etNum.setText("");
        etNum.setLongClickable(false);
        rllTaskDescribe.setOnClickListener(this);
        etNum.setOnClickListener(this);

        etNum.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
                    if (!isOpen)
                        etNum.setText(showText);
                    else {
                        etNum.setText("");
                    }
                } else {
                    etNum.setText(showText);
                }
            }
        });

        //此处是禁止复制粘贴
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
            etNum.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        addView(view);
    }

    public void setText(String s) {
        etNum.setText(s);
    }

    public String getText() {
        return etNum.getText().toString();
    }

    public void setFilter() {
        EditFilter.IntegerFilter(etNum, 0, maxNum);
    }

    public void setDisable(boolean  disable) {
        if(disable){
            etNum.setEnabled(false);
            plus.setVisibility(GONE);
            decrease.setVisibility(GONE);
        }
        else{
            etNum.setEnabled(true);
            plus.setVisibility(VISIBLE);
            decrease.setVisibility(VISIBLE);
        }


    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        String numStr = etNum.getText().toString();
        if (StringUtils.isBlank(numStr)) {
            numStr = "1";
            showText = "1";
        }
        switch (viewId) {
            case R.id.decrease:
                int curNum = Integer.parseInt(numStr);
                int resultNum = 1;
                if (curNum > minNum) {
                    resultNum = curNum - 1;
                } else {
                    resultNum = minNum;
                }
                etNum.setText(resultNum + "");
                showText = resultNum + "";
                break;
            case R.id.plus:
                int curNum1 = Integer.parseInt(numStr);
                int resultNum2 = 0;
                if (curNum1 < maxNum) {
                    resultNum2 = curNum1 + 1;
                } else {
                    resultNum2 = maxNum;
                }
                etNum.setText(resultNum2 + "");
                showText = resultNum2 + "";
                break;

            case R.id.rll_task_describe:
            case R.id.et_num:
                etNum.setText("");
                break;
        }
    }

    public void setOnNumChangeListener(NumChangeListener numChangeListener) {
        this.numChangeListener = numChangeListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if (s.toString().length() >= 1) {
//            if (s.toString().substring(0).equals("0")) {
//                etNum.setText("");
//                return;
//            }
//        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        int resultNum = 0;
        if (!StringUtils.isBlank(etNum.getText().toString())) {
            resultNum = Integer.parseInt(etNum.getText().toString());
            showText = etNum.getText().toString();
        } else {
            resultNum = defaultNum;
        }
        etNum.setSelection(etNum.getText().toString().length());
        if (numChangeListener != null)
            numChangeListener.onNumChanged(resultNum);
    }

    public interface NumChangeListener {
        void onNumChanged(int num);
    }


}
