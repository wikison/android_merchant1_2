package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;


/**
 * 服务方案
 * @author djy
 * @time 2017/4/26 15:58
 */

public class M_Plan implements Serializable{
    @Expose
    public int 	planId			;	//	方案id
    @Expose
    public String	name			;	//	标题
    @Expose
    public String	note			;	//	内容
    @Expose
    public String	pics			;	//	图片，多张
    @Expose
    public int	state			;	//	状态(0:未启用,1:已启用)
    @Expose
    public String	createTime			;

}
