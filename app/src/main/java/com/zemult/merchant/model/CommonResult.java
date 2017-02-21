package com.zemult.merchant.model;//package moon.volley.entity;

import com.google.gson.annotations.Expose;

public class CommonResult {
    @Expose
    public int status;
    @Expose
    public String info;
    @Expose
    public int userId;
    @Expose
    public int isConfirm;//是否实名认证过(0:否1:是)
    @Expose
    public int flag;    //	是否有该角色 (0：否,1：有)-- flag=0表示用户没有该角色,让用户去参与这个角色
    @Expose
    public int managerId;    //	用户作为该角色的经营人id flag=1时有值,
    @Expose
    public int merchantId;//新增的商家的id
    @Expose
    public int num;//未读的消息数量

    //1.1新增 获取用户实名认证信息
    @Expose
    public String realName;//真实姓名
    @Expose
    public String IDNum;//身份证号
    @Expose
    public String mobileNum;//手机号(实名认证使用的)
    @Expose
    public String confirmtime;//认证时间"yyyy-MM-dd HH:mm:ss"

    //1.1  用户获取账号绑定信息
    @Expose
    public int isBand;//是否已经绑定(0:否,1:是)
    @Expose
    public String alipayNumber;//绑定的支付宝账号

    //1.1 获取通讯录中不是平台账户的手机号
    @Expose
    public String phones;//不是平台账户的手机号(多个","分隔)

    @Expose
    public int taskIndustryRecordId;//用户角色任务领取 id
    @Expose
    public String payNumber;//买单的编号
    @Expose
    public String payTime;//支付时间
    @Expose
    public String number;//订单号
    @Expose
    public int userPayId;//订单id
    @Expose
    public String PARTNER;//公司PID
    @Expose
    public String SELLER;//公司收款账号
    @Expose
    public String RSA_PRIVATE;//公司私钥，pkcs8格式
    @Expose
    public String RSA_PUBLIC;//支付宝公钥
    @Expose
    public String orderStr;//支付后台签名串

    //代金券扫码验证的
    @Expose
    public String name;//				商家名称
    @Expose
    public String code;//				兑换码

    @Expose
    public double money;//				单张抵扣金额
    @Expose
    public int taskIndustryId;//生成的任务 id

    @Expose
    public double serviceMoney;//			提现手续费

    @Expose
    public int experience;//			探索经验 实际领取的

    @Expose
    public double bonuseMoney;//			领取红包金额

    @Expose
    public String tagName;//			获取的标签名

    @Expose
    public String commissionMoney;//总佣金数

    @Expose
    public String reservationId;//用户的预约单列表
    @Expose
    public String rewardMoney;//打赏金额（，隔开）

    @Expose
    public double cashMoney;//当天已经提现的金额
    @Expose
    public String bankName;//银行卡名称(status=1时有值)
    @Expose
    public String bankNumber;//银行卡号码
}
