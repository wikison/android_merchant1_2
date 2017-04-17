package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.createroleadapter.DragAdapter;
import com.zemult.merchant.adapter.createroleadapter.OtherAdapter;
import com.zemult.merchant.aip.slash.CommonServiceTagListRequest;
import com.zemult.merchant.aip.slash.UserSaleMerchantEditRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.bean.IndusPreferItem;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.view.DragGrid;
import com.zemult.merchant.view.OtherGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;

public class TabManageSecondActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static String TAG = "TabManageActivity";
    public static String NAME = "merchantname";
    public static String TAGS = "tags";
    public static String SELECTEDTAGS = "selectedtags";
    public static String COMEFROM = "COMEFROM";

    /**
     * 用户栏目对应的适配器，可以拖动
     */
    DragAdapter userAdapter;
    /**
     * 其它栏目对应的适配器
     */
    OtherAdapter otherAdapter;
    /**
     * 其它栏目列表
     */
    ArrayList<IndusPreferItem> otherChannelList = new ArrayList<IndusPreferItem>();
    /**
     * 用户栏目列表
     */
    ArrayList<IndusPreferItem> userChannelList = new ArrayList<IndusPreferItem>();
    @Bind(R.id.cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.tv_protocol)
    TextView tvProtocol;

    @Bind(R.id.subscribe_main_layout)
    LinearLayout subscribeMainLayout;
    @Bind(R.id.xieyi_ll)
    LinearLayout xieyiLl;
    @Bind(R.id.choose_yv)
    TextView chooseYv;
    @Bind(R.id.shop_name_tv)
    TextView shopNameTv;


    private List<M_Industry> sysdatalist = new ArrayList<M_Industry>();

    private List<M_Industry> thirddatalist = new ArrayList<M_Industry>();


    TextView textView;
    Boolean isFalse = true;
    /**
     * 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。
     */

    boolean isMove = false;
    boolean showDialog;
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.chatdetail)
    TextView chatdetail;
    @Bind(R.id.shopname_tv)
    TextView shopnameTv;
    @Bind(R.id.my_category_text)
    TextView myCategoryText;
    @Bind(R.id.my_category_tip_text)
    TextView myCategoryTipText;
    @Bind(R.id.apply_btn)
    Button applyBtn;
    private Context mContext;
    private int merchantId;
    private int comefrom = 1;//来源于申请为1,来源为修改为2
    private int isvisibily = -1;
    /**
     * 用户栏目的GRIDVIEW
     */
    private DragGrid userGridView;
    /**
     * 其它栏目的GRIDVIEW
     */
    private OtherGridView otherGridView;
    //获取服务人员标签

    private String tags = "";
    public  String selectedtags="";


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_tab_manage_second);
    }

    @Override
    public void init() {
        mContext = this;
        textView = (TextView) findViewById(R.id.channel_edit);
        merchantId = getIntent().getIntExtra(TAG, -1);
        comefrom = getIntent().getIntExtra(COMEFROM, 2);
        //tvProtocol.setText("<<服务管家协议>>");
        //  cbAgree.setChecked(true);


        shopnameTv.setVisibility(View.GONE);
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append("选择您要提供的服务");
//        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);//粗体
//        spannableString.setSpan(styleSpan, 5, 4 + name.length() + 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        shopNameTv.setText(spannableString);

        tags = getIntent().getStringExtra(TAGS);
        selectedtags=getIntent().getStringExtra(SELECTEDTAGS);

        otherChannelList.clear();
        myCategoryTipText.setVisibility(View.VISIBLE);
        chatdetail.setText("修改服务标签");
        textView.setText("完成");
        textView.setVisibility(View.INVISIBLE);
        applyBtn.setVisibility(View.VISIBLE);
        applyBtn.setText("保存");
        myCategoryText.setText("已提供的服务");


        initView();
        initData();
        userAdapter.setB(isFalse);


        String[] res = tags.split(",");
        if (!res[0].equals("")) {
            for (int j = 0; j < res.length; j++) {
                otherChannelList.add(new IndusPreferItem(0, res[j], j + 1, 0));//数组越界
            }
        }
        String[] seleres = selectedtags.split(",");
        if (!seleres[0].equals("")) {
            for (int j = 0; j < seleres.length; j++) {
                userChannelList.add(new IndusPreferItem(0, seleres[j], j + 1, 0));//数组越界
            }
        }

        //从otherChannelList里面删除与userChannelList里面name参数相同的部分
        for (int i = 0; i < userChannelList.size(); i++) {
            for (int j = otherChannelList.size() - 1; j >= 0; j--) {
                if (otherChannelList.get(j).getName().equals(userChannelList.get(i).getName())) {
                    otherChannelList.remove(j);
                }
            }
        }

        userAdapter.setListDate(userChannelList);
        otherAdapter.setListDate(otherChannelList);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        userGridView = (DragGrid) findViewById(R.id.userGridView);
        otherGridView = (OtherGridView) findViewById(R.id.otherGridView);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (comefrom == 1) {
            userChannelList.clear();
        }
        userAdapter = new DragAdapter(this, userChannelList);
        userGridView.setAdapter(userAdapter);
        otherAdapter = new OtherAdapter(this, otherChannelList);
        otherGridView.setAdapter(otherAdapter);
        //设置GRIDVIEW的ITEM的点击监听

        otherGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        if (textView.getText().toString().equals("完成") || isvisibily == 0) {

            if (isMove) {
                return;
            }
            switch (parent.getId()) {
                case R.id.userGridView:
                    //position为 0，1 的不可以进行任何操作
                    if (position != -1) {//&& position != 1
                        final ImageView moveImageView = getView(view);
                        if (moveImageView != null) {
                            TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                            final int[] startLocation = new int[2];
                            newTextView.getLocationInWindow(startLocation);
                            final IndusPreferItem channel = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                            otherAdapter.setVisible(false);
                            //添加到最后一个
                            otherAdapter.addItem(channel);


                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    try {
                                        int[] endLocation = new int[2];
                                        //获取终点的坐标
                                        otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                        MoveAnim(moveImageView, startLocation, endLocation, channel, userGridView);
                                        userAdapter.setRemove(position);
                                    } catch (Exception localException) {
                                    }
                                }
                            }, 50L);
                        }
                    }

                    break;
                case R.id.otherGridView:
                    final ImageView moveImageView = getView(view);
                    if (moveImageView != null) {
                        TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                        final int[] startLocation = new int[2];
                        newTextView.getLocationInWindow(startLocation);
                        final IndusPreferItem channel = ((OtherAdapter) parent.getAdapter()).getItem(position);
                        userAdapter.setVisible(false);
                        //添加到最后一个
                        userAdapter.addItem(channel);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    int[] endLocation = new int[2];
                                    //获取终点的坐标
                                    userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                    MoveAnim(moveImageView, startLocation, endLocation, channel, otherGridView);
                                    otherAdapter.setRemove(position);
                                } catch (Exception localException) {
                                }
                            }
                        }, 50L);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final IndusPreferItem moveChannel,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                } else {
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;


                if (userAdapter.getCount() > 0) {
                    applyBtn.setEnabled(true);
                    applyBtn.setBackgroundResource(R.drawable.common_selector_btn);
                } else {
                    applyBtn.setEnabled(false);
                    applyBtn.setBackgroundResource(R.drawable.next_bg_btn_select);
                }

            }
        });
    }

    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @OnClick({R.id.back, R.id.ll_back, R.id.tv_protocol, R.id.apply_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:

            case R.id.ll_back:
                onBackPressed();
                break;

//            case R.id.tv_protocol:
//
//                IntentUtil.start_activity(this, BaseWebViewActivity.class,
//                        new Pair<String, String>("titlename", "服务管家协议"), new Pair<String, String>("url", Constants.SERVICEXIEYI));
//                break;
            case R.id.apply_btn:

                    if (userChannelList.size() == 0) {
                        ToastUtils.show(this, "服务标签不能为空");
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("result",getTags());
                        this.setResult(RESULT_OK,intent);
                        finish();
                    }

                break;
        }
    }

    private String getTags() {
        StringBuffer tagsname = new StringBuffer();
        for (int i = 0; i < userChannelList.size(); i++) {
            if (-1 != userChannelList.get(i).getId()) {
                if (i == userChannelList.size() - 1) {
                    tagsname.append(userChannelList.get(i).getName() + "");
                } else {
                    tagsname.append(userChannelList.get(i).getName() + ",");
                }
            }
        }
        return  tagsname.toString();
    }


}
