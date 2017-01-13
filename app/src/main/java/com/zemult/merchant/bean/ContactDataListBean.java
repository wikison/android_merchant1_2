package com.zemult.merchant.bean;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.M_Area;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class ContactDataListBean {
    @Expose
   public  String	name			;	//	地区名称(省)
    @Expose
    public  String	code			;	//	地区编号(省)
    @Expose
    public List<M_Area> cityList			;	//	市列表

}
