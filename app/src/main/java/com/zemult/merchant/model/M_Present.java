package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;


/**
 * 礼物
 * @author djy
 * @time 2017/1/20 13:23
 */

public class M_Present {
    @Expose
    public  int presentId;//  礼物id
    @Expose
    public   String	pic			    ;	//	图片
    @Expose
    public   String	name			;	//	图片
    @Expose
    public   double	price			;	//	价格
    @Expose
    public   double	exchangePrice	;	//	兑换价格
    @Expose
    public   int	num		     	;	//	数量

}



