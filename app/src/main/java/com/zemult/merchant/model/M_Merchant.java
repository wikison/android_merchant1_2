package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.R;

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
    public String merchantName;    //	商家名称
    @Expose
    public String head;    //	头像
    @Expose
    public String merchantPic;    //	商家封面
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
    public int commentNum;  // 被评价的总次数

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

    @Expose
    public int isSaleUser;  // 是否有服务管家(0:否,1:是，没有时下面服务管家的返回为null)
    @Expose
    public int saleUserId;  // 是否有服务管家(0:否,1:是，没有时下面服务管家的返回为null)
    @Expose
    public String saleUserName;  // 服务管家的用户昵称
    @Expose
    public String saleUserHead;  // 服务管家的用户头像
    @Expose
    public String saleUserTags;  // 服务管家的标签(","分隔)
    @Expose
    public double saleUserExperience;  // 服务管家的经验值
    @Expose
    public int saleUserSumScore;  // 前7日的服务分总和
    @Expose
    public int sumScore;  // 前7日的服务分总和
    @Expose
    public int saleUserFanNum;  // 服务管家的顾客数
    @Expose
    public int unSureOrderNum;  //待确认服务单数量
    @Expose
    public String position;  //管家在该商户下职位

    public String getExperienceText() {
        String result = "";
        if (saleUserExperience < 100) {
            result = "新手";
        } else if (saleUserExperience >= 100 && saleUserExperience < 10000) {
            result = "铜牌";
        } else if (saleUserExperience >= 10000 && saleUserExperience < 100000) {
            result = "银牌";
        } else if (saleUserExperience >= 100000 && saleUserExperience < 1000000) {
            result = "金牌";
        } else {
            result = "钻石";
        }

        return result;
    }

    public int getExperienceImg() {
        int result = 0;
        if (saleUserExperience < 100) {
            result = R.mipmap.xinshou_icon;
        } else if (saleUserExperience >= 100 && saleUserExperience < 10000) {
            result = R.mipmap.tong_icon2;
        } else if (saleUserExperience >= 10000 && saleUserExperience < 100000) {
            result = R.mipmap.yin_icon2;
        } else if (saleUserExperience >= 100000 && saleUserExperience < 1000000) {
            result = R.mipmap.jingpai_icon2;
        } else {
            result = R.mipmap.demon_icon2;
        }

        return result;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantPic() {
        return merchantPic;
    }

    public void setMerchantPic(String merchantPic) {
        this.merchantPic = merchantPic;
    }

    public String getIndustryNames() {
        return industryNames;
    }

    public void setIndustryNames(String industryNames) {
        this.industryNames = industryNames;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getPersonNum() {
        return personNum;
    }

    public void setPersonNum(int personNum) {
        this.personNum = personNum;
    }

    public List<M_Merchant> getMerchantList() {
        return merchantList;
    }

    public void setMerchantList(List<M_Merchant> merchantList) {
        this.merchantList = merchantList;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public int getIndustryId() {
        return industryId;
    }

    public void setIndustryId(int industryId) {
        this.industryId = industryId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEast() {
        return east;
    }

    public void setEast(String east) {
        this.east = east;
    }

    public String getWest() {
        return west;
    }

    public void setWest(String west) {
        this.west = west;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getIDphotos() {
        return IDphotos;
    }

    public void setIDphotos(String IDphotos) {
        this.IDphotos = IDphotos;
    }

    public double getCommissionDiscount() {
        return commissionDiscount;
    }

    public void setCommissionDiscount(double commissionDiscount) {
        this.commissionDiscount = commissionDiscount;
    }

    public double getPerMoney() {
        return perMoney;
    }

    public void setPerMoney(double perMoney) {
        this.perMoney = perMoney;
    }

    public double getSaleMoney() {
        return saleMoney;
    }

    public void setSaleMoney(double saleMoney) {
        this.saleMoney = saleMoney;
    }

    public int getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(int saleNum) {
        this.saleNum = saleNum;
    }

    public int getPayNum() {
        return payNum;
    }

    public void setPayNum(int payNum) {
        this.payNum = payNum;
    }

    public double getSaleUserMoney() {
        return saleUserMoney;
    }

    public void setSaleUserMoney(double saleUserMoney) {
        this.saleUserMoney = saleUserMoney;
    }

    public int getSaleuserNum() {
        return saleuserNum;
    }

    public void setSaleuserNum(int saleuserNum) {
        this.saleuserNum = saleuserNum;
    }

    public String getSaleUserHeads() {
        return saleUserHeads;
    }

    public void setSaleUserHeads(String saleUserHeads) {
        this.saleUserHeads = saleUserHeads;
    }

    public int getIsFan() {
        return isFan;
    }

    public void setIsFan(int isFan) {
        this.isFan = isFan;
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public double getFMoney() {
        return FMoney;
    }

    public void setFMoney(double FMoney) {
        this.FMoney = FMoney;
    }

    public String getAliAccount() {
        return aliAccount;
    }

    public void setAliAccount(String aliAccount) {
        this.aliAccount = aliAccount;
    }

    public int getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(int moneyType) {
        this.moneyType = moneyType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankUser() {
        return bankUser;
    }

    public void setBankUser(String bankUser) {
        this.bankUser = bankUser;
    }

    public int getReviewstatus() {
        return reviewstatus;
    }

    public void setReviewstatus(int reviewstatus) {
        this.reviewstatus = reviewstatus;
    }

    public String getCheckNote() {
        return checkNote;
    }

    public void setCheckNote(String checkNote) {
        this.checkNote = checkNote;
    }

    public int getIsCommission() {
        return isCommission;
    }

    public void setIsCommission(int isCommission) {
        this.isCommission = isCommission;
    }

    public int getPicNum() {
        return picNum;
    }

    public void setPicNum(int picNum) {
        this.picNum = picNum;
    }

    public boolean isDaiqiyue() {
        return daiqiyue;
    }

    public void setDaiqiyue(boolean daiqiyue) {
        this.daiqiyue = daiqiyue;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public int getIsSaleUser() {
        return isSaleUser;
    }

    public void setIsSaleUser(int isSaleUser) {
        this.isSaleUser = isSaleUser;
    }

    public int getSaleUserId() {
        return saleUserId;
    }

    public void setSaleUserId(int saleUserId) {
        this.saleUserId = saleUserId;
    }

    public String getSaleUserName() {
        return saleUserName;
    }

    public void setSaleUserName(String saleUserName) {
        this.saleUserName = saleUserName;
    }

    public String getSaleUserHead() {
        return saleUserHead;
    }

    public void setSaleUserHead(String saleUserHead) {
        this.saleUserHead = saleUserHead;
    }

    public String getSaleUserTags() {
        return saleUserTags;
    }

    public void setSaleUserTags(String saleUserTags) {
        this.saleUserTags = saleUserTags;
    }

    public double getSaleUserExperience() {
        return saleUserExperience;
    }

    public void setSaleUserExperience(double saleUserExperience) {
        this.saleUserExperience = saleUserExperience;
    }

    public int getSaleUserSumScore() {
        return saleUserSumScore;
    }

    public void setSaleUserSumScore(int saleUserSumScore) {
        this.saleUserSumScore = saleUserSumScore;
    }

    public int getSumScore() {
        return sumScore;
    }

    public void setSumScore(int sumScore) {
        this.sumScore = sumScore;
    }

    public int getSaleUserFanNum() {
        return saleUserFanNum;
    }

    public void setSaleUserFanNum(int saleUserFanNum) {
        this.saleUserFanNum = saleUserFanNum;
    }

    public int getUnSureOrderNum() {
        return unSureOrderNum;
    }

    public void setUnSureOrderNum(int unSureOrderNum) {
        this.unSureOrderNum = unSureOrderNum;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Expose
    public int planId;  //服务方案id
    @Expose
    public String planName; //服务方案标题
}
