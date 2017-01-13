package com.zemult.merchant.util.oss;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.zemult.merchant.config.Constants;

import java.util.HashMap;

/**
 * Created by oss on 2015/12/7 0007.
 * 支持普通上传，普通下载和断点上传
 */
public class OssFileService {

    private OSS oss;
    private String bucket;
    private String callbackAddress;
    Context context;

    public OssFileService(Context context, OSS oss, String bucket) {
        this.oss = oss;
        this.bucket = bucket;
        this.context = context;
    }

    public void asyncPutFile(final String object, String localFile) {
        if (object.equals("")) {
            Log.w("AsyncPutFile", "ObjectNull");
            return;
        }

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, object, localFile);
        if (callbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    put("callbackUrl", callbackAddress);
                    //callbackBody可以自定义传入的信息
                    put("callbackBody", "filename=${object}");
                }
            });
        }


        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                Intent intent = new Intent(
                        Constants.BROCAST_OSS_UPLOADSOUND);
                intent.putExtra("status", "ok");
                intent.putExtra("info", result.getRequestId());
                intent.putExtra("object", object);

                context.sendBroadcast(intent);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                String info = "";
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    info = clientExcepion.toString();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                    info = serviceException.toString();
                    Intent intent = new Intent(
                            Constants.BROCAST_OSS_UPLOADIMAGE);
                    intent.putExtra("status", "error");
                    intent.putExtra("info", info);
                    context.sendBroadcast(intent);
                }
            }
        });
    }


}
