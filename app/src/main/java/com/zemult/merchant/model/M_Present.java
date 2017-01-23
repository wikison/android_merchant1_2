package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;


/**
 * 礼物
 * @author djy
 * @time 2017/1/20 13:23
 */

public class M_Present implements Serializable {
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

    public int getPresentId() {
        return presentId;
    }

    public void setPresentId(int presentId) {
        this.presentId = presentId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getExchangePrice() {
        return exchangePrice;
    }

    public void setExchangePrice(double exchangePrice) {
        this.exchangePrice = exchangePrice;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}



