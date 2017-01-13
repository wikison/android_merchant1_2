package com.zemult.merchant.view;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.HeaderChannelAdapter;
import com.zemult.merchant.model.M_Industry;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunfusheng on 16/4/20.
 */
public class HeaderChannelViewView extends HeaderViewInterface<List<M_Industry>> {

    @Bind(R.id.gv_channel)
    FixedGridView gvChannel;

    public HeaderChannelViewView(Activity context) {
        super(context);
    }

    public void setData(List<M_Industry> list) {
        dealWithTheView(list);
    }

    @Override
    protected void getView(List<M_Industry> list, ListView listView) {
        View view = mInflate.inflate(R.layout.header_channel_layout, listView, false);
        ButterKnife.bind(this, view);

        dealWithTheView(list);
        listView.addHeaderView(view);
    }

    private void dealWithTheView(List<M_Industry> list) {
        HeaderChannelAdapter adapter = new HeaderChannelAdapter(mContext, list);
        gvChannel.setAdapter(adapter);
        gvChannel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    // Item点击
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
