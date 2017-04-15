package com.zemult.merchant.im;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.YWCustomMessageBody;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.android.volley.VolleyError;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ShareAppointmentActivity;
import com.zemult.merchant.activity.mine.AppointmentDetailActivity;
import com.zemult.merchant.activity.mine.PayInfoActivity;
import com.zemult.merchant.activity.mine.SettingMoney4OrderActivity;
import com.zemult.merchant.activity.slash.FindPayActivity;
import com.zemult.merchant.activity.slash.SendRewardActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.activity.slash.dotask.DoTaskVoiceActivity;
import com.zemult.merchant.adapter.slashfrgment.SendRewardAdapter;
import com.zemult.merchant.aip.common.CommonRewardRequest;
import com.zemult.merchant.aip.mine.User2ReservationInfoRequest;
import com.zemult.merchant.aip.mine.User2ReservationPayRequest;
import com.zemult.merchant.aip.mine.UserReservationInfoRequest;
import com.zemult.merchant.aip.mine.UserRewardPayAddRequest;
import com.zemult.merchant.aip.reservation.User2ReservationDelRequest;
import com.zemult.merchant.aip.reservation.User2ReservationEditRequest;
import com.zemult.merchant.aip.reservation.User2ReservationSureRequest;
import com.zemult.merchant.aip.reservation.UserReservationEditRequest;
import com.zemult.merchant.alipay.taskpay.Assessment4ServiceActivity;
import com.zemult.merchant.alipay.taskpay.ChoosePayType4OrderActivity;
import com.zemult.merchant.alipay.taskpay.ChoosePayTypeActivity;
import com.zemult.merchant.alipay.taskpay.TaskPayResultActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.model.apimodel.APIM_PresentList;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.DateTimePickDialogUtil;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.sound.HttpOperateUtil;
import com.zemult.merchant.view.FixedGridView;
import com.zemult.merchant.view.PMNumView;
import com.zemult.merchant.view.common.CommonDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zema.volley.network.ResponseListener;


import static com.zemult.merchant.config.Constants.OSSENDPOINT;

/**
 * Created by admin on 2017/1/19.
 */
//预约详情
public class AppointmentDetailNewActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.head_iv)
    ImageView headIv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.shop_tv)
    TextView shopTv;
    @Bind(R.id.pernumber_tv)
    TextView pernumberTv;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_extra)
    TextView tvExtra;
    @Bind(R.id.tv_dingjin)
    TextView tvDingjin;
    @Bind(R.id.tv_dingjintips)
    TextView tvDingjintips;

    @Bind(R.id.tv_dingdanhaoma)
    TextView tvDingdanhaoma;
    @Bind(R.id.tv_weikuan)
    TextView tvWeikuan;


    @Bind(R.id.ordersuccess_btn_rl)
    RelativeLayout ordersuccessBtnRl;
    @Bind(R.id.rl_service)
    RelativeLayout rlService;
    @Bind(R.id.ll_zanshang)
    LinearLayout llZanshang;
    @Bind(R.id.rl_dingjin)
    RelativeLayout rlDingjin;
    @Bind(R.id.rl_ordertime)
    RelativeLayout rlOrdertime;
    @Bind(R.id.ll_state)
    LinearLayout llState;
    @Bind(R.id.firstline)
    View firstline;
    @Bind(R.id.rlcustomer)
    RelativeLayout rlcustomer;
    @Bind(R.id.et_dingjin)
    EditText etDingjin;
    @Bind(R.id.et_customerremark)
    EditText etCustomerremark;
    @Bind(R.id.pmnv_select_deadline)
    PMNumView pmnvSelectDeadline;
    @Bind(R.id.play_btn)
    Button playBtn;
    @Bind(R.id.customerconfirm_btn)
    Button customerconfirmBtn;
    @Bind(R.id.billdetails_btn)
    Button billdetailsBtn;
    @Bind(R.id.sl_data)
    ScrollView slData;
    @Bind(R.id.tv_nodata)
    TextView tvNodata;


    private MediaPlayer mMediaPlayer;


    public static String INTENT_RESERVATIONID = "intent";
    public static String INTENT_TYPE = "type";
    @Bind(R.id.yuyueresultcommit_rl)
    RelativeLayout yuyueresultcommitRl;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.ll_weikuan)
    LinearLayout llWeikuan;
    @Bind(R.id.ll_dingdanhaoma)
    LinearLayout llDingdanhaoma;

    @Bind(R.id.btn_modify)
    RoundTextView btnModify;
    @Bind(R.id.dinghaole_tv)
    TextView dinghaoleTv;
    @Bind(R.id.v_user)
    ImageView vUser;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.serveraccount_btn)
    Button serveraccountBtn;
    String reservationId = "";
    int type;
    String replayNote;
    int  merchantReviewstatus;
    M_Reservation mReservation;
    User2ReservationInfoRequest user2ReservationInfoRequest;
    User2ReservationEditRequest user2ReservationEditRequest;
    User2ReservationDelRequest  user2ReservationDelRequest;
    User2ReservationPayRequest  user2ReservationPayRequest;
    User2ReservationSureRequest user2ReservationSureRequest;
    UserRewardPayAddRequest rewardPayAddRequest;

    public static String REFLASH_MYAPPOINT = "reflash_myappoint";
    String userName="",fileUrl="";
    ImageManager mimageManager;
    @Bind(R.id.cb_reward)
    CheckBox cbReward;
    double  rewardMoney = 0;
    Set<Integer> selectIdSet = new HashSet<Integer>();
    Set<Integer> selectIdSetTemp = new HashSet<Integer>();
    private SendRewardAdapter adapterReward;
    List<M_Bill> moneyList = new ArrayList<M_Bill>();
    CommonRewardRequest commonRewardRequest;
    Dialog alertDialog;
    int orderpeople;
    int dialogShowCount;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_appointmentdetailnew);
    }

    @Override
    public void init() {
        lhTvTitle.setText("预约详情");
        reservationId = getIntent().getStringExtra(INTENT_RESERVATIONID);
        EventBus.getDefault().register(this);
        showPd();
        userReservationInfo();
        mimageManager = new ImageManager(getApplicationContext());
        alertDialog = new Dialog(this, R.style.MMTheme_DataSheet);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userReservationInfo();
    }

    private void userReservationInfo() {
        if (user2ReservationInfoRequest != null) {
            user2ReservationInfoRequest.cancel();
        }
        User2ReservationInfoRequest.Input input = new User2ReservationInfoRequest.Input();
        input.reservationId = reservationId;
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
                    userName=mReservation.saleUserName;
                    fileUrl=mReservation.replayNote;
                    merchantReviewstatus = mReservation.merchantReviewstatus;
                    slData.setVisibility(View.VISIBLE);
                    tvNodata.setVisibility(View.GONE);

                    if (merchantReviewstatus == 2) {//商户审核状态(0未审核,1待审核,2审核通过)
                        rlDingjin.setVisibility(View.VISIBLE);
                        tvDingjintips.setVisibility(View.VISIBLE);
                        Drawable drawable1 = getResources().getDrawable(R.mipmap.money_red);
                        drawable1.setBounds(0, 0, 40, 40);
                        tvDingjin.setCompoundDrawables(drawable1, null, null, null);
                    } else {
                        rlDingjin.setVisibility(View.GONE);
                        tvDingjintips.setVisibility(View.GONE);
                    }

                    if (mReservation.saleUserId == SlashHelper.userManager().getUserId()) {
                        type = 1;//服务管家
                    } else {
                        type = 0;//客户
                    }
                    //显示用户头像,管家头像
                    if (type == 1) {//服务管家
                        llState.setVisibility(View.VISIBLE);
                        firstline.setVisibility(View.VISIBLE);
                        rlcustomer.setVisibility(View.VISIBLE);
                        rlService.setVisibility(View.GONE);
                        tvName.setText(mReservation.name);
                        mimageManager.loadCircleImage(mReservation.head, vUser);
                        //语音信息
                        if(!StringUtils.isBlank(mReservation.replayNote)){
                            playBtn.setVisibility(View.VISIBLE);
                        }
                        else{
                            playBtn.setVisibility(View.GONE);
                        }
                    } else if (type == 0) {//客户
                        mimageManager.loadCircleImage(mReservation.saleUserHead, headIv);
                        nameTv.setText(mReservation.saleUserName);
                        firstline.setVisibility(View.GONE);
                        rlcustomer.setVisibility(View.GONE);
                        rlService.setVisibility(View.VISIBLE);
                    }

                    //状态(0:待确认,1:预约成功,2:已支付,3:预约失效(待确认超时)，4：预约未支付(超时))
                    if (mReservation.state == 0) {
                        tvState.setText("待确认");
                        //修改撤销按钮   客户确认
                        if (type == 0) {//我是客户的状态下
                            yuyueresultcommitRl.setVisibility(View.GONE);
                            customerconfirmBtn.setVisibility(View.VISIBLE);
                            llZanshang.setVisibility(View.VISIBLE);
                            common_reward();
                        } else if (type == 1) {//我是管家的情况下
                            yuyueresultcommitRl.setVisibility(View.VISIBLE);
                            customerconfirmBtn.setVisibility(View.GONE);
                        }
                    } else if (mReservation.state == 1) {
                        //状态(0:待确认,1:预约成功,2:已支付,3:预约失效(待确认超时)，4：预约未支付(超时))
                        tvState.setText("预约成功");
                        if (type == 0) {//客户
                            customerconfirmBtn.setVisibility(View.GONE);
                            llZanshang.setVisibility(View.GONE);
                            dinghaoleTv.setVisibility(View.VISIBLE);
                            ordersuccessBtnRl.setVisibility(View.VISIBLE);
                            if(mReservation.merchantReviewstatus==2){
                                lhBtnRight.setVisibility(View.VISIBLE);
                                lhBtnRight.setText("快捷买单");
                            }

                            serveraccountBtn.setVisibility(View.GONE);
                        if(dialogShowCount==0){
                            dialogShowCount++;
                            CommonDialog.showDialogListener(AppointmentDetailNewActivity.this, "生成邀请函", "否", "是", "太棒了！订单已确认，做个精美的邀请函去邀请好友吧~", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CommonDialog.DismissProgressDialog();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CommonDialog.DismissProgressDialog();

                                    Intent urlintent = new Intent(AppointmentDetailNewActivity.this, ShareAppointmentActivity.class);
                                    urlintent.putExtra("shareurl", Urls.BASIC_URL.replace("inter_json", "app") + "share_reservation_info.do?reservationId=" + reservationId);
                                    urlintent.putExtra("sharetitle", "您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】邀您赴约");
                                    urlintent.putExtra("sharecontent", "您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】刚刚预定了" + mReservation.reservationTime + mReservation.merchantName +
                                            "，诚挚邀请，期待您的赴约。");
                                    startActivity(urlintent);
                                }
                            });
                        }
                        }
                        else{

                            if (merchantReviewstatus == 2) {//商户审核状态(0未审核,1待审核,2审核通过)
                                serveraccountBtn.setVisibility(View.VISIBLE);
                            } else {
                                serveraccountBtn.setVisibility(View.GONE);
                            }
                        }


                        //状态(0:待确认,1:预约成功,2:已支付,3:预约失效(待确认超时)，4：预约未支付(超时))
                    } else if (mReservation.state == 2) {
                        tvState.setText("已支付");
                        serveraccountBtn.setVisibility(View.VISIBLE);
                        customerconfirmBtn.setVisibility(View.GONE);
                        llZanshang.setVisibility(View.GONE);
                        llWeikuan.setVisibility(View.VISIBLE);
                        llDingdanhaoma.setVisibility(View.VISIBLE);
                        tvDingdanhaoma.setText(mReservation.userPayNumber);
                        tvWeikuan.setText(mReservation.userPayMoney+"");
                        Drawable drawable1 = getResources().getDrawable(R.mipmap.money_gray);
                        drawable1.setBounds(0, 0, 40, 40);
                        tvWeikuan.setCompoundDrawables(drawable1, null, null, null);//只放左边
                        billdetailsBtn.setVisibility(View.VISIBLE);
                    } else if (mReservation.state == 3||mReservation.state == 4) {
                        tvState.setText("已结束");
                    }

                    shopTv.setText(mReservation.merchantName);
                    pernumberTv.setText(mReservation.num + "人");
                    tvTime.setText(mReservation.reservationTime);
                    tvExtra.setText(mReservation.note);
                    tvDingjin.setText(mReservation.reservationMoney+"");
                    etCustomerremark.setText(mReservation.note);

                    etDingjin.setText(mReservation.reservationMoney+"");
                    orderpeople = mReservation.num  ;

                } else {
                    slData.setVisibility(View.GONE);
                    tvNodata.setVisibility(View.VISIBLE);
                    ToastUtils.show(AppointmentDetailNewActivity.this, ((M_Reservation) response).info);
                }

            }
        });
        sendJsonRequest(user2ReservationInfoRequest);
    }


    //服务管家修改预约单(未确认的)
    private void user2_reservation_edit() {

        try {
            if (user2ReservationEditRequest != null) {
                user2ReservationEditRequest.cancel();
            }
            User2ReservationEditRequest.Input input = new User2ReservationEditRequest.Input();
            input.reservationId = reservationId;
            input.num = orderpeople;
            input.note = etCustomerremark.getText().toString();
            if(tvTime.getText().toString().length()<17){
                input.reservationTime= tvTime.getText().toString()+ ":00";
            }
            else{
                input.reservationTime= tvTime.getText().toString();
            }

            input.reservationMoney= etDingjin.getText().toString();
            input.convertJosn();

            user2ReservationEditRequest = new User2ReservationEditRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    if (((CommonResult) response).status == 1) {

                        YWCustomMessageBody messageBody = new YWCustomMessageBody();
                        //定义自定义消息协议，用户可以根据自己的需求完整自定义消息协议，不一定要用JSON格式，这里纯粹是为了演示的需要
                        JSONObject object = new JSONObject();
                        try {
                            object.put("customizeMessageType", "Task");
                            object.put("tasktype", "ORDER");
                            object.put("taskTitle", "[服务订单-修改] " +(tvTime.getText().toString().length()<17?tvTime.getText().toString()
                                    :tvTime.getText().toString().substring(0,16) )+ "  " + mReservation.merchantName+"(商户)");
                            object.put("serviceId", mReservation.saleUserId + "");
                            object.put("reservationId", reservationId + "");
                        } catch (JSONException e) {

                        }
                        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
                        messageBody.setSummary("[预约单]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
                        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                        IYWContact appContact = YWContactFactory.createAPPContact(mReservation.userId + "", imKit.getIMCore().getAppKey());
                        imKit.getConversationService()
                                .forwardMsgToContact(appContact
                                        , message, forwardCallBack);
//                        startActivity(imKit.getChattingActivityIntent(mReservation.saleUserId+""));
                        finish();

                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(user2ReservationEditRequest);
        } catch (Exception e) {
        }
    }



    //服务管家撤销预约单(未确认的)
    private void user2_reservation_del() {
        try {
            if (user2ReservationDelRequest != null) {
                user2ReservationDelRequest.cancel();
            }
            User2ReservationDelRequest.Input input = new User2ReservationDelRequest.Input();
            input.reservationId = reservationId;
            input.convertJosn();

            user2ReservationDelRequest = new User2ReservationDelRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    if (((CommonResult) response).status == 1) {
                        ToastUtil.showMessage("撤销成功");
                        finish();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(user2ReservationDelRequest);
        } catch (Exception e) {
        }
    }

    //用户确认预约单
    private void user2_reservation_sure() {
        try {
            if (user2ReservationSureRequest != null) {
                user2ReservationSureRequest.cancel();
            }
            User2ReservationSureRequest.Input input = new User2ReservationSureRequest.Input();
            input.reservationId = reservationId;
            input.convertJosn();

            user2ReservationSureRequest = new User2ReservationSureRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    if (((CommonResult) response).status == 1) {
                        userReservationInfo();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(user2ReservationSureRequest);
        } catch (Exception e) {
        }
    }

    private void user_reward_pay_add() {
        try {
            showPd();

            if (rewardPayAddRequest != null) {
                rewardPayAddRequest.cancel();
            }
            UserRewardPayAddRequest.Input input = new UserRewardPayAddRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.toUserId = mReservation.saleUserId;
            input.money = rewardMoney;
            input.convertJosn();

            rewardPayAddRequest = new UserRewardPayAddRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissPd();
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        Intent intent = new Intent(AppointmentDetailNewActivity.this, ChoosePayTypeActivity.class);
                        intent.putExtra("consumeMoney", rewardMoney);
                        intent.putExtra("order_sn", ((CommonResult) response).number);
                        intent.putExtra("userPayId", ((CommonResult) response).userPayId);
                        intent.putExtra("toUserId", mReservation.saleUserId);
                        intent.putExtra("merchantName", "赞赏");
                        String imMessageTitle = "";
                        String imMessageContent = "";
                        for (int i : selectIdSet) {
                            imMessageTitle = imMessageTitle + moneyList.get(i).name + ",";
                            imMessageContent = imMessageContent + moneyList.get(i).name + moneyList.get(i).money + ",";
                        }
                        if (imMessageTitle.indexOf(",") != -1) {
                            intent.putExtra("imMessageTitle", imMessageTitle.substring(0, imMessageTitle.length() - 1));
                            intent.putExtra("imMessageContent", imMessageContent.substring(0, imMessageContent.length() - 1));
                        }
                        intent.putExtra("merchantHead", "");
                        intent.putExtra("managerhead", "");
                        intent.putExtra("managername", "");
                        startActivityForResult(intent, 10001);
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(rewardPayAddRequest);
        } catch (Exception e) {
            dismissPd();
        }
    }

    //生成定金支付单(用户确认预约单)
    private void user2_reservation_pay() {
        try {
            if (user2ReservationPayRequest != null) {
                user2ReservationPayRequest.cancel();
            }
            User2ReservationPayRequest.Input input = new User2ReservationPayRequest.Input();
            input.reservationId = mReservation.reservationId;
            input.userId= SlashHelper.userManager().getUserId();
            input.merchantId= mReservation.merchantId;
            input.saleUserId= mReservation.saleUserId;
            input.money= mReservation.reservationMoney;//支付单金额(定金金额)
            input.consumeMoney=Convert.getMoneyString( mReservation.reservationMoney+rewardMoney);//当次支付总金额(money+rewardMoney)
            input.rewardMoney= rewardMoney;//打赏金额(没有为0)
            input.convertJosn();

            user2ReservationPayRequest = new User2ReservationPayRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    if (((CommonResult) response).status == 1) {
                        Intent intent = new Intent(AppointmentDetailNewActivity.this, ChoosePayType4OrderActivity.class);
                        intent.putExtra("consumeMoney", (mReservation.reservationMoney+rewardMoney));
                        intent.putExtra("order_sn", ((CommonResult) response).number);
                        intent.putExtra("userPayId",  ((CommonResult) response).userPayId);
                        intent.putExtra("reservationId", reservationId);
                        intent.putExtra("rewardMoney", rewardMoney);
                        intent.putExtra("toUserId", mReservation.saleUserId);
                        intent.putExtra("money", mReservation.reservationMoney);
                        intent.putExtra("saleName", mReservation.saleUserName);
                        intent.putExtra("saleHead", mReservation.saleUserHead);
                        String imMessageTitle="",imMessageContent="";
                        for (int i : selectIdSet) {
                            imMessageTitle = imMessageTitle + moneyList.get(i).name + ",";
                            imMessageContent = imMessageContent + moneyList.get(i).name + moneyList.get(i).money + ",";
                        }
                        if (imMessageTitle.indexOf(",") != -1) {
                            intent.putExtra("imMessageTitle", imMessageTitle.substring(0, imMessageTitle.length() - 1));
                            intent.putExtra("imMessageContent", imMessageContent.substring(0, imMessageContent.length() - 1));
                        }
                        startActivityForResult(intent, 1000);
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(user2ReservationPayRequest);
        } catch (Exception e) {
        }
    }


    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(AppointmentDetailNewActivity.this, "forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(AppointmentDetailNewActivity.this, "forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.head_iv, R.id.serveraccount_btn,R.id.lh_btn_right,R.id.invite_btn,R.id.billdetails_btn, R.id.jiezhang_btn, R.id.btn_cancel,R.id.btn_modify,R.id.iv_reward,R.id.customerconfirm_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                EventBus.getDefault().post(REFLASH_MYAPPOINT);
                onBackPressed();
                break;

            case R.id.serveraccount_btn:
                Intent intentserver = new Intent(this, SettingMoney4OrderActivity.class);
                intentserver.putExtra("reservationMoney",mReservation.reservationMoney);
                intentserver.putExtra("experience",mReservation.saleUserExperience);
                intentserver.putExtra("saleUserId",mReservation.saleUserId);
                intentserver.putExtra("saleHead",mReservation.saleUserHead);
                intentserver.putExtra("saleName",mReservation.saleUserName);
                intentserver.putExtra("merchantName",mReservation.merchantName);
                intentserver.putExtra("merchantId",mReservation.merchantId);
                if (!TextUtils.isEmpty(reservationId))
                    intentserver.putExtra("reservationId", Integer.valueOf(reservationId));
                startActivity(intentserver);

                break;
            case R.id.head_iv:
                if (type == 0) {
                    //服务管家的头像和姓名
                    IntentUtil.intStart_activity(this, UserDetailActivity.class, new Pair<String, Integer>(UserDetailActivity.USER_ID, mReservation.saleUserId));
                } else if (type == 1) {
                    //客户的头像和姓名
                    IntentUtil.intStart_activity(this, UserDetailActivity.class, new Pair<String, Integer>(UserDetailActivity.USER_ID, mReservation.userId));
                }
                break;
            case R.id.billdetails_btn:
                //查看订单详情
                IntentUtil.intStart_activity(this,
                        PayInfoActivity.class, new Pair<String, Integer>("userPayId", mReservation.userPayId));
                break;
            case R.id.customerconfirm_btn:
                //确认预约单
                if (!cbReward.isChecked()) {
                    rewardMoney=0;
                }
                if(mReservation.reservationMoney+rewardMoney==0){
                    user2_reservation_sure();
                }
                else if(mReservation.reservationMoney==0&&rewardMoney!=0){
                    user2_reservation_sure();
                    user_reward_pay_add();
                }
                else{
                    user2_reservation_pay();
                }

                break;

            case R.id.invite_btn:

                //评价
                Intent intent2 = new Intent(this, Assessment4ServiceActivity.class);
                intent2.putExtra(FindPayActivity.M_RESERVATION, mReservation);
                intent2.putExtra("managerhead", mReservation.saleUserHead);
                intent2.putExtra("managername",mReservation.saleUserName);
                intent2.putExtra("merchantName", mReservation.merchantName);
                if (!TextUtils.isEmpty(reservationId))
                    intent2.putExtra("reservationId", Integer.valueOf(reservationId));
                startActivity(intent2);

                break;
            case R.id.jiezhang_btn:

                //邀请好友
                Intent urlintent = new Intent(this, ShareAppointmentActivity.class);
                urlintent.putExtra("shareurl", Urls.BASIC_URL.replace("inter_json", "app") + "share_reservation_info.do?reservationId=" + reservationId);
                urlintent.putExtra("sharetitle", "您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】邀您赴约");
                urlintent.putExtra("sharecontent", "您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】刚刚预定了" + mReservation.reservationTime + mReservation.merchantName +
                        "，诚挚邀请，期待您的赴约。");
                startActivity(urlintent);


                break;


            case R.id.btn_cancel:

                CommonDialog.showDialogListener(AppointmentDetailNewActivity.this,null, "否", "是", "是否撤销服务单", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                        user2_reservation_del();
                    }
                });

                break;

            case R.id.play_btn:
                if(!StringUtils.isBlank(fileUrl)){
                    startPlay();
                  }
                break;

            case R.id.btn_modify:
                if(btnModify.getText().toString().equals("修改")){
                    btnModify.setText("完成并发送");
                    etDingjin.setVisibility(View.VISIBLE);
                    etCustomerremark.setVisibility(View.VISIBLE);
                    pmnvSelectDeadline.setVisibility(View.VISIBLE);
                    Drawable dra= getResources().getDrawable(R.mipmap.right_btn);
                    dra.setBounds( 0, 0, dra.getMinimumWidth(),dra.getMinimumHeight());
                    tvTime.setCompoundDrawablePadding(10);
                    tvTime.setCompoundDrawablesWithIntrinsicBounds(null, null, dra, null);

                    pernumberTv.setVisibility(View.GONE);
                    tvExtra.setVisibility(View.GONE);
                    tvDingjin.setVisibility(View.GONE);


                    pmnvSelectDeadline.setMinNum(1);
                    pmnvSelectDeadline.setMaxNum(99);
                    pmnvSelectDeadline.setDefaultNum(orderpeople);
                    pmnvSelectDeadline.setText(orderpeople+"");
                    pmnvSelectDeadline.setFilter();

                    pmnvSelectDeadline.setOnNumChangeListener(new PMNumView.NumChangeListener() {
                        @Override
                        public void onNumChanged(int num) {
                            orderpeople = num ;
                            pmnvSelectDeadline.setDefaultNum(num);
                            pernumberTv.setText(orderpeople+ "人");
                        }
                    });

                    etCustomerremark.setText(tvExtra.getText().toString());
                    etDingjin.setText(tvDingjin.getText().toString());
                    rlOrdertime.setClickable(true);
                    rlOrdertime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                                    AppointmentDetailNewActivity.this, tvTime.getText().toString(), "预约时间必须大于当前时间", 1);
                            dateTimePicKDialog.dateTimePicKDialog(tvTime);
                        }
                    });

                }
                else{
                    btnModify.setText("修改");
                    etDingjin.setVisibility(View.GONE);
                    etCustomerremark.setVisibility(View.GONE);
                    pmnvSelectDeadline.setVisibility(View.GONE);
                    tvTime.setCompoundDrawables(null, null, null, null);

                    pernumberTv.setVisibility(View.VISIBLE);
                    tvExtra.setVisibility(View.VISIBLE);
                    tvDingjin.setVisibility(View.VISIBLE);

                    rlOrdertime.setClickable(false);
                    tvExtra.setText(etCustomerremark.getText().toString());
                    tvDingjin.setText(etDingjin.getText().toString());
                    user2_reservation_edit();
                }
                break;
            case R.id.lh_btn_right:
                //快速结账
                Intent intent = new Intent(this, FindPayActivity.class);
                intent.putExtra(FindPayActivity.M_RESERVATION, mReservation);
                intent.putExtra("merchantId", Integer.valueOf(mReservation.merchantId));
                intent.putExtra("userSaleId", Integer.valueOf(mReservation.saleUserId));
                if (!TextUtils.isEmpty(reservationId))
                    intent.putExtra("reservationId", Integer.valueOf(reservationId));
                startActivity(intent);
                break;

            case R.id.iv_reward:
                showDialog();

            case R.id.cb_reward:
                if (cbReward.isChecked()) {
                    rewardMoney = 0;
                    if(selectIdSet.size()==0){
                        selectIdSet.add(1);
                    }
                    for (Integer selectidposition : selectIdSet) {
                        rewardMoney = rewardMoney + adapterReward.getItem(selectidposition).money;
                    }
                    cbReward.setTextColor(getResources().getColor(R.color.bg_head_red));
                    cbReward.setText(String.format("赞赏%s", Convert.getMoneyString(rewardMoney)));
//                    tvMoneyRealpay.setText("￥" +  rewardMoney);
                } else {
                    selectIdSet.clear();
                    selectIdSetTemp.clear();
                    selectIdSet.add(1);
                    rewardMoney = moneyList.get(1).money;
                    cbReward.setChecked(false);
                    cbReward.setTextColor(getResources().getColor(R.color.font_black_999));
//                    tvMoneyRealpay.setText("￥" + Convert.getMoneyString(getMoney()));
                    cbReward.setText(String.format("赞赏%s", Convert.getMoneyString(rewardMoney)));
                }
                break;
        }
    }

    private void showDialog() {

        View view = LayoutInflater.from(AppointmentDetailNewActivity.this).inflate(R.layout.dialog_reward, null);
        FixedGridView gv = (FixedGridView) view.findViewById(R.id.gv);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);

        gv.setAdapter(adapterReward);

        for (Integer selectIdPosition : selectIdSet) {
            selectIdSetTemp.add(selectIdPosition);
        }

        if (!cbReward.isChecked()) {
            selectIdSet.clear();
            selectIdSetTemp.clear();
        } else {
            adapterReward.setSelected(selectIdSetTemp);
        }

        alertDialog.setContentView(view);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectIdSetTemp.clear();
                alertDialog.dismiss();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewardMoney = 0;
                selectIdSet.clear();
                for (Integer selectIdPosition : selectIdSetTemp) {
                    selectIdSet.add(selectIdPosition);
                }
                selectIdSetTemp.clear();

                for (Integer selectIdPosition : selectIdSet) {
                    rewardMoney = rewardMoney + adapterReward.getItem(selectIdPosition).money;
                }

                if (rewardMoney == 0) {
                    selectIdSet.clear();
                    selectIdSet.add(1);
                    rewardMoney = moneyList.get(1).money;
                    cbReward.setChecked(false);
                    cbReward.setTextColor(getResources().getColor(R.color.font_black_999));
//                    tvMoneyRealpay.setText("￥" + Convert.getMoneyString(getMoney()));
                } else {
                    cbReward.setTextColor(getResources().getColor(R.color.bg_head_red));
                    cbReward.setChecked(true);
//                    tvMoneyRealpay.setText("￥" + Convert.getMoneyString(rewardMoney + getMoney()));
                }

                cbReward.setText(String.format("赞赏%s", Convert.getMoneyString(rewardMoney)));

                alertDialog.dismiss();
            }
        });


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectIdSetTemp.contains(position)) {
                    selectIdSetTemp.remove(position);
                } else {
                    selectIdSetTemp.add(position);
                }

                adapterReward.setSelected(selectIdSetTemp);
            }
        });

        Window dialogWindow = alertDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialog_style);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);
        alertDialog.show();
    }

    private void common_reward() {
        if (commonRewardRequest != null) {
            commonRewardRequest.cancel();
        }
        commonRewardRequest = new CommonRewardRequest(new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {

                if (((APIM_PresentList) response).status == 1) {
                    if (((APIM_PresentList) response).moneyList.size() > 0) {
                        moneyList = ((APIM_PresentList) response).moneyList;
                        selectIdSet.add(1);
                        rewardMoney = moneyList.get(1).money;
                        adapterReward = new SendRewardAdapter(AppointmentDetailNewActivity.this, moneyList);
                        cbReward.setText(String.format("赞赏%s", Convert.getMoneyString(rewardMoney)));
                    }
                } else {
                    ToastUtils.show(AppointmentDetailNewActivity.this, ((APIM_PresentList) response).info);
                }
                dismissPd();
            }
        });

        sendJsonRequest(commonRewardRequest);
    }


    public void startPlay() {
        stopPlay();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                // TODO Auto-generated method stub

                String fileName = HttpOperateUtil.downLoadFile(fileUrl,
                        fileUrl.substring(fileUrl.lastIndexOf("/") + 1));

                Log.i("keanbin", "fileName = " + fileName);
                File file = new File(fileName);

                if (!file.exists()) {
//                    Toast.makeText(DoTaskVoiceActivity.this, "没有语音文件！", Toast.LENGTH_SHORT)
//                            .show();
                    return;
                }
                try{
                    mMediaPlayer = MediaPlayer.create(AppointmentDetailNewActivity.this,
                            Uri.parse(fileName));
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.start();
                }catch (Exception e){
                }
                Looper.loop();
            }
        }).start();

    }

    ;

    public void stopPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
           finish();
        }
    }

    /**
     * =================================================处理刷新请求===========================================================================
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshEvent(String s) {
        if (TaskPayResultActivity.APPOINT_REFLASH.equals(s))
            userReservationInfo();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
