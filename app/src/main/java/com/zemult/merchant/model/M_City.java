package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_City {
    @Expose
   public  String	name			;	//	地区名称(省)
    @Expose
    public  String	code			;	//	地区编号(省)
    @Expose
    public List<M_Area> cityList			;	//	市列表

}
