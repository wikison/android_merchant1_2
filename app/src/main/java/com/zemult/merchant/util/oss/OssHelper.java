package com.zemult.merchant.util.oss;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.zemult.merchant.config.Constants;

/**
 * Created by zhangkai on 2016/6/14.
 */

public class OssHelper {

    //初始化一个OssService用来上传下载
    public static OssService initOSS(Context context) {

        OSSCredentialProvider credentialProvider;
        // 使用自己的获取STSToken的类
            credentialProvider = new STSGetter(Constants.STSSERVER);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(context, Constants.ENDPOINT, credentialProvider, conf);
        return new OssService(context,oss, Constants.BUCKET);

    }


    //初始化一个OssService用来上传下载
    public static OssFileService initFileOSS(Context context) {

        OSSCredentialProvider credentialProvider;
        // 使用自己的获取STSToken的类
        credentialProvider = new STSGetter(Constants.STSSERVER);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(context, Constants.ENDPOINT, credentialProvider, conf);
        return new OssFileService(context,oss, Constants.BUCKET);

    }
}
