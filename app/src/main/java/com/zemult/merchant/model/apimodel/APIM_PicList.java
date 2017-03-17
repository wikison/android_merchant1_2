package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Pic;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_PicList extends CommonResult {
    @Expose
    public List<M_Pic> picList;
    @Expose
    public List<M_Pic> noteList;
    @Expose
    public int   maxpage;//当分页获取时，最大的页数
}
