package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.lang.reflect.Array;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Message {
    @Expose
    public String  note;//消息内容
    @Expose
    public String  createtime;//创建时间(格式"yyyy-MM-dd HH:mm:ss")

    @Expose
    public int	type			;	//	消息类型(1:@我的,2:评论,3:赞,4:系统消息)
    @Expose
    public int	num			;	//	消息未读数量


    @Expose
    public int	messageId			;	//	消息id
    @Expose
    public int	infoType			;	//	消息类型(1:红包提醒,2:广告宣传)


    @Expose
    public  int		infoId			;	//	消息类型id(0:本身 1:任务完成记录id)
    @Expose
    public String		title			;	//	消息内容
    @Expose
    public  int		cashType			;	//	任务奖励方式(0:无,1:红包,2:代金券)(infoType=1时)
    @Expose
    public int		recordNum			;	//	参与人数(完成记录数)(infoType=1时)
    @Expose
    public double		bounseMoney			;	//	红包金额(infoType=1时)
    @Expose
    public  String		pic			;	//	图片(infoType=0时)
    @Expose
    public  String		url			;	//	链接(infoType=0时)


    @Expose public int pushUserId	;	//		@我的用户id
    @Expose public  String userHead	;	//		@我的用户头像
    @Expose  public String userName	;	//		@我的用户名称
    @Expose  public int userSex		;	//	@我的用户性别(0男,1女)

    @Expose public int  commentId		;		//探索完成记录的评论id
    @Expose public int   taskIndustryRecordId	;	//		探索记录id
    @Expose public String  taskName			;	//探索标题
    @Expose public int  userId		;//		评论用户id
    @Expose public int ruserId			;//		被回复用户id(type=1)
    @Expose public String  ruserName	;//				被回复用户昵称(type=1)
    @Expose public int  messageType	;//				消息类型(-1:系统广告，0:红包)
    @Expose public int  billId	;//账单id（红包类型时有值--点击查看详情用 user_bill_info_commission）

    @Expose public String  number	;//	订单号
    @Expose public String  fromUserHead	;//来自 用户头像（messageType=5/8）
    @Expose public String  fromUserName	;//来自 用户名称（messageType=5/8）
    @Expose public double  money	;//金额
    @Expose public int userPayId			;

    @Expose public int urlType			;//链接类型(0:web网页类,1:app内部业务页面-)

    @Expose public M_Zone appUrl			;
}
