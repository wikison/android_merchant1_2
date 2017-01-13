package com.zemult.merchant.adapter.slashfrgment;

import java.io.IOException;
import java.util.List;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.EmojiModle;


public class EmojiAdapter extends BaseAdapter {

    private List<EmojiModle> data;

    private LayoutInflater inflater;

    private int size = 0;
    private Context context;

    public EmojiAdapter(Context context, List<EmojiModle> list) {
        this.inflater = LayoutInflater.from(context);
        this.data = list;
        this.size = list.size();
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmojiModle emoji = data.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.face_item, null);
            viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.face_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if ("delete".equals(emoji.getName())) {
            convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setImageResource(R.mipmap.backspace_emoji);
        } else if (TextUtils.isEmpty(emoji.getName())) {
            convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setImageDrawable(null);
        } else {
            try {
                viewHolder.iv_face.setTag(emoji);
                Bitmap mBitmap = BitmapFactory.decodeStream(context.getAssets().open("emoji/" + "emoji_"
                        + emoji.getName().substring(emoji.getName().indexOf("]") + 1, emoji.getName().lastIndexOf("[")) + ".png"));

                viewHolder.iv_face.setImageBitmap(mBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

    class ViewHolder {

        public ImageView iv_face;
    }
}