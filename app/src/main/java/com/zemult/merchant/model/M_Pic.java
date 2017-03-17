package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;


/**
 * 我的相册 商户相册
 * @author djy
 * @time 2016/11/24 16:12
 */

public class M_Pic {
    @Expose
    public  int picId;//  照片id
    @Expose
    public   String	picPath			;	//	照片地址
    @Expose
    public   String	note			;	//	描述内容

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}



