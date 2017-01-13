package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Comment {
    @Expose
    public int	commentId		;	//	评论id
    @Expose
    public int	type		;	//	类型(0:正常评论;1:回复别人的评论 )
    @Expose
    public int	userId		;	//	评论用户id
    @Expose
    public String	userHead		;	//	评论用户头像
    @Expose
    public String	userName		;	//	评论用户昵称
    @Expose
    public int	ruserId		;	//	被回复用户id(type=1)
    @Expose
    public String	ruserName		;	//	被回复用户昵称(type=1)
    @Expose
    public String	note		;	//	评论内容
    @Expose
    public String	createtime		;	//	评论时间 "yyyy-MM-dd HH:mm:ss"

    @Expose
    public int	TaskIndustryRecordId			;	//	任务记录id
    @Expose
    public String	taskName			;	//	任务标题
    @Expose
    public int	taskNum			;	//	任务参与人数
    @Expose
    public int	cashType			;	//	任务的金钱奖励方式(0:无,1:红包,2:代金券)
    @Expose
    public int  userLevel;//评论用户的等级
    @Expose
    public List<M_UserRole> userIndustryList;//用户的所有角色列表
    @Expose
    public int	userIndustryNum			;	//	评论用户的所有角色数量
    @Expose
    public int	isGood			;	//	操作用户是否赞过该评论(0:否1:是)
    @Expose
    public int	goodNum			;	//	点赞数

}
