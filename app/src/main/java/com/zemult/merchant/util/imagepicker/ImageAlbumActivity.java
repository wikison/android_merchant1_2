package com.zemult.merchant.util.imagepicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.config.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageAlbumActivity extends MBaseActivity {
	// ArrayList<Entity> dataList;//用来装载数据源的列表
	private List<ImageBucket> bucketList;
	GridView gridView;
	ImageBucketAdapter adapter;// 自定义的适配器
	AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "currentImagelist";
	public static Bitmap bimap;
	private int maxSize = Constants.DEFAULT_IMAGE_MAX_SIZE;
	private int currentPosition;
	private String oldBucketName;
	private int oldSelectCount = 0;
	private ArrayList<String> allSelectedlist = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("ImageAlbumActivity");
		View appView = getLayoutInflater().inflate(
				R.layout.activity_image_bucket, null);
		appMainView.addView(appView, layoutParams);
		setTitleText("相册");
		setTitleRightButton("取消");
		// setTitleLeftButton("相册");
		AlbumHelper.setAlbumHelperNull();
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		maxSize = getIntent().getIntExtra("maxSize",
				Constants.DEFAULT_IMAGE_MAX_SIZE);

		initData();
		initView();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		bucketList = helper.getImagesBucketList(false);// false true
		bimap = BitmapFactory.decodeResource(getResources(),
				R.mipmap.icon_addpic_unfocused);
	}

	/**
	 * 初始化view视图
	 */
	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new ImageBucketAdapter(ImageAlbumActivity.this, bucketList);
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * 通知适配器，绑定的数据发生了改变，应当刷新视图
				 */
				// adapter.notifyDataSetChanged();
				currentPosition = position;
				Intent intent = new Intent(ImageAlbumActivity.this,
						ImageGridActivity.class);
				intent.putExtra(ImageAlbumActivity.EXTRA_IMAGE_LIST,
						(Serializable) bucketList.get(position).imageList);
				intent.putExtra("maxSize", maxSize);
				intent.putExtra("chooseHead", getIntent().getIntExtra("chooseHead",0));
				intent.putExtra("cameraName",
						bucketList.get(position).bucketName);
				intent.putExtra("bucketCount", bucketList.get(position).count);
				intent.putExtra("oldSelectCount", oldSelectCount);
				intent.putExtra("oldBucketName", oldBucketName);
				intent.putExtra("allSelectedlist", allSelectedlist);
				startActivityForResult(intent, 101);
				// finish();
			}

		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_right_btn:
			Bimp.clear();
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101 && resultCode == 500) {
			ImageBucket bucket = (ImageBucket) bucketList.get(currentPosition);
			List<ImageItem> oldImageItem = (List<ImageItem>) data
					.getSerializableExtra(EXTRA_IMAGE_LIST);
			bucket.imageList = oldImageItem;
			oldBucketName = oldBucketName + ","
					+ data.getStringExtra("oldBucketName");
			oldSelectCount = oldSelectCount
					+ +data.getIntExtra("selectCount", 0);
			bucketList.set(currentPosition, bucket);

			ArrayList<String> collection = data
					.getStringArrayListExtra("oldBucketList");
			allSelectedlist.addAll(collection);

		} else if (requestCode == 101 && resultCode == RESULT_OK) {
			finish();
		}
	}

}
