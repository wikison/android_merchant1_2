package com.zemult.merchant.mvp.view;

import com.zemult.merchant.model.M_News;

/**
 * 心情小记详情接口
 *
 * @author djy
 * @time 2016/7/20 15:26
 */
public interface IMoodDetailView {
    void showError(String error);

    void setMoodDetailInfo(M_News moodDetailInfo);
}
