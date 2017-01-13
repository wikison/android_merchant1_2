package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Friend {
    @Expose
    int	friendId		;	//	好友用户id
    @Expose
    String	head		;	//	好友头像
    @Expose
    String	name		;	//	好友昵称
    @Expose
    int	sex		;	//	性别(0男,1女)
    @Expose
    String	company		;	//	公司名称
    @Expose
    String	position		;	//	职位名称
    @Expose
    int	isOpen		;	//	是否公开工作经历(0:否,1:是)
    @Expose
    int	merchantNum		;	//	场景数
    @Expose
    int	industryNum		;	//	角色数
    @Expose
    String	note		;	//	请求的内容
    @Expose
    int	state		;	//	请求的内容
    @Expose
    public
    int  userLevel		;		//用户的等级
    @Expose
    public
    int  userIndustryNum		;		//用户的所有角色数量
    @Expose
    public
    ArrayList<M_UserRole> userIndustryList;//用户的所有角色列表

    public String phone;//手机号
    public String header;
    private String nicknamepinyin; // 拼音
    private boolean isselected;

    /**
     * 1.1 add
     * @return
     */
    @Expose
    int	userId		;	//	好友用户id
    @Expose
    String	userHead		;	//	好友头像
    @Expose
    String	userName		;	//	好友昵称
    @Expose
    int	userSex		;	//	性别(0男,1女)

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isselected() {
        return isselected;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public int getMerchantNum() {
        return merchantNum;
    }

    public void setMerchantNum(int merchantNum) {
        this.merchantNum = merchantNum;
    }

    public int getIndustryNum() {
        return industryNum;
    }

    public void setIndustryNum(int industryNum) {
        this.industryNum = industryNum;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getNicknamepinyin() {
        return nicknamepinyin;
    }

    public void setNicknamepinyin(String nicknamepinyin) {
        this.nicknamepinyin = nicknamepinyin;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public int getUserIndustryNum() {
        return userIndustryNum;
    }

    public void setUserIndustryNum(int userIndustryNum) {
        this.userIndustryNum = userIndustryNum;
    }

    public ArrayList<M_UserRole> getUserIndustryList() {
        return userIndustryList;
    }

    public void setUserIndustryList(ArrayList<M_UserRole> userIndustryList) {
        this.userIndustryList = userIndustryList;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }
}
