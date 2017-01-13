package com.zemult.merchant.view;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Message;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2016/6/3.
 */
public class HeaderInfoView extends HeaderViewInterface2<String> {
    @Bind(R.id.viewFlipper)
    ViewFlipper viewFlipper;
    @Bind(R.id.iv_info_del)
    ImageView ivInfoDel;

    public HeaderInfoView(Activity context) {
        super(context);
    }

    @Override
    protected void getView(String s, ViewGroup viewGroup) {
        View view = mInflate.inflate(R.layout.header_info_layout, viewGroup, false);
        if (viewGroup instanceof ListView) {
            ListView listView = (ListView) viewGroup;
            listView.addHeaderView(view);
        } else {
            viewGroup.addView(view);
        }
        ButterKnife.bind(this, view);
    }

    public void setData(List<M_Message> list) {
        viewFlipper.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            TextView textView = new TextView(mContext);
            textView.setTextColor(0xff999999);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setText(list.get(i).note);
            textView.setMaxLines(1);
            viewFlipper.addView(textView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }
    }


    @OnClick(R.id.iv_info_del)
    public void onClick() {
        if(onInfoDelClickListener != null)
            onInfoDelClickListener.OnInfoDelClick();
    }

    public interface OnInfoDelClickListener{
        void OnInfoDelClick();
    }
    private OnInfoDelClickListener onInfoDelClickListener;

    public void setOnInfoDelClickListener(OnInfoDelClickListener onInfoDelClickListener) {
        this.onInfoDelClickListener = onInfoDelClickListener;
    }
}
