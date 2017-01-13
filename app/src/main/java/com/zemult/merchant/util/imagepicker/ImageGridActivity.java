package com.zemult.merchant.util.imagepicker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zemult.merchant.R;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ImageGridActivity extends MBaseActivity {

	private List<ImageItem> currentFolderimageList;
	private GridView gridView;
	private RelativeLayout rl;
	private ImageGridAdapter adapter;
	private AlbumHelper helper;
	private TextView buttonFinish;
	private ArrayList<String> list = new ArrayList<String>();
	private ArrayList<String> oldSelectedlist = new ArrayList<String>();
	private int selectCount;
	private int oldSelectCount = 0;
	// private ImageBucket bucket;

	private int maxSize = Constants.DEFAULT_IMAGE_MAX_SIZE;
	private int chooseHead;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ImageGridActivity.this,
						"一次最多选择" + maxSize + "张图片",
						Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("ImageGridActivity");
		maxSize = getIntent().getIntExtra("maxSize",
				Constants.DEFAULT_IMAGE_MAX_SIZE);

		chooseHead = getIntent().getIntExtra("chooseHead", 0);
		View appView = getLayoutInflater().inflate(
				R.layout.activity_image_grid, null);
		appMainView.addView(appView, layoutParams);
		setTitleRightButton("取消");
		setTitleText(getIntent().getStringExtra("cameraName"));
		setTitleLeftButton("相册");
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		currentFolderimageList = (List<ImageItem>) getIntent()
				.getSerializableExtra(ImageAlbumActivity.EXTRA_IMAGE_LIST);
		oldSelectedlist = getIntent()
				.getStringArrayListExtra("allSelectedlist");
		oldSelectCount = getIntent().getIntExtra("oldSelectCount", 0);
		// bucket = (ImageBucket) getIntent().getSerializableExtra(
		// "currentImageBucket");

		initView();
		buttonFinish = (TextView) findViewById(R.id.bt);
		buttonFinish.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getPics();
			}
		});
	}

	private void getPics() {
		Collection<String> c = adapter.map.values();
		Iterator<String> it = c.iterator();
		for (; it.hasNext();) {
            list.add(it.next());
        }

		for (int i = 0; i < list.size(); i++) {
            list.set(i, "file://" + list.get(i));
        }
		Intent intentImgs = new Intent();
		intentImgs.setAction("CHOOSEIMG");

		String oldBucketName = getIntent().getStringExtra(
                "oldBucketName");
		String currentBucketName = getIntent().getStringExtra(
                "cameraName");
		if (oldBucketName != null
                && oldBucketName.contains(currentBucketName) == true) {
            for (String str : list) {
                if (oldSelectedlist.contains("str") == true) {
                    oldSelectedlist.remove(str);
                }

            }
        }
		oldSelectedlist.addAll(list);
		intentImgs.putExtra("path", oldSelectedlist);
		ImageGridActivity.this.sendBroadcast(intentImgs);
		setResult(RESULT_OK);
		finish();

		System.out.println("Bimp.drr.size()--->" + Bimp.drr.size());
		Bimp.clear();
	}

	private void initView() {
		rl = (RelativeLayout)findViewById(R.id.photo_relativeLayout);
		if(chooseHead == 1)
			rl.setVisibility(View.GONE);

		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this,
				currentFolderimageList, maxSize, mHandler, oldSelectCount);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new ImageGridAdapter.TextCallback() {
			public void onListen(int count) {
				// buttonFinish.setText("完成");
				selectCount = count;
				int allCount = oldSelectCount + count;
				buttonFinish.setText("完成" + "(" + allCount + ")");

				if(chooseHead == 1)
					getPics();
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.notifyDataSetChanged();
			}

		});
	}

	@Override
	public void onClick(View v) {
		// super.onClick(v);
		switch (v.getId()) {
		case R.id.top_left_btn:
			Intent intentImgs = new Intent();
			intentImgs.putExtra("oldBucketName",
					getIntent().getStringExtra("cameraName"));
			intentImgs.putExtra(ImageAlbumActivity.EXTRA_IMAGE_LIST,
					(Serializable) currentFolderimageList);
			// intentImgs.putExtra("oldBucketCount",
			// getIntent().getStringExtra("bucketCount"));
			intentImgs.putExtra("selectCount", selectCount);

			Collection<String> c = adapter.map.values();
			Iterator<String> it = c.iterator();
			ArrayList<String> list2 = new ArrayList<String>();
			for (; it.hasNext();) {
				list2.add(it.next());
			}

			for (int i = 0; i < list.size(); i++) {
				list2.set(i, "file://" + list.get(i));
			}
			intentImgs.putExtra("oldBucketList", list2);
			// Bimp.clear();
			setResult(500, intentImgs);
			finish();
			break;
		case R.id.top_right_btn:
			Bimp.clear();
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void beforeFinish() {
		Intent intent = new Intent(ImageGridActivity.this,
				ImageAlbumActivity.class);
		intent.putExtra(
				"maxSize",
				getIntent().getIntExtra("maxSize",
						Constants.DEFAULT_IMAGE_MAX_SIZE));
		startActivity(intent);
		super.beforeFinish();
	}
}
