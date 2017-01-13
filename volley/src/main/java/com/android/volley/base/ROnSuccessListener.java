package com.android.volley.base;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class ROnSuccessListener<T> implements Response.Listener<String> {
    protected NetBean netBean = new NetBean();


    @Override
    public void onResponse(String  response) {
        try {

            JSONObject jSONObject=new JSONObject(response);
            if(jSONObject.has("response")){
                netBean.setResponse(jSONObject.optString("response"));
            }
            if(jSONObject.has("error_info")){
                netBean.setError_info(jSONObject.optString("error_info"));
            }
            if(jSONObject.has("s")){
                netBean.setS(jSONObject.optInt("s"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public ROnSuccessListener() {
    }



}
