package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Voucher {
    @Expose
    public  int userVoucherId;//					用户的代金券id
    @Expose
    public  int voucherId;//					代金券id
    @Expose
    public  int merchantId;//     商家id


    @Expose
    public  String head	;//				商家头像
    @Expose
    public  String name	;//				商家名称

    @Expose
    public  String code	;//				兑换码

    @Expose
    public  double money	;//				单张抵扣金额
    @Expose
    public   double minMoney	;//				最低消费额
    @Expose
    public   int isUnion		;//			是否可以和其它优惠叠加使用(0:否,1:是)
    @Expose
    public  String  note	;//				备注,使用规则
    @Expose
    public   int state		;//			状态(0:未使用,1:已使用,2:已过期)
    @Expose
    public   String endtime	;//				有效截止时间 "yyyy-MM-dd HH:mm:ss"

    @Expose
    public   String userHead;//					用户头像
    @Expose
    public   String userName	;//					用户名
    @Expose
    public   String number;//						用户的优惠券编号
    @Expose
    public   String useTime	;//					使用时间 "yyyy-MM-dd HH:mm:ss"

    @Expose
    public String createtime;//     创建时间 "yyyy-MM-dd HH:mm:ss"

    @Expose
    public int num;   //数量

    @Expose
    public int useNum;  //已经使用数量

}
