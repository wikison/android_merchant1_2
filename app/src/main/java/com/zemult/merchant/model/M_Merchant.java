package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Merchant implements Serializable {
    @Expose
    public int merchantId;    //	商家(场景)id
    @Expose
    public String name;    //	商家名称
    @Expose
    public String head;    //	头像
    @Expose
    public String industryNames;    //	场景下的可加盟的角色名称(多个用","分隔)
    @Expose
    public String distance;    //	距中心点距离(米)
    @Expose
    public int personNum;    //	参与人数
    @Expose
    public List<M_Merchant> merchantList;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Expose
    public String pic;    //	封面
    @Expose
    public String pics;    //	相册前10图片(","分隔)
    @Expose
    public int industryId;    //	行业(场景分类)id
    @Expose
    public String province;    //	场景省 地区编号
    @Expose
    public String city;    //	场景市 地区编号
    @Expose
    public String area;    //	场景区 地区编号
    @Expose
    public String tel;    //	联系电话
    @Expose
    public String address;    //	详细地址
    @Expose
    public String east;    //	商家地址的经度 (高德地图上的)
    @Expose
    public String west;    //	商家地址的纬度 (高德地图上的)
    @Expose
    public String detail;    //	商家简介
    @Expose
    public String industryName;    //	行业(场景分类)名称
    @Expose
    public String provinceName;    //	场景省 名称
    @Expose
    public String cityName;    //	场景市 名称
    @Expose
    public String areaName;    //	场景区 名称
    @Expose
    public int status, state;    //	审核状态(0审核未通过,1审核中,2审核通过)
    @Expose
    public String shortName;
    @Expose
    public String IDphotos;

    private String strHeadTag;

    public String getStrHeadTag() {
        return strHeadTag;
    }

    public void setStrHeadTag(String strHeadTag) {
        this.strHeadTag = strHeadTag;
    }

    @Expose
    public double commissionDiscount;  //佣金百分比(0-100)

    public boolean isNoData = false;
    public int height;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isNoData() {
        return isNoData;
    }

    public void setNoData(boolean noData) {
        isNoData = noData;
    }

    @Expose
    public double perMoney; // 人均消费
    @Expose
    public double saleMoney; // 交易额
    @Expose
    public int saleNum; // 交易数量
    @Expose
    public int payNum; // 消费人数
    @Expose
    public double saleUserMoney; // 获得佣金
    @Expose
    public int saleuserNum; // 营销经理人数
    @Expose
    public String saleUserHeads; // 营销经理们的头像(最对显示3个，以"，"分隔)
    @Expose
    public int isFan; // 是否有熟人-(关注的人)(0:否1:是)--游客默认为0
    @Expose
    public int fromType;    //	来源类型(0:系统录入 1:用户提交)
    @Expose
    public String bankCard; // 商户银行卡号
    @Expose
    public double FMoney; // 商户账户金额
    @Expose
    public String aliAccount; // 商家支付宝账号
    @Expose
    public int moneyType; // 支付账号类型(0:银行卡,1:支付宝)
    @Expose
    public String bankName; // 银行名称
    @Expose
    public String bankUser; // 商户银行卡号(moneyType=0必填)
    @Expose
    public int reviewstatus; // 审核状态(0待审核,1审核失败,2审核通过)--request的状态
    @Expose
    public String checkNote; // 审核原因(失败)
    @Expose
    public int isCommission; //操作用户是否已经是该商户的营销经理(0:否,1:是)(operateUserId为空时默认为0)
    @Expose
    public int picNum;    //	相册数目

    public boolean daiqiyue;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Expose
    public String tags;  // 他在商家下的标签(多个用","分隔)

    @Expose
    public int comment;  // 被评价的总星数

    @Expose
    public int commentNumber;  // 被评价的总次数
    @Expose
    public String createTime;  // 时间"yyyy-MM-dd HH:mm:ss"
    @Expose
    public int isFavorite;  // 收藏商户 0否1是


    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

}
