package com.zemult.merchant.util.imagepicker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.zemult.merchant.R;
import com.zemult.merchant.config.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageGridAdapter extends BaseAdapter {

	final String TAG = getClass().getSimpleName();
	public Map<String, String> map = new HashMap<String, String>();
	Activity act;
	List<ImageItem> dataList;
	BitmapCache cache;
	BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};
	private TextCallback textcallback = null;
	private int maxSize = Constants.DEFAULT_IMAGE_MAX_SIZE;
	private Handler mHandler;
	private int selectTotal = 0;
	private int oldSelectCount;

	public ImageGridAdapter(Activity act, List<ImageItem> list, int maxSize,
							Handler mHandler, int oldSelectCount) {
		this.maxSize = maxSize;
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
		this.mHandler = mHandler;
		this.oldSelectCount = oldSelectCount;
	}

	public void setTextCallback(TextCallback listener) {
		textcallback = listener;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_image_grid, null);
			holder.image = (ImageView) convertView.findViewById(R.id.iv_scan);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.isselected);
			holder.text = (TextView) convertView
					.findViewById(R.id.item_image_grid_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final ImageItem item = dataList.get(position);

		holder.image.setTag(item.imagePath);
		cache.displayBmp(holder.image, item.thumbnailPath, item.imagePath,
				callback);
		if (item.isSelected) {
			holder.selected.setImageResource(R.drawable.icon_data_select);
			holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
		} else {
			holder.selected.setImageResource(-1);
			holder.text.setBackgroundColor(0x00000000);
		}

		holder.image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String path = dataList.get(position).imagePath;
				Log.v("ImageGridAdapter",
						"" + Bimp.drr.size());
				// selectTotal = selectTotal + oldSelectCount;
				if ((Bimp.drr.size() + selectTotal + oldSelectCount) < maxSize) {
					item.isSelected = !item.isSelected;
					if (item.isSelected) {
						holder.selected
								.setImageResource(R.drawable.icon_data_select);
						holder.text
								.setBackgroundResource(R.drawable.bgd_relatly_line);
						selectTotal++;

						map.put(path, path);
						if (textcallback != null)
							textcallback.onListen(selectTotal);

					} else if (!item.isSelected) {
						holder.selected.setImageResource(-1);
						holder.text.setBackgroundColor(0x00000000);
						selectTotal--;

						map.remove(path);
						if (textcallback != null)
							textcallback.onListen(selectTotal);
					}
				} else if ((Bimp.drr.size() + selectTotal + oldSelectCount) >= maxSize) {
					if (item.isSelected == true) {
						item.isSelected = !item.isSelected;
						holder.selected.setImageResource(-1);
						selectTotal--;

						map.remove(path);
						if (textcallback != null)
							textcallback.onListen(selectTotal);

					} else {
						Message message = Message.obtain(mHandler, 0);
						message.sendToTarget();
					}
				}
			}

		});

		return convertView;
	}

	public static interface TextCallback {
		public void onListen(int count);
	}

	class Holder {
		private ImageView image;
		private ImageView selected;
		private TextView text;
	}
}
