package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Reservation;

import java.util.List;

/**
 * Created by admin on 2017/1/20.
 */

public class APIM_UserReservationList extends CommonResult {
    @Expose
    public List<M_Reservation> reservationList;

    @Expose
    public int maxpage;//当分页获取时，最大的页数

}
