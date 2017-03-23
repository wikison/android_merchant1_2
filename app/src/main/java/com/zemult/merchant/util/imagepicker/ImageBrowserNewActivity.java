package com.zemult.merchant.util.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.MerchantPicDelRequest;
import com.zemult.merchant.aip.mine.UserPicDelRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.view.ScrollViewPager;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class ImageBrowserNewActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener, View.OnClickListener {
    public static final String INTENT_DELABLE = "deleteable";
    public static final String INTENT_COVERABLE = "coverable";
    public static final String INTENT_POS = "pos";
    public static final String INTENT_PICS = "pics";
    public static final String INTENT_NOTES = "notes";
    public static final String INTENT_PICIDS = "picIds";
    public static final String INTENT_MERCHANTID = "merchantId";
    public static final String INTENT_USERID = "userId";
    public static final String INTENT_UNSHOWTITLE = "unshowTitle";

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.vp)
    ScrollViewPager vp;
    @Bind(R.id.ll_cover)
    LinearLayout llCover;
    @Bind(R.id.ll_del)
    LinearLayout llDel;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;
    @Bind(R.id.tv_note)
    TextView tvNote;
    @Bind(R.id.ll_note)
    RelativeLayout llNote;

    private boolean coverable = false, deleteable = false, unshowTitle = false;
    private List<String> photos = new ArrayList<String>();
    private List<String> notes = new ArrayList<String>();
    private List<Integer> ids = new ArrayList<Integer>();
    private ImageBrowserAdapter mAdapter;
    private int mPosition;
    private int mTotal;
    private String picIds, coverPic = "";
    private int merchantId, userId;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_image_browser_new);
    }

    @Override
    public void init() {
        deleteable = getIntent().getBooleanExtra(INTENT_DELABLE, false);
        coverable = getIntent().getBooleanExtra(INTENT_COVERABLE, false);
        unshowTitle = getIntent().getBooleanExtra(INTENT_UNSHOWTITLE, false);
        mPosition = getIntent().getIntExtra(INTENT_POS, 0);
        photos = (List<String>) getIntent().getSerializableExtra(INTENT_PICS);
        notes = (List<String>) getIntent().getSerializableExtra(INTENT_NOTES);
        ids = (List<Integer>) getIntent().getSerializableExtra(INTENT_PICIDS);
        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, -1);
        userId = getIntent().getIntExtra(INTENT_USERID, -1);
        if (deleteable) {
            llBottom.setVisibility(View.VISIBLE);
            llDel.setVisibility(View.VISIBLE);
            if (coverable)
                llCover.setVisibility(View.VISIBLE);
        }

        if (photos == null) {
            return;
        }
        mAdapter = new ImageBrowserAdapter(this, photos);
        if (unshowTitle) {
            lhTvTitle.setVisibility(View.GONE);
            mAdapter.setOnItemClickCallback(new ImageBrowserAdapter.OnItemClickCallback() {
                @Override
                public void onItemClick() {
                    onBackPressed();
                }
            });
        }

        if (photos.size() > 1) {
            mTotal = photos.size();
            if (mPosition > mTotal) {
                mPosition = mTotal - 1;
            }
            if (mTotal > 1) {
                lhTvTitle.setText((mPosition % mTotal) + 1 + "/" + mTotal);
                vp.setAdapter(mAdapter);
                vp.setCurrentItem(mPosition, false);
            }
        } else if (photos.size() == 1) {
            lhTvTitle.setText("1/1");
            vp.setAdapter(mAdapter);
        }
        vp.setOnPageChangeListener(this);

        if (notes != null && !notes.isEmpty()) {
            if(StringUtils.isBlank(notes.get(0)))
                llNote.setVisibility(View.GONE);
            else {
                llNote.setVisibility(View.VISIBLE);
                tvNote.setText(notes.get(0));
            }
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.ll_cover, R.id.ll_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll_cover:
                ToastUtil.showMessage("设定成功");
                coverPic = photos.get(mPosition);
                break;
            case R.id.ll_del:
                picIds = ids.get(mPosition) + "";
                if (merchantId != -1)
                    merchant_pic_del();
                else
                    user_pic_del();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
        lhTvTitle.setText((mPosition % mTotal) + 1 + "/" + mTotal);
        if (notes != null && !notes.isEmpty()) {
            if(StringUtils.isBlank(notes.get(position)))
                llNote.setVisibility(View.GONE);
            else {
                llNote.setVisibility(View.VISIBLE);
                tvNote.setText(notes.get(position));
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("coverPic", coverPic);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private UserPicDelRequest userPicDelRequest;
    private MerchantPicDelRequest merchangtPicDelRequest;

    private void merchant_pic_del() {
        showPd();
        if (merchangtPicDelRequest != null) {
            merchangtPicDelRequest.cancel();
        }
        MerchantPicDelRequest.Input input = new MerchantPicDelRequest.Input();
        input.merchantId = merchantId;
        input.picIds = picIds;

        input.convertJosn();
        merchangtPicDelRequest = new MerchantPicDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    refresh();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(merchangtPicDelRequest);
    }

    private void user_pic_del() {
        showPd();
        if (userPicDelRequest != null) {
            userPicDelRequest.cancel();
        }
        UserPicDelRequest.Input input = new UserPicDelRequest.Input();
        input.userId = userId;
        input.picIds = picIds;

        input.convertJosn();
        userPicDelRequest = new UserPicDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    refresh();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userPicDelRequest);
    }

    private void refresh() {
        if (photos.get(mPosition).equals(coverPic))
            coverPic = "";
        photos.remove(mPosition);
        ids.remove(mPosition);
        if (photos.isEmpty())
            onBackPressed();
        else {
            mAdapter.notifyDataSetChanged();
            if (photos.size() > 1) {
                mTotal = photos.size();
                if (mPosition > mTotal) {
                    mPosition = mTotal - 1;
                }
                if (mTotal > 1) {
                    lhTvTitle.setText((mPosition % mTotal) + 1 + "/" + mTotal);
                }
            } else if (photos.size() == 1) {
                lhTvTitle.setText("1/1");
                mPosition = 0;
            }
            vp.setAdapter(mAdapter);
            vp.setCurrentItem(mPosition, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
