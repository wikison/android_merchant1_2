package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.R;

import java.io.Serializable;
import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Userinfo implements Serializable {
    //状态0：--不能点添加好友；状态1:跳到添加好友页，输申请理由；状态2：直接调用接受好友请求的接口
    @Expose
    public boolean isNoData = false;
    @Expose
    public int height;
    public String header;
    @Expose
    public int userId;    //	用户id
    @Expose
    String phoneNum;    //	注册手机号
    @Expose
    String account;    //	斜杠账号
    @Expose
    public String name, userName;    //	用户昵称
    @Expose
    public String head, userHead;    //	用户头像
    @Expose
    public String note;    //	用户签名
    @Expose
    public String userNote;    //	用户签名
    @Expose
    public int sex, userSex;    //	性别(0男,1女)
    @Expose
    String province;    //	所属省份
    @Expose
    public String password;

    @Expose
    String city;    //	所属城市
    @Expose
    String area;    //	所属地区
    @Expose
    public int isConfirm;    //	是否实名认证(0未认证,1已认证)
    @Expose
    String pic;    //	封面图--先默认
    @Expose
    String company;    //	用户公司名称
    @Expose
    String position;    //	用户职位名称
    @Expose
    int isOpen;       //	是否公开手机号(0:否,1:是)
    @Expose
    int fansNum;    //	粉丝数
    @Expose
    String provinceName;    //	所属省份 名称
    @Expose
    String cityName;    //	所属城市 名称
    @Expose
    String areaName;    //	所属地区 名称
    @Expose
    int attractNum;    //	关注数
    @Expose
    public int isFan;    //operateUserId有值时提供 是否已经关注(0:未关注1:已关注)
    @Expose
    int isFriend;    //	operateUserId有值时提供 是否好友(0:是好友,1:不是好友或者我之前申请过对方暂未同意,2:不是好友且 对方曾向我申请成为好友,我还未通过)
    private String nicknamepinyin; // 拼音

    //1.1新增
    @Expose
    public double money;//账户余额
    @Expose
    public int isSetPaypwd;//是否设置过安全密码  0否  1是

    @Expose
    String audioTime, audio;//语音长度
    @Expose
    public int friendDimension;//好友维度(0:非好友,1:1度好友,2:2度好友)

    @Expose
    public int state;//状态(0:不是好友且无申请1:等待验证;2:被请求中--等待接收请求)//状态(0:空闲,1:休息,2:忙碌)


    @Expose
    public int level, userLevel;//账户等级

    @Expose int userState;

    public int getUserState() {
        return userState;
    }

    public void setUserState(int userState) {
        this.userState = userState;
    }

    @Expose
    int commonNum;//账户等级

    //1.1搜索 用户(条件:用户名)--搜索出来的是用户列表
    @Expose
    public List<M_UserRole> userIndustryList;//用户的所有角色列表
    @Expose
    int userIndustryNum;//用户的所有角色数量


    @Expose
    public String tagNames;//标签名称
    @Expose
    public int saleNum;//找他买单人数
    @Expose
    public int saleUserNum;//挂靠的商家数
    @Expose
    public String merchantpics;//商家图片(多个用","分隔，最多显示3个)
    @Expose
    public String pics;//相册图片(多个用","分隔，最多显示5个)
    @Expose
    public int managerUserNum;//管理商家数(管理的商家)

    public int getManagerUserNum() {
        return managerUserNum;
    }

    public void setManagerUserNum(int managerUserNum) {
        this.managerUserNum = managerUserNum;
    }

    public int getSaleUserNum() {
        return saleUserNum;
    }

    public void setSaleUserNum(int saleUserNum) {
        this.saleUserNum = saleUserNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getIsSetPaypwd() {
        return isSetPaypwd;
    }

    public void setIsSetPaypwd(int isSetPaypwd) {
        this.isSetPaypwd = isSetPaypwd;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public int getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }

    public int getIsFan() {
        return isFan;
    }

    public void setIsFan(int isFan) {
        this.isFan = isFan;
    }

    public String getNicknamepinyin() {
        return nicknamepinyin;
    }

    public void setNicknamepinyin(String nicknamepinyin) {
        this.nicknamepinyin = nicknamepinyin;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
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

    public int getIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(int isConfirm) {
        this.isConfirm = isConfirm;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
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

    public int getAttractNum() {
        return attractNum;
    }

    public void setAttractNum(int attractNum) {
        this.attractNum = attractNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

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

    public String getAudioTime() {
        return audioTime;
    }

    public void setAudioTime(String audioTime) {
        this.audioTime = audioTime;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public int getFriendDimension() {
        return friendDimension;
    }

    public void setFriendDimension(int friendDimension) {
        this.friendDimension = friendDimension;
    }

    public int getCommonNum() {
        return commonNum;
    }

    public void setCommonNum(int commonNum) {
        this.commonNum = commonNum;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<M_UserRole> getUserIndustryList() {
        return userIndustryList;
    }

    public void setUserIndustryList(List<M_UserRole> userIndustryList) {
        this.userIndustryList = userIndustryList;
    }

    public int getUserIndustryNum() {
        return userIndustryNum;
    }

    public void setUserIndustryNum(int userIndustryNum) {
        this.userIndustryNum = userIndustryNum;
    }

    public boolean showLatest;
    public boolean showAll;
    @Expose
    public double saleMoney;//交易额
    @Expose
    public double saleUserMoney;//获得佣金
    @Expose
    public int isBlack;//是否在黑名单（0否1是）
    @Expose
    public int comment;//被评价的总星数
    @Expose
    public int commentNumber;//被评价的总次数

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Expose
    public String tags;//标签(多个用"，"分隔)
    @Expose
    public double experience;//经验值

    public double getExperience() {
        return experience;
    }

    public void setExperience(double experience) {
        this.experience = experience;
    }

    public String getExperienceText() {
        String result = "";
        if (experience < 100) {
            result = "新手";
        } else if (experience >= 100 && experience < 10000) {
            result = "铜牌";
        } else if (experience >= 10000 && experience < 100000) {
            result = "银牌";
        } else if (experience >= 100000 && experience < 1000000) {
            result = "金牌";
        } else {
            result = "钻石";
        }

        return result;
    }

    public int getExperienceImg() {
        int result = 0;
        if (experience < 100) {
            result = R.mipmap.xinshou_icon;
        } else if (experience >= 100 && experience < 10000) {
            result = R.mipmap.tong_icon2;
        } else if (experience >= 10000 && experience < 100000) {
            result = R.mipmap.yin_icon2;
        } else if (experience >= 100000 && experience < 1000000) {
            result = R.mipmap.jingpai_icon2;
        } else {
            result = R.mipmap.demon_icon2;
        }

        return result;
    }

    public String remarkName; // 备注名
}
