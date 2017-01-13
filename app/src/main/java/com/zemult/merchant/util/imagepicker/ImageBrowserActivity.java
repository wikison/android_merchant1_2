package com.zemult.merchant.util.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.app.view.ScrollViewPager;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.PhotoFileUtil;
import com.zemult.merchant.view.common.MMAlert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageBrowserActivity extends MBaseActivity implements
		OnPageChangeListener, OnClickListener, OnCheckedChangeListener,
		ImageBrowserAdapter.OnItemClickCallback, ImageBrowserAdapter.OnItemLongClickCallback {
	private View appTopView;
	private TextView titleText, photo_info;
	private TextView titleLeftButton;
	private CheckBox ckTitleRight;
	ImageButton moreaction_right_btn;
	// private ImageButton btn_delete;
	private View btmLayout;
	private ScrollViewPager mSvpPager;
	private ImageBrowserAdapter mAdapter;
	private TextView photo_bt_enter;
	private int mPosition;
	private int mTotal;
	private boolean deleteable = false;
	public static final String IMAGE_TYPE = "image_type";
	public static final String TYPE_ALBUM = "image_album";
	public static final String TYPE_PHOTO = "image_photo";

	public List<String> photos = new ArrayList<String>();
	List<String> schoolPhotoInfo = new ArrayList<String>();

	public HashMap<String, Boolean> photoTags = new HashMap<String, Boolean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// View view =
		// getLayoutInflater().inflate(R.layout.activity_imagebrowser, null);
		// appMainView.addView(view, layoutParams);
		deleteable = getIntent().getBooleanExtra("deleteable", false);
		setContentView(R.layout.activity_imagebrowser);
		initViews();
		initEvents();
		init();
	}

	protected void initViews() {
		appTopView = findViewById(R.id.app_layout_top);
		titleText = (TextView) findViewById(R.id.top_center_tv);
		photo_info = (TextView) findViewById(R.id.photo_info);
		titleText.setVisibility(View.VISIBLE);
		ckTitleRight = (CheckBox) findViewById(R.id.top_right_btn);
		moreaction_right_btn = (ImageButton) findViewById(R.id.moreaction_right_btn);
		titleLeftButton = (TextView) findViewById(R.id.top_left_btn);
		mSvpPager = (ScrollViewPager) findViewById(R.id.imagebrowser_svp_pager);
		btmLayout = findViewById(R.id.photo_relativeLayout);
		titleLeftButton.setText("");
		moreaction_right_btn.setVisibility(View.VISIBLE);
		titleLeftButton.setVisibility(View.VISIBLE);
		if (deleteable) {
			ckTitleRight.setVisibility(View.VISIBLE);
			moreaction_right_btn.setVisibility(View.GONE);
			ckTitleRight.setText(" ");
			ckTitleRight.setChecked(true);
			ckTitleRight.setOnCheckedChangeListener(this);
			btmLayout.setVisibility(View.VISIBLE);
			photo_bt_enter = (TextView) findViewById(R.id.photo_bt_enter);
			photo_bt_enter.setVisibility(View.VISIBLE);
			photo_bt_enter.setOnClickListener(this);
		}
	}

	protected void initEvents() {
		mSvpPager.setOnPageChangeListener(this);
		titleLeftButton.setOnClickListener(this);
		moreaction_right_btn.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		PupWindowUtil.dismiss();
		super.onDestroy();
	}

	private void init() {
		// mType = getIntent().getStringExtra(IMAGE_TYPE);
		photos.clear();
		schoolPhotoInfo.clear();
		photos = (List<String>) getIntent().getSerializableExtra("paths");
		schoolPhotoInfo = (List<String>) getIntent().getSerializableExtra(
				"photoinfo");
		if (photos == null) {
			return;
		}
		if (deleteable) {
			for (String photo : photos) {
				photoTags.put(photo, true);
			}
		}
		mAdapter = new ImageBrowserAdapter(this, photos);
		mAdapter.setOnItemClickCallback(this);
//		mAdapter.setOnItemLongClickCallback(this);
		if (photos.size() > 1) {
			mPosition = getIntent().getIntExtra("position", 0);
			mTotal = photos.size();
			if (mPosition > mTotal) {
				mPosition = mTotal - 1;
			}
			if (mTotal > 1) {
				titleText.setText((mPosition % mTotal) + 1 + "/" + mTotal);
				mSvpPager.setAdapter(mAdapter);
				mSvpPager.setCurrentItem(mPosition, false);
			}
		} else if (photos.size() == 1) {
			titleText.setText("1/1");
			mSvpPager.setAdapter(mAdapter);
		}
		if (deleteable) {
			photo_bt_enter.setText("完成(" + photos.size() + "/" + photos.size()
					+ ")");
		}

		if (null != schoolPhotoInfo && schoolPhotoInfo.size() > 0&&!schoolPhotoInfo.toString().equals("[]")) {
			if (!(schoolPhotoInfo.get(mPosition) == null ? "": schoolPhotoInfo.get(mPosition)).equals("")) {
				photo_info.setVisibility(View.VISIBLE);
			}
			else{
				photo_info.setVisibility(View.GONE);
			}
			photo_info.setText(schoolPhotoInfo.get(mPosition) == null ? ""
					: schoolPhotoInfo.get(mPosition));
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		try {
			mPosition = arg0;
			titleText.setText((mPosition % mTotal) + 1 + "/" + mTotal);
			if (deleteable) {
				ckTitleRight.setChecked(photoTags.get(photos.get(mPosition)));
			}
			if (null != schoolPhotoInfo && schoolPhotoInfo.size() > 0) {
				if (!(schoolPhotoInfo.get(mPosition) == null ? "": schoolPhotoInfo.get(mPosition)).equals("")) {
					photo_info.setVisibility(View.VISIBLE);
				}
				else{
					photo_info.setVisibility(View.GONE);
				}
				photo_info.setText(schoolPhotoInfo.get(mPosition) == null ? ""
						: schoolPhotoInfo.get(mPosition));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.photo_bt_enter:
			sendBroadcast();
			this.finish();
			break;
		case R.id.top_left_btn:
			this.finish();
			break;
		case R.id.moreaction_right_btn:
			ShowSavePhotoDialog();

			break;
		default:
			break;
		}
	}

	private void ShowSavePhotoDialog() {
		String[] items = { "保存到手机" };
		MMAlert.showAlert(this, null, items, null, new MMAlert.OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				if (whichButton == 0) {
					showPd();
					PhotoFileUtil.saveImg(ImageBrowserActivity.this,
							photos.get(mSvpPager.getCurrentItem()));
				}
			}
		});
	}

	private void sendBroadcast() {
		Intent intent = new Intent(Constants.EDIT_IMG_REQCODE);
		intent.putExtra("path", getSelectedPhotos(photoTags));
		this.sendBroadcast(intent);
	}

	@Override
	public void onBackPressed() {
		ImageBrowserActivity.this.finish();
	}

	private ArrayList<String> getSelectedPhotos(HashMap<String, Boolean> hashMap) {
		ArrayList<String> list = new ArrayList<String>();
		if (hashMap == null || hashMap.size() == 0) {
			return list;
		}
		for (String string : photos) {
			if (hashMap.get(string)) {
				list.add(string);
			}
		}
		return list;
	}

	private int getSelectedPhotosSize(HashMap<String, Boolean> hashMap) {
		if (hashMap == null || hashMap.size() == 0) {
			return 0;
		}
		int t = 0;
		for (String string : photos) {
			if (hashMap.get(string)) {
				t++;
			}
		}
		return t;
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		photoTags.put(photos.get(mPosition), arg1);
		photo_bt_enter.setText("完成(" + getSelectedPhotosSize(photoTags) + "/"
				+ photos.size() + ")");
	}

	@Override
	public void onItemClick() {
		try {
			this.photos.clear();
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.finish();
	}


	@Override
	public void onLongClick(View imageAware) {

	}
}
