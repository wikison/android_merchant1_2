package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_UserRole implements Serializable{
    @Expose
    public int id, industryId;    //	角色id
    @Expose
    public  String	name		;	//	角色名称
    @Expose
    public  String	icon		;	//	角色图标 地址
    @Expose
    public  String	pic		;	//	角色封面 地址
    @Expose
    public  String	noteClause		;	//	角色条款 地址
    @Expose
    public String	tag		;	//	标签
    @Expose
    public  int   num		;	//	参与人数
    @Expose
    public  String createPic;	//	创建角色的显示图片
    @Expose
    public String createNote1;	//	创建角色显示文字第一行内容
    @Expose
    public String createNote2;	//	创建角色显示文字第二行内容
    @Expose
    public  String createNote3;	//	创建角色显示文字第三行内容
    @Expose
    public  String level;	//	等级 added 20160711 by lc

    //1.1新增  搜索好友列表user_friendList
    @Expose
    public  String industryLevel;//用户的角色等级
    @Expose
    public  String industryName;//角色名称
    @Expose
    public  int   friendNum		;	//	杠友数(用户id有值时)
    @Expose
    public  int   isManager		;	//	用户是否有这个角色(0:否1:是)

    @Expose
    public String typeName;   //数据来源说明()
    @Expose
    public int viewType = 1;


}
