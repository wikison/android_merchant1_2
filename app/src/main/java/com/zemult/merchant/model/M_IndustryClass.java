package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/10/25.
 */

public class M_IndustryClass implements Serializable {

    @Expose
    public int	id			;	//	行业id
    @Expose
    public String	name			;	//	行业名称
    @Expose
    public List<M_UserRole> industryList;

    public int getItemCount() {
        return industryList.size() + 1;
    }

    public Object getItem(int pPosition) {
        // Category排在第一位
        if (pPosition == 0) {
            return name;
        } else {
            return industryList.get(pPosition - 1);
        }
    }



}
