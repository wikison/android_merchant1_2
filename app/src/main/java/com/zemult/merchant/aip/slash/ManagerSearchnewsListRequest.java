package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_ManagerSearchnewsList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//搜索方案列表
public class ManagerSearchnewsListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	operateUserId			;	//	操作的用户id(预留)
        public String	city			;	//	市编号
        public String	center			;	//	用户中心点坐标规则：经度和纬度用","分割;例 "119.971736,31.829737"
        public int	industryId			;	//	行业id(为-1时 表示全行业)
        public int 	sex			;	//	性别 (0:男,1:女,-1:全部)
        public int	distance			;	//	距离(-1:不限,)单位:米,例：5公里以内--5000
        public String	industryName			;	//	角色名称(模糊)
        public String	merchantName			;	//	场景名称(模糊)
        public int	orderType			;	//	排序方式(0:发布时间,1:人气-点赞数,2:距离)
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn(){
            if(operateUserId==0){
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("city", city), new Pair<String, String>("center", center),
                        new Pair<String, String>("industryId", industryId+""), new Pair<String, String>("sex", sex+""), new Pair<String, String>("distance", distance+""),
                        new Pair<String, String>("industryName", industryName), new Pair<String, String>("merchantName", merchantName), new Pair<String, String>("orderType", orderType+""),
                        new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
            }
            else{
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId+""), new Pair<String, String>("city", city), new Pair<String, String>("center", center),
                        new Pair<String, String>("industryId", industryId+""), new Pair<String, String>("sex", sex+""), new Pair<String, String>("distance", distance+""),
                        new Pair<String, String>("industryName", industryName), new Pair<String, String>("merchantName", merchantName), new Pair<String, String>("orderType", orderType+""),
                        new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
            }

        }

    }

    public ManagerSearchnewsListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_SEARCHNEWSLIST,input.ejson , new TypeToken<APIM_ManagerSearchnewsList>() {
        }.getType() , listener);

    }
}
