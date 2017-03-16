package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Invitation;

import java.util.List;

/**
 * Created by admin on 2017/3/16.
 */

public class APIM_MyInvitationList extends CommonResult {

    @Expose
    public List<M_Invitation> invitationList;

    @Expose
    public int maxpage;//当分页获取时，最大的页数
}
