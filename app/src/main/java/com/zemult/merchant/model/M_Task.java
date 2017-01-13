package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Task implements Serializable{

    @Expose
    public int industryId; //角色id

    @Expose
    public   int taskIndustryRecordId	;			//用户角色任务记录id
    @Expose
    public   int taskIndustryId	;			//角色任务id
    @Expose
    public String industryIcon;   //角色图标
    @Expose
    public String industryTag;     //角色标签说明
    @Expose public   String title	;			//任务标题
    @Expose public   String note		;		//任务描述
    @Expose public   int type		;		//任务类型(0:图文，1:语音，2:投票,3买单)     在用户等级列表类型为(-1:成长任务,2:每天任务)
    @Expose public   String industryName		;		//任务角色名称
    @Expose public   int experience		;		//完成任务实际领取的经验值            在用户等级列表中为任务的经验值
    @Expose public   double bonuseMoney	;			//完成任务获得的红包金额
    @Expose public   String userHead		;		//完成任务的用户头像
    @Expose public   String userName		;		//完成任务的用户名称
    @Expose public   int userId		;		//完成任务的用户id
    @Expose public   int userSex		;		//完成任务的用户性别(0男,1女)
    @Expose public   int userLevel	;		//	用户的等级
    @Expose public   int userIndustryNum	;//			用户的所有角色数量
    @Expose public   String userNote	;	//		文字内容
    @Expose public   String pic		;		//图片(多张","分隔)
    @Expose public  String audio	;		//	音频地址
    @Expose public  String audioTime	;	//		音频时长
    @Expose public   String completeTime;		//		完成时间(格式为:yyyy-MM-dd HH:mm:ss)
    @Expose public   String merchantPic;		//		封面
    @Expose public   String merchantName;		//		买单商家（买单类型有值）
    @Expose public  double payMoney	;		//	买单金额（买单类型有值）
    @Expose public  int commentNum	;		//	评论数
    @Expose public  int goodNum		;	//	点赞数
    @Expose public   int friendDimension	;//			好友维度(0:非好友,1:1度好友,2:2度好友)
    @Expose
    public List<M_UserRole> userIndustryList;//用户的所有角色列表
    @Expose
    public List<M_Vote> voteList;//选项列表
    @Expose
    public String voteChose;    //	用户选项内容
    @Expose
    public int voteAllNum;    //	总票数
    @Expose
    public int isFavorite; // 操作用户是否收藏该方案(0:否1:是)---操作用户id无值时默认为0
    @Expose
    public int isGood; // 操作用户是否赞过该方案(0:否1:是)---操作用户id无值时默认为0
    @Expose
    public int state;//任务状态(0:进行中,1:已结束 )
    @Expose
    public int isVoucher; //是否获得优惠券(0:否,1:是 )
    @Expose
    public double userPayMoney;//该任务累计买单的金额（当taskIndustryRecordId!=-1且type=3时有值）,默认0
    //1.1  用户获取可领取的角色任务列表(新任务)
    //1.1 用户获取已经领取的任务列表(待完成,已完成,已过期)
    //1.1 用户获取自己发布的任务(全部,进行中,已结束)
    @Expose
    public int pushType;            //任务发布类型(0:用户,1:商家,2:系统)
    @Expose
    public String createtime;        //		发布时间(格式为:yyyy-MM-dd HH:mm:ss)
    @Expose
    public String createTime;        //		发布时间(格式为:yyyy-MM-dd HH:mm:ss)
    @Expose public  String endtime			;	//	截止时间(格式为:yyyy-MM-dd HH:mm:ss)
    @Expose public  String endTime			;	//	截止时间(格式为:yyyy-MM-dd HH:mm:ss)
    @Expose public  int cashType		;		//	金钱奖励方式(0:无,1:红包,2:代金券)
    @Expose
    public int isSendBonuse;        //	操作用户是否可以手动发红包(0:否,1:是)
    // --操作用户id无则默认为0，操作用户为发布人&任务奖励为红包&红包为手动分配&红包还有剩余&任务完成时间在任务有效期之前
    @Expose public  int bonuseNum		;		//	红包总量
    @Expose public  int outNum			;		//已经领取红包数量
    @Expose
    public double outMoney;        //已经领取红包金额
    @Expose public  double voucherMoney	;		//		代金券面额
    @Expose public   int voucherNum		;		//	代金券数量
    @Expose public   int outVoucherNum		;	//		代金券已出数量
    @Expose
    public String name;   //任务名称
    @Expose
    public int taskNum;  //任务要求的数量(例:完成邀请5个好友，则为5)

    public int getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(int isComplete) {
        this.isComplete = isComplete;
    }

    @Expose
    public int isComplete; //用户是否完成该任务(0:否1:是)


    //用户等级任务列表
    @Expose public int addExp;//用户获取的经验值
    @Expose public int completeNum;//用户当前完成的任务要求数量（例如用户邀请了2个好友，则为2）
    @Expose public int merchantId;
    @Expose public String   merchantHead;
    @Expose public String     merchantAddress;
    @Expose public String     merchantTel;
    @Expose
    public  int viewType;
    public  boolean isNoData = false;
    public  int height;
    @Expose
    public  double commissionDiscount;//佣金百分比(0-100)


    public int getTaskIndustryRecordId() {
        return taskIndustryRecordId;
    }

    public void setTaskIndustryRecordId(int taskIndustryRecordId) {
        this.taskIndustryRecordId = taskIndustryRecordId;
    }
    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isNoData() {
        return isNoData;
    }

    public void setNoData(boolean noData) {
        isNoData = noData;
    }
    @Expose
    public int recordNum; //参与人数(完成记录数)
    @Expose
    public  double discount		;		//	折扣(FType=3时有值)(0-10)
    public  double commissionMoney		;		//	该任务累计佣金的金额（买单类型有值）
    public  String tagName		;		//	获得的标签名
}
