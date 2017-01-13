package com.zemult.merchant.fragment;

import android.os.Bundle;

/**
 * 上传证件fragment与activity通信的回调接口
 */
public interface UploadCredentialsFragmentCallBack {
    void showTwo(Bundle bundle);

    void showSuccess();
}
