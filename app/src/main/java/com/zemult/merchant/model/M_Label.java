package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Label {
    @Expose
    public  int	historyId		;	//	记录id
    @Expose
    public  int	labelId		;	//	标签id
    @Expose
    public String	toUserName		;	//	被贴标签的用户名
    @Expose
    public String	labelName		;	//	标签名(形容词+角色名)
    @Expose
    public String	note		;	//	贴标签用户的留言
    @Expose
    public String	createTime		;	//	创建时间(格式为:yyyy-MM-dd HH:mm:ss)
    @Expose
    public  int	fromUserId		;	//	贴标签的用户id
    @Expose
    public String	fromUserName		;	//	贴标签的用户名
    @Expose
    public String	fromUserHead		;	//	贴标签的用户头像
    @Expose
    public  int	tagId		;
    @Expose
    public String	tagName		;

}
