package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ShareAppointmentActivity;
import com.zemult.merchant.activity.slash.FindPayActivity;
import com.zemult.merchant.activity.slash.ServiceCommentActivity;
import com.zemult.merchant.activity.slash.ServicePlanActivity;
import com.zemult.merchant.aip.mine.UserReservationInfoRequest;
import com.zemult.merchant.alipay.taskpay.Assessment4ServiceActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.AppointmentDetailNewActivity;
import com.zemult.merchant.im.CreateRoomBespeakActivity;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class ServiceTicketDetailActivity extends BaseActivity {
    public static final String INTENT_RESERVATIONID = "reservationid";

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.object_tv)
    TextView objectTv;
    @Bind(R.id.head_iv)
    ImageView headIv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.shop_tv)
    TextView shopTv;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.pernumber_tv)
    TextView pernumberTv;
    @Bind(R.id.tv_beizhu)
    TextView tvBeizhu;
    @Bind(R.id.tv_room)
    TextView tvRoom;
    @Bind(R.id.tv_dingjin_haved)
    TextView tvDingjinHaved;
    @Bind(R.id.tv_result)
    TextView tvResult;
    @Bind(R.id.realmoney_tv)
    TextView realmoneyTv;
    @Bind(R.id.yuyueresult_rl)
    RelativeLayout yuyueresultRl;
    @Bind(R.id.ordernum_tv)
    TextView ordernumTv;
    @Bind(R.id.ordernum_rl)
    RelativeLayout ordernumRl;
    @Bind(R.id.lookorder_btn)
    Button lookorderBtn;
    @Bind(R.id.havepayed_ll)
    LinearLayout havepayedLl;
    @Bind(R.id.tv_dingjin_success)
    TextView tvDingjinSuccess;
    @Bind(R.id.recom_btn)
    Button recomBtn;
    @Bind(R.id.invite_btn)
    Button inviteBtn;
    @Bind(R.id.bottom_rl)
    RelativeLayout bottomRl;
    @Bind(R.id.success1_ll)
    LinearLayout success1Ll;
    @Bind(R.id.creatinvite_btn)
    Button creatinviteBtn;
    @Bind(R.id.success2_ll)
    LinearLayout success2Ll;
    @Bind(R.id.tv_redingjin_finished)
    TextView tvRedingjinFinished;
    @Bind(R.id.finished_ll)
    LinearLayout finishedLl;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    int userPayId = 0;
    String reservationId = "";
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.bespek_plan)
    TextView bespekPlan;
    @Bind(R.id.rl_plan)
    RelativeLayout rlPlan;
    @Bind(R.id.rl_room)
    RelativeLayout rlRoom;
    @Bind(R.id.bespek_room)
    TextView bespekRoom;

    private Context mContext;
    private Activity mActivity;
    M_Reservation mReservation;
    protected ImageManager mImageManager;
    UserReservationInfoRequest userReservationInfoRequest;
    int merchantReviewstatus, planId, CHOOSEPLAN = 101;
    String inordertime = "", outordertime = "", ordername = "", orderphone = "";
    int roomnum;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_service_ticket_detail);
    }

    @Override
    public void init() {
        reservationId = getIntent().getStringExtra(INTENT_RESERVATIONID);
        mContext = this;
        mActivity = this;
        mImageManager = new ImageManager(mContext);
        lhTvTitle.setText("服务定单");
        userReservationInfo();

    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_DISABLE_PLAN.equals(intent.getAction())) {
            if (intent.getIntExtra("planId", 0) == planId && 0 == intent.getIntExtra("state", 0)) {
                planId = 0;
                bespekPlan.setText("选择服务方案");
            }
            if (intent.getIntExtra("planId", 0) == planId && 1 == intent.getIntExtra("state", 0)) {
                bespekPlan.setText(intent.getStringExtra("planName"));
            }

        }
    }


    private void userReservationInfo() {
        showPd();
        if (userReservationInfoRequest != null) {
            userReservationInfoRequest.cancel();
        }
        UserReservationInfoRequest.Input input = new UserReservationInfoRequest.Input();
        input.reservationId = reservationId;
        input.convertJosn();
        userReservationInfoRequest = new UserReservationInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((M_Reservation) response).status == 1) {

                    mReservation = (M_Reservation) response;

                    if (mReservation.userPayId != 0) {
                        userPayId = mReservation.userPayId;
                    }
                    objectTv.setText("服务管家");
                    if (!TextUtils.isEmpty(mReservation.saleUserHead)) {
                        mImageManager.loadCircleImage(mReservation.saleUserHead, headIv);
                    }

                    if (!StringUtils.isBlank(mReservation.planName)) {
                        planId = mReservation.planId;
                        bespekPlan.setText(mReservation.planName);
                        rlPlan.setVisibility(View.VISIBLE);
                    } else {
                        rlPlan.setVisibility(View.GONE);
                    }


                    if(mReservation.isRoom!=0){
                        inordertime =mReservation.checkInTime;
                        outordertime = mReservation.checkOutTime;
                        ordername = mReservation.userName;
                        orderphone = mReservation.userPhone;
                        roomnum=mReservation.roomNum;
                        bespekRoom.setText("共"+DateTimeUtil.getDiffDays2(inordertime,outordertime)+"天");
                        rlRoom.setVisibility(View.VISIBLE);
                    }
                    else {
                        rlRoom.setVisibility(View.GONE);
                    }

                    nameTv.setText(mReservation.saleUserName);
                    shopTv.setText(mReservation.merchantName);
//                    tvTime.setText(mReservation.reservationTime);

//                    Date dateInfo = DateTimeUtil.getDateFromString(mReservation.reservationTime, "yyyy-MM-dd HH:mm:ss");
//                    Calendar calendar = GregorianCalendar.getInstance();
//                    calendar.setTime(dateInfo);
//                    String reservationTimeInfo = mReservation.reservationTime.substring(0, 10)+" (" + DateTimeUtil.getDateFromWeekString(dateInfo) + ") " + mReservation.reservationTime.substring(11, 16);
                    tvTime.setText( DateTimeUtil.getDateFromWeekString(mReservation.reservationTime));

                    pernumberTv.setText("" + mReservation.num);
                    tvRoom.setText(mReservation.note);






                    switch (mReservation.state) {
//状态(状态(0:待确认,1:预约成功,2:已支付,3:预约失效(待确认超时)，4：预约未支付(超时)))
                        case 0:
                            break;
                        case 1:
                            tvState.setText("已确定");

                            if (mReservation.isComment == 1) {
                                recomBtn.setText("查看评价");
                            } else {
                                recomBtn.setText("评价一下");
                            }

                            if (mReservation.merchantReviewstatus == 2) {
                                //商家审核状态(0未审核,1待审核,2审核通过)
                                success1Ll.setVisibility(View.VISIBLE);
                                tvRight.setVisibility(View.VISIBLE);
                                tvRight.setText("快捷买单");
                                if(mReservation.isInvitation==0){//邀请函文字描述   是否生成了邀请函(0:否,1:是)
                                    inviteBtn.setText("生成邀请函");
                                }
                                else{
                                    inviteBtn.setText("查看邀请函");
                                }


                                tvDingjinSuccess.setText("￥" + (mReservation.reservationMoney == 0 ? "0" : Convert.getMoneyString(mReservation.reservationMoney)));
                            } else {
                                success2Ll.setVisibility(View.VISIBLE);

                                if(mReservation.isInvitation==0){//邀请函文字描述   是否生成了邀请函(0:否,1:是)
                                    creatinviteBtn.setText("生成邀请函");
                                }
                                else{
                                    creatinviteBtn.setText("查看邀请函");
                                }
                            }
                            break;
                        case 2:
                            havepayedLl.setVisibility(View.VISIBLE);
                            tvState.setText("已支付");
                            tvDingjinHaved.setText("￥" + (mReservation.reservationMoney == 0 ? "0" : Convert.getMoneyString(mReservation.reservationMoney)));
                            realmoneyTv.setText("￥" + (mReservation.userPayMoney == 0 ? "0" : Convert.getMoneyString(mReservation.userPayMoney)));
                            ordernumTv.setText("" + mReservation.userPayNumber);
                            break;
                        case 3:

                        case 4:
                            tvState.setText("已结束");
                            finishedLl.setVisibility(View.VISIBLE);
                            tvRedingjinFinished.setText("￥" + (mReservation.reservationMoney == 0 ? "0" : Convert.getMoneyString(mReservation.reservationMoney)));
                            break;
                    }


                } else {
                    ToastUtils.show(ServiceTicketDetailActivity.this, ((M_Reservation) response).info);
                }

            }
        });
        sendJsonRequest(userReservationInfoRequest);
    }


    @OnClick({R.id.lh_btn_back, R.id.rl_plan, R.id.ll_back, R.id.lookorder_btn, R.id.recom_btn, R.id.invite_btn, R.id.creatinvite_btn, R.id.tv_right,R.id.rl_room})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.rl_room:
            Intent roomintent =new Intent(ServiceTicketDetailActivity.this,CreateRoomBespeakActivity.class);
            roomintent.putExtra("inordertime",inordertime);
            roomintent.putExtra("outordertime",outordertime);
            roomintent.putExtra("ordername",ordername);
            roomintent.putExtra("orderphone",orderphone);
            roomintent.putExtra("roomnum",roomnum);
            roomintent.putExtra("roletype",0);//  type = 1;//服务管家  0  客户
            startActivity(roomintent);
                break;
            case R.id.rl_plan:
                Intent planintent = new Intent(this, ServicePlanActivity.class);//
                planintent.putExtra("planId", mReservation.planId);
                planintent.putExtra("choosePlan", true);
                startActivity(planintent);
                break;
            case R.id.lookorder_btn:
                IntentUtil.intStart_activity(this,
                        PayInfoActivity.class, new Pair<String, Integer>("userPayId", userPayId));
                break;
            case R.id.recom_btn:
                //评价
                if (mReservation.isComment == 1) {//是否评价(0:否,1:是)
                    //查看评价
                    Intent intent2 = new Intent(this, ServiceCommentActivity.class);
                    intent2.putExtra("comment", mReservation.comment);
                    intent2.putExtra("commentNote", mReservation.commentNote);
                    intent2.putExtra("commentTime", mReservation.commentTime);
                    startActivity(intent2);
                } else {
                    goAssessment();
                }
                break;
            case R.id.invite_btn:
            case R.id.creatinvite_btn:
                //邀请好友
                if(mReservation.isInvitation==0){//邀请函文字描述   是否生成了邀请函(0:否,1:是)
                    invite();
                }
                else{
                    IntentUtil.start_activity(this, BaseWebViewActivity.class,
                            new Pair<String, String>("titlename", "邀请函详情"), new Pair<String, String>("share", "true"), new Pair<String, String>("reservationId", reservationId + ""), new Pair<String, String>("url", Constants.RESERVATIONFEEDBACKINFO + reservationId+"&type=0"));
                }
                break;
            case R.id.tv_right:
                //快速结账
                Intent intent = new Intent(this, FindPayActivity.class);
                intent.putExtra(FindPayActivity.M_RESERVATION, mReservation);
                intent.putExtra("merchantId", Integer.valueOf(mReservation.merchantId));
                intent.putExtra("userSaleId", Integer.valueOf(mReservation.saleUserId));
                intent.putExtra("reservationId", Integer.valueOf(reservationId));
                startActivityForResult(intent, 1000);
                break;
        }
    }

    private void goAssessment() {


        Intent intent2 = new Intent(this, Assessment4ServiceActivity.class);
        intent2.putExtra(FindPayActivity.M_RESERVATION, mReservation);
        intent2.putExtra("managerhead", mReservation.saleUserHead);
        intent2.putExtra("managername", mReservation.saleUserName);
        intent2.putExtra("merchantName", mReservation.merchantName);
        if (!TextUtils.isEmpty(reservationId))
            intent2.putExtra("reservationId", Integer.valueOf(reservationId));
        startActivityForResult(intent2, 10002);

    }

    private void invite() {
        Intent urlintent = new Intent(this, ShareAppointmentActivity.class);
        urlintent.putExtra("reservationId",mReservation.reservationId);
        urlintent.putExtra("shareurl", Urls.BASIC_URL.replace("inter_json", "app") + "share_reservation_info.do?reservationId=" + reservationId);
        urlintent.putExtra("sharetitle", "您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】邀您赴约");
        urlintent.putExtra("sharecontent", "您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】刚刚预定了" + mReservation.reservationTime + mReservation.merchantName +
                "，诚挚邀请，期待您的赴约。");
        startActivityForResult(urlintent,10003);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                Intent intent = new Intent(Constants.BROCAST_REFRESH_MYSERVICETICKET);
                intent.putExtra("userPayId", userPayId);
                setResult(RESULT_OK, intent);
                setResult(RESULT_OK);
                onBackPressed();

            } else if (requestCode == 10002) {
                userReservationInfo();
                Intent intent = new Intent(Constants.BROCAST_REFRESH_MYSERVICETICKET);
                intent.putExtra("userPayId", userPayId);
                setResult(RESULT_OK, intent);
                sendBroadcast(intent);
            }
            else  if(requestCode==10003){
                userReservationInfo();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
