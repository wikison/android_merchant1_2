package com.zemult.merchant.fragment;

import android.os.Bundle;

/**
 * 绑定银行卡fragment与activity通信的回调接口
 */
public interface BindCardFragmentCallBack {
    void showTwo(Bundle bundle);

//    void showThree();

    void showSuccess(Bundle bundle);

    void finishAll();
}
