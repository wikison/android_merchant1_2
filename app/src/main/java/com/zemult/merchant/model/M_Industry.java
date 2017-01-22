package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Industry implements Serializable{
    @Expose
    public int	id,industryId			;	//	行业id
    @Expose
    public String	name			;	//	行业名称
    @Expose
    public String	icon			;	//	行业图标 地址
    @Expose
    public String	icon_select			;	//	行业图标 地址
    @Expose
    public List<M_UserRole> childList			;	//	行业下的角色列表信息

    @Expose
    public String tag;                //标签说明
    @Expose
    public int num;              //参与人数

    @Expose
    public int friendNum;           //杠友数

    @Expose
    public String typeName;          //数据来源说明


    @Expose
    public String industryName;//角色名称
    @Expose
    public int    personNum;//参与人数
    @Expose
    public int status;//申请状态(0:未申请,1:已申请)
    @Expose
    public int level;//角色等级

    @Expose
    public List<M_Industry> industryList			;	//	行业列表

    @Expose
    public List<M_Merchant> merchantList            ;	//	商家列表

    private boolean showAll = false;
    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public int getIndustryId() {
        return industryId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private boolean selected;
    public int	int_icon			;	//	行业图标 地址
    public int	int_icon_select			;	//	行业图标 地址

}
