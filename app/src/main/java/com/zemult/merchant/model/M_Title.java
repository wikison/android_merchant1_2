package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Wikison on 2017/3/17.
 */

public class M_Title implements Serializable {
    @Expose
    public int titleId;    //	主题id
    @Expose
    public String name;    //	主题名称
    @Expose
    public String icon;    //	主题图标

}
