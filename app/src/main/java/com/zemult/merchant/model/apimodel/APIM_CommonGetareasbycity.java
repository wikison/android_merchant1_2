package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Zone;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_CommonGetareasbycity extends CommonResult {
    @Expose
    List<M_Zone> areaList ;//区列表

}
