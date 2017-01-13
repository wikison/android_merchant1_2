package com.zemult.merchant.mvp.view;

import com.zemult.merchant.model.M_Comment;
import com.zemult.merchant.model.M_Pic;

import java.util.List;

/**
 * IPicView
 * @author djy
 * @time 2016/11/25 9:21
 */
public interface IPicView {
    void showProgressDialog();
    void hideProgressDialog();
    void showError(String error);

    void setPicList(List<M_Pic> list);
    void addPicSuccess();
    void delPicSuccess();
}
