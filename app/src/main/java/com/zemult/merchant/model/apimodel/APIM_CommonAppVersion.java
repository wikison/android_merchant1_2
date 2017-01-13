package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_AppInfo;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_CommonAppVersion extends CommonResult {
    @Expose
   public M_AppInfo appInfo;
    @Expose
    public String url_share_app;//分享app/角色的链接地址 (http://www.54xiegang.com/csdown/index.html)
    @Expose
    public String url_share_news;
    //分享方案的链接地址 (http://server.54xiegang.com/yongyou/app/share_newsInfo.do?id=)
    // --app分享出去时需要app在这个地址后面加上 方案id具体的值例:http://server.54xiegang.com/yongyou/app/share_newsInfo.do?id=141
    @Expose
    public String url_share_taskIndustryRecord;
    //任务完成记录的链接地址 (http://server.54xiegang.com/yongyou/app/share_taskIndustryRecord.do?id=)
    // --app分享出去时需要app在这个地址后面加上 任务完成记录id具体的值例:http://server.54xiegang.com/yongyou/app/share_taskIndustryRecord.do?id=141
    @Expose
    public String url_share_commission;
//    分享探索消费的地址(通过营销经理)
//    (http://server.54xiegang.com/yongyou/wappay/wappay_index.do?taskIndustryRecordId=)--app分享出去时需要app在这个地址后面加上 探索完成记录id具体的值
//    例:http://server.54xiegang.com/yongyou/wappay/wappay_index.do?taskIndustryRecordId=?id=141

}
