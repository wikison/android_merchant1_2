package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Comment;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_ManagerNewsCommentList extends CommonResult {
    @Expose
    public List<M_Comment> commentList;
    @Expose
    public List<M_Comment> recordList;
    @Expose
    public int   maxpage;//当分页获取时，最大的页数

}
