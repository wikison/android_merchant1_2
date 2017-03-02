package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//修改用户资料信息
public class UserEditinfoRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	用户id
        public String name;    //	名字
        public String head="";    //	头像
    //    public String account;    //	斜杠号
        public int sex;    //	性别(0男,1女)
    //    public String company;    //	公司名称
    //    public String position;    //	职位名称
        public int isOpen;    //	是否公开工作经历(0:否,1:是)
     //   public String province;    //	所属省份 编号
       // public String city;    //	所属城市 编号
     //   public String area;    //	所属地区 编号

   //     public String note;    //	个性简介 文字内容
    //    public String audio;    //	个人简介 语音地址
    //    public String audioTime;    //	语音长度

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("name", name),
                    new Pair<String, String>("head", head == null ? "":head),
                    new Pair<String, String>("sex", sex + ""),
                    new Pair<String, String>("isOpen", isOpen + "")

                    ));//,new Pair<String, String>("area", area)
        }

    }

    public UserEditinfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_EDITINFO, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
