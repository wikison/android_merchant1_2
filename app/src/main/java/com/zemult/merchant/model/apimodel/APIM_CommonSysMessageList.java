package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Message;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_CommonSysMessageList extends CommonResult {
    @Expose
    public  List<M_Message> sysmessageList ;//系统消息列表
    @Expose
    public   int maxpage;//当分页获取时，最大的页数
    @Expose
    public  List<M_Message> broadcastList ;//
    @Expose
    public  List<M_Message> messageList ;//


}
