package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Wikison on 2017/3/28.
 */

public class M_WxData {

    //---------微信支付参数-------------------
    @Expose
    public String appid;  //应用ID
    @Expose
    public String partnerid;  //商户号
    @Expose
    public String prepayid;  //预支付交易会话ID
    @Expose
    public String packagevalue;  // public String package; 暂填写固定值Sign=WXPay
    @Expose
    public String noncestr;  //随机字符串
    @Expose
    public String timestamp;  //时间戳
    @Expose
    public String sign;  //签名

    //---------微信支付参数-------------------
}
