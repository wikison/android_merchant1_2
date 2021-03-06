package com.zemult.merchant.activity;

import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.MyQrActivity;
import com.zemult.merchant.activity.slash.FindPayActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.aip.mine.User2ReservationInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by wikison on 2016/6/21.
 */
public class ScanQrActivity extends BaseActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private final static String TAG = "ScanQrActivity";
    @Bind(R.id.qrv_View)
    QRCodeReaderView qrvView;
    @Bind(R.id.iv_line)
    ImageView ivLine;
    @Bind(R.id.ll_back)
    LinearLayout ll_back;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_hint)
    TextView tvHint;
    @Bind(R.id.tv_mine)
    TextView tvMine;

    //商家id, 营销经理id, 用户id;
    String merchantId = "", userSaleId = "", userId = "", scanMoney = "", reservationId = "";
    String merchantIdPrefix = "merchantId=", userSaleIdPrefix = "userId=", userIdPrefix = "userId=";
    User2ReservationInfoRequest user2ReservationInfoRequest;
    M_Reservation mReservation;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_scan_qr);
    }

    @Override
    public void init() {

        initView();
        initListener();
    }

    private void initView() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText(getResources().getString(R.string.title_scan));

        TranslateAnimation mAnimation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 1f);
        mAnimation.setDuration(1000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        ivLine.setAnimation(mAnimation);
    }

    private void initListener() {
        qrvView.setOnQRCodeReadListener(this);
        lhBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanQrActivity.this.finish();
            }
        });

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanQrActivity.this.finish();
            }
        });
        tvMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.start_activity(ScanQrActivity.this, MyQrActivity.class);
            }
        });
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if (!StringUtils.isBlank(text)) {
            // pay://doScan?merchantId=300757&userId=111057&price=0.01&reservationId=1111
            if (text.startsWith(Constants.QR_PAY_PREFIX)) {
                Uri uri = Uri.parse(text);
                merchantId = uri.getQueryParameter("merchantId");
                userSaleId = uri.getQueryParameter("userId");
                scanMoney = uri.getQueryParameter("price");
                reservationId = uri.getQueryParameter("reservationId");
                try {
                    qrvView.getCameraManager().stopPreview();
                    userReservationInfo();

                } catch (Exception e) {
                    System.out.println(TAG + "----->" + e.getMessage());
                }


            }
            // userInfo://userId=11111
            else if (text.startsWith(Constants.QR_USER_PREFIX)) {
                String info = text.substring(Constants.QR_USER_PREFIX.length());
                if (!userId.equals(info.substring(userIdPrefix.length()))) {
                    userId = info.substring(userIdPrefix.length());
                    try {
                        qrvView.getCameraManager().stopPreview();
                        Intent intent = new Intent(ScanQrActivity.this, UserDetailActivity.class);
                        intent.putExtra(UserDetailActivity.USER_ID, Integer.valueOf(userId));
                        startActivity(intent);
                        this.finish();
                    } catch (Exception e) {
                        System.out.println(TAG + "----->" + e.getMessage());
                    }
                }
            } else
                ToastUtil.showMessage("期待您使用" + Constants.APP_CHINESE_NAME + "APP");
        }
    }

    private void userReservationInfo() {
        if (user2ReservationInfoRequest != null) {
            user2ReservationInfoRequest.cancel();
        }
        User2ReservationInfoRequest.Input input = new User2ReservationInfoRequest.Input();
        input.reservationId = reservationId + "";
        input.convertJosn();
        user2ReservationInfoRequest = new User2ReservationInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((M_Reservation) response).status == 1) {
                    mReservation = (M_Reservation) response;
                    if(mReservation.userId== SlashHelper.userManager().getUserId()){
                        qrvView.getCameraManager().stopPreview();
                        Intent intent = new Intent(ScanQrActivity.this, FindPayActivity.class);
                        intent.putExtra(FindPayActivity.M_RESERVATION, mReservation);
                        intent.putExtra("merchantId", Integer.valueOf(merchantId));
                        intent.putExtra("userSaleId", Integer.valueOf(userSaleId));
                        intent.putExtra("reservationId", Integer.valueOf(reservationId));
                        intent.putExtra("scanMoney", scanMoney);
                        startActivity(intent);
                    }else{
                        ToastUtil.showMessage("该预约单不是您的预约单, 无法买单");
                        qrvView.getCameraManager().startPreview();
                    }

                }

            }
        });
        sendJsonRequest(user2ReservationInfoRequest);
    }


    @Override
    public void cameraNotFound() {
        ToastUtil.showMessage("未找到摄像头!");
    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        qrvView.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrvView.getCameraManager().stopPreview();
    }
}
