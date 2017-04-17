package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Ad {
    @Expose
   public  int pageNum;//  页面编号(0:app开启页1:首页广告位2:我的斜杠3:我是商家)
    @Expose
    public   String	img			;	//	图片地址
    @Expose
    public   String	url			;	//	 图片对应连接
    @Expose
    public   String	name			;	//	 图片对应连接
    @Expose
    public   String	note			;	//	 图片描述


    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}



