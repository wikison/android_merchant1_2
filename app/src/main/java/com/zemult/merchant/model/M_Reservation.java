package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by admin on 2017/1/20.
 */

public class M_Reservation implements Serializable {
    @Expose
    public int reservationId;//预约单id

    @Expose
    public int userId;//用户id

    @Expose
    public String name;//用户昵称

    @Expose
    public String head;//用户头像

    @Expose
    public int merchantId;//商家id

    @Expose
    public String merchantName;//商家名称

    @Expose
    public int saleUserId;//约客的用户id

    @Expose
    public String saleUserName;//约客的昵称

    @Expose
    public String saleUserHead;//约客的头像

    @Expose
    public String reservationTime;//预约时间(格式为"yyyy-MM-dd HH:mm:ss")

    @Expose
    public int num;//人数

    @Expose
    public String userName;//联系人名称

    @Expose
    public String userPhone;//联系人电话

    @Expose
    public int userSex;//联系人性别((0男,1女))

    @Expose
    public String note;//备注

    @Expose
    public String replayNote;//答复

    @Expose
    public int state;//状态(1:预约成功,2:已支付,3:预约结束)

    @Expose
    public String number;//预约单号

    @Expose
    public int userPayId;//订单id

    @Expose
    public String userPayNumber;//订单号

    @Expose
    public int status;
    @Expose
    public String info;
    @Expose
    public int merchantReviewstatus;//商户审核状态(0未审核,1待审核,2审核通过)
    @Expose
    public boolean isChecked;//是否选中

    @Expose
    public  double userPayMoney;//消费金额(支付单金额)(总额为支付单金额+预约单的定金)

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Expose
    public double userPayMoney;//消费金额(支付单金额)(总额为支付单金额+预约单的定金)


}
