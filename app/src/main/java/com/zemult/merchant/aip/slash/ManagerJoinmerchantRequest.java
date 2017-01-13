package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户申请商户的某个经营角色(--成功跳转角色管理)
public class ManagerJoinmerchantRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	industryId			;	//	角色id
        public int	merchantId			;	//	商家id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("industryId", industryId+""),
                    new Pair<String, String>("merchantId", merchantId+"")));
        }

    }

    public ManagerJoinmerchantRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_JOINMERCHANT,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
