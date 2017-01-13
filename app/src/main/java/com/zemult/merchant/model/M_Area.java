package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Area {
    @Expose
    public String	name			;	//	地区名称(市)
    @Expose
    public String	code			;	//	地区编号(市)
    @Expose
    public List<M_Zone> areaList			;	//	区列表

}
