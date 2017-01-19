package com.zemult.merchant.view;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.model.M_Label;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by wikison on 2017/01/13.
 */
public class SearchAutoView extends LinearLayout implements View.OnClickListener {
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.iv_delete)
    ImageView ivDelete;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.view_container)
    View viewContainer;
    @Bind(R.id.lv_search_auto)
    ListView lvSearchAuto;

    List<M_Label> labelList = new ArrayList<M_Label>();
    CommonAdapter commonAdapter;
    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 搜索回调接口
     */
    private SearchViewListener mListener;

    private String strHint;

    private String strSearch;

    private int maxWordNum;


    public SearchAutoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_search_auto, this);
        initViews();
    }

    public String getStrHint() {
        return etSearch.getHint().toString();
    }

    public void setStrHint(String strHint) {
        etSearch.setHint(strHint);
    }

    public int getMaxWordNum() {
        return maxWordNum;
    }

    public void setMaxWordNum(int maxWordNum) {
        this.maxWordNum = maxWordNum;
    }

    public String getStrSearch() {
        return etSearch.getText().toString();
    }

    public void setStrSearch(String strSearch) {
        this.etSearch.setText(strSearch);
    }

    //View.VISIBLE GONE
    public void setBackVisible(int visible) {
        llBack.setVisibility(visible);
    }

    public void setTvCancelVisible(int visible) {
        tvCancel.setVisibility(visible);
    }

    public void setTvCancelColor(int colorId) {
        tvCancel.setTextColor(colorId);
    }

    public void setBgColor(int colorId) {
        llRoot.setBackgroundColor(colorId);
    }

    public void setTvCancelSize(int textSize) {
        tvCancel.setTextSize(textSize);
    }

    /**
     * 设置搜索回调接口
     *
     * @param listener 监听者
     */
    public void setSearchViewListener(SearchViewListener listener) {
        mListener = listener;
    }

    private void initViews() {
        ivDelete.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        llBack.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        etSearch.setMaxLines(1);
        tvCancel.setTextColor(getResources().getColor(R.color.white));
        tvCancel.setTextSize(16);

        etSearch.addTextChangedListener(new EditChangedListener());
        etSearch.setOnClickListener(this);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    notifyStartSearching(etSearch.getText().toString());
                }
                return true;
            }
        });

        lvSearchAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                notifyStartSearching(labelList.get(position).labelName);
            }
        });
    }

    /**
     * 通知监听者 进行搜索操作
     *
     * @param text
     */
    private void notifyStartSearching(String text) {
        if (mListener != null) {
            mListener.onSearch(text);
        }
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_et_input:
                break;
            case R.id.search_iv_delete:
                etSearch.setText("");
                ivDelete.setVisibility(GONE);
                break;
            case R.id.ll_back:
            case R.id.search_btn_back:
            case R.id.tv_cancel:
                if (onBackClickListner != null)
                    onBackClickListner.onBackClick();

                ((Activity) mContext).onBackPressed();
                break;
        }
    }

    public interface OnBackClickListener {
        void onBackClick();
    }

    private OnBackClickListener onBackClickListner;

    public void setOnBackClickListener(OnBackClickListener onBackClickListner) {
        this.onBackClickListner = onBackClickListner;
    }

    /**
     * search view回调方法
     */
    public interface SearchViewListener {
        /**
         * 开始搜索
         *
         * @param text 传入输入框的文本
         */
        void onSearch(String text);
    }

    //设置控件的输入字符最大数
    public void setFilter() {
        InputFilter[] filters = {new InputFilter.LengthFilter(maxWordNum)};
        etSearch.setFilters(filters);
    }

    private class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!"".equals(charSequence.toString())) {
                ivDelete.setVisibility(VISIBLE);
            } else {
                ivDelete.setVisibility(GONE);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
