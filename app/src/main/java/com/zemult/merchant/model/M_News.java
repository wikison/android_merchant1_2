package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_News implements Serializable{
    @Expose
    public int	newsId		;	//	方案id
    @Expose
    public String	userName		;	//	用户名
    @Expose
    public  String	userHead		;	//	用户头像
    @Expose
    public  String	company		;	//	用户公司名称
    @Expose
    public  String	position		;	//	用户职位名称
    @Expose
    public  int	isOpen		;	//	用户是否公开工作经历(0:否,1:是)
    @Expose
    public int	sex	,userSex	;	//	用户性别 (0:男,1:女)
    @Expose
    public String	merchantName		;	//	方案所在场景名称
    @Expose
    public  String	industryName		;	//	经营人角色名称
    @Expose
    public String	note		;	//	方案内容
    @Expose
    public String	pic		;	//	方案图片(多张以","分隔)
    @Expose
    public  String	distance		;	//	距中心点距离(米)
    @Expose
    public int	commentNum		;	//	评论数
    @Expose
    public int	goodNum		;	//	点赞数
    @Expose
    public String	createtime		;	//	发布时间(格式为:yyyy-MM-dd HH:mm:ss)
    @Expose
    public int	managerId		;	//	经营人id
    @Expose
    public int	merchantId		;	//	商户(场景)id
    @Expose
    public int	industryId		;	//	角色id--  (预留)
    @Expose
    public int	userId		;	//	发布人id--  (预留)

    @Expose
    public String	head		;	//	发布人头像
    @Expose
    public boolean isNoData = false;
    @Expose
    public int height;

    @Expose
    public int isFavorite; // 操作用户是否收藏该方案(0:否1:是)---操作用户id无值时默认为0
    @Expose
    public int isGood; // 操作用户是否赞过该方案(0:否1:是)---操作用户id无值时默认为0

    @Expose
    public String	name		;	//	发布人名称

    //1.1新增
    @Expose
    public int	favoriteId		;//收藏id
    @Expose
    public String title;//主题名(心情小记的内容/ 任务记录的标题)
    @Expose
    public int type;//收藏类型(0:心情小记  1:角色任务记录 )
    @Expose
    public int infoId;//对应信息id

    //1.1 首页(搜索) 心情小记（时间倒排序）
    @Expose
    public int userLevel;//用户的等级
    @Expose
    public int userIndustryNum;//用户的所有角色数量
    @Expose
    public List<M_UserRole> userIndustryList;//用户的所有角色列表
    @Expose public   int friendDimension	;//			好友维度(0:非好友,1:1度好友,2:2度好友)



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
}
